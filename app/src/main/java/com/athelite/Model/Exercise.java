package com.athelite.Model;

import java.util.ArrayList;
import java.util.Date;

public class Exercise {

    private long _id;
    private String _exerciseName;
    private ArrayList<ExerciseSet> _exerciseSets = new ArrayList<>();
    private double _oneRepMax;
    private Date _exerciseDate;

    private Exercise(Builder builder) {
        _id = builder.bId;
        _exerciseName = builder.bExerciseName;
        _exerciseSets = builder.bExerciseSets;
        _oneRepMax = builder.bOneRepMax;
        _exerciseDate = builder.bExerciseDate;
    }

    public String getExerciseName() {
        return _exerciseName;
    }

    public ArrayList<ExerciseSet> getExerciseSets() {
        return _exerciseSets;
    }

    public void setExerciseSets(ArrayList<ExerciseSet> exerciseSets) {
        _exerciseSets = exerciseSets;
    }

    public void addExerciseSet(ExerciseSet exerciseSet) {
        _exerciseSets.add(exerciseSet);
    }

    public long getId() {
        return _id;
    }

    public double getOneRepMax() { return _oneRepMax; }

    public Date getExerciseDate() { return _exerciseDate; }

    public void setId(long id) {
        _id = id;
    }

    public void setExerciseName(String name) {
        _exerciseName = name;
    }

    public void setOneRepMax(double orm) { _oneRepMax = orm; }

    public void setExerciseDate(Date date) {
        _exerciseDate = date;
    }

    public void calculateOneRepMax() {
        // Epley Formula
        double maxWeight = 0;
        int maxReps = 0;
        for(ExerciseSet es : _exerciseSets) {
            double weight = es.getSetWeight();
            if(weight > maxWeight) {
                maxWeight = weight;
                maxReps = es.getSetReps();
            }
        }
        _oneRepMax = maxWeight * (1 + (maxReps / 30.0));
    }

    public static class Builder {

        private long bId;
        private String bExerciseName;
        private ArrayList<ExerciseSet> bExerciseSets = new ArrayList<>();
        private double bOneRepMax;
        private Date bExerciseDate;

        public Builder(String name) {
            this.bExerciseName = name;
        }

        public Builder exerciseId(long id) {
            this.bId = id;
            return this;
        }

        public Builder exerciseSets(ArrayList<ExerciseSet> sets) {
            this.bExerciseSets = sets;
            return this;
        }

        public Builder exerciseSet(ExerciseSet exerciseSet) {
            this.bExerciseSets.add(exerciseSet);
            return this;
        }

        public Builder oneRepMax(double orm) {
            this.bOneRepMax = orm;
            return this;
        }

        public Builder exerciseDate(Date date) {
            this.bExerciseDate = date;
            return this;
        }

        public Exercise build() {
            return new Exercise(this);
        }
    }
}
