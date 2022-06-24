package it.polimi.ingsw.server.controller.persistence;

import java.io.File;
import java.util.HashMap;
import java.util.function.Function;

/**
 * Stubbable mock that simulates a {@link File}. To be used in conjunction with {@link PersistenceManagerTest}.
 */
public class MockFile extends File {
    private HashMap<String, Function<Object[], Object>> handlers;

    private static File getTmpDir() {
        return new File(System.getProperty("java.io.tmpdir"), "eriantys");
    }

    public MockFile(HashMap<String, Function<Object[], Object>> handlers) {
        super(getTmpDir().getAbsolutePath());
        this.handlers = handlers;
    }

    public MockFile(File parent, String child, HashMap<String, Function<Object[], Object>> handlers) {
        super(getTmpDir(), child);
        this.handlers = handlers;
    }

    public HashMap<String, Function<Object[], Object>> getHandlers() {
        return handlers;
    }

    public void setHandlers(HashMap<String, Function<Object[], Object>> handlers) {
        this.handlers = handlers;
    }

    @Override
    public boolean isFile() {
        Function<Object[], Object> h = handlers.get("isFile");
        if (h == null) throw new IllegalStateException("not mocked");
        return (boolean) h.apply(new Object[]{});
    }

    @Override
    public boolean isDirectory() {
        Function<Object[], Object> h = handlers.get("isDirectory");
        if (h == null) throw new IllegalStateException("not mocked");
        return (boolean) h.apply(new Object[]{});
    }

    @Override
    public boolean canRead() {
        Function<Object[], Object> h = handlers.get("canRead");
        if (h == null) throw new IllegalStateException("not mocked");
        return (boolean) h.apply(new Object[]{});
    }

    @Override
    public boolean canWrite() {
        Function<Object[], Object> h = handlers.get("canWrite");
        if (h == null) throw new IllegalStateException("not mocked");
        return (boolean) h.apply(new Object[]{});
    }

    @Override
    public String[] list() {
        Function<Object[], Object> h = handlers.get("list");
        if (h == null) throw new IllegalStateException("not mocked");
        return (String[]) h.apply(new Object[]{});
    }

    @Override
    public File[] listFiles() {
        Function<Object[], Object> h = handlers.get("listFiles");
        if (h == null) throw new IllegalStateException("not mocked");
        return (File[]) h.apply(new Object[]{});
    }

    @Override
    public boolean delete() {
        Function<Object[], Object> h = handlers.get("delete");
        if (h == null) throw new IllegalStateException("not mocked");
        return (boolean) h.apply(new Object[]{});
    }
}
