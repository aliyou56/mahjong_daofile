package fr.univubs.inf1603.mahjong.daofile.filemanagement;

import static fr.univubs.inf1603.mahjong.daofile.FileDAOUtilities.checkNotNull;
import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException;
import fr.univubs.inf1603.mahjong.daofile.exception.DAOFileWriterException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * La classe {@code DAOFileWriter} gère tout ce qui est lecture et écriture
 * dans un fichier. Elle fournit des méthodes qui écrivent ou suppriment des
 * tuples d'un fichier. Elle fournit également des méthodes statiques qui
 * permettent d'écrire ou de lire des objets tels que <code>UUID</code>,
 * <code>String</code> dans un tampon de d'octets <code>ByteBuffer</code>.
 *
 * @author aliyou, nesrine
 * @version 1.1.0
 */
public class DAOFileWriter implements PropertyChangeListener {

    /**
     * Logging
     */
    private final static Logger LOGGER = Logger.getLogger(DAOFileWriter.class.getName());

    /**
     * Temps d'attente (en séconde) avant d'écrire dans un fichier.
     */
    private static final int WRITING_TIMER = 2;
    /**
     * Chemin d'accès d'un fichier.
     */
    private final Path filePath;
    /**
     * Fichier
     */
    private RandomAccessFile file;
//    private FileChannel fileChannel;
    
    /**
     * Liste de tuples qui sont écrits d'un seul coup (liste de tuples ordonnés
     * suivant le pointeur de tuple).
     */
    private List<AbstractRow> multipleWritingList;
    /**
     * Liste de tuples qui sont écrits un à un (liste de tuples désordonnés
     * suivant le pointeur de tuple).
     */
    private List<AbstractRow> singleWritingList;

    /**
     *
     */
    ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
    /**
     *
     */
    ScheduledFuture<?> future = null;

    /**
     * Constructeur avec un chemin de fichier {@code filePath}.
     *
     * @param filePath Chemin d'accès d'un fichier. NE DOIT PAS ETRE NULL.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileWriterException
     * s'il y'a une erreur lors de l'instanciation.
     */
    public DAOFileWriter(Path filePath) throws DAOFileWriterException {
        try {
            checkNotNull("filePath", filePath);
            this.filePath = filePath;
//            open();
            this.file = new RandomAccessFile(filePath.toFile(), "rw");
//            this.ficleChannel = this.file.getChannel();
            this.multipleWritingList = new ArrayList<>();
            this.singleWritingList = new ArrayList<>();
        } catch (FileNotFoundException ex) { // must never come because file is open with rw mode.
            throw new DAOFileWriterException("File not found : " + filePath);
        }
    }
    
//    private void open() throws FileNotFoundException {
//        this.file = new RandomAccessFile(filePath.toFile(), "rw");
//        this.fileChannel = this.file.getChannel();
//    }

    public long getFileLenght() throws IOException {
        return this.file.length();
    }

    /**
     * Renvoi un tampon d'octets {@code ByteBuffer} contenant
     * <code>lenght</code> octets lus depuis un fichier à partir de la position
     * <code>position</code>. Et rétourne un un tampon d'octets
     * <code>ByteBuffer</code> contenant les données.
     *
     * @param position Position à partir de laquelle la lecture est commencée. -
     * DOIT ETRE POSITIF - NE DOIT PAS ETRE SUPERIEUR A LA TAILLE DU FICHIER.
     * @param lenght Nombre d'octets à lire.
     * @return Le tampon contenant les données lues s'il y'a des données à lire
     * à partir du pointeur sinon <code>null</code>
     * @throws
     * fr.univubs.inf1603.mahjong.daofile.exception.DAOFileWriterException s'il
     * y'a une erreur lors de la lecture d'un fichier de données.
     */
    synchronized public ByteBuffer read(long position, int lenght) throws DAOFileWriterException {
        if (position < 0) {
            throw new IllegalArgumentException(" position '" + position + "' must be positive or zero");
        }
        if (lenght < 0) {
            throw new IllegalArgumentException(" lenght '" + lenght + "' must be greater than zero.");
        }
        try {
            if (position > file.length()) {
                throw new DAOFileWriterException(" position '" + position + "' is greater than file size '" + file.length() + "'");
            }
//            if(!this.fileChannel.isOpen()) {
//                open();
//            }
            FileChannel fileChannel = file.getChannel();
            fileChannel.position(position);
            ByteBuffer buffer = ByteBuffer.allocate(lenght);
            if (fileChannel.read(buffer) != -1) {
                buffer.flip();
                return buffer;
            }
        } catch (IOException ex) {
            String message = " couldn't read "+lenght+" bytes from position '" +position+ "' in the file '" +filePath+ "'"
                    + "\n\t cause -> IO error occurs : " + ex.getMessage();
            LOGGER.log(Level.WARNING, message);
            throw new DAOFileWriterException(message);
        }
        return null;
    }

    /**
     * Ecrit un tampon d'octets <code>buffer</code> dans un fichier à partir de
     * la position <code>position</code>.
     *
     * @param position Position à partir de laquelle l'écriture est commencée.
     * DOIT ETRE POSITIVE.
     * @param buffer Tampon d'octets à écrire dans le fichier. NE DOIT PAS ETRE
     * NULL.
     * @return Le nombre d'octets écrit dans le ficheir.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileWriterException si
     * il y'a une erreur lors de l'écriture.
     */
    synchronized public int write(long position, ByteBuffer buffer) throws DAOFileWriterException {
        checkNotNull("buffer", buffer);
        if (position >= 0) {
            try {
//                if (!this.fileChannel.isOpen()) {
//                    open();
//                }
                FileChannel fileChannel = file.getChannel();
                buffer.flip();
                FileLock fileLock = fileChannel.tryLock(position, buffer.capacity(), true);
                if (fileLock != null) {
                    fileChannel.position(position);
                    while (buffer.hasRemaining()) {
                        fileChannel.write(buffer);
                    }
                    fileLock.release();
                    return (int) (buffer.position() - position);
                } else {
                    LOGGER.log(Level.WARNING, " **** can't have lock");
                }
            } catch (IOException ex) {
                String message = " couldn't write the buffer '"+ buffer+"' at the position '"+position+"' "
                        + " \n\t cause -> IO error occurs : " +ex.getMessage();
                if (!this.file.getChannel().isOpen()) {
                    try {
                        this.file = new RandomAccessFile(filePath.toFile(), "rw");
                    } catch (FileNotFoundException ex1) {}
                }
                LOGGER.log(Level.WARNING, message);
                throw new DAOFileWriterException(message);
            }
        }
        return -1;
    }

    /**
     * Ce thread répresente le processus qui écrit dans un fichier. Il écrit
     * tous les tuples présents dans les deux listes d'attentes
     * <code>multipleWritingList</code> et <code>msingleWritingList</code>
     * <br>S'il y'a des tuples dans la liste <code>multipleWritingList</code> et
     * que les pointeurs de ces tuples sont bien dans l'ordre et se suivent, il
     * initialise un tampon d'octets <code>ByteBuffer</code> avec une taille
     * correspondant à la somme des tailles des tuples dans la liste
     * <code>multipleWritingList</code> puis ce tampon est passé à chaque tuple
     * présent dans la liste <code>multipleWritingList</code> qui va s'y écrire.
     * Enfin le tampon est écrit dans le fichier.
     */
    Runnable writeToDisk = () -> {
        try {
            if (!multipleWritingList.isEmpty()) {
                AbstractRow firstRow = multipleWritingList.get(0);
                ByteBuffer buffer = ByteBuffer.allocate(multipleWritingList.size() * firstRow.getRowSize());
                long offset = firstRow.getRowPointer() + firstRow.getRowSize();
                for (AbstractRow row : multipleWritingList) {
                    if (offset - row.getRowPointer() == firstRow.getRowSize()) {
                        if (row.getRowPointer() > -1) {
//                        if (row.hasChanged()) {
                            row.write(buffer);
                        }
                        offset += firstRow.getRowSize();
                    } else {
                        addRowToSingleWritingList(row);
                    }
                }
                if (write(firstRow.getRowPointer(), buffer) != -1) {
                    System.out.println(" multiple writing list writed on disk -> "
                            + multipleWritingList.size() + " " + firstRow.getClass().getSimpleName()
                            + ", buff : " + buffer);
                }
                multipleWritingList.clear();
            }

            if (!singleWritingList.isEmpty()) {
                for (AbstractRow row : singleWritingList) {
                    ByteBuffer buffer = ByteBuffer.allocate(row.getRowSize());
                    row.write(buffer);
                    if (write(row.getRowPointer(), buffer) != -1) {
                        System.out.println(" single writng list writed on disk -> " + row.getClass().getSimpleName());
                    }
                }
                singleWritingList.clear();
            }
        } catch (DAOFileWriterException | DAOFileException e) {
            e.printStackTrace(System.out);
        }
    };

    /**
     * Ajoute un tuple <code>row</code> à la liste de tuples dont le contenu est
     * écrit d'un seul coup dans un fichier <code>multipleWritingList</code>.
     *
     * @param row Tuple à ajouter. NE DOIT PAS ETRE NULL.
     * @return {@code true} si le tuple a été ajouté sinon {@code false}
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     * s'il y'a une erreur lors d el'ajout du tuple.
     */
    synchronized public boolean addRowToMultipleWritingList(AbstractRow row) throws DAOFileException {
        return add(multipleWritingList, row);
    }

    /**
     * Ajoute un tuple <code>row</code> à la liste de tuples dont le contenu est
     * écrit un à un dans un fichier <code>singleWritingList</code>.
     *
     * @param row Tuple à ajouter. NE DOIT PAS ETRE NULL.
     * @return {@code true} si le tuple a été ajouté sinon {@code false}
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileException
     * s'il y'a une erreur lors de l'ajout du tuple.
     */
    synchronized public boolean addRowToSingleWritingList(AbstractRow row) throws DAOFileException {
        return add(singleWritingList, row);
    }

    /**
     * Ajoute un tuple <code>row</code> à une liste de tuples triée suivant le
     * pointeur de tuple <code>rowPointer</code>.
     *
     * @param sortedListByPointer Liste trié à laquelle le tuple est ajouté.
     * @param row Tuple à ajouter. NE DOIT PAS ETRE NULL.
     */
    private boolean add(List<AbstractRow> sortedListByPointer, AbstractRow row) throws DAOFileException {
//        checkNotNull("sortedListByPointer", sortedListByPointer);
//        checkNotNull("row", row);
        if (!sortedListByPointer.contains(row)) {
            RowUtilities.addRowToSortedListByPointer(sortedListByPointer, row);
            if (future != null) {
                future.cancel(true);
            }
            future = ses.schedule(writeToDisk, WRITING_TIMER, TimeUnit.SECONDS);
            return true;
        }
        return false;
    }

    /**
     *
     * @param evt Evenement
     */
    @Override
    synchronized public void propertyChange(PropertyChangeEvent evt) {
        AbstractRow row = (AbstractRow) evt.getSource();
        if (row.hasChanged()) {
            try {
                addRowToSingleWritingList(row);
            } catch (DAOFileException ex) {
                LOGGER.log(Level.SEVERE, ex.getMessage());
            }
        }
    }

    /**
     * Supprime d'un fichier <code>offset</code> octets à partir de la position
     * <code>position</code>.
     * <br>
     * exemple : Supposons qu'il y'a "This is a file" dans un fichier, en
     * appelant cette méthode avec position=5 et size=5, le fichier contiendra
     * après l'execution "This file". La partie "is a " est supprimée.
     *
     * @param position Position à partir de laquelle la suppression commence.
     * DOIT ETRE SUPERIEUR A 0.
     * @param offset Nombre d'octets à supprimer. DOIT ETRE SUPERIEUR A 0.
     * @return <code>true</code> si la suppression a été éffectuée avec succès
     * sinon <code>false</code>.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileWriterException
     * s'il y'a une erreur lors de la suppression.
     */
    synchronized public boolean deleteFromFile(int position, int offset) throws DAOFileWriterException {
        if (position < 0) {
            throw new IllegalArgumentException(" position '"+position+"' must be greater than 0.");
        }
        if (offset < 0) {
            throw new IllegalArgumentException(" offset '"+offset+"' must be greater than 0.");
        }
        LOGGER.log(Level.FINE, "position={0}, size={1}", new Object[]{position, offset});
        boolean result = false;
        try {
//            if(!this.fileChannel.isOpen()) {
//                open();
//            }
            FileChannel fileChannel = file.getChannel();
            long fileSize = fileChannel.size();
            if (fileChannel.size() > position) {
                int nextPosition = position + offset;
                int nbRemaingBytes = (int) (fileChannel.size() - nextPosition);
                nbRemaingBytes = nbRemaingBytes < 0 ? 0 : nbRemaingBytes;
                LOGGER.log(Level.FINE, "nextPosition={0}, nbRemaingBytes={1}", new Object[]{nextPosition, nbRemaingBytes});
                ByteBuffer remainingBytes = read(nextPosition, nbRemaingBytes);
                if (remainingBytes != null) {
//                write(position, remainingBytes);
                    fileChannel.position(position);
                    while (remainingBytes.hasRemaining()) {
                        fileChannel.write(remainingBytes);
                    }
                }
                fileChannel.truncate(position + nbRemaingBytes);
                result = true;
//            }
            }
            LOGGER.log(Level.FINE, "fileSize : {0} -> {1}", new Object[]{fileSize, fileChannel.size()});
        } catch (IOException ex) {
            throw new DAOFileWriterException(ex.getMessage(), ex);
        }
        return result;
    }

    /**
     * Charge et rétourne, depuis un fichier, un tuple d'en-tete de fichier
     * <code>FileHeaderRow</code> si le fichier n'est pas vide et contient bien
     * un tuple d'en-tete {@link FileHeaderRow} sinon rétourne un nouveaux tuple
     * d'en-tete de fichier <code>FileHeaderRow</code>.
     *
     * @return Tuple d'en-tete de fichier <code>FileHeader</code>.
     */
    public FileHeaderRow loadFileHeader() {
        FileHeaderRow fhr;
        try {
            fhr = new FileHeaderRow(this);
        } catch (DAOFileException de) {
            fhr = new FileHeaderRow(new FileHeader(0, 0));
            LOGGER.log(Level.INFO, "New file header has been created."
                    + "\n\t cause -> File header could not be load from the file -> {0}"
                    + "\n\t\t cause -> {1}"
                    , new Object[]{this.filePath, de.getMessage()});
        }
        fhr.addPropertyChangeListener(this);
        return fhr;
    }

    /**
     * Ecrit un identifiant <code>uuidToWrite</code> à la position courante d'un
     * tampon d'octets <code>buffer</code>.
     *
     * @param buffer Tampon d'octets dans lequel les données sont écrites. NE
     * DOIT PAS ETRE NULL.
     * @param uuidToWrite Identifiant à écrire dans le tampon d'octets. NE DOIT
     * PAS ETRE NULL.
     * @return Le nombre d'octets écrits dans le tampon d'octets
     * <code>buffer</code>.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileWriterException
     * si l'identifiant <code>uuidToWrite</code> ne peut pas etre rajouté au tampon d'octets
     * <code>buffer</code>.
     */
    public static int writeUUID(ByteBuffer buffer, UUID uuidToWrite) throws DAOFileWriterException {
        checkNotNull("buffer", buffer);
        checkNotNull("uuidToWrite", uuidToWrite);
        int lenght = Long.BYTES * 2;
        if ((buffer.remaining()) < lenght) {
            String message = "\nUUID '" + uuidToWrite + "' can not be added to the buffer '" + buffer + "'"
                    + " \t\n cause -> remaining bytes in the buffer is less than UUID size '" +lenght +"'";
            LOGGER.log(Level.WARNING, message);
            throw new DAOFileWriterException(message);
        }
        buffer.putLong(uuidToWrite.getMostSignificantBits());
        buffer.putLong(uuidToWrite.getLeastSignificantBits());
        return lenght;
    }

    /**
     * Ecrit la taille et la chaine de caractèeres <code>stringToWrite</code> à
     * la position courante d'un tampon d'octets <code>buffer</code>.
     *
     * @param buffer Tampon d'octets dans lequel les données sont écrites. NE
     * DOIT PAS ETRE NULL.
     * @param stringToWrite Chaine de caractères à écrire dans le tampon. NE
     * DOIT PAS ETRE NULLE.
     * @return Le nombre d'octets écrits dans le tampon d'octets
     * <code>buffer</code>.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileWriterException 
     * si la chaine de caractère <code>stringToWrite</code> ne peut pas etre rajoutée au tampon d'octets
     * <code>buffer</code>..
     */
    public static int writeString(ByteBuffer buffer, String stringToWrite) throws DAOFileWriterException {
        checkNotNull("buffer", buffer);
        checkNotNull("stringToWrite", stringToWrite);
        int lenght = Integer.BYTES + stringToWrite.length();
        if ((buffer.remaining()) < lenght) {
            String message = "\nString '" + stringToWrite + "' can not be added to the buffer '" + buffer + "'"
                    + " \t\n cause -> remaining bytes in the buffer is less than string lenght '" +lenght +"'";
            LOGGER.log(Level.WARNING, message);
            throw new DAOFileWriterException(message);
        }
        buffer.putInt(stringToWrite.length());
        buffer.put(stringToWrite.getBytes());
        return lenght;
    }

    /**
     * Renvoie une chaine de caractères lue depuis un tampon d'octets
     * <code>buffer</code> à partir de la position courante du tampon d'octets +
     * la taille d'un entier. La taille de la chaine correspond à l'entier lu au
     * au début du tampon d'octets <code>buffer</code>,
     *
     * @param buffer Tampon d'octets à partir duquel les données sont lues. NE
     * DOIT PAS ETRE NULL.
     * @return La chaine de caratères lue si le nombre d'octes restant dans le
     * tampon d'octets <code>buffer</code> est supérieur ou égal à la taille
     * d'un entier + l'entier lu au début du tampon d'octets <code>buffer</code>
     * sinon <code>null</code>.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileWriterException 
     * s'il y'a une erreur lors de la lecture.
     */
    public static String readString(ByteBuffer buffer) throws DAOFileWriterException {
        checkNotNull("buffer", buffer);
        if (buffer.remaining() < 4) {
            String message = "\ncouldn't read string form buffer '" + buffer + "'"
                    + " \n\t cause -> remaining bytes '" + buffer.remaining() + "' is less than 4";
            LOGGER.log(Level.WARNING, message);
            throw new DAOFileWriterException(message);
        }
        int lenght = buffer.getInt();
        return readString(buffer, lenght);
    }

    /**
     * Renvoie une chaine de caractères de taille <code>lenght</code> lue depuis
     * un tampon d'octets <code>buffer</code> à partir de la position courante
     * du tampon d'octets <code>buffer</code>.
     *
     * @param buffer Tampon d'octets à partir duquel les données sont lues. NE
     * DOIT PAS ETRE NULL.
     * @param lenght Taille de la chaine de caractères à lire.
     * @return La chaine de caratères lue si le nombre d'octes restant dans le
     * tampon d'octets <code>buffer</code> est supérieur ou égal à la taille
     * <code>lenght</code> sinon <code>null</code>.
     * @throws fr.univubs.inf1603.mahjong.daofile.exception.DAOFileWriterException 
     * s'il y'a une erreur lors de la lecture.
     */
    public static String readString(ByteBuffer buffer, int lenght) throws DAOFileWriterException {
        checkNotNull("buffer", buffer);
        if (buffer.remaining() < lenght) {
            String message = "\ncouldn't read string form buffer '"+buffer+"'"
                    + " \n\t cause -> lenght '"+lenght+"' is greater than remaining bytes '"+buffer.remaining()+ "'";
            LOGGER.log(Level.WARNING, message);
            throw new DAOFileWriterException(message);
        }
        byte buf[] = new byte[lenght];
        buffer.get(buf);
        return new String(buf);
    }
}
