package app.start.lonewolf.mybudget.Dialogues;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

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

import app.start.lonewolf.mybudget.Budget_History;
import app.start.lonewolf.mybudget.MainActivity;
import app.start.lonewolf.mybudget.R;
import app.start.lonewolf.mybudget.Resources.Date_Picker;
import app.start.lonewolf.mybudget.Resources.Resource;
import app.start.lonewolf.mybudget.Resources.Settings;

public class Copy_Budget {

    private static DatabaseReference databaseReference, budgetRef;
    private static FirebaseAuth auth;
    private static String userId ;
    private static Settings settings;
    private static AlertDialog.Builder alert;
    private static ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

    public Copy_Budget(){

    }

    public static void copyBudget(final Activity activity, LinearLayout linearLayout){
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View view = layoutInflater.inflate(R.layout.layout_item_add, linearLayout, false);

        settings = new Settings(activity);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        budgetRef = FirebaseDatabase.getInstance().getReference().child("budgets").child(userId).child("simple");


        alert = new AlertDialog.Builder(activity);

        LinearLayout hideElement = view.findViewById(R.id.linAmountMain);
        final EditText budgetName = view.findViewById(R.id.edtChangeItem);
        final EditText budgetDate = view.findViewById(R.id.edtAddDate);
        final ImageView dateSel = view .findViewById(R.id.imgDate);
        final Button submit = view.findViewById(R.id.btnChangeSubmit);
        final ProgressBar progressBar = view.findViewById(R.id.progressChange);

        hideElement.setVisibility(View.GONE);
        budgetName.setHint(R.string.newBudgetName);
        budgetDate.setText(Resource.getCurrentDateFormat2());

        dateSel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date_Picker date_picker = new Date_Picker();
                date_picker.setEditTextDisplay(budgetDate);
                date_picker.show(activity.getFragmentManager(), null);
            }
        });



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                budgetName.setEnabled(false);
                submit.setEnabled(false);
                dateSel.setEnabled(false);
                budgetDate.setEnabled(false);

                progressBar.setVisibility(View.VISIBLE);


                budgetRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        List list = new ArrayList();
                        for(DataSnapshot baby:dataSnapshot.getChildren()){

                            list.add(baby.getKey().toString());

                        }

                        if(list.contains(budgetDate.getText().toString())){
                            Toast.makeText(activity, "This Budget name has already been use. Please enter a different budget name", Toast.LENGTH_SHORT).show();
                            budgetName.setEnabled(true);
                            submit.setEnabled(true);
                            dateSel.setEnabled(true);
                            budgetDate.setEnabled(true);

                            progressBar.setVisibility(View.GONE);
                        }else{
                            setCopy(budgetName, budgetDate, submit, activity);



                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        alert.setView(view);
        alert.show();


    }

    private static void setCopy(final EditText budgetName, final EditText budgetDate, Button submit, final Activity activity) {
        Log.d("hero", settings.getVAR1());
        arrayList.clear();
        databaseReference.child("budgets").child(userId).child("simple").child(settings.getVAR1()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    HashMap<String, String> hashMap = new HashMap<>();

//                    String itemPeriod = dataSnapshot1.child("date").getValue().toString();
//                    String iPeriod[] = itemPeriod.split("-");


                    if(!dataSnapshot1.child("amount").getValue().toString().equals("0.00")  ) {
                        hashMap.put("item", dataSnapshot1.child("item").getValue().toString());
                        hashMap.put("amount", dataSnapshot1.child("amount").getValue().toString());
                        hashMap.put("id", dataSnapshot1.child("id").getValue().toString());
                        hashMap.put("type", dataSnapshot1.child("type").getValue().toString());
                        hashMap.put("date", Resource.getFormatDateAPI(budgetDate.getText().toString()));


                        arrayList.add(hashMap);

                    }


                    if(arrayList.size()>0){
                        databaseReference.child("budgets").child(userId).child("simple").child(budgetName.getText().toString()).setValue(arrayList).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                settings.setCURRENTBUDGET(budgetName.getText().toString());

                                Intent intent = new Intent(activity, Budget_History.class);
                                activity.startActivity(intent);
                                activity.finish();
                            }
                        });

                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
