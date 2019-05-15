package app.start.lonewolf.mybudget.Dialogues;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.zip.Inflater;

import app.start.lonewolf.mybudget.MainActivity;
import app.start.lonewolf.mybudget.R;
import app.start.lonewolf.mybudget.Register;
import app.start.lonewolf.mybudget.Resources.Resource;
import app.start.lonewolf.mybudget.Resources.Settings;

public class SecondaryLogin {
    private static FirebaseAuth auth;
    private static DatabaseReference databaseReference;
    private static String userId;
    private static AlertDialog.Builder alertDialog;
    private static String  refCode="";
    private static Settings settings;
    private static String selectedCurrency ="", selectedAmount = "0";

    public static void showLogin(Activity activity, LinearLayout linearLayout){

        settings = new Settings(activity);
        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();


        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.dialogue_secondary_setup, linearLayout, false);
        ProgressBar progressBar = view.findViewById(R.id.progresSecondaryReg);
        alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setView(view);
        alertDialog.show();
        progressBar.setVisibility(View.GONE);
        if(settings.getPERIOD().equals("")) {
            settings.setPERIOD("Month");

        }

        //setupUser(activity, progressBar);
        getButtons(activity, progressBar, view);
    }

    private static void getButtons(final Activity activity, final ProgressBar progressBar, View view) {

        final Button submit = view.findViewById(R.id.btnSubmitSecondary);
        RadioGroup radioGroup = view.findViewById(R.id.radioSecMain);
        EditText edtAmount = view.findViewById(R.id.edtSecondAmount);
        EditText edtRefId = view.findViewById(R.id.edtSecondRefId);
        final LinearLayout linCurrency = view.findViewById(R.id.linRadioSetup);
        final LinearLayout linAmount = view.findViewById(R.id.linAmountSetup);
        final LinearLayout linRefId = view.findViewById(R.id.linRefIdSetup);


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if(checkedId==R.id.radioGH){
                    selectedCurrency ="GH₵";
                }else if(checkedId==R.id.radioNig){
                    selectedCurrency ="₦";
                }else if(checkedId==R.id.radioBrit){
                    selectedCurrency ="£";
                }else if(checkedId==R.id.radioEuro){
                    selectedCurrency ="€";
                }else if(checkedId==R.id.radioCfa){
                    selectedCurrency ="CFA";
                }else if(checkedId==R.id.radioJap){
                    selectedCurrency ="¥";
                }else if(checkedId==R.id.radioUs){
                    selectedCurrency ="$";
                }
            }
        });

        edtAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                    selectedAmount = s.toString();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(submit.getText().toString().equals(activity.getString(R.string.submitCurr))){
                    if(selectedCurrency.equals("")){
                        Toast.makeText(activity, "Select a currency", Toast.LENGTH_SHORT).show();
                    }else {
                        linAmount.setVisibility(View.VISIBLE);
                        linCurrency.setVisibility(View.GONE);
                        submit.setText(activity.getString(R.string.submitAmount));
                    }
                } else if(submit.getText().toString().equals(activity.getString(R.string.submitAmount))){
                    if(selectedAmount.trim().equals("")){
                        selectedAmount = "0.00";
                    }
                    linAmount.setVisibility(View.GONE);
                    linRefId.setVisibility(View.VISIBLE);
                    submit.setText(activity.getString(R.string.skip));
                }else if(submit.getText().toString().equals(activity.getString(R.string.submitRef)) || submit.getText().toString().equals(activity.getString(R.string.skip))){
                    progressBar.setVisibility(View.VISIBLE);
                    setupUser(activity, progressBar);
                }
            }
        });


        edtRefId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                submit.setText(activity.getString(R.string.submitRef));
                refCode = s.toString();
            }
        });
    }

    private static void setupUser(final Activity activity, final ProgressBar progressBar) {

        databaseReference.child("data_identifiers").child("reference_codes").child("refId").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("jac", dataSnapshot.toString());
                //  if(dataSnapshot.hasChildren()){
                try {

                    final int refId = Integer.parseInt(dataSnapshot.getValue().toString()) + 1;

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("first_name", auth.getCurrentUser().getDisplayName());
                    hashMap.put("last_name", "");
                    hashMap.put("email", auth.getCurrentUser().getEmail());
                    hashMap.put("password", "");
                    hashMap.put("id", userId);
                    hashMap.put("budget_name", "FIRST BUDGET");
                    hashMap.put("dob", "1990-09-09");
                    hashMap.put("reference_code", "BDG-" + refId);
                    hashMap.put("references", "0");
                    hashMap.put("date", Resource.getCurrentDate());

                    databaseReference.child("users").child(userId).setValue(hashMap);
                    HashMap<String, String> hashMap1 = new HashMap<>();
                    hashMap1.put("currency", selectedCurrency);
                    hashMap1.put("period","Month");

                    databaseReference.child("settings").child(userId).setValue(hashMap1);


                    //hashRev.put("", "");
                    databaseReference.child("data_identifiers").child("ledger_identifiers").child("ledger_id").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int ledgerId = Integer.parseInt(dataSnapshot.getValue().toString()) + 1;

                            HashMap<String, String> hashRev = new HashMap<>();
                            hashRev.put("item", "Start Amount");

                            if(selectedAmount.isEmpty()) {
                                hashRev.put("amount", "0.00");
                            }else {
                                hashRev.put("amount", selectedAmount);
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

                            Toast.makeText(activity, databaseError.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });






                    databaseReference.child("data_identifiers").child("reference_codes").child("refId").setValue(refId);

                }catch (Exception e){
                    e.printStackTrace();
                }
                if(refCode.startsWith("BDG-")){
                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChildren()){
                                for(DataSnapshot child : dataSnapshot.getChildren()){

                                    if(child.child("reference_code").getValue() !=null) {
                                        Log.d("toga", child.child("reference_code").getValue().toString());
                                        if (child.child("reference_code").getValue().toString().equals(refCode)) {
                                            String references = child.child("references").getValue().toString();
                                            String personId = child.child("id").getValue().toString();
                                            int addRef = Integer.parseInt(references) + 1;
                                            databaseReference.child("users").child(personId).child("references").setValue(addRef).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    settings.setCURRENTBUDGET("FIRST BUDGET");
                                                    progressBar.setVisibility(View.GONE);

                                                    Intent intent = new Intent(activity, MainActivity.class);
                                                    activity.startActivity(intent);
                                                    activity.finish();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {


                                                    Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT).show();
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

                    Intent intent = new Intent(activity, MainActivity.class);
                    activity.startActivity(intent);
                    activity.finish();
                }
                // }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
