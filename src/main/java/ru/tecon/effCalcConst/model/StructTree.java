package ru.tecon.effCalcConst.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;

public class StructTree implements Serializable {
    private String id;
    private String name;
    private String parent;
    private int myId;
    private String myType;
    private String myIcon;

    public StructTree() {
    }

    public StructTree(String id, String name, String parent, int myId, String myType, String myIcon) {
        this.id = id;
        this.name = name;
        this.parent = parent;
        this.myId = myId;
        this.myType = myType;
        this.myIcon = myIcon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public int getMyId() {
        return myId;
    }

    public void setMyId(int myId) {
        this.myId = myId;
    }

    public String getMyType() {
        return myType;
    }

    public void setMyType(String myType) {
        this.myType = myType;
    }

    public String getMyIcon() {
        return myIcon;
    }

    public void setMyIcon(String myIcon) {
        this.myIcon = myIcon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StructTree that = (StructTree) o;
        return myId == that.myId && Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(parent, that.parent) && Objects.equals(myType, that.myType) && Objects.equals(myIcon, that.myIcon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, parent, myId, myType, myIcon);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", StructTree.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("name='" + name + "'")
                .add("parent='" + parent + "'")
                .add("myId=" + myId)
                .add("myType='" + myType + "'")
                .add("myIcon='" + myIcon + "'")
                .toString();
    }
}
