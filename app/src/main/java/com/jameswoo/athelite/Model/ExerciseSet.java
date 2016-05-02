package com.jameswoo.athelite.Model;

public class ExerciseSet {
    private long _id;
    private int _setNumber;
    private double _setWeight;
    private String _weightType = "lb";
    private int _setReps;


    public ExerciseSet(long id, int setNumber, double setWeight, String weightType, int setReps) {
        _id = id;
        _setNumber = setNumber;
        _setWeight = setWeight;
        _weightType = weightType;
        _setReps = setReps;
    }

    public long getId() {
        return _id;
    }

    public int getSetNumber() {
        return _setNumber;
    }

    public double getSetWeight() {
        return _setWeight;
    }

    public String getWeightType() {
        return _weightType;
    }

    public int getSetReps() {
        return _setReps;
    }

    public void setId(long id) {
        _id = id;
    }

    public void setSetNumber(int setNumber) {
        _setNumber = setNumber;
    }

    public void setSetWeight(double setWeight) {
        _setWeight = setWeight;
    }

    public void setWeightType(String weightType) {
        _weightType = weightType;
    }

    public void setSetReps(int setReps) {
        _setReps = setReps;
    }

    public String toString() {
        return _setNumber + " " + _setWeight + " " + _setReps;
    }
}
