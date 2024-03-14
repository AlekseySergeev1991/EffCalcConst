package ru.tecon.effCalcConst.model;

import java.util.StringJoiner;

public class ParamHistory {

    private String date;
    private String newValue;
    private String userName;
    private String constName;
    private String oldValue;


    public ParamHistory(String date, String newValue, String userName, String constName, String oldValue) {
        this.date = date;
        this.newValue = newValue;
        this.userName = userName;
        this.constName = constName;
        this.oldValue = oldValue;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getConstName() {
        return constName;
    }

    public void setConstName(String constName) {
        this.constName = constName;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ParamHistory.class.getSimpleName() + "[", "]")
                .add("date='" + date + "'")
                .add("newValue='" + newValue + "'")
                .add("userName='" + userName + "'")
                .add("constName='" + constName + "'")
                .add("oldValue='" + oldValue + "'")
                .toString();
    }
}
