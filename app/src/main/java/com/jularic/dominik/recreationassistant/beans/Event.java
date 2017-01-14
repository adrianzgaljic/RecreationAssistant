package com.jularic.dominik.recreationassistant.beans;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Dominik on 6.1.2017..
 */

public class Event extends RealmObject {
    private String name;
    @PrimaryKey
    private long dateAdded;
    private long dateOfEvent;
    private boolean completed;

    public Event() {
    }

    public Event(String name, long dateAdded, long dateOfEvent, boolean completed) {
        this.name = name;
        this.dateAdded = dateAdded;
        this.dateOfEvent = dateOfEvent;
        this.completed = completed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }

    public long getDateOfEvent() {
        return dateOfEvent;
    }

    public void setDateOfEvent(long dateOfEvent) {
        this.dateOfEvent = dateOfEvent;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
