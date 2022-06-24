package it.polimi.ingsw.server.controller.persistence;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test stub of a {@link Writer} that takes a files and writes to it. It wraps a {@link StringWriter}.
 */
public class MockWriter extends Writer {
    private final StringWriter writer;
    private final Predicate<String> onClose;

    /**
     * Creates a new instance that will simulate writing to the given file. The passed file can be tested with the given
     * predicate (useful for injecting checks into classes in testing).
     *
     * @param file    the file to write to
     * @param check   the check to do on the given file
     * @param onClose a check that will be done on the effectively written string on writer close (see {@link #onClose})
     */
    public MockWriter(File file, Predicate<File> check, Predicate<String> onClose) {
        super();
        if (!check.test(file)) fail();
        this.onClose = onClose;
        writer = new StringWriter();
    }

    /**
     * @see StringWriter#write(char[], int, int)
     */
    @Override
    public void write(char[] cbuf, int off, int len) {
        writer.write(cbuf, off, len);
    }

    /**
     * First checks the written contents with the predicate given during construction, then closes the wrapped stream.
     *
     * @throws IOException if IO errors happen
     * @see StringWriter#close()
     */
    @Override
    public void close() throws IOException {
        if (!onClose.test(writer.toString())) fail();
        writer.close();
    }

    /**
     * @see StringWriter#flush()
     */
    @Override
    public void flush() {
        writer.flush();
    }
}
