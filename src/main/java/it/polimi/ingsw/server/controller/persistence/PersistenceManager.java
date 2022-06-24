package it.polimi.ingsw.server.controller.persistence;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import it.polimi.ingsw.functional.ThrowingFunction;
import it.polimi.ingsw.server.controller.Messages;
import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.model.Phase;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * Handles the persistence of {@link Phase} objects. A simple filesystem-based database is used where each object will
 * be stored as UTF-8 encoded JSON and addressable by its id (which most likely will be its game-id, see {@link Game}).
 * <p>
 * The class retrieves all the necessary information from disk, so it can be safely recreated using the same
 * {@link File} as the root folder.
 * <p>
 * A {@link Phase} can be committed (saved to disk), pulled (retrieved and reconstructed from disk) or dropped (removed
 * from the database).
 */
public class PersistenceManager {
    /**
     * Default factory for obtaining new {@link FileReader} objects relative to a given {@link File}.
     */
    public final static ThrowingFunction<File, Reader, IOException> DEFAULT_READER_SUPPLIER = (f) -> new FileReader(f, StandardCharsets.UTF_8);
    /**
     * Default factory for obtaining new {@link FileWriter} objects relative to a given {@link File}.
     */
    public final static ThrowingFunction<File, Writer, IOException> DEFAULT_WRITER_SUPPLIER = (f) -> new FileWriter(f, StandardCharsets.UTF_8);
    /**
     * Default factory for obtaining new {@link File} objects representing a file with the given name in a directory
     * represented by the specified {@link File}.
     */
    public final static BiFunction<File, String, File> DEFAULT_CHILD_FILE_SUPPLIER = File::new;
    /**
     * Name of the JSON key used for reconstructing the runtime type of the various {@link Phase} objects
     */
    public static final String CLASS_DISCRIMINATOR_PROP_NAME = "_classFullName";

    /**
     * The base directory the class will use.
     */
    private final File dir;
    /**
     * The factory for obtaining new {@link Reader} objects relative to a given {@link File}.
     *
     * @see PersistenceManager#DEFAULT_READER_SUPPLIER
     */
    private ThrowingFunction<File, Reader, IOException> readerSupplier = DEFAULT_READER_SUPPLIER;
    /**
     * The factory for obtaining new {@link FileWriter} objects relative to a given {@link File}.
     *
     * @see PersistenceManager#DEFAULT_WRITER_SUPPLIER
     */
    private ThrowingFunction<File, Writer, IOException> writerSupplier = DEFAULT_WRITER_SUPPLIER;
    /**
     * The factory for obtaining new {@link File} objects representing a file with the given name in a directory
     * represented by the specified {@link File}.
     *
     * @see PersistenceManager#DEFAULT_CHILD_FILE_SUPPLIER
     */
    private BiFunction<File, String, File> childFileSupplier = DEFAULT_CHILD_FILE_SUPPLIER;

    /**
     * Creates a new instance using the specified {@link File} as the root directory.
     *
     * @param dir the root directory
     * @throws IllegalArgumentException if {@code dir} is null, if is not a directory or if this process has not got
     *                                  enough permissions to read and write to said directory
     */
    public PersistenceManager(File dir) {
        if (dir == null) throw new IllegalArgumentException("dir shouldn't be null");
        if (!dir.isDirectory()) throw new IllegalArgumentException("dir should be a directory");
        if (!dir.canRead() || !dir.canWrite())
            throw new IllegalArgumentException("not enough permission to do IO to dir");
        this.dir = dir;
    }

    /**
     * Sets a new factory of {@link FileReader}
     *
     * @param readerSupplier the new {@link FileReader} factory
     * @throws IllegalArgumentException if {@code readerSupplier} is null
     * @see PersistenceManager#readerSupplier
     */
    public void setReaderSupplier(ThrowingFunction<File, Reader, IOException> readerSupplier) {
        if (readerSupplier == null) throw new IllegalArgumentException("readerSupplier shouldn't be null");
        this.readerSupplier = readerSupplier;
    }

    /**
     * Sets a new factory of {@link Writer}
     *
     * @param writerSupplier the new {@link Writer} factory
     * @throws IllegalArgumentException if {@code writerSupplier} is null
     * @see PersistenceManager#writerSupplier
     */
    public void setWriterSupplier(ThrowingFunction<File, Writer, IOException> writerSupplier) {
        if (writerSupplier == null) throw new IllegalArgumentException("writerSupplier shouldn't be null");
        this.writerSupplier = writerSupplier;
    }

    /**
     * Sets a new factory of {@link File}
     *
     * @param childFileSupplier the new {@link File} factory
     * @throws IllegalArgumentException if {@code childFileSupplier} is null
     * @see PersistenceManager#childFileSupplier
     */
    public void setChildFileSupplier(BiFunction<File, String, File> childFileSupplier) {
        if (childFileSupplier == null) throw new IllegalArgumentException("childFileSupplier shouldn't be null");
        this.childFileSupplier = childFileSupplier;
    }

    /**
     * Return true if there are entries in the database that have not yet been dropped.
     *
     * @return true if there are entries in the database that have not yet been dropped.
     */
    public boolean hasPending() {
        String[] p = dir.list();
        assert p != null;
        return p.length != 0;
    }

    /**
     * Pull each record from disk and feed them to a consuming function.
     *
     * @param consumer a {@link BiConsumer} taking the id of the record and the pulled {@link Phase}
     * @throws IllegalArgumentException if {@code consumer} is null
     * @see #pull(long)
     */
    public void forEach(BiConsumer<Long, Phase> consumer) {
        if (consumer == null) throw new IllegalArgumentException("consumer shouldn't be null");
        File[] fs = dir.listFiles();
        assert fs != null;
        for (File f : fs)
            consumer.accept(getIdFromFilename(f), pull(f));
    }

    /**
     * Utility that converts the given file's name to its id
     *
     * @param f the file to use
     * @return the id of the record associated with the given file
     */
    private long getIdFromFilename(File f) {
        String fName = f.getName();
        int extPos = fName.lastIndexOf('.');
        return Long.parseLong(fName.substring(0, extPos));
    }

    /**
     * Commits a new {@link Phase} to disk with the specified id.
     *
     * @param id    the id of the object
     * @param phase the object to save
     * @throws IllegalArgumentException If {@code phase} is null
     * @throws StorageException         if there was an unpreventable IO error
     */
    public void commit(long id, Phase phase) {
        if (phase == null) throw new IllegalArgumentException("phase shouldn't be null");
        File store = childFileSupplier.apply(dir, id + ".json");
        JsonObject json = new Gson().toJsonTree(phase).getAsJsonObject();
        json.addProperty(CLASS_DISCRIMINATOR_PROP_NAME, phase.getClass().getCanonicalName());
        try (Writer writer = writerSupplier.apply(store)) {
            writer.write(json.toString());
            writer.flush();
        } catch (IOException e) {
            throw new StorageException("Failed to do IO", e);
        }
    }

    /**
     * Pulls the {@link Phase} object with the given id from disk.
     *
     * @param id the id of the object to pull.
     * @return the reconstructed object
     * @throws IllegalArgumentException if there is no record with the given id
     * @throws IllegalStateException    if the object read from disk is not consistent with the program state
     * @throws JsonSyntaxException      if the object read from disk is not a valid {@link Phase} or a subclass
     * @throws StorageException         if there was an unpreventable IO error
     */
    public Phase pull(long id) {
        File toPull = childFileSupplier.apply(dir, id + ".json");
        if (!toPull.isFile() || !toPull.canRead())
            throw new IllegalArgumentException("Cannot pull Phase: unsaved id");
        return pull(toPull);
    }

    /**
     * Pulls the {@link Phase} object from the given {@link File}
     *
     * @param file the {@link File} to read
     * @return the reconstructed object
     * @throws IllegalArgumentException if there is no record with the given id
     * @throws IllegalStateException    if the database has been corrupted
     * @throws JsonSyntaxException      if the object read from disk is not a valid {@link Phase} or a subclass
     * @throws StorageException         if there was an unpreventable IO error
     */
    @SuppressWarnings("unchecked")
    private Phase pull(File file) {
        try (Reader r = readerSupplier.apply(file);
             JsonReader reader = new JsonReader(r)) {
            JsonObject read = JsonParser.parseReader(reader).getAsJsonObject();
            String className = Messages.extractString(read, CLASS_DISCRIMINATOR_PROP_NAME);
            Class<? extends Phase> clazz = (Class<? extends Phase>) Class.forName(className);
            read.remove(CLASS_DISCRIMINATOR_PROP_NAME);
            return new Gson().fromJson(read, clazz);
        } catch (IOException e) {
            throw new StorageException("Cannot do IO on file", e);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Cannot pull phase: " + CLASS_DISCRIMINATOR_PROP_NAME + " is not a string");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Cannot pull phase: cannot find phase with given name", e);
        }
    }

    /**
     * Drops all records.
     *
     * @throws StorageException if there was an unpreventable IO error
     */
    public void clear() {
        File[] fs = dir.listFiles();
        assert fs != null;
        for (File f : fs)
            drop(f);
    }

    /**
     * Drops the record with the given id.
     *
     * @param id the id of the record to drop
     * @throws IllegalArgumentException if there isn't any record with the specified id
     * @throws StorageException         if there was an unpreventable IO error
     */
    public void drop(long id) {
        File toDrop = childFileSupplier.apply(dir, id + ".json");
        if (!toDrop.isFile()) throw new IllegalArgumentException("Cannot drop file: unsaved id");
        drop(toDrop);
    }

    /**
     * Deletes the given file
     *
     * @param f the file to delete
     * @throws StorageException if there was an unpreventable IO error
     */
    private void drop(File f) {
        if (!f.delete()) throw new StorageException("Could not delete file");
    }
}
