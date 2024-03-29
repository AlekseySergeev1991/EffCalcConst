package ru.tecon.effCalcConst.model;

import java.io.Serializable;
import java.util.StringJoiner;

public class Const implements Serializable {
    private int num;
    private int id;
    private String name;
    private String shortName;
    private String measure;
    private String value;
    private String edit;
    private String constGroupName;
    private int constGroupId;

    public Const(int num, int id, String name, String shortName, String measure, String value, String edit, String constGroupName, int constGroupId) {
        this.num = num;
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.measure = measure;
        this.value = value;
        this.edit = edit;
        this.constGroupName = constGroupName;
        this.constGroupId = constGroupId;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getEdit() {
        return edit;
    }

    public void setEdit(String edit) {
        this.edit = edit;
    }

    public String getConstGroupName() {
        return constGroupName;
    }

    public void setConstGroupName(String constGroupName) {
        this.constGroupName = constGroupName;
    }

    public int getConstGroupId() {
        return constGroupId;
    }

    public void setConstGroupId(int constGroupId) {
        this.constGroupId = constGroupId;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Const.class.getSimpleName() + "[", "]")
                .add("num=" + num)
                .add("id=" + id)
                .add("name='" + name + "'")
                .add("shortName='" + shortName + "'")
                .add("measure='" + measure + "'")
                .add("value='" + value + "'")
                .add("edit='" + edit + "'")
                .add("constGroupName='" + constGroupName + "'")
                .add("constGroupId=" + constGroupId)
                .toString();
    }
}
