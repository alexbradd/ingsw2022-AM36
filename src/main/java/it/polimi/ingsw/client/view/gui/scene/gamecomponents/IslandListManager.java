package it.polimi.ingsw.client.view.gui.scene.gamecomponents;

import it.polimi.ingsw.client.control.state.IslandGroup;
import it.polimi.ingsw.functional.Tuple;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Translate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

import static it.polimi.ingsw.client.view.gui.GUIUtils.runLaterIfNotOnFxThread;

/**
 * Layout manager for the island list
 */
public class IslandListManager {
    /**
     * The horizontal radius of the ellipse
     */
    private final static double ELIPSE_RADIUS_X = 350;
    /**
     * The vertical radius of the ellipse
     */
    private final static double ELIPSE_RADIUS_Y = 250;

    /**
     * The currently displayed list of island group managers
     */
    private final ArrayList<IslandGroupManager> islands;
    /**
     * The group in which the content will be inserted
     */
    private final Group islandPane;
    /**
     * Boolean binding indicating whether the layout should be interactive
     */
    private final BooleanBinding anIslandIsClickable;
    /**
     * The islands property
     */
    private final ListProperty<IslandGroup> islandsProperty;
    /**
     * The mother nature property
     */
    private final IntegerProperty motherNature;
    /**
     * Handler a click on mother nature (called only if the layout is interactive)
     */
    private final Consumer<MouseEvent> onMnClick;

    /**
     * Creates a new instance with the given parameters
     *
     * @param islandGroup         The group in which the content will be inserted
     * @param anIslandIsClickable Boolean binding indicating whether the layout should be interactive
     * @param islands             The islands property
     * @param motherNature        The mother nature property
     * @param onMnClick           Handler a click on mother nature
     * @throws IllegalArgumentException if any parameter is null
     */
    public IslandListManager(Group islandGroup,
                             BooleanBinding anIslandIsClickable,
                             ListProperty<IslandGroup> islands,
                             IntegerProperty motherNature,
                             Consumer<MouseEvent> onMnClick) {
        if (islandGroup == null) throw new IllegalArgumentException("islandGroup shouldn't be null");
        if (anIslandIsClickable == null) throw new IllegalArgumentException("canBeClickable shouldn't be null");
        if (islands == null) throw new IllegalArgumentException("islands shouldn't be null");
        if (motherNature == null) throw new IllegalArgumentException("motherNature shouldn't be null");
        if (onMnClick == null) throw new IllegalArgumentException("onMnClick shouldn't be null");

        this.islands = new ArrayList<>();
        this.islandPane = islandGroup;
        this.onMnClick = onMnClick;
        this.islandsProperty = islands;
        this.anIslandIsClickable = anIslandIsClickable;
        this.motherNature = motherNature;
    }

    /**
     * Start reactively managing the layout.
     * <p>
     * Note: the absence of a stopManaging() here is intentional. The lifecycle of each GameState object is as long as
     * that of its view. Therefore, we have to worry only about internal cleanup between relayouts.
     */
    public void startManaging() {
        islandsProperty.addListener((ListChangeListener<IslandGroup>) change ->
                runLaterIfNotOnFxThread(() -> layout(change.getList())));
        layout(islandsProperty.get());
    }

    /**
     * Cleans the slate and injects up-to-date content into the root group.
     *
     * @param list the islands property
     */
    private void layout(ObservableList<? extends IslandGroup> list) {
        int n = list.size();
        islands.forEach(IslandGroupManager::disconnectListeners);
        islands.clear();
        islandPane.getChildren().clear();
        for (int i = 0; i < n; i++) {
            double angle = i * (360.0 / n);
            IslandGroup data = list.get(i);
            BooleanBinding isMnHere = new BooleanBinding() {
                {
                    super.bind(motherNature);
                }

                @Override
                protected boolean computeValue() {
                    return Arrays.stream(data.getIds()).anyMatch(i -> i == motherNature.get());
                }
            };
            IslandGroupManager builder = new IslandGroupManager(data, isMnHere, anIslandIsClickable.and(isMnHere), onMnClick);
            islands.add(builder);
            Node island = builder.build(angle);
            Tuple<Double, Double> radius = getEllipseRadius(angle);
            island.getTransforms().add(new Translate(radius.getFirst(), radius.getSecond()));
            islandPane.getChildren().add(island);
        }
    }

    /**
     * Calculate the position for a point at the given angle (in degrees) on the ellipse,
     *
     * @param deg the angle (in degrees)
     * @return a Tuple containing the position of the point (x-y)
     */
    private Tuple<Double, Double> getEllipseRadius(double deg) {
        double rad = Math.toRadians(deg);
        return new Tuple<>(ELIPSE_RADIUS_X * Math.cos(rad), ELIPSE_RADIUS_Y * Math.sin(rad));
    }
}
