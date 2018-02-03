package com.luis_santiago.aigol_admin.UI.tabs_actions;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import java.util.*;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luis_santiago.aigol_admin.R;
import com.luis_santiago.aigol_admin.UI.form.FormActivity;
import com.luis_santiago.aigol_admin.tools.adapters.MatchesAdapter;
import com.luis_santiago.aigol_admin.tools.data.ScoreTeam;
import com.luis_santiago.aigol_admin.tools.data.State;
import com.luis_santiago.aigol_admin.tools.utils.Keys;

/**
 * A simple {@link Fragment} subclass.
 */
public class MatchesFragment extends Fragment{

    // This for getting our results from the observable
    private LinearLayoutManager mLayoutManager;
    //RecycleView UI
    private RecyclerView mRecyclerView;
    //Builder for the dialog incase there is no internet
    private ArrayList<ScoreTeam> mTableTeamArrayList;
    // Setting the adapter
    private MatchesAdapter mScoreAdapters;
    private LinearLayout mLinearLayout;
    // This String is for knowing the legue to request
    private DatabaseReference mDatabase;
    private String TAG = MatchesFragment.class.getSimpleName();
    private FloatingActionButton fab;


    public MatchesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_matches ,container, false);
        init(view);
        //Getting the bundle object to know in which type of team were are in
        final String league;
        final Bundle bundle =  this.getArguments();
        if(bundle!=null){
            league = bundle.getString(Keys.KEY_LEAGUE);
        }
        else {
            league = "Empty string";
        }
        /* In order to upload a match, we need to send which type of league is going to be*/
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent formIntent = new Intent(getContext(), FormActivity.class);
                Bundle bundle1 = new Bundle();
                bundle1.putString(Keys.KEY_LEAGUE, league);
                formIntent.putExtras(bundle1);
                startActivity(formIntent);
            }
        });
        mTableTeamArrayList = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mLayoutManager = new LinearLayoutManager(view.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        DatabaseReference scores = mDatabase.child("Scores").child(league);
        scores.keepSynced(true);
        scores.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<ScoreTeam> localList = new ArrayList<ScoreTeam>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Log.e(TAG, "THE DATA OF SNAPSHOT "+ snapshot.getValue());
                    String key= snapshot.getKey();
                    String dates = (String) snapshot.child("date").getValue();
                    String roundSlug = (String) snapshot.child("slugRound").getValue();
                    String finalScore = (String) snapshot.child("finalScore").getValue();
                    String teamAway = (String) snapshot.child("teamAway").getValue();
                    String teamAwayLogo = (String)snapshot.child("teamAwayLogo").getValue();
                    String teamHome = (String)snapshot.child("team_home").getValue();
                    String team_home_logo = (String) snapshot.child("team_home_logo").getValue();

                    State state;
                    try {
                        state = new State(
                                (boolean) snapshot.child("state").child("hasStarted").getValue(),
                                (boolean) snapshot.child("state").child("done").getValue()
                        );
                        Log.e(TAG, "I GOT THE RECENT DATA");
                    }
                    catch (NullPointerException e){
                        //If i dont recive any data, it's going to be false by default
                        Log.e(TAG, "I got an error uploading ");
                        state = new State(false, false);
                    }

                    ScoreTeam scoreTeam = new ScoreTeam(
                            dates,
                            state,
                            key,
                            roundSlug,
                            finalScore,
                            teamAway,
                            teamAwayLogo,
                            teamHome,
                            team_home_logo);
                    localList.add(scoreTeam);
                }
                mTableTeamArrayList.clear();
                mTableTeamArrayList = localList;
                mScoreAdapters = new MatchesAdapter(mTableTeamArrayList, getContext(),league);
                mRecyclerView.setAdapter(mScoreAdapters);
                mLinearLayout.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "There was an error");
            }
        });
        return view;
    }

    private void init(View v){
        mLinearLayout = v.findViewById(R.id.progress_bar_layout_score);
        mRecyclerView = v.findViewById(R.id.recycle_view_scores);
        fab = v.findViewById(R.id.fab);
    }

}
