package de.quantum.core.entities;

import lombok.ToString;

import java.util.LinkedList;

@ToString
public class CircularList {

    private final int n;
    private final LinkedList<Object> list;

    public CircularList() {
        this.list = new LinkedList<>();
        this.n = -1;
    }

    public CircularList(int n) {
        this.list = new LinkedList<>();
        this.n = n;
    }

    public void addLast(Object o) {
        this.list.addLast(o);
        if (this.list.size() > this.n && this.n != -1) {
            this.list.removeFirst();
        }
    }

    public boolean contains(Object o) {
        return this.list.contains(o);
    }

}
