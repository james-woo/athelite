package com.athelite.Model;

import java.util.ArrayList;
import java.util.Date;

public class WorkoutPlan {

    private long _id;
    private String _workoutPlanName;
    private ArrayList<Exercise> _exercises = new ArrayList<>();
    private Date _date;

    private WorkoutPlan(Builder builder) {
        _id = builder.bId;
        _workoutPlanName = builder.bWorkoutPlanName;
        _exercises = builder.bExercises;
        _date = builder.bDate;
    }

    public long getId() {
        return _id;
    }

    public String getWorkoutPlanName() {
        return _workoutPlanName;
    }

    public ArrayList<Exercise> getWorkoutPlanExercises() {
        return _exercises;
    }

    public Date getDate() { return _date; }

    public void setWorkoutPlanName(String name) {
        _workoutPlanName = name;
    }

    public void setId(long id) {
        _id = id;
    }

    public void addExercise(Exercise exercise) {
        _exercises.add(exercise);
    }

    public void setExercises(ArrayList<Exercise> exercises) {
        _exercises = exercises;
    }

    public void setDate(Date date) { _date = date; }

    public static class Builder {
        private long bId;
        private String bWorkoutPlanName;
        private ArrayList<Exercise> bExercises = new ArrayList<>();
        private Date bDate;

        public Builder(String name) {
            this.bWorkoutPlanName = name;
        }

        public Builder workoutPlanId(long id) {
            this.bId = id;
            return this;
        }

        public Builder exercises(ArrayList<Exercise> exercises) {
            this.bExercises = exercises;
            return this;
        }

        public Builder exercise(Exercise exercise) {
            this.bExercises.add(exercise);
            return this;
        }

        public Builder date(Date date) {
            this.bDate = date;
            return this;
        }

        public WorkoutPlan build() {
            return new WorkoutPlan(this);
        }
    }


}
