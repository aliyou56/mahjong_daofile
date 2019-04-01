package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.dao.DAOMahJong;
import fr.univubs.inf1603.mahjong.dao.Persistable;
import fr.univubs.inf1603.mahjong.daofile.FileHeaderRow.FileHeader;
import fr.univubs.inf1603.mahjong.daofile.IndexRow.Index;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * La classe générique <code>FileDAOMahJong</code> définit la notion de
 * persistance d'un objet T dans un fichier. Un fichier d'index est associé à
 * chaque fichier de données.
 * <pre>
 * format d'un fichier de données :
 *        ---------------------------------------------
 *        | ----------------------------------------- |
 *        | | idRow = 0 |          FileHeader       | |  --> FileHeaderRow
 *        | ----------------------------------------- |
 *        | | idRow = x |             T             | |  --> TRow
 *        | ----------------------------------------- |
 *        | | idRow = x |             T             | |  --> TRow
 *        | ----------------------------------------- |
 *        | | idRow = x |             T             | |  --> TRow
 *        | ----------------------------------------- |
 *        ---------------------------------------------
 * </pre>
 *
 * @author aliyou
 * @version 1.0.0
 * @param <T> Objet à persister
 */
public abstract class FileDAOMahJong<T extends Persistable> extends DAOMahJong<T> {

    /**
     * Logging
     */
    private final static Logger LOGGER = Logger.getLogger(FileDAOMahJong.class.getName());

    /**
     * Chemin d'accès du repertoire racine.
     */
    protected Path rootDirPath;
    /**
     * Chemin d'accès du fichier de données
     */
    protected Path dataFilePath;
    /**
     * Chemin d'accès du fichier d'index.
     */
    protected Path indexFilePath;
    /**
     * Fichier de données
     */
    protected RandomAccessFile dataFile;
    /**
     * Liste des tuples de données
     */
    protected List<AbstractRow<T>> dataRows;
    /**
     * Gère l'ensemble des index associés aux données
     */
    protected IndexManager indexManager;
    /**
     * stock l'en-tete du fichier
     */
    private FileHeader fileHeader;
    /**
     * Contient l'en-tete du fichier
     */
    private FileHeaderRow fhr;
    /**
     * Processus qui écrit dans le fichier de données.
     */
    protected FileWriter dataWriter;

    /**
     * Constructeur avec le chemin d'accès du repertoire racine et les noms des
     * fichiers de données et d'index.
     *
     * @param rootDir Chemin d'accès du repertoire racine.
     * @param dataFilename Nom du fichier de données.
     * @param indexFilename Nom du fichier d'index.
     * @throws DAOException s'il y'a une erreur lors de l'instanciation.
     */
    FileDAOMahJong(Path rootDir, String dataFilename, String indexFilename) throws DAOException {
        try {
            this.rootDirPath = rootDir;
            this.dataFilePath = Paths.get(rootDir.toString(), dataFilename);
            this.indexFilePath = Paths.get(rootDir.toString(), indexFilename);
            this.dataRows = new ArrayList<>();
            if (!rootDir.toFile().exists()) {
                rootDir.toFile().mkdirs();
                LOGGER.log(Level.INFO, "rootDir created");
            }
            this.dataFile = new RandomAccessFile(dataFilePath.toFile(), "rw");
            this.dataWriter = new FileWriter(this.dataFile.getChannel());
            this.indexManager = new IndexManager(this.indexFilePath);
            this.fhr = FileUtilities.loadFileHeader(dataFile.getChannel());
            this.fhr.addPropertyChangeListener(dataWriter);
            this.fileHeader = fhr.getData();
            System.out.println("FileDAOMahJong : " + fileHeader);
        } catch (IOException ioe) {
            throw new DAOException("Erreur IO : " + ioe.getMessage());
        }
    }

    /**
     * Mets un tuple dans la liste d'attente du processus qui écrit dans le
     * fichier de données. Crée un nouvel index associé à la objet encapsulé
     * dans le tuple via le gestionnaire d'index. Et incrémente le nombre total
     * de tuple dans le fichier de données.
     *
     * @param dataID Identifiant de l'objet encapuslé dans le tuple.
     * @param row Tuple à écrire dans le fichier de données.
     * @throws IOException s'il y'a une erreur lors de la création de l'index
     * associé à la donnée.
     */
    protected final void write(UUID dataID, AbstractRow row) throws IOException { 
        dataRows.add(row);
        dataWriter.addRowToDirtyList(row); 
        row.addPropertyChangeListener(dataWriter);
        Index index = new Index(dataID, row.getRowPointer());
        indexManager.addIndex(index);
        fileHeader.incrementRowNumber();
    }

    /**
     * Lis le fichier de données à partir du pointeur reçu en argument et mets
     * les données lues dans un tampon d'octets initialisé avec la taille du
     * tuple.
     *
     * @param dataID Identifiant de l'objet à charger
     * @param rowSize Taille du tuple
     * @param pointer Pointeur du tuple
     * @return Le tampon contenant les données lues s'il y'a des données à lire
     * à partir du pointeur sinon <code>null</code>
     * @throws IOException s'il y'a une erreur lors de la lecture du fichier de
     * données.
     */
    protected final ByteBuffer load(UUID dataID, int rowSize, long pointer) throws IOException {
        if (pointer != -1) {
            FileChannel fileChannel = dataFile.getChannel();
            fileChannel.position(pointer);
            ByteBuffer buffer = ByteBuffer.allocate(rowSize);
            if (fileChannel.read(buffer) > 0) {
                buffer.flip();
                return buffer;
            }
        }
        return null;
    }

    /**
     * Supprimer un tuple du fichier de données en tronquant le fichier à la
     * position du tuple plus des éventuels tuples restants.
     *
     * @param dataID Identifiant de l'objet encapsulé dans le tuple à retirer.
     * @param rowSize Taille du tuple à retirer
     * @return <code>true</code> si le tuple a bien été supprimer sinon
     * <code>false</code>.
     * @throws IOException s'il y'a une erreur lors de la suppression.
     */
    protected final boolean remove(UUID dataID, int rowSize) throws IOException {
        IndexRow indexRow = (IndexRow) indexManager.getRow(dataID);
        if (indexRow != null) {
            Index index = indexRow.getData();
            int pointer = (int) index.getPointer();
            FileChannel fc = dataFile.getChannel();
//                    System.out.println("FDM -> pointer : " +pointer + "rowSize : " + rowSize);
            if (FileUtilities.deleteFromFile(fc, pointer, rowSize)) {
                AbstractRow r = null;
                for (AbstractRow dataRow : dataRows) {
//                    System.out.println("dataRow.getRowPointer() ? pointer : " +dataRow.getRowPointer() + " ? " + pointer);
                    if (dataRow.getRowPointer() == pointer) {
                        dataRow.setRowPointer(-1, false);
                        r = dataRow;
                    }
                    if (dataRow.getRowPointer() > pointer) {
                        long newPointer = dataRow.getRowPointer() - rowSize;
                        newPointer = newPointer > 12 ? newPointer : 12;
                        System.out.println(dataRow + " -> newPointer : " + newPointer);
                        dataRow.setRowPointer(newPointer, false);
                    }
                }
                if(r != null) {
                    r.removePropertyChangeListener(dataWriter);
                    dataRows.remove(r);
                }
                fileHeader.decrementRowNumber();
                indexManager.removeIndex(dataID, rowSize);
                return true;
            }
        }
        return false;
    }

    /**
     * Rétourne le nombre total de tuple dans le fichier de données.
     *
     * @return Nombre total de tuple dans le fichier de données
     */
    protected int getRowNumber() {
        return fileHeader.getRowNumber();
    }

    /**
     * Rétourne un identifiant pour le prochain tuple.
     *
     * @return Identifiant pour le prochain tuple
     */
    protected int getNexRowID() {
        fileHeader.updateRowLastId();
        return fileHeader.getRowLastId();
    }

    /**
     * Rétourne un pointeur de données pour le prochain tuple.
     *
     * @param recordSize Taille du tuple.
     * @return Pointeur de données pour le prochain tuple.
     */
    protected long getNextRowPointer(int recordSize) {
        return FileHeaderRow.FILE_HEADER_ROW_SIZE + (fileHeader.getRowNumber() * recordSize);
    }
}
