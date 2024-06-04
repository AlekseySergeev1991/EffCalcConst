package ru.tecon.effCalcConst.model;

import java.io.Serializable;
import java.util.StringJoiner;

public class ObjProp implements Serializable {
    private int propId;
    private String propName;

    public ObjProp() {
    }

    public ObjProp(int propId, String propName) {
        this.propId = propId;
        this.propName = propName;
    }

    public int getPropId() {
        return propId;
    }

    public void setPropId(int propId) {
        this.propId = propId;
    }

    public String getPropName() {
        return propName;
    }

    public void setPropName(String propName) {
        this.propName = propName;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ObjProp.class.getSimpleName() + "[", "]")
                .add("propId=" + propId)
                .add("propName='" + propName + "'")
                .toString();
    }
}
