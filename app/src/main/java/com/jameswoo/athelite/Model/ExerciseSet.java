package com.jameswoo.athelite.Model;

public class ExerciseSet {
    private int _setNumber;
    private double _setWeight;
    private int _setReps;

    public ExerciseSet(int setNumber, double setWeight, int setReps) {
        _setNumber = setNumber;
        _setWeight = setWeight;
        _setReps = setReps;
    }

    public int getSetNumber() {
        return _setNumber;
    }

    public double getSetWeight() {
        return _setWeight;
    }

    public int getSetReps() {
        return _setReps;
    }

    public void setSetNumber(int setNumber) {
        _setNumber = setNumber;
    }

    public void setSetWeight(double setWeight) {
        _setWeight = setWeight;
    }

    public void setSetReps(int setReps) {
        _setReps = setReps;
    }

    public String toString() {
        return _setNumber + " " + _setWeight + " " + _setReps;
    }
}
