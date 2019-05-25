package app.start.lonewolf.mybudget.Dialogues;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import app.start.lonewolf.mybudget.Budget_History;
import app.start.lonewolf.mybudget.MainActivity;
import app.start.lonewolf.mybudget.R;
import app.start.lonewolf.mybudget.Resources.Settings;

public class View_Budget {

    private static DatabaseReference budgetsRef, userRef;
    private static FirebaseAuth auth;
    private static String userId;
    private static Settings settings;
    private static ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
    private static Double totalRevenue = 0.00, totalExpense = 0.00, totalBalance=0.00;
    private static ProgressBar progressBar;
    private static AlertDialog.Builder alert;
    private static AlertDialog dialog;



    public static void showBudgets(Activity activity, LinearLayout linear){

        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View view = layoutInflater.inflate(R.layout.layout_budgets, linear, false);

        arrayList = new ArrayList<>(1999);
        settings = new Settings(activity);
        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();
        budgetsRef = FirebaseDatabase.getInstance().getReference();
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        LinearLayout linearLayout = view.findViewById(R.id.linHistoryMain);



        //linTotal = findViewById(R.id.linTotal);
//        totalBudgetRevenue = view.findViewById(R.id.txtTotalRevenue);
//        totalBudgetExpense = view.findViewById(R.id.txtTotalExpense);
//        totalBudgetBalance = findViewById(R.id.txtTotalBalance);
        progressBar = view.findViewById(R.id.progressBudgetHistory);

        getBudgets(activity, linearLayout);

        alert = new AlertDialog.Builder(activity);
        dialog = alert.create();
        dialog.setView(view);
        dialog.show();

    }
    private static void getBudgets(final Activity activity, final LinearLayout linearLayout) {

        budgetsRef.child("budgets").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    arrayList.clear();
                    for(DataSnapshot baby : dataSnapshot.child("simple").getChildren()){
                        baby.getRef().orderByChild("date");
                        String budgetName = baby.getKey().toString();
                        String startDate ="";
                        Double localRevTotal = 0.00;
                        Double localExpTotal = 0.00;
                        for(DataSnapshot grandchild : baby.getChildren()) {

                            if(grandchild.child("type").exists()) {

                                if (grandchild.child("type").getValue().toString().equals("Revenue")) {
                                    Double revAmount = Double.parseDouble(grandchild.child("amount").getValue().toString());

                                    localRevTotal = localRevTotal + revAmount;


                                }

                                if (grandchild.child("type").getValue().toString().equals("Expense")) {
                                    Double expAmount = Double.parseDouble(grandchild.child("amount").getValue().toString());

                                    localExpTotal = localExpTotal + expAmount;

                                }

                                if(grandchild.child("item").equals("Start Amount")){
                                    startDate = grandchild.child("date").getValue().toString();
                                }


                            }



                        }


                        Double balance = localRevTotal - localExpTotal;

                        totalRevenue = totalRevenue+localRevTotal;
                        totalExpense = totalExpense+localExpTotal;
                        totalBalance = totalBalance + balance;

                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("name", budgetName);
                        hashMap.put("revenue", String.valueOf(localRevTotal));
                        hashMap.put("expense", String.valueOf(localExpTotal));
                        hashMap.put("remaining", String.valueOf(balance));
                        hashMap.put("date", startDate);

                        arrayList.add(hashMap);
                    }

//                    totalBudgetRevenue.setText(totalRevenue.toString());
//                    totalBudgetExpense.setText(totalExpense.toString());
//                    totalBudgetBalance.setText(totalBalance.toString());
                    //linTotal.setVisibility(View.VISIBLE);

                }
                if(arrayList.size()>0){
                    // Log.d("array", arrayList.toString());
                    linearLayout.removeAllViews();
                    filterData();
                    setBugets(activity, linearLayout);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private static void setBugets(final Activity activity, LinearLayout linearLayout) {

        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        for(int x=0; x<arrayList.size(); x++){
            View view = layoutInflater.inflate(R.layout.layout_history, linearLayout, false);
            TextView bName = view.findViewById(R.id.txtHistoryBudgetName);
            TextView bRevenue = view.findViewById(R.id.txtHistoryRevenue);
            TextView bExpense = view.findViewById(R.id.txtHistoryExpense);
            TextView bRemaining = view.findViewById(R.id.txtHistoryBalance);
            LinearLayout layout = view.findViewById(R.id.linHistoryText);

            final HashMap<String, String> hashMap = arrayList.get(x);

            Double add = Double.parseDouble(hashMap.get("revenue"))+ Double.parseDouble(hashMap.get("expense"));
            bName.setText(hashMap.get("name"));
            bRevenue.setVisibility(View.GONE);
            bExpense.setVisibility(View.GONE);
            bRemaining.setText(String.valueOf(add));

            if(hashMap.get("name").toLowerCase().equals(settings.getCURRENTBUDGET().toLowerCase())){
                layout.setBackgroundResource(R.drawable.active_style);
                layout.requestFocus();
                layout.setFocusable(true);
                layout.setFocusableInTouchMode(true);
            }


//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    settings.setVAR1(hashMap.get("name"));
//                    Budget_History_Details.showHistory(Budget_History.this, linearLayout);
//                }
//            });
//
//            view.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//
//                    settings.setCURRENTBUDGET(hashMap.get("name"));
//                    return false;
//                }
//            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  //  if(!settings.getCURRENTBUDGET().equals(hashMap.get("name"))) {

                        userRef.child("budget_name").setValue(settings.getCURRENTBUDGET()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                settings.setCURRENTBUDGET(hashMap.get("name"));
                                Intent intent = new Intent(activity, MainActivity.class);
                                activity.startActivity(intent);
                                activity.finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });


                   // }

                }
            });


            linearLayout.addView(view);
        }
        progressBar.setVisibility(View.GONE);

    }




    private static void filterData(){
        Collections.sort(arrayList, new Comparator<HashMap<String, String>>() {
            @Override
            public int compare(HashMap<String, String> lhs, HashMap<String, String> rhs) {

                //linearLayout.removeAllViews();

                return lhs.get("date").compareTo(rhs.get("date"));
            }


        });
    }
}
