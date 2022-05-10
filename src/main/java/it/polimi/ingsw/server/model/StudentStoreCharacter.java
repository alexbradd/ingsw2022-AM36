package it.polimi.ingsw.server.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.ingsw.functional.ThrowingBiFunction;
import it.polimi.ingsw.functional.Tuple;
import it.polimi.ingsw.server.model.enums.CharacterType;
import it.polimi.ingsw.server.model.enums.PieceColor;
import it.polimi.ingsw.server.model.exceptions.ContainerIsFullException;
import it.polimi.ingsw.server.model.exceptions.EmptyContainerException;
import it.polimi.ingsw.server.model.exceptions.InvalidPhaseUpdateException;

import java.util.EmptyStackException;
import java.util.Objects;
import java.util.Set;

/**
 * Represents and abstract character card that has the ability to store students a maximum amount of students.
 *
 * @author Alexandru Gabriel Bradatan
 */
abstract class StudentStoreCharacter extends Character implements StudentContainerInterface {
    /**
     * Container that will store students
     */
    private BoundedStudentContainer container;

    /**
     * Base constructor. Sets up the card's initial cost, character and maximum number of students that this card can
     * have.
     *
     * @param characterType    this card's character
     * @param maxStudentAmount the maximum amount fo students this card can hold
     * @throws IllegalArgumentException if {@code characterType} is null
     */
    StudentStoreCharacter(CharacterType characterType, int maxStudentAmount) {
        super(characterType);
        container = new BoundedStudentContainer(maxStudentAmount);
    }

    /**
     * Returns a copy of the passed Character
     *
     * @param old Character to copy
     * @throws IllegalArgumentException if {@code old} is null
     */
    StudentStoreCharacter(StudentStoreCharacter old) {
        super(old);
        this.container = old.container;
    }

    /**
     * {@link PreparePhase} hook. This implementation additionally requests {@code maxStudentAmount} of {@link Student}
     * from the Sack. Subclasses should call this implementation if they want this behaviour.
     *
     * @param phase the {@link PreparePhase} the card's hook has been called from
     * @return a Tuple containing the updated PreparePhase and the updated Character
     * @throws IllegalArgumentException if {@code phase} is null
     */
    @Override
    Tuple<PreparePhase, Character> doPrepare(PreparePhase phase) {
        return super.doPrepare(phase)
                .map(t -> {
                    PreparePhase newPhase = t.getFirst();
                    StudentStoreCharacter newCharacter = (StudentStoreCharacter) t.getSecond().shallowCopy();

                    BoundedStudentContainer c = newCharacter.container;
                    while (true) {
                        try {
                            Tuple<PreparePhase, Student> t2 = newPhase.extractFromSack();
                            newPhase = t2.getFirst();
                            c = c.add(t2.getSecond());
                        } catch (ContainerIsFullException ignored) {
                            break;
                        }
                    }

                    newCharacter.container = c;
                    return new Tuple<>(newPhase, newCharacter);
                });
    }

    /**
     * Returns the students currently placed on the card.
     *
     * @return a set containing all the students placed on the card
     */
    @Override
    public Set<Student> getStudents() {
        return container.getStudents();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return container.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size(PieceColor color) {
        return container.size(color);
    }

    /**
     * Returns true if the card is full
     *
     * @return true if the card is full
     */
    public boolean isFull() {
        return container.isFull();
    }

    /**
     * Remove and return a {@link Student} of the given color from the store.
     *
     * @param color the color of the student to remove
     * @return A {@link Tuple}<StudentStoreCharacter, Student> with the new Character instance and the removed student
     * @throws IllegalArgumentException if the color passed is null
     * @throws EmptyStackException      if there are no students of the specified color in the container
     * @throws EmptyContainerException  if the container is empty
     */
    @Override
    public Tuple<StudentStoreCharacter, Student> remove(PieceColor color) {
        if (color == null) throw new IllegalArgumentException("color shouldn't be null");
        StudentStoreCharacter character = (StudentStoreCharacter) this.shallowCopy();
        Student st = container.remove(color)
                .map((f, s) -> {
                    character.container = f;
                    return s;
                });
        return new Tuple<>(character, st);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tuple<StudentStoreCharacter, Student> remove() throws EmptyContainerException {
        StudentStoreCharacter character = (StudentStoreCharacter) this.shallowCopy();
        Student st = container.remove()
                .map((f, s) -> {
                    character.container = f;
                    return s;
                });
        return new Tuple<>(character, st);
    }

    /**
     * Adds the given {@link Student} to the internal store.
     *
     * @param student the student to add
     * @return the new container instance including the new student
     * @throws IllegalArgumentException if the student to add is null
     * @throws ContainerIsFullException if the container is already full (if there is a bound to the number of students)
     */
    @Override
    public StudentStoreCharacter add(Student student) {
        if (student == null) throw new IllegalArgumentException("student shouldn't be null");
        StudentStoreCharacter s = (StudentStoreCharacter) this.shallowCopy();
        s.container = s.container.add(student);
        return s;
    }

    /**
     * Maps the given tuple to a new one where the student of the specified color has been stuffed somewhere
     * in an ActionPhase by the given function. A new student will be placed on the card by the given retriever.
     *
     * @param tuple     a {@link Tuple} containing an ActionPhase and a StudentStoreCharacter
     * @param color     the color to extract from the card
     * @param stuffer   a {@link ThrowingBiFunction} that takes an ActionPhase and a Student
     * @param retriever a {@link ThrowingBiFunction} that takes and ActionPhase and a StudentStoreCharacter
     * @return a {@link Tuple} containing the updated ActionPhase and Character
     * @throws IllegalArgumentException    if any parameter is null
     * @throws InvalidPhaseUpdateException if this card doesn't have ar least ont student or either the stuffer or
     *                                     retriever fail
     */
    Tuple<ActionPhase, Character> moveFromHere(Tuple<ActionPhase, StudentStoreCharacter> tuple,
                                               PieceColor color,
                                               ThrowingBiFunction<ActionPhase, Student, ActionPhase, InvalidPhaseUpdateException> stuffer,
                                               ThrowingBiFunction<ActionPhase, StudentStoreCharacter, Tuple<ActionPhase, StudentStoreCharacter>, InvalidPhaseUpdateException> retriever)
            throws InvalidPhaseUpdateException {
        if (tuple == null) throw new IllegalArgumentException("tuple shouldn't be null");
        if (color == null) throw new IllegalArgumentException("color shouldn't be null");
        if (stuffer == null) throw new IllegalArgumentException("stuffer shouldn't be null");
        if (retriever == null) throw new IllegalArgumentException("putter shouldn't be null");
        if (size(color) < 1)
            throw new InvalidPhaseUpdateException("card hasn't got enough students");
        return tuple.throwMap((originalPhase, originalCard) ->
                originalCard.remove(color)
                        .throwMap((c, s) -> new Tuple<>(retriever.apply(originalPhase, c), s))
                        .throwMap((t, s) -> {
                            ActionPhase afterStuffing = stuffer.apply(t.getFirst(), s);
                            return new Tuple<>(afterStuffing, t.getSecond());
                        }));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        StudentStoreCharacter that = (StudentStoreCharacter) o;
        return Objects.equals(container, that.container);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), container);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonElement toJson() {
        JsonObject ret = super.toJson().getAsJsonObject();

        JsonArray students = new JsonArray();
        getStudents().forEach(s -> students.add(s.getColor().toString()));
        ret.add("students", students);

        return ret;
    }
}
