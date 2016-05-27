package com.jameswoo.athelite.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jameswoo.athelite.Activity.ViewGraph;
import com.jameswoo.athelite.Database.DBHandler;
import com.jameswoo.athelite.Model.Exercise;
import com.jameswoo.athelite.R;
import com.jameswoo.athelite.Util.JsonSerializer;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class GraphExerciseListAdapter extends ArrayAdapter<Exercise> {

    private Context _context;
    private ArrayList<Exercise> _exerciseList;
    private DBHandler _db;
    private SharedPreferences _sp;

    public GraphExerciseListAdapter(Context context, ArrayList<Exercise> exercises) {
        super(context, 0, exercises);
        this._context = context;
        this._exerciseList = exercises;
        this._db = new DBHandler(_context);
        this._sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_view_graph_exercise_list, parent, false);
        }

        double oneRepMax = _exerciseList.get(position).getOneRepMax();
        DecimalFormat oneRepDF = new DecimalFormat("#####.##");
        TextView oneRepMaxTV = (TextView) convertView.findViewById(R.id.graph_exercise_one_rep_max);
        oneRepMaxTV.setText(String.format("%s %s %s","1 RM: ", String.valueOf(oneRepDF.format(oneRepMax)), _sp.getString("units", "lb")));

        TextView exerciseNameTV = (TextView) convertView.findViewById(R.id.graph_exercise_name);
        exerciseNameTV.setText(_exerciseList.get(position).getExerciseName());

        CardView eCardView = (CardView) convertView.findViewById(R.id.graph_exercise_card_view);

        eCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewGraph = new Intent(getContext(), ViewGraph.class);
                viewGraph.putExtra("VIEW_GRAPH_EXERCISE", JsonSerializer.workoutPlanExerciseToJson(_exerciseList.get(position)));
                viewGraph.putExtra("VIEW_GRAPH_PARENT", "Graph");
                _context.startActivity(viewGraph);
            }
        });

        return convertView;
    }
}
