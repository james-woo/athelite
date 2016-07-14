package com.athelite.Model;

import java.util.ArrayList;
import java.util.Date;

public class Cardio {
    private long _id;
    private String _cardioName;
    private double _duration;
    private double _calories;
    private ArrayList<UserDefinedField> _userFields;
    private Date _date;

    private Cardio(Builder builder) {
        _id = builder.bId;
        _cardioName = builder.bCardioName;
        _date = builder.bDate;
        _duration = builder.bDuration;
        _calories = builder.bCalories;
        _userFields = builder.bFields;
    }

    public long getId() {
        return _id;
    }

    public String getCardioName() {
        return _cardioName;
    }

    public double getDuration() {
        return _duration;
    }

    public double getCalories() {
        return _calories;
    }

    public ArrayList<UserDefinedField> getUserFields() {
        return _userFields;
    }

    public Date getDate() { return _date; }

    public void setCardioName(String name) {
        _cardioName = name;
    }

    public void setDuration(double duration) {
        _duration = duration;
    }

    public void setCalories(double calories) {
        _calories = calories;
    }

    public void setId(long id) {
        _id = id;
    }

    public void addField(UserDefinedField field) {
        _userFields.add(field);
    }

    public void setFields(ArrayList<UserDefinedField> fields) {
        _userFields = fields;
    }

    public void setDate(Date date) { _date = date; }

    public static class Builder {
        private long bId;
        private String bCardioName;
        private double bDuration;
        private double bCalories;
        private ArrayList<UserDefinedField> bFields = new ArrayList<>();
        private Date bDate;

        public Builder(String name) {
            this.bCardioName = name;
        }

        public Builder cardioId(long id) {
            this.bId = id;
            return this;
        }

        public Builder duration(double duration) {
            this.bDuration = duration;
            return this;
        }

        public Builder calories(double calories) {
            this.bCalories = calories;
            return this;
        }

        public Builder fields(ArrayList<UserDefinedField> fields) {
            this.bFields = fields;
            return this;
        }

        public Builder field(UserDefinedField field) {
            this.bFields.add(field);
            return this;
        }

        public Builder date(Date date) {
            this.bDate = date;
            return this;
        }

        public Cardio build() {
            return new Cardio(this);
        }
    }

}
