package com.luis_santiago.aigol_admin.UI.tabs_actions;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import java.util.*;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.luis_santiago.aigol_admin.R;
import com.luis_santiago.aigol_admin.tools.adapters.TableTeamAdapter;
import com.luis_santiago.aigol_admin.tools.data.TableTeam;
import com.luis_santiago.aigol_admin.tools.utils.Keys;

/**
 * A simple {@link Fragment} subclass.
 */
public class TablesFragment extends Fragment {

    // This is for debugging
    private static String TAG = TablesFragment.class.getSimpleName();
    private LinearLayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    // Progress bar, when we have conection it's remove
    //private LinearLayout mLinearLayout;
    // The list we are going to display
    ArrayList<TableTeam> mTableTeamArrayList;
    private LinearLayout mLinearLayout;
    private TableTeamAdapter mTableAdapter;
    private String league;

    public TablesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tables, container, false);
        init(view);

        mLayoutManager = new LinearLayoutManager(view.getContext());
        //
        mRecyclerView.setLayoutManager(mLayoutManager);
        // Creating our list
        mTableTeamArrayList = new ArrayList<>();
        //To know which type of league it is we reach our from the main fragment
        Bundle bundle =  this.getArguments();
        if(bundle!=null){
            league = bundle.getString(Keys.KEY_LEAGUE);
        }
        else {
            league = "Empty string";
        }
        //Setting our adapter
        Log.e(TAG, "Estoy en la liga "+ league);
        mTableAdapter = new TableTeamAdapter(mTableTeamArrayList, league);
        mRecyclerView.setAdapter(mTableAdapter);
        // Creating an instance of the database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        //Finding the correct reference to read our data
        DatabaseReference standing = databaseReference.child("Standings").child(league);
        standing.keepSynced(true);
        // To read our data we need to add the value Listener
        Query query = standing.orderByChild("position");
        query.addListenerForSingleValueEvent(valueEventListener);

        standing.addValueEventListener(new ValueEventListener() {
            ArrayList<TableTeam> refreshList = new ArrayList<>();
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    TableTeam tableTeam = generateTableTeam(snapshot);
                    refreshList.add(tableTeam);
                }
                mTableTeamArrayList = refreshList;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        ArrayList<TableTeam> finalList = new ArrayList<>();
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                TableTeam tableTeam = generateTableTeam(snapshot);
                finalList.add(tableTeam);
            }
            mTableAdapter.setTableTeams(finalList);
            //We remove the progress bar and all there textview
            mLinearLayout.setVisibility(View.GONE);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(TAG, "There was an error" + databaseError.getDetails());
        }
    };

    private void init(View view){
        //Casting all the UI components
        mRecyclerView = view.findViewById(R.id.recycle_view_table_fragment);
        mLinearLayout = view.findViewById(R.id.progress_bar_layout);
    }

    public static TableTeam generateTableTeam(DataSnapshot snapshot){
        String id = snapshot.getKey();
        Long position = (Long) snapshot.child("position").getValue();
        String name = (String) snapshot.child("name").getValue();
        String logo = (String) snapshot.child("team_logo").getValue();
        Long matchesPlayed = (Long) snapshot.child("matches_played").getValue();
        Long goalDifference = (Long) snapshot.child("goal_difference").getValue();
        Long goalFor = (Long) snapshot.child("goal_for").getValue();
        Long goalAfter = (Long) snapshot.child("goal_afer").getValue();
        Long points = (Long) snapshot.child("points").getValue();

        return new TableTeam(
                id,
                Long.toString(position),
                logo,
                name,
                Long.toString(matchesPlayed),
                Long.toString(goalFor),
                Long.toString(goalAfter),
                Long.toString(goalDifference),
                Long.toString(points)
        );
    }
}
