package app.start.lonewolf.mybudget;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import app.start.lonewolf.mybudget.Resources.Date_Picker;
import app.start.lonewolf.mybudget.Resources.Resource;
import app.start.lonewolf.mybudget.Resources.Settings;

public class Register extends AppCompatActivity {


    private Button register;
    private EditText fname, lname, email, pword, confpword, dob, refCode, startAmount, custom;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private ImageView imgDate;
    private ProgressBar progressBar;
    private Spinner spinner;
    private String currCurrency;
    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
        setContentView(R.layout.activity_register);

        fname = findViewById(R.id.edtRegisterFirstName);
        lname = findViewById(R.id.edtRegisterLastName);
        email = findViewById(R.id.edtRegisterEmail);
        pword = findViewById(R.id.edtRegisterPassword);
        confpword = findViewById(R.id.edtRegisterConfPassword);
        dob = findViewById(R.id.edtRegisterDOB);
        refCode = findViewById(R.id.edtRegisterRefCode);
        startAmount = findViewById(R.id.edtRegisterStartAmount);
        progressBar = findViewById(R.id.progressRegister);
        imgDate = findViewById(R.id.imgDate);
        auth = FirebaseAuth.getInstance();
        spinner= findViewById(R.id.spinCurrrency);
        custom = findViewById(R.id.edtRegisterCustom);
        settings = new Settings(this);

        register = findViewById(R.id.btnRegisterRegister);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        checkFields();
    }

    private void checkFields() {
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(email.getText().toString().isEmpty()) {
                    Toast.makeText(Register.this, "Enter an Email", Toast.LENGTH_SHORT).show();

                }else
                if(!email.getText().toString().contains("@")){
                    Toast.makeText(Register.this, "Enter a valid Email", Toast.LENGTH_SHORT).show();
                }else if(pword.getText().toString().isEmpty()){
                    Toast.makeText(Register.this, "Enter a password", Toast.LENGTH_SHORT).show();
                }else if(pword.getText().toString().length()<8){
                    Toast.makeText(Register.this, "Password should be 8 characters or more", Toast.LENGTH_SHORT).show();
                }else if(!pword.getText().toString().equals(confpword.getText().toString())){
                    Toast.makeText(Register.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
                /*else if(dob.getText().toString().isEmpty()){
                    Toast.makeText(Register.this, "Please Enter your date of birth", Toast.LENGTH_SHORT).show();
                }*/
                else{
                    settings.setPERIOD("Month");
                    progressBar.setVisibility(View.VISIBLE);
                    fname.setEnabled(false);
                    lname.setEnabled(false);
                    email.setEnabled(false);
                    pword.setEnabled(false);
                    confpword.setEnabled(false);
                    dob.setEnabled(false);
                    startAmount.setEnabled(false);
                    refCode.setEnabled(false);

                    registerAccount();
                }
            }
        });

        imgDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date_Picker date_picker = new Date_Picker();
                date_picker.setEditTextDisplay(dob);
                date_picker.show(getFragmentManager(), null);
            }
        });


        List<String> list = new ArrayList<>();


        list.add(0, getString(R.string.ghCurrency) );
        list.add(1, getString(R.string.nigNaira) );
        list.add(2, getString(R.string.usCurrency));
        list.add(3, getString(R.string.ukCurr));
        list.add(4, getString(R.string.euroCurr));
        list.add(5, getString(R.string.cfaFranc));
        list.add(6, getString(R.string.japanCurr));
        list.add(7, getString(R.string.customCurr));
        //list.add(0, "Select Currency");
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.spiner_layout, list);
        arrayAdapter.setDropDownViewResource(R.layout.dropdown_layout);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                    //custom.setText(settings.getCURRENCY());
                    custom.setVisibility(View.VISIBLE);
                    currCurrency=custom.getText().toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




    }

    private void registerAccount() {

        if(spinner.getSelectedItemId()==7){
            currCurrency = custom.getText().toString();
        }
        final String emailAddress = email.getText().toString();
        final String password = pword.getText().toString();
        auth.createUserWithEmailAndPassword(emailAddress, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    final String userId = auth.getCurrentUser().getUid();
                    Toast.makeText(Register.this, userId, Toast.LENGTH_SHORT).show();
                    databaseReference.child("data_identifiers").child("reference_codes").child("refId").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d("jac", dataSnapshot.toString());
                          //  if(dataSnapshot.hasChildren()){
                                try {

                                    final int refId = Integer.parseInt(dataSnapshot.getValue().toString()) + 1;

                                    HashMap<String, String> hashMap = new HashMap<>();
                                    hashMap.put("first_name", fname.getText().toString());
                                    hashMap.put("last_name", lname.getText().toString());
                                    hashMap.put("email", emailAddress);
                                    hashMap.put("password", password);
                                    hashMap.put("id", userId);
                                    hashMap.put("budget_name", "First Budget");

                                    if(dob.getText().toString().isEmpty()){
                                        hashMap.put("dob", "1990-09-09");
                                    }else {
                                        hashMap.put("dob", dob.getText().toString());
                                    }
                                    hashMap.put("reference_code", "BDG-" + refId);
                                    hashMap.put("references", "0");
                                    hashMap.put("date", Resource.getCurrentDate());
                                    //hashMap.put("period", "Month");
                                    //hashMap.put("currency", spinner.getSelectedItem().toString());

                                    databaseReference.child("users").child(userId).setValue(hashMap);
                                    HashMap<String, String> hashMap1 = new HashMap<>();
                                    hashMap1.put("currency", currCurrency);
                                    hashMap1.put("period","Month");
                                    hashMap1.put("currentBudget", "First Budget");

                                    databaseReference.child("settings").child(userId).setValue(hashMap1);


                                //hashRev.put("", "");
                                databaseReference.child("data_identifiers").child("ledger_identifiers").child("ledger_id").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        int ledgerId = Integer.parseInt(dataSnapshot.getValue().toString()) + 1;

                                        HashMap<String, String> hashRev = new HashMap<>();
                                        hashRev.put("item", "Start Amount");

                                        if(startAmount.getText().toString().isEmpty()) {
                                            hashRev.put("amount", "0.00");
                                        }else {
                                            hashRev.put("amount", startAmount.getText().toString());
                                        }
                                        hashRev.put("type", "Revenue");
                                        hashRev.put("id", String.valueOf(ledgerId));
                                        hashRev.put("date", Resource.getCurrentDate());


                                        databaseReference.child("budgets").child(userId).child("simple").child("FIRST BUDGET").child(String.valueOf(ledgerId)).setValue(hashRev);
                                        databaseReference.child("data_identifiers").child("ledger_identifiers").child("ledger_id").setValue(ledgerId);

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        progressBar.setVisibility(View.GONE);
                                        fname.setEnabled(true);
                                        lname.setEnabled(true);
                                        email.setEnabled(true);
                                        pword.setEnabled(true);
                                        confpword.setEnabled(true);
                                        dob.setEnabled(true);
                                        startAmount.setEnabled(true);
                                        refCode.setEnabled(true);
                                        Toast.makeText(Register.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });






                                databaseReference.child("data_identifiers").child("reference_codes").child("refId").setValue(refId);

                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                if(refCode.getText().toString().startsWith("BDG-")){
                                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.hasChildren()){
                                                for(DataSnapshot child : dataSnapshot.getChildren()){

                                                    if(child.child("reference_code").getValue() !=null) {

                                                        if (child.child("reference_code").getValue().toString().equals(refCode.getText().toString())) {
                                                            String references = child.child("references").getValue().toString();
                                                            String personId = child.child("id").getValue().toString();
                                                            int addRef = Integer.parseInt(references) + 1;
                                                            databaseReference.child("users").child(personId).child("references").setValue(addRef).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    settings.setCURRENTBUDGET("FIRST BUDGET");
                                                                    progressBar.setVisibility(View.GONE);
                                                                    fname.setEnabled(true);
                                                                    lname.setEnabled(true);
                                                                    email.setEnabled(true);
                                                                    pword.setEnabled(true);
                                                                    confpword.setEnabled(true);
                                                                    dob.setEnabled(true);
                                                                    startAmount.setEnabled(true);
                                                                    refCode.setEnabled(true);
                                                                    Intent intent = new Intent(Register.this, MainActivity.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    progressBar.setVisibility(View.GONE);
                                                                    fname.setEnabled(true);
                                                                    lname.setEnabled(true);
                                                                    email.setEnabled(true);
                                                                    pword.setEnabled(true);
                                                                    confpword.setEnabled(true);
                                                                    dob.setEnabled(true);
                                                                    startAmount.setEnabled(true);
                                                                    refCode.setEnabled(true);
                                                                    Toast.makeText(Register.this, e.toString(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });

                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    }); ;
                                }else{
                                    settings.setCURRENTBUDGET("FIRST BUDGET");
                                    progressBar.setVisibility(View.GONE);
                                    fname.setEnabled(true);
                                    lname.setEnabled(true);
                                    email.setEnabled(true);
                                    pword.setEnabled(true);
                                    confpword.setEnabled(true);
                                    dob.setEnabled(true);
                                    startAmount.setEnabled(true);
                                    refCode.setEnabled(true);
                                    Intent intent = new Intent(Register.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                           // }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            fname.setEnabled(true);
                            lname.setEnabled(true);
                            email.setEnabled(true);
                            pword.setEnabled(true);
                            confpword.setEnabled(true);
                            dob.setEnabled(true);
                            startAmount.setEnabled(true);
                            refCode.setEnabled(true);
                            progressBar.setVisibility(View.GONE);
                        }
                    });


                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Register.this, e.toString(), Toast.LENGTH_SHORT).show();
                fname.setEnabled(true);
                lname.setEnabled(true);
                email.setEnabled(true);
                pword.setEnabled(true);
                confpword.setEnabled(true);
                dob.setEnabled(true);
                startAmount.setEnabled(true);
                refCode.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                e.printStackTrace();
            }
        });
    }
}
