package it.polimi.ingsw.server.controller.persistence;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test stub of a {@link Reader} that takes a files and reads from it. It wraps a {@link StringReader}.
 */
public class MockReader extends Reader {
    private final StringReader reader;

    /**
     * Creates a new instance that will simulate reading from the given file the given string. The passed file can be
     * tested with the given predicate (useful for injecting checks into classes in testing).
     *
     * @param file     the file to read from
     * @param check    the check to do on the given file
     * @param contents the contents that the file should have
     */
    public MockReader(File file, Predicate<File> check, String contents) {
        if (!check.test(file)) fail();
        reader = new StringReader(contents);
    }

    /**
     * @see StringReader#read(char[], int, int)
     */
    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        return reader.read(cbuf, off, len);
    }

    /**
     * @see StringReader#close()
     */
    @Override
    public void close() {
        reader.close();
    }
}
