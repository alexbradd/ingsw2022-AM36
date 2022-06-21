package it.polimi.ingsw.client.view.gui;

import javafx.application.Platform;
import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.function.BiConsumer;

/**
 * Static utilities used by the GUI application.
 */
public class GUIUtils {
    /**
     * Runs the given {@link Runnable} if the thread the method is invoked on is the JavaFX application thread.
     * If that is not the case, it executes it with {@link Platform#runLater(Runnable)}.
     *
     * @param runnable the {@link Runnable}
     * @throws IllegalArgumentException if {@code runnable} is null
     */
    public static void runLaterIfNotOnFxThread(Runnable runnable) {
        if (runnable == null) throw new IllegalArgumentException("runnable shouldn't be null");
        if (Platform.isFxApplicationThread())
            runnable.run();
        else
            Platform.runLater(runnable);
    }

    /**
     * For each value of the given enum array, it loads the corresponding image and feeds it into a {@link BiConsumer}.
     * The filepath loaded is {@code [pathPrefix][value.toString().toLowerCase()].png}.
     *
     * @param enumValues The enum values to loop on
     * @param pathPrefix The path prefix to apply to the constructed file path
     * @param consumer   the consumer accepting the enum value and its corresponding image
     * @param <T>        enum type
     * @throws IllegalArgumentException if any argument is null
     */
    public static <T extends Enum<T>> void forEachEnumValueLoadImage(T[] enumValues, String pathPrefix, BiConsumer<T, Image> consumer) {
        if (enumValues == null) throw new IllegalArgumentException("enumValues shouldn't be null");
        if (pathPrefix == null) throw new IllegalArgumentException("pathPrefix shouldn't be null");
        if (consumer == null) throw new IllegalArgumentException("consumer shouldn't be null");
        for (T val : enumValues)
            consumer.accept(val, loadImageFromDisk(pathPrefix + val.toString().toLowerCase() + ".png"));
    }

    /**
     * Loads an image resource from the given path.
     *
     * @param path the resource's path
     * @return a new {@link Image}
     * @throws IllegalArgumentException if {@code path} is null
     */
    public static Image loadImageFromDisk(String path) {
        if (path == null) throw new IllegalArgumentException();
        InputStream in = GUIUtils.class.getResourceAsStream(path);
        assert in != null;
        return new Image(in);
    }
}
