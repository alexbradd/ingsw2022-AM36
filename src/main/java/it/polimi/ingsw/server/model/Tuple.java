package it.polimi.ingsw.server.model;

public class Tuple<T, P> {

    public final T t;
    public final P p;

    public Tuple(T t, P p) {
        this.t = t;
        this.p = p;
    }
}
