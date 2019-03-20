
package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.dao.DAOException;
import fr.univubs.inf1603.mahjong.dao.DAOMahJong;
import fr.univubs.inf1603.mahjong.daofile.FileHeaderRow.FileHeader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author aliyou
 * @version 1.0.0
 * @param <T> Objet à persister
 */
public abstract class FileDAOMahJong<T> extends DAOMahJong<T> {
   
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
    protected List<Row> dataRows;
    /**
     * Gère l'ensemble des index associés aux données
     */
    protected IndexManager indexManager;
    /**
     * stock l'en-tete du fichier
     */
    protected FileHeader fileHeader;
    /**
     * Contient l'en-tete du fichier
     */
    private FileHeaderRow fhr;
    /**
     * Processus qui écrit dans le fichier de données.
     */
    protected FileWriter dataWriter;
    
    /**
     * Constructeur avec les noms des fichiers de données et d'index.
     * @param dataFilename Nom du fichier de données.
     * @param indexFilename Nom du fichier d'index.
     * @throws DAOException s'il y'a une erreur lors de l'instanciation.
     */
    FileDAOMahJong(String dataFilename, String indexFilename) throws DAOException {
        this(Paths.get(System.getProperty("user.home"), "MahJong"), dataFilename, indexFilename);
    }

    /**
     * Constructeur avec le chemin d'accès du repertoire racine et les noms des
     * fichiers de données et d'index.
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
        } catch (IOException ioe) {
            throw new DAOException("Erreur IO : " + ioe.getMessage());
        } 
    }
    
//    protected void write(Row row, int rowSize) throws IOException {
//            long recordPointer = getNextRowPointer(rowSize);
//            Record record = new TileRecord(getNexRowId(), tile, recordPointer);
//            dataRows.add(row);
//            
//            dataWriter.addRecordToDirtyList(row);
//            record.addObserver(dataWriter);
//            
//            indexManager.addDataIndex(uuid, recordPointer);
//            
//            fileHeader.incrementRowNumber();
//    }

    /**
     * 
     * @return 
     */
//    protected int getRowNumber() {
//        return fileHeader.getRowNumber();
//    }
    /**
     * Rétourne un identifiant pour le prochain tuple.
     * @return Identifiant pour le prochain tuple
     */
    protected int getNexRowId() {
        fileHeader.updateRowLastId();
        return fileHeader.getRowLastId();
    }
    
    /**
     * Rétourne un pointeur de données pour le prochain tuple. 
     * @param recordSize Taille du tuple.
     * @return Pointeur de données pour le prochain tuple.
     */
    protected long getNextRowPointer(int recordSize) {
        return FileHeaderRow.FILE_HEADER_ROW_SIZE + (fileHeader.getRowNumber() * recordSize);
    }
}