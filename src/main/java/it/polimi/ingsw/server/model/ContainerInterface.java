package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.exceptions.ColorIsFullException;
import it.polimi.ingsw.server.model.exceptions.ContainerIsFullException;
import it.polimi.ingsw.server.model.enums.PieceColor;
import it.polimi.ingsw.server.model.exceptions.EmptyContainerException;

import java.util.EmptyStackException;
import java.util.Set;

interface ContainerInterface {

    /**
     * It returns the total number of Students inside the Container.
     *
     * @return the number of Students inside the Container
     */
    int size();

    /**
     * It returns the number of Students of a certain color inside the container.
     *
     * @param color the color of the Students
     * @return the number of Students of that color inside the container
     */
    int size(PieceColor color);

    /**
     * It returns a Set containing all the Students inside the container.
     *
     * @return the Set<{@link Student}> containing all the Students of the container
     */
    Set<Student> getStudents();

    /**
     * This method allows to add a {@link Student} to the container, and returns a copy of the container containing
     * that student.
     *
     * @param s the student to add
     * @return the new container instance including the new student
     * @throws IllegalArgumentException if the student to add is null
     * @throws ContainerIsFullException if the container is already full (if there is a bound to the number of students)
     * @throws ColorIsFullException     if there are too many students of this color inside the container (if there is a
     *                                  bound to the number of students of the same color)
     */
    ContainerInterface add(Student s) throws IllegalArgumentException, ContainerIsFullException, ColorIsFullException;

    /**
     * This method removes a random {@link Student} from the container, and returns it with a new container instance, not
     * containing the removed student.
     *
     * @return A {@link Tuple}<Container, Student> with the new container instance and the removed student
     * @throws EmptyContainerException if the container is empty
     */
    Tuple<? extends ContainerInterface, Student> remove() throws EmptyContainerException;

    /**
     * This method removes a {@link Student} of the given {@link PieceColor} from the container, and returns it with a
     * new container instance, not including the removed student.
     *
     * @param color the color of the student to remove
     * @return A {@link Tuple}<Container, Student> with the new container instance and the removed student
     * @throws IllegalArgumentException if the color passed is null
     * @throws EmptyStackException if there are no students of the specified color in the container
     */
    Tuple<? extends ContainerInterface, Student> remove(PieceColor color) throws IllegalArgumentException, EmptyStackException;
}
