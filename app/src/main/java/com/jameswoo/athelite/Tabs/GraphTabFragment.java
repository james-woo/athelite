package com.jameswoo.athelite.Tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.jameswoo.athelite.Adapter.GraphExerciseListAdapter;
import com.jameswoo.athelite.Database.DBHandler;
import com.jameswoo.athelite.Model.Exercise;
import com.jameswoo.athelite.R;

import java.util.ArrayList;

public class GraphTabFragment extends Fragment {

    private DBHandler _db;
    private ArrayList<Exercise> _graphExerciseList;
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

        _db = new DBHandler(getContext());
        _graphExerciseList = _db.getCompletedExercises(_db.getWritableDatabase());
        _adapter = new GraphExerciseListAdapter(getContext(), _graphExerciseList);
        _listView = (ListView) rootView.findViewById(R.id.graph_exercise_list);
        if(_listView != null)
            _listView.setAdapter(_adapter);

        return rootView;
    }


}
