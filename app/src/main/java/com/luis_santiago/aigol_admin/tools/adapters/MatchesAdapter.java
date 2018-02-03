package com.luis_santiago.aigol_admin.tools.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.*;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.luis_santiago.aigol_admin.R;
import com.luis_santiago.aigol_admin.UI.form.EditMatchActivity;
import com.luis_santiago.aigol_admin.tools.data.ScoreTeam;
import com.luis_santiago.aigol_admin.tools.data.State;
import com.luis_santiago.aigol_admin.tools.utils.Keys;
import com.luis_santiago.aigol_admin.tools.utils.Utils;

/**
 * Created by Luis Fernando Santiago Ruiz on 9/8/17.
 */

public class MatchesAdapter extends RecyclerView.Adapter <MatchesAdapter.HoldViewer> {
    private List <ScoreTeam> mScoreArraList = new ArrayList<>();
    private String TAG = MatchesAdapter.class.getSimpleName();
    private Context mContext;
    private String league;

    public MatchesAdapter(List<ScoreTeam> fl, Context context, String league){
        this.mScoreArraList = fl;
        this.mContext = context;
        this.league = league;
    }

    public void setTableTeams(List <ScoreTeam> team){
        if (team == null){
            return;
        }
        mScoreArraList.clear();
        mScoreArraList.addAll(team);
        notifyDataSetChanged();
    }

    @Override
    public MatchesAdapter.HoldViewer onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_match_available,parent,false);

        return new HoldViewer(view);
    }

    @Override
    public void onBindViewHolder(final HoldViewer holder, int position) {

        final ScoreTeam scoreTeam = mScoreArraList.get(position);
        final State state = new State(
                scoreTeam.getState().getHasStarted(),
                scoreTeam.getState().isDone()
        );


        if(state.getHasStarted()){
            holder.state.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle));
            holder.state.setText("LIVE");
        }
        else if(state.isDone()){
            holder.state.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_green));
            holder.state.setText("'90");
        }
        else if(!state.isDone() && !state.getHasStarted()){
            holder.state.setVisibility(View.INVISIBLE);
        }
        //holder.state.setBackground();
        //holder.state.setText();
        String url_home = scoreTeam.getTeam_home_logo();
        String url_away = scoreTeam.getTeamAwayLogo();
        /*
         * This is for downloading and saving the image on a local file
         */
        Utils.DownloadImage(holder.homeTeam, url_home);
        Utils.DownloadImage(holder.awayTeam, url_away);

        /*We get the score in this format 1-1*/
        String finalScore = scoreTeam.getFinalScore();
        Log.e(TAG, "This is the final score"+ finalScore);
        // we split it
        String scores[] = finalScore.split("-");

        holder.date.setText(scoreTeam.getDate());

        //Now we set the text from the split
        holder.scoreLocal.setText(scores[0]);
        holder.scoreVisitor.setText(scores[1]);
        Log.e(TAG, "THE FIRST SCORE IS "+ scores[0]);
        Log.e(TAG, "THE SECOND SCORE IS "+ scores[1]);

        holder.slugRound.setText(scoreTeam.getSlugRound());
        holder.nameHomeTeam.setText(scoreTeam.getTeam_home());
        holder.nameAwayTeam.setText(scoreTeam.getTeamAway());

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpDialog(scoreTeam);
            }
        });
        Log.e(TAG, "VALORES DE BOLEAAN DONE "+ scoreTeam.getState().isDone());
        Log.e(TAG, "VALORES DE BOLEAAN HAS STARTED" + scoreTeam.getState().getHasStarted());
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, EditMatchActivity.class);
                intent.putExtra(Keys.KEY_OBJECT, scoreTeam);
                intent.putExtra(Keys.STATE_OBJECT, state);
                intent.putExtra(Keys.KEY_LEAGUE, league);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mScoreArraList.size();
    }

    public class HoldViewer extends RecyclerView.ViewHolder{

        private TextView date;
        private TextView slugRound;
        private ImageView homeTeam;
        private TextView nameHomeTeam;
        private ImageView awayTeam;
        private TextView nameAwayTeam;
        private TextView scoreLocal;
        private TextView scoreVisitor;
        private Button delete;
        private Button editButton;
        private TextView state;

        public HoldViewer(View v) {
            super(v);
            slugRound = v.findViewById(R.id.slug_round);
            homeTeam = v.findViewById(R.id.homeLogo);
            nameHomeTeam = v.findViewById(R.id.home_team);
            awayTeam = v.findViewById(R.id.awayLogo);
            nameAwayTeam = v.findViewById(R.id.team_away);
            scoreLocal = v.findViewById(R.id.score_local);
            scoreVisitor = v.findViewById(R.id.score_visitor);
            delete = v.findViewById(R.id.delete_button);
            editButton = v.findViewById(R.id.edit_button);
            date = v.findViewById(R.id.date);
            state = v.findViewById(R.id.state_drawable);
        }
    }

    private void setUpDialog(final ScoreTeam scoreTeam){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Dialog_Alert);

        builder.setTitle("Confirmar");
        builder.setMessage("¿Estas seguro de eliminar este partido?");

        builder.setPositiveButton("Borrar", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                deleteItem(scoreTeam.getKey());
                dialog.dismiss();
            }

            private void deleteItem(String key) {
                DatabaseReference database  = FirebaseDatabase.getInstance().getReference();
                database.child("Scores")
                        .child(league)
                        .child(key)
                        .removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(
                                        mContext,
                                        "Partido Eliminado",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        });
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}
