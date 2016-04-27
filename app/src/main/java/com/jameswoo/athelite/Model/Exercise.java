package com.jameswoo.athelite.Model;

import java.util.ArrayList;

public class Exercise {

    private long _id;
    private String _exerciseName;
    private ArrayList<ExerciseSet> _exerciseSets = new ArrayList<>();

    private Exercise(Builder builder) {
        _id = builder.bId;
        _exerciseName = builder.bExerciseName;
        _exerciseSets = builder.bExerciseSets;
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

    public String toString() {
        return _exerciseName;
    }

    public void setId(long id) {
        _id = id;
    }

    public void setExerciseName(String name) {
        _exerciseName = name;
    }

    public static class Builder {

        private long bId;
        private long bWorkoutPlanId;
        private String bExerciseName;
        private ArrayList<ExerciseSet> bExerciseSets = new ArrayList<>();

        public Builder(String name) {
            this.bExerciseName = name;
        }

        public Builder exerciseId(long id) {
            this.bId = id;
            return this;
        }

        public Builder workoutId(long id) {
            this.bWorkoutPlanId = id;
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

        public Exercise build() {
            return new Exercise(this);
        }
    }
}
