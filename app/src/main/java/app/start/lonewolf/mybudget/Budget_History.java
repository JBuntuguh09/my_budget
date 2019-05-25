package app.start.lonewolf.mybudget;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
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

import app.start.lonewolf.mybudget.Dialogues.Budget_History_Details;
import app.start.lonewolf.mybudget.Dialogues.Copy_Budget;
import app.start.lonewolf.mybudget.Resources.Resource;
import app.start.lonewolf.mybudget.Resources.Settings;

public class Budget_History extends AppCompatActivity {

    private BarChart barChart;
    private DatabaseReference budgetsRef, userRef;
    private FirebaseAuth auth;
    private Settings settings;
    private LinearLayout linearLayout, linTotal;
    private ArrayList<HashMap<String, String>> arrayList;
    private String userId;
    private Double totalRevenue = 0.00, totalExpense = 0.00, totalBalance=0.00;
    private ArrayList<String> arrayLabel = new ArrayList<>();
    private TextView totalBudgetRevenue, totalBudgetExpense, totalBudgetBalance;
    private ProgressBar progressBar;
    //private

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_budget_history2);


        arrayList = new ArrayList<>(1999);
        settings = new Settings(this);
        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();
        budgetsRef = FirebaseDatabase.getInstance().getReference();
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        linearLayout = findViewById(R.id.linHistoryMain);
        barChart = findViewById(R.id.barHistory);

        linTotal = findViewById(R.id.linTotal);
        totalBudgetRevenue = findViewById(R.id.txtTotalRevenue);
        totalBudgetExpense = findViewById(R.id.txtTotalExpense);
        totalBudgetBalance = findViewById(R.id.txtTotalBalance);
        progressBar =findViewById(R.id.progressBudgetHistory);



        try {
            getBudgets();
        }catch (Exception e){
            e.printStackTrace();
        }
        budgetsRef.orderByChild("date");
    }

    private void getBudgets() {

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

                    totalBudgetRevenue.setText(totalRevenue.toString());
                    totalBudgetExpense.setText(totalExpense.toString());
                    totalBudgetBalance.setText(totalBalance.toString());
                    linTotal.setVisibility(View.VISIBLE);

                }
                if(arrayList.size()>0){
                   // Log.d("array", arrayList.toString());
                    linearLayout.removeAllViews();
                    filterData();
                    setBugets();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setBugets() {

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        for(int x=0; x<arrayList.size(); x++){
            View view = layoutInflater.inflate(R.layout.layout_history, linearLayout, false);
            TextView bName = view.findViewById(R.id.txtHistoryBudgetName);
            TextView bRevenue = view.findViewById(R.id.txtHistoryRevenue);
            TextView bExpense = view.findViewById(R.id.txtHistoryExpense);
            TextView bRemaining = view.findViewById(R.id.txtHistoryBalance);
            LinearLayout layout = view.findViewById(R.id.linHistoryText);

            final HashMap<String, String> hashMap = arrayList.get(x);
            bName.setText(hashMap.get("name"));
            bRevenue.setText(hashMap.get("revenue"));
            bExpense.setText(hashMap.get("expense"));
            bRemaining.setText(hashMap.get("remaining"));

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
                    AlertDialog.Builder alert = new AlertDialog.Builder(Budget_History.this);
                    //alert.setTitle("Alert");
                    settings.setVAR1(hashMap.get("name"));

                    LayoutInflater layoutInflater1 = LayoutInflater.from(Budget_History.this );
                    View view1 = layoutInflater1.inflate(R.layout.layout_budget_view, linearLayout, false);
                    Button switchBudget = view1.findViewById(R.id.btnSwitchBudget);
                    Button detail = view1.findViewById(R.id.btnViewBudgetDetails);
                    Button copy = view1.findViewById(R.id.btnCopyBudget);

                    switchBudget.setVisibility(View.GONE);

                    if(!settings.getCURRENTBUDGET().equals(hashMap.get("name"))) {
                        switchBudget.setVisibility(View.VISIBLE);

                        switchBudget.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                userRef.child("budget_name").setValue(settings.getCURRENTBUDGET()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        settings.setCURRENTBUDGET(hashMap.get("name"));
                                        Intent intent = new Intent(Budget_History.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Budget_History.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });

                            }
                        });

                    }


                    detail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Budget_History_Details.showHistory(Budget_History.this, linearLayout);
                        }
                    });
                    copy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Copy_Budget.copyBudget(Budget_History.this, linearLayout);
                        }
                    });

                    alert.setView(view1);
                    alert.show();

                }
            });


            linearLayout.addView(view);
        }
        progressBar.setVisibility(View.GONE);
        if(arrayList.size()>0) {
            Log.d("ghTotal", totalExpense +" gh "+totalRevenue);
            setChart();
        }
    }

    private void setChart() {
        ArrayList<BarEntry> arrayBar = new ArrayList<>();

        //mValues= new String[]{"dsda", "sadas", "dsdas", "dsadasd", "fsfsf", "fdsf", "sdfsf"};
        //
        for (int y = 0; y < arrayList.size(); y++) {
            //mValues = new String[0];
            HashMap<String, String> hashBar = arrayList.get(y);
            arrayBar.add(y, new BarEntry(y, Float.parseFloat(hashBar.get("remaining"))));
            arrayLabel.add(y, hashBar.get("name"));

            //mValues[y] = hashBar.get("name");

        }

        BarDataSet barData = new BarDataSet(arrayBar, "Budgets");

        BarData barData1 = new BarData(barData);
        //barData1.setBarWidth(0.9f);

        barChart.setData(barData1);
        barChart.getDescription().setText("");
        barChart.animateXY(1000, 1000);
        barChart.getAxisRight().setEnabled(false);




        String[] mValues = new String[arrayList.size()];

        for (int y = 0; y < arrayList.size(); y++) {
            //mValues = new String[0];
            HashMap<String, String> hashBar = arrayList.get(y);
            //Log.d("mn", mValues[y]);
            mValues[y] = hashBar.get("name");

        }
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new myXaxisFormatter(mValues));
        //xAxis.setAxisMinimum(1);
        xAxis.setGranularity(1);
        xAxis.setGridColor(Color.WHITE);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        //barChart.set
    }


    public class myXaxisFormatter implements IAxisValueFormatter{

        String[] mValues2 = new String[arrayList.size()];
        public myXaxisFormatter(String[] values) {
            mValues2=new String[arrayList.size()];
            mValues2=values;
        }

        public myXaxisFormatter(ArrayList<String> arrayLabel2) {
            arrayLabel=arrayLabel2;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            int x;
            for( x=0; x<arrayList.size(); x++) {

                    String[] a = String.valueOf(value).split(".");

                }
            return mValues2[(int)(value)];

        }
    }


    private void filterData(){
        Collections.sort(arrayList, new Comparator<HashMap<String, String>>() {
            @Override
            public int compare(HashMap<String, String> lhs, HashMap<String, String> rhs) {

                //linearLayout.removeAllViews();

                return lhs.get("date").compareTo(rhs.get("date"));
            }


        });
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Budget_History.this, MainActivity.class);
        startActivity(intent);
        finish();
        //super.onBackPressed();
    }
}
