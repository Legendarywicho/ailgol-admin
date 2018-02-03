package com.luis_santiago.aigol_admin.UI.form;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.support.annotation.IdRes;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import java.util.*;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.luis_santiago.aigol_admin.R;
import com.luis_santiago.aigol_admin.tools.data.ScoreTeam;
import com.luis_santiago.aigol_admin.tools.data.State;
import com.luis_santiago.aigol_admin.tools.utils.Keys;
import com.squareup.picasso.Picasso;

public class EditMatchActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private final String TAG = EditMatchActivity.class.getSimpleName();

    private Toolbar toolbar;
    private Button mDateButton;
    private TextView mDateText;
    private ImageView logoHome;
    private TextView mNameHome;
    private EditText mScoreHome;
    private ImageView mLogoVisit;
    private TextView mNameVisit;
    private EditText mScoreVisit;
    private RadioGroup mRadioGroup;
    private State state;
    private TextInputLayout slugLayout;
    private EditText slugRound;
    private RadioButton liveButton;
    private ProgressDialog mProgressDialog;
    private int mYear, mMonth, day;
    private final int DATE_PICKER_ID = 0;
    private RadioButton doneButton;
    private RadioButton toPlayButton;
    private Button mUploadButton;
    private ScoreTeam scoreTeam;
    private TextInputLayout mLayout1;
    private TextInputLayout mLayout2;
    private String league = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_match);
        init();
        //Getting the object Match through bundle
        Bundle data = getIntent().getExtras();
        scoreTeam = data.getParcelable(Keys.KEY_OBJECT);
        league = data.getString(Keys.KEY_LEAGUE);
        state = data.getParcelable(Keys.STATE_OBJECT);
        Log.e(TAG, state.toString());

        //Setting up the views to the current data
        updateView(scoreTeam);
        initRadioCircle(state);

        mRadioGroup.setOnCheckedChangeListener(this);
        mDateButton.setOnClickListener(this);
        mUploadButton.setOnClickListener(this);
    }

    private void updateView(ScoreTeam scoreTeam) {
        mDateText.setText(scoreTeam.getDate());
        Picasso.with(EditMatchActivity.this).load(scoreTeam.getTeam_home_logo()).into(logoHome);
        mNameHome.setText(scoreTeam.getTeam_home());
        String score[] = scoreTeam.getFinalScore().split("-");
        mScoreHome.setText(score[0]);
        Picasso.with(EditMatchActivity.this).load(scoreTeam.getTeamAwayLogo()).into(mLogoVisit);
        mNameVisit.setText(scoreTeam.getTeamAway());
        mScoreVisit.setText(score[1]);
        slugRound.setText(scoreTeam.getSlugRound());
        //Store the date variables into the global variables
        String[] dates = scoreTeam.getDate().split("/");
        day = Integer.valueOf(dates[0]);
        mMonth = Integer.valueOf(dates[1]);
        mYear = Integer.valueOf(dates[2]);
    }
    private void init(){
        mDateButton = (Button) findViewById(R.id.date_input);
        mDateText = (TextView) findViewById(R.id.show_date);
        logoHome = (ImageView) findViewById(R.id.homeLogo);
        mNameHome = (TextView) findViewById(R.id.home_team);
        mScoreHome = (EditText) findViewById(R.id.score_local);
        mLogoVisit = (ImageView) findViewById(R.id.awayLogo);
        mNameVisit = (TextView) findViewById(R.id.team_away);
        mScoreVisit = (EditText) findViewById(R.id.score_visitor);
        mRadioGroup = (RadioGroup) findViewById(R.id.circle_group);
        mUploadButton = (Button) findViewById(R.id.upload_button);
        mLayout1 = (TextInputLayout) findViewById(R.id.layout_1);
        mLayout2 = (TextInputLayout) findViewById(R.id.layout_2);
        slugRound = (EditText) findViewById(R.id.input);
        slugLayout = (TextInputLayout) findViewById(R.id.layout_editText);
    }

    private void initRadioCircle(State state){
        liveButton = (RadioButton) findViewById(R.id.live);
        doneButton = (RadioButton) findViewById(R.id.done);
        toPlayButton = (RadioButton) findViewById(R.id.toPlay);

        boolean live = state.getHasStarted();
        boolean done = state.isDone();


        liveButton.setChecked(live);
        doneButton.setChecked(done);
        toPlayButton.setChecked(!live && !done);
    }

    private DatePickerDialog.OnDateSetListener mPickerListener =
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                    mYear = i;
                    mMonth = i1;
                    day = i2;
                    mDateText.setText(day+"/"+mMonth+"/"+mYear);
                }
            };

    @Override
    protected Dialog onCreateDialog(int id) {
        if(id  == DATE_PICKER_ID){
            return new DatePickerDialog(this, mPickerListener, mYear, mMonth, day);
        }
        return null;
    }

    @Override
    public void onClick(View view) {
        int item = view.getId();
        switch (item){
            case R.id.date_input:{
                showDialog(DATE_PICKER_ID);
                break;
            }

            case R.id.upload_button:{
                //upload Data
                uploadData();
                break;
            }
        }
    }

    private void uploadData() {
        String score1 = mScoreHome.getText().toString().trim();
        String score2 = mScoreVisit.getText().toString().trim();
        String roundSlugText = slugRound.getText().toString().trim();

        if(TextUtils.isEmpty(roundSlugText)){
            slugLayout.setError("No puedes dejar este campo vacio");
        }

        if(!TextUtils.isEmpty(score1) && !TextUtils.isEmpty(score2) && !TextUtils.isEmpty(roundSlugText)){
            // they have numbers, time to upload
            Log.e(TAG, league);
            String result = score1+"-"+score2;
            String date = day+"/"+mMonth+"/"+mYear;
            HashMap<String, Object> updateData = new HashMap<>();
            updateData.put("finalScore", result);
            updateData.put("date", date);
            updateData.put("slugRound", roundSlugText);
            updateData.put("state", state);

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference
                    .child("Scores")
                    .child(league)
                    .child(scoreTeam.getKey())
                    .updateChildren(updateData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(EditMatchActivity.this,"Datos actualizados",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
        }
        else{
            // show an error
            mLayout1.setError("llenar");
            mLayout2.setError("llenar");
            Toast.makeText(EditMatchActivity.this, "No puedes dejar los campos vacios", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
        switch (i){
            case R.id.live:{
                state.setHasStarted(true);
                state.setDone(false);
                break;
            }
            case R.id.done:{
                state.setHasStarted(false);
                state.setDone(true);
                break;
            }
            case R.id.toPlay:{
                state.setHasStarted(false);
                state.setDone(false);
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //TODO: create a dialog in order to ask if he really wants to cancel de update
    }
}
