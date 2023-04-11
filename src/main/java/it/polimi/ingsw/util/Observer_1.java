package it.polimi.ingsw.util;

public interface Observer_1<SubjectType extends Observable_1, T> {
    void update(SubjectType o, T arg);
}
