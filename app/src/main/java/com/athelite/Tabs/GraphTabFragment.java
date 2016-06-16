package com.athelite.Tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.athelite.Adapter.GraphExerciseListAdapter;
import com.athelite.Database.DBHandler;
import com.athelite.Dialog.ErrorDialog;
import com.athelite.Model.Exercise;
import com.athelite.R;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GraphTabFragment extends Fragment {

    private DBHandler _db;
    private ArrayMap<String, ArrayList<Exercise>> _graphExerciseList;
    private ArrayList<Exercise> _exercises = new ArrayList<>();
    private ListView _listView;
    private GraphExerciseListAdapter _adapter;

    private static GraphTabFragment _gFragment = new GraphTabFragment();
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public GraphTabFragment() {

    }

    public static GraphTabFragment getInstance() {
        return _gFragment;
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static GraphTabFragment newInstance(int sectionNumber) {
        GraphTabFragment fragment = _gFragment;
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_graph, container, false);

        initInstances();
        updateExercises();
        _adapter = new GraphExerciseListAdapter(getContext(), _exercises);
        _listView = (ListView) rootView.findViewById(R.id.graph_exercise_list);
        if(_listView != null)
            _listView.setAdapter(_adapter);

        return rootView;
    }

    private void initInstances() {
        _db = new DBHandler(getContext());
    }

    public void updateExercises() throws NullPointerException{
        try {
            if (_exercises != null) {
                _exercises.clear();
            }
            if (_graphExerciseList != null) {
                _graphExerciseList.clear();
            }
            if (_db == null && getContext() != null) {
                _db = new DBHandler(getContext());
            }
            _graphExerciseList = _db.getCompletedExercises(_db.getWritableDatabase());
            for (int i = 0; i < _graphExerciseList.size(); i++) {
                Double highestOneRepMax = 0.0;
                String exerciseName = _graphExerciseList.keyAt(i);
                ArrayList<Exercise> exerciseArrayList = _graphExerciseList.get(exerciseName);
                Exercise heaviestExercise = new Exercise.Builder(exerciseName).build();
                for (Exercise e : exerciseArrayList) {
                    if (highestOneRepMax < e.getOneRepMax()) {
                        highestOneRepMax = e.getOneRepMax();
                        heaviestExercise.setExerciseName(e.getExerciseName());
                        heaviestExercise.setExerciseSets(e.getExerciseSets());
                        heaviestExercise.setId(e.getId());
                        heaviestExercise.setOneRepMax(highestOneRepMax);
                        heaviestExercise.setExerciseDate(e.getExerciseDate());
                    }
                }
                if(heaviestExercise.getOneRepMax() != 0.0 &&
                        heaviestExercise.getExerciseDate().getTime() < System.currentTimeMillis()) {
                    _exercises.add(heaviestExercise);
                }
            }

            Collections.sort(_exercises, Exercise.Comparators.NAME);

            if (_adapter != null)
                _adapter.notifyDataSetChanged();
        } catch (Exception e) {
            ErrorDialog.logError("Error Updating Graph", e.getMessage());
        }
    }
}
