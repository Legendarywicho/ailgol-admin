package com.luis_santiago.aigol_admin.tools.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import java.util.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.luis_santiago.aigol_admin.R;
import com.luis_santiago.aigol_admin.tools.data.TableTeam;

import java.util.ArrayList;

import static com.google.android.gms.internal.zzt.TAG;

/**
 * Created by Luis Fernando Santiago Ruiz on 9/6/17.
 */

public class TableTeamAdapter extends RecyclerView.Adapter<TableTeamAdapter.TableAdapterHolder>{

    private ArrayList <TableTeam> tableTeams = new ArrayList<>();
    private String leagueName;

    public TableTeamAdapter(ArrayList<TableTeam> args, String name){
        this.tableTeams = args;
        this.leagueName = name;
    }

    public void setTableTeams(ArrayList <TableTeam> team){
        if (team == null){
            return;
        }
        tableTeams.clear();
        tableTeams.addAll(team);
        notifyDataSetChanged();
    }


    @Override
    public TableAdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_table_team, parent, false);
        return new TableAdapterHolder(view);
    }



    @Override
    public void onBindViewHolder(final TableAdapterHolder holder, int position) {
        final TableTeam tableTeam = tableTeams.get(position);
        holder.position.setText(tableTeam.getPosition());
        holder.position.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String text = holder.position.getText().toString();
                Log.e(TAG, "the text is being change"+ text);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        holder.teamName.setText(tableTeam.getName());
        holder.matchesPlayed.setText(tableTeam.getMp());
        holder.goalFor.setText(tableTeam.getGf());
        holder.goalAfter.setText(tableTeam.getGa());
        holder.goalDiference.setText(tableTeam.getGd());
        holder.points.setText(tableTeam.getPts());
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                Log.e(TAG, "This is from the league : "+ leagueName);
                database.child("Standings").child(leagueName).child(tableTeam.getId())
                        .updateChildren(uploadData(holder, tableTeam))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e(TAG, "Im done uploading");
                        Toast.makeText(view.getContext(),"Datos subidos", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });



    }
    @Override
    public int getItemCount() {
        return tableTeams.size();
    }

    static class TableAdapterHolder extends RecyclerView.ViewHolder{
        EditText position;
        TextView teamName;
        EditText matchesPlayed;
        EditText goalFor;
        EditText goalAfter;
        EditText goalDiference;
        EditText points;
        Button button;
        View layout;

        public TableAdapterHolder(View v) {
            super(v);
            layout = v;
            position = v.findViewById(R.id.position);
            teamName = v.findViewById(R.id.team);
            matchesPlayed = v.findViewById(R.id.matches_played);
            goalFor = v.findViewById(R.id.goal_for);
            goalAfter = v.findViewById(R.id.goal_after);
            goalDiference = v.findViewById(R.id.goal_difference);
            points = v.findViewById(R.id.points);
            button = v.findViewById(R.id.button);
        }
    }

    private HashMap<String,Object> uploadData(final TableAdapterHolder holder, TableTeam tableTeam){
        Long position = Long.valueOf(holder.position.getText().toString());
        Long matchesPlayed = Long.valueOf(holder.matchesPlayed.getText().toString());
        Long goalFor = Long.valueOf(holder.goalFor.getText().toString());
        Long goalAfter = Long.valueOf(holder.goalAfter.getText().toString());
        Long goalDiference = Long.valueOf(holder.goalDiference.getText().toString());
        Long points = Long.valueOf(holder.points.getText().toString());
        String name =  tableTeam.getName();
        String teamLogo = tableTeam.getLogo();

        HashMap<String,Object> result = new HashMap<>();
        result.put("goal_afer", goalAfter);
        result.put("goal_difference", goalDiference);
        result.put("goal_for", goalFor);
        result.put("name", name);
        result.put("team_logo", teamLogo);
        result.put("matches_played", matchesPlayed);
        result.put("points", points);
        Log.e(TAG, "UPLOADING POINTS: "+points);
        result.put("position", position);
        Log.e(TAG, "UPLOADING POSITION: "+position);
        return result;
    }

    private void uploadData(){

    }
}
