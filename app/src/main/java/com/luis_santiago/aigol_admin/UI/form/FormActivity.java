package com.luis_santiago.aigol_admin.UI.form;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import java.text.DecimalFormat;
import java.util.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.luis_santiago.aigol_admin.R;
import com.luis_santiago.aigol_admin.tools.data.ScoreTeam;
import com.luis_santiago.aigol_admin.tools.data.State;
import com.luis_santiago.aigol_admin.tools.utils.Keys;

import static android.media.CamcorderProfile.get;
import static com.luis_santiago.aigol_admin.R.id.slug_round;
import static com.luis_santiago.aigol_admin.R.id.spinner;
import static com.luis_santiago.aigol_admin.R.id.spinner_2;

public class FormActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        View.OnClickListener{


    private final int DATE_PICKER_ID = 0;
    private Spinner mLocalTeam,mVisitTeam;
    private TextView mShowDate;
    private String TAG = FormActivity.class.getSimpleName();
    private Button uploadData, datePicker;
    private EditText slugRound;
    private ArrayList<String> teamsOfLeague;
    private String mLeagueName;
    private TextInputLayout textInputLayout;
    private ArrayAdapter<String> mAdapter;
    private ProgressDialog mProgressDialog;
    private String mAnswerfromVisitTeam, mAnswerfromLocalTeam;
    int mYear, mMonth, day;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        init();
        final android.icu.util.Calendar cal = Calendar.getInstance();
        // Setting the current date
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);
        /* Getting the league we are in by a bundle object*/
        Bundle bundle = getIntent().getExtras();
        //Extracting the bundle
        mLeagueName = bundle.getString(Keys.KEY_LEAGUE);

        teamsOfLeague = new ArrayList<>();

        mAdapter = new ArrayAdapter<>(
                this,R.layout.spinner_layout,
                R.id.text1,
                teamsOfLeague);

        mLocalTeam.setOnItemSelectedListener(this);
        mVisitTeam.setOnItemSelectedListener(this);

        //Click Listener
        uploadData.setOnClickListener(this);
        datePicker.setOnClickListener(this);

        mLocalTeam.setAdapter(mAdapter);
        mVisitTeam.setAdapter(mAdapter);
        fetchData(mLeagueName);
    }

    private void fetchData (String league){
        /* Return an array list of String of all the teams in the mLeague*/
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("Standings").child(league);
        Query query = databaseReference.orderByChild("name");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot mDataSnapshot: dataSnapshot.getChildren()){
                    String name = (String) mDataSnapshot.child("name").getValue();
                    teamsOfLeague.add(name);
                }
                mAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void init(){
        mLocalTeam = (Spinner) findViewById(spinner);
        mVisitTeam = (Spinner) findViewById(R.id.spinner_2);
        uploadData = (Button) findViewById(R.id.upload_button);
        datePicker = (Button) findViewById(R.id.date_input);
        mShowDate = (TextView) findViewById(R.id.show_date);
        slugRound = (EditText) findViewById(R.id.input);
        textInputLayout = (TextInputLayout) findViewById(R.id.layout_editText);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Spinner spinner = (Spinner) adapterView;
        switch (spinner.getId()){
            case R.id.spinner:{
                mAnswerfromLocalTeam = teamsOfLeague.get(i);
                break;
            }
            case R.id.spinner_2: {
                mAnswerfromVisitTeam = teamsOfLeague.get(i);
                break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.upload_button:{
                verifyData();
                break;
            }
            case R.id.date_input:{
                showDialog(DATE_PICKER_ID);
                break;
            }
        }
    }
    private void verifyData() {
        String roundSlugText = slugRound.getText().toString().trim();
        // Verify if the other data is filled up;
        if(TextUtils.isEmpty(roundSlugText)){
            textInputLayout.setError("No puedes dejar este campo vacio");
        }
        if(mAnswerfromLocalTeam.equals(mAnswerfromVisitTeam)) {
            //Something is wrong
            Toast.makeText(FormActivity.this, "No puedes tener dos equipos iguales",
                    Toast.LENGTH_SHORT).show();
        }
        if(!mAnswerfromLocalTeam.equals(mAnswerfromVisitTeam) && !TextUtils.isEmpty(roundSlugText)){
            if(Integer.valueOf(roundSlugText)<40){
                mProgressDialog = ProgressDialog.show(
                        FormActivity.this,
                        "Subiendo",
                        "Espere, Por favor!",
                        true,
                        false);
                String date  = String.valueOf(day)+"/"+String.valueOf(mMonth)+"/"+String.valueOf(mYear);
                findLogos(date, mAnswerfromVisitTeam, mAnswerfromLocalTeam, roundSlugText);
            }
        }
    }


    private void findLogos(final String dates, final String visit, final String local,
                           final String roundSlug){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("Standings")
                .child(mLeagueName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            String logo_local;
            String logo_visit;
            Boolean foundLocalLogo = false;
            Boolean foundVisitLogo = false;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot mDataSnapshot: dataSnapshot.getChildren()){
                    String name = (String) mDataSnapshot.child("name").getValue();
                    String logo = (String) mDataSnapshot.child("team_logo").getValue();
                    if(name.equals(local)){
                        // set the logo for the equal team;
                        logo_local = logo;
                        foundLocalLogo = true;
                    }
                    if(name.equals(visit)){
                        // set the logo for the visit team
                        logo_visit = logo;
                        foundVisitLogo = true;
                    }
                    if(foundLocalLogo && foundVisitLogo){
                        String logos[] = {logo_local, logo_visit};
                        publishData(dates,roundSlug, logos,local, visit);
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void publishData(final String dates, final String roundSlug, String[] logos,
                             String nameLocal, String visit) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference firebase = database.child("Scores").child(mLeagueName).push();

        String key = firebase.getKey();
        ScoreTeam scoreTeam = new ScoreTeam(
                dates,
                new State(false,false), //has started : is done:if both are false it means it still hasn't started
                key,
                roundSlug,
                "0-0",
                visit,
                logos[1],
                nameLocal,
                logos[0]);
        
        Log.e(TAG, "THE KEY IS"+ key);
        firebase.setValue(scoreTeam).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e(TAG, "Termine de subir datos");
                mProgressDialog.dismiss();
                Toast.makeText(FormActivity.this, "Datos subidos", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if(id  == DATE_PICKER_ID){
            return new DatePickerDialog(this, mPickerListener, mYear, mMonth, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener mPickerListener =
            new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            mYear = i;
            mMonth = i1;
            day = i2;
            mShowDate.setText(day+"/"+mMonth+"/"+mYear);
        }
    };
}
