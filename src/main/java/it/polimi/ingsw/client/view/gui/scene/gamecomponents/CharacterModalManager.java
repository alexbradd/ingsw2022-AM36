package it.polimi.ingsw.client.view.gui.scene.gamecomponents;

import it.polimi.ingsw.client.control.state.Character;
import it.polimi.ingsw.enums.CharacterType;
import it.polimi.ingsw.enums.PieceColor;
import it.polimi.ingsw.functional.Tuple;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Manager for the character invocation modal
 */
public class CharacterModalManager {
    /**
     * The modal header. It contains a title and che close button
     */
    private final HBox top;
    /**
     * The main content of the modal
     */
    private final Pane center;
    /**
     * Callback called when the "submit" button is clicked
     */
    private final BiConsumer<CharacterModalManager, Tuple<CharacterType, List<Map<String, String>>>> onSubmit;
    /**
     * Runnable called when the modal is closed either via the "close" button or the {@link #close()}
     */
    private final Runnable onClose;

    /**
     * An observable list containing String to Observable string maps. Each will be translated into a String-String map
     * and then into steps by the modal user.
     */
    private final ObservableList<Map<String, ObservableObjectValue<String>>> steps;
    /**
     * The name of the character that the modal is referring to. Used ad the title of the modal.
     */
    private final StringProperty characterName;
    /**
     * The pane where each element of the modal is actually injected. It is set as the content node of a ScrollView that
     * will be the actual child of {@link #center}
     */
    private VBox viewport;

    /**
     * Creates a new instance with the given parameters.
     * <p>
     * Before using, a call to {@link #initialize()} is necessary to set up the environment the modal will be shown
     * into.
     *
     * @param top      the modal header
     * @param center   the main content of the modal
     * @param onSubmit callback called when the "submit" button is clicked
     * @param onClose  Runnable called when the modal is closed either via the "close" button or the {@link #close()}
     * @throws IllegalArgumentException if any parameter is null
     */
    public CharacterModalManager(HBox top,
                                 Pane center,
                                 BiConsumer<CharacterModalManager, Tuple<CharacterType, List<Map<String, String>>>> onSubmit,
                                 Runnable onClose) {
        if (top == null) throw new IllegalArgumentException("top shouldn't be null");
        if (center == null) throw new IllegalArgumentException("center shouldn't be null");
        if (onSubmit == null) throw new IllegalArgumentException("onSubmit shouldn't be null");
        if (onClose == null) throw new IllegalArgumentException("onClose shouldn't be null");

        this.characterName = new SimpleStringProperty("Character");
        this.steps = FXCollections.observableArrayList();
        this.top = top;
        this.center = center;
        this.onSubmit = onSubmit;
        this.onClose = onClose;
    }

    /**
     * Sets up the environment and makes this manager ready for action
     */
    public void initialize() {
        initTop();
        viewport = initViewport();
    }

    /**
     * Sets up the modal header
     */
    private void initTop() {
        top.getChildren().clear();

        Button close = new Button("Close");
        close.setOnMouseClicked(__ -> close());
        Label characterNameLabel = new Label();
        characterNameLabel.textProperty().bind(characterName);

        top.getChildren().addAll(List.of(characterNameLabel, close));
    }

    /**
     * Sets up the modal viewport
     *
     * @return a VBox
     */
    private VBox initViewport() {
        viewport = new VBox();
        viewport.setId("characterModalViewport");
        ScrollPane pane = new ScrollPane(viewport);
        pane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        pane.setFitToWidth(true);
        center.getChildren().add(pane);
        return viewport;
    }

    /**
     * Displays the invocation modal for the given character
     *
     * @param character the character to invoke
     * @throws IllegalArgumentException if {@code character} is null
     */
    public void showFor(Character character) {
        if (character == null) throw new IllegalArgumentException("character shouldn't be null");
        viewport.getChildren().clear();

        CharacterType type = character.getType();
        characterName.set(lowerAndCapitalize(type.toString()));

        for (int i = 0; i < type.getMinSteps(); i++)
            viewport.getChildren().add(makeStep(false, type));
        if (type.getMaxSteps() > type.getMinSteps())
            viewport.getChildren().add(makeAddButton(type));
        viewport.getChildren().add(makeSubmitButton(type));
    }

    /**
     * Creates a step Node for the given character
     *
     * @param removable true if the step should be removable
     * @param type      the type of Character
     * @return a Node
     */
    private Node makeStep(boolean removable, CharacterType type) {
        HashMap<String, ObservableObjectValue<String>> step = new HashMap<>();
        steps.add(step);
        return makeStepBlueprint(removable, type, step);
    }

    /**
     * Creates the UI for a step of the given character type. The interface will store its data in the given map.
     *
     * @param removable true if the step should be removable
     * @param type      the type of Character
     * @param step      the Map each property will be stored keyed by its name (as reported by {@link CharacterType})
     * @return a new Node
     */
    private Node makeStepBlueprint(boolean removable, CharacterType type, Map<String, ObservableObjectValue<String>> step) {
        VBox box = new VBox();
        box.getStyleClass().add("characterStep");

        if (removable) box.getChildren().add(makeRemoveButton(step));

        for (Tuple<String, CharacterType.ParameterType> param : type.getStepParameters()) {
            Label label = makeStepLabel(param.getFirst());
            Tuple<Node, ObservableObjectValue<String>> input = makeStepInput(param.getSecond());
            step.put(param.getFirst(), input.getSecond());
            HBox entry = new HBox(label, input.getFirst());
            entry.setAlignment(Pos.CENTER_LEFT);
            box.getChildren().add(entry);
        }

        return box;
    }

    /**
     * Creates a remove button for the given step
     *
     * @param step the step to remove
     * @return a new Node
     */
    private Node makeRemoveButton(Map<String, ObservableObjectValue<String>> step) {
        Button remove = new Button("Remove");
        HBox wrapper = new HBox(remove);
        wrapper.setAlignment(Pos.CENTER_RIGHT);
        remove.setOnMouseClicked(__ -> {
            steps.remove(step);
            viewport.getChildren().remove(wrapper.getParent());
        });
        return wrapper;
    }

    /**
     * Creates the label for a step property
     *
     * @param paramName the name of the property
     * @return a new Label
     */
    private Label makeStepLabel(String paramName) {
        Label label = new Label(lowerAndCapitalize(paramName));
        label.setPadding(new Insets(0, 5, 0, 0));
        return label;
    }

    /**
     * Creates a step input Node of the given type
     *
     * @param type the type of input to make
     * @return a Tuple containing the Node created an ObservableObjectValue pointing to the user-selection
     */
    private Tuple<Node, ObservableObjectValue<String>> makeStepInput(CharacterType.ParameterType type) {
        return switch (type) {
            case PIECE_COLOR -> makePieceColorInput();
            case ISLAND_INDEX -> makeIslandIndexSpinner();
        };
    }

    /**
     * Creates a ChoiceBox with the various possible colors as items.
     *
     * @return a Tuple containing the Node created an ObservableObjectValue pointing to the user-selection
     */
    private Tuple<Node, ObservableObjectValue<String>> makePieceColorInput() {
        ChoiceBox<String> colorChoice = new ChoiceBox<>();
        ObservableList<String> values = FXCollections.observableArrayList();
        for (PieceColor value : PieceColor.values())
            values.add(lowerAndCapitalize(value.toString()));
        colorChoice.setItems(values);
        colorChoice.getSelectionModel().selectFirst();
        StringBinding choice = new StringBinding() {
            {
                super.bind(colorChoice.valueProperty());
            }

            @Override
            protected String computeValue() {
                return colorChoice.getValue().toUpperCase();
            }
        };
        return new Tuple<>(colorChoice, choice);
    }

    /**
     * Creates a Spinner with the various possible island indexes
     *
     * @return a Tuple containing the Node created an ObservableObjectValue pointing to the user-selection
     */
    private Tuple<Node, ObservableObjectValue<String>> makeIslandIndexSpinner() {
        Spinner<Integer> islandIndexSpinner = new Spinner<>(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 11));
        return new Tuple<>(islandIndexSpinner, islandIndexSpinner.valueProperty().asString());
    }

    /**
     * Creates an "Add" button that adds a step for the given character type
     *
     * @param type the type of Character
     * @return a new Node
     */
    private Node makeAddButton(CharacterType type) {
        Button add = new Button("Add");
        BooleanExpression disable = new SimpleListProperty<>(steps)
                .sizeProperty()
                .isEqualTo(type.getMaxSteps());
        add.disableProperty().bind(disable);
        add.setOnMouseClicked(__ -> {
            Node step = makeStep(true, type);
            int pos = viewport.getChildren().lastIndexOf(add);
            if (pos >= 0)
                viewport.getChildren().add(pos, step);
            else
                viewport.getChildren().add(step);
        });
        return add;
    }

    /**
     * Creates the submit button for the modal
     *
     * @param type the type of Character
     * @return a new Node
     */
    private Node makeSubmitButton(CharacterType type) {
        Button submit = new Button("Invoke");
        submit.setId("characterModalSubmit");
        submit.setOnMouseClicked(__ -> {
            List<Map<String, String>> list = new ArrayList<>();
            steps.forEach(step -> {
                HashMap<String, String> map = new HashMap<>();
                step.forEach((k, v) -> map.put(k, v.get()));
                list.add(map);
            });
            onSubmit.accept(this, new Tuple<>(type, list));
        });
        VBox.setMargin(submit, new Insets(10, 0, 0, 0));
        return submit;
    }

    /**
     * Closes the modal, cleaning up internal state. {@link #onClose} is then called.
     */
    public void close() {
        cleanup();
        onClose.run();
    }

    /**
     * Cleans up resources used in the modal presentation and behaviour
     */
    private void cleanup() {
        viewport.getChildren().clear();
        steps.clear();
    }

    /**
     * Static helper that makes only the first letter capitalize.
     *
     * @param str a string
     * @return a new string
     */
    private static String lowerAndCapitalize(String str) {
        String lowered = str.toLowerCase();
        return lowered.substring(0, 1).toUpperCase() + lowered.substring(1);
    }
}
