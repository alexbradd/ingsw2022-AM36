package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.PieceColor;
import it.polimi.ingsw.server.model.exceptions.EmptyStudentContainerException;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class StudentContainer {
    EnumMap<PieceColor, List<Student>> store;

    StudentContainer() {
        store = new EnumMap<PieceColor, List<Student>>(PieceColor.class);
    }

    StudentContainer(StudentContainer old) {
        this.store = old.store; // shallow copy;
    }

    Set<Student> getStudents() {
        return store.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toSet());
    }

    StudentContainer add(Student s) {
        if (s == null) throw new IllegalArgumentException();
        StudentContainer newContainer = new StudentContainer(this);
        newContainer.store = new EnumMap<>(this.store);

        if (!newContainer.store.containsKey(s.getColor())) {
            newContainer.store.putIfAbsent(s.getColor(), new ArrayList<>());
        } else {
            List<Student> old = newContainer.store.get(s.getColor());
            newContainer.store.put(s.getColor(), new ArrayList<>(old));
        }
        newContainer.store.get(s.getColor()).add(s);
        return newContainer;
    }

    StudentContainer remove(Consumer<Student> consumer) throws EmptyStudentContainerException {
        if (consumer == null) throw new IllegalArgumentException();
        Random r = new Random();
        List<Student> allStudents = this.store.values().stream()
                .flatMap(List::stream)
                .toList();
        int random = r.nextInt(allStudents.size());
        Student s = allStudents.get(random);
        return remove(s.getColor(), consumer);

    }

    StudentContainer remove(PieceColor color, Consumer<Student> consumer) throws EmptyStudentContainerException {
        if (consumer == null) throw new IllegalArgumentException();
        if (!this.store.containsKey(color))
            throw new IllegalArgumentException();

        StudentContainer newContainer = new StudentContainer(this);
        newContainer.store = new EnumMap<>(this.store);

        newContainer.store.put(color, new ArrayList<>(newContainer.store.get(color)));
        List<Student> students = newContainer.store.get(color);
        Student removed = students.stream()
                .filter(s -> s.getColor().equals(color))
                .findAny()
                .orElseThrow(EmptyStudentContainerException::new);
        students.remove(removed);
        consumer.accept(removed);
        return newContainer;
    }
}
