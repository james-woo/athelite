package com.athelite.Model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class Exercise implements Comparable<Exercise> {

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

    public boolean hasNoReps() {
        boolean result = true;
        for(ExerciseSet es : _exerciseSets) {
            if(es.getSetReps() > 0) {
                result = false;
            }
        }
        return result;
    }

    public void copy(Exercise exercise) {
        this._exerciseSets = exercise.getExerciseSets();
        this._exerciseDate = exercise.getExerciseDate();
        this._exerciseName = exercise.getExerciseName();
        this._id = exercise.getId();
        this._oneRepMax = exercise.getOneRepMax();
    }

    @Override
    public int compareTo(Exercise another) {
        return Comparators.NAME.compare(this, another);
    }

    public static class Comparators {
        public static Comparator<Exercise> NAME = new Comparator<Exercise>() {
            @Override
            public int compare(Exercise lhs, Exercise rhs) {
                return lhs.getExerciseName().compareTo(rhs.getExerciseName());
            }
        };
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
