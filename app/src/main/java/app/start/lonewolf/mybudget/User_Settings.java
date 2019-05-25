package app.start.lonewolf.mybudget;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.start.lonewolf.mybudget.Resources.Date_Picker;
import app.start.lonewolf.mybudget.Resources.Resource;
import app.start.lonewolf.mybudget.Resources.Settings;

public class User_Settings extends AppCompatActivity {

    private Spinner spinPeriod, spinCurrency;
    private Settings settings;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private String userId, currPeriod, currCurrency, prevCurrency, prevPeriod;
    private EditText custom, edtDate;
    private Button submit;
    private LinearLayout mainLin, dateLin;
    private ProgressBar progressBar;
    private ImageView imgDate;
    private TextView customDate, refinfo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_user__settings);

        settings = new Settings(this);
        spinCurrency = findViewById(R.id.spinSettingCurrency);
        spinPeriod = findViewById(R.id.spinSettingPeriod);
        custom = findViewById(R.id.edtSettingCurr);
        submit = findViewById(R.id.btnSettingsSubmit);
        mainLin = findViewById(R.id.linSettings);
        imgDate = findViewById(R.id.imgDate);
        customDate = findViewById(R.id.txtSettingsDateText);
        refinfo = findViewById(R.id.txtRefInfo);
        edtDate = findViewById(R.id.edtAddDate);
        dateLin = findViewById(R.id.linCustomDate);
        progressBar = findViewById(R.id.progressSettings);
        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("settings").child(userId);


        fillSettings();

        refinfo.setText("REFERENCE CODE : "+settings.getREFERENCECODE()+"\nNo. OF REFERENCES : "+settings.getREFERENCES());
       // getCurrentSettings();
    }

    private void fillSettings() {
        customDate.setText(Resource.getCurrentDateFormat2());

        prevCurrency = settings.getCURRENCY();
        prevPeriod = settings.getPERIOD();

        List<String> list = new ArrayList<>();

        list.add(0, getString(R.string.ghCurrency) );
        list.add(1, getString(R.string.nigNaira) );
        list.add(2, getString(R.string.usCurrency));
        list.add(3, getString(R.string.ukCurr));
        list.add(4, getString(R.string.euroCurr));
        list.add(5, getString(R.string.cfaFranc));
        list.add(6, getString(R.string.japanCurr));
        list.add(7, getString(R.string.customCurr));

        ArrayAdapter arrayAdapterCurrency = new ArrayAdapter(this, R.layout.spiner_layout, list);
        arrayAdapterCurrency.setDropDownViewResource(R.layout.dropdown_layout);
        spinCurrency.setAdapter(arrayAdapterCurrency);
        spinCurrency.setVisibility(View.VISIBLE);

        if(settings.getCURRENCY().equals("GH₵")){
            spinCurrency.setSelection(0);
            custom.setVisibility(View.GONE);
            currCurrency="GH₵";

        }else if(settings.getCURRENCY().equals("₦")){
            spinCurrency.setSelection(1);
            custom.setVisibility(View.GONE);
            currCurrency="₦";

        }else if(settings.getCURRENCY().equals("$")){
            spinCurrency.setSelection(2);
            custom.setVisibility(View.GONE);
            currCurrency="$";

        }else if(settings.getCURRENCY().equals("£")){
            spinCurrency.setSelection(3);
            custom.setVisibility(View.GONE);
            currCurrency="£";

        }else if(settings.getCURRENCY().equals("€")){
            spinCurrency.setSelection(4);
            custom.setVisibility(View.GONE);
            currCurrency = "€";

        }else if(settings.getCURRENCY().equals("CFA")){
            spinCurrency.setSelection(5);
            custom.setVisibility(View.GONE);
            currCurrency="CFA";

        }else if(settings.getCURRENCY().equals("¥")){
            spinCurrency.setSelection(6);
            custom.setVisibility(View.GONE);
            currCurrency="¥";

        }else {
            spinCurrency.setSelection(7);
            custom.setText(settings.getCURRENCY());
            custom.setVisibility(View.VISIBLE);
            currCurrency=custom.getText().toString();

        }


        List<String> listPeriod = new ArrayList<>();
        listPeriod.add(0, getString(R.string.day));
        listPeriod.add(1, getString(R.string.month));
        listPeriod.add(2, getString(R.string.year));
        listPeriod.add(3, "Custom");

        ArrayAdapter arrayAdapterPeriod = new ArrayAdapter(this, R.layout.spiner_layout, listPeriod);
        arrayAdapterPeriod.setDropDownViewResource(R.layout.dropdown_layout);
        spinPeriod.setAdapter(arrayAdapterPeriod);
        spinPeriod.setVisibility(View.VISIBLE);

        if(settings.getPERIOD().equals(getString(R.string.day))){
            spinPeriod.setSelection(0);
            currPeriod = getString(R.string.day);

        }else if(settings.getPERIOD().equals(getString(R.string.month))){
            spinPeriod.setSelection(1);
            currPeriod = getString(R.string.month);

        }else if(settings.getPERIOD().equals(getString(R.string.year))){
            spinPeriod.setSelection(2);
            currPeriod = getString(R.string.year);

        }else if(settings.getPERIOD().equals("Custom")){

        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spinCurrency.getSelectedItemId()==7){
                    currCurrency=custom.getText().toString();
                }


                if(spinCurrency.getSelectedItemId()==7 && custom.getText().toString().isEmpty()){
                    Toast.makeText(User_Settings.this, "Enter your Custom Currency", Toast.LENGTH_SHORT).show();

                }else if(currCurrency.equals(prevCurrency) && currPeriod.equals(prevPeriod) && spinPeriod.getSelectedItemId()!=3){
                    Intent intent = new Intent(User_Settings.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(User_Settings.this, "No Changes made", Toast.LENGTH_SHORT).show();
                }else{
                    mainLin.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);
                    setSettings();

                }
            }
        });//₦

        spinCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    custom.setVisibility(View.GONE);
                    currCurrency="GH₵";
                }else if(position==1){
                    custom.setVisibility(View.GONE);
                    currCurrency="₦";

                }else if(position==2){
                    custom.setVisibility(View.GONE);
                    currCurrency="$";

                }else if(position==3){
                    custom.setVisibility(View.GONE);
                    currCurrency="£";

                }else if(position==4){
                    custom.setVisibility(View.GONE);
                    currCurrency = "€";

                }else if(position==5){
                    custom.setVisibility(View.GONE);
                    currCurrency="CFA";

                }else if(position==6){
                    custom.setVisibility(View.GONE);
                    currCurrency="¥";

                }else if(position==7){
                    custom.setText(settings.getCURRENCY());
                    custom.setVisibility(View.VISIBLE);
                    currCurrency=custom.getText().toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinPeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    currPeriod = getString(R.string.day);
                    settings.setCUSTOMDATEINDICATOR(false);
                    dateLin.setVisibility(View.GONE);
                }else if(position==1){
                    settings.setCUSTOMDATEINDICATOR(false);
                    currPeriod = getString(R.string.month);
                    dateLin.setVisibility(View.GONE);
                }else if(position==2){
                    settings.setCUSTOMDATEINDICATOR(false);
                    currPeriod = getString(R.string.year);
                    dateLin.setVisibility(View.GONE);
                }else if(position==3){
                    currPeriod = "Day";
                    dateLin.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                currCurrency=custom.getText().toString();
            }
        });

//        custom.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//
//                return false;
//            }
//        });

        imgDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date_Picker date_picker = new Date_Picker();
                date_picker.setEditTextDisplay(edtDate);
                date_picker.show(getFragmentManager(), null);


            }
        });

        edtDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newDate = edtDate.getText().toString();
                customDate.setText(newDate);
                settings.setCUSTOMDATE(Resource.getFormatDateAPI(newDate));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }



    private void setSettings() {

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("currency", currCurrency);
        hashMap.put("period", currPeriod);

        databaseReference.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                settings.setCURRENCY(currCurrency);
                settings.setPERIOD(currPeriod);
                if(spinPeriod.getSelectedItemId()==3) {
                    settings.setCUSTOMDATEINDICATOR(true);
                }
                mainLin.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(User_Settings.this, "Successfully Updated", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(User_Settings.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mainLin.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(User_Settings.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(User_Settings.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
