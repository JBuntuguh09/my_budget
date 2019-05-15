package app.start.lonewolf.mybudget;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.start.lonewolf.mybudget.Resources.Date_Picker;
import app.start.lonewolf.mybudget.Resources.MyColors;
import app.start.lonewolf.mybudget.Resources.Resource;
import app.start.lonewolf.mybudget.Resources.Settings;

public class Setup_Budget extends AppCompatActivity {

    private DatabaseReference budgetNumref, budgetRef, budgetId;
    private FirebaseAuth auth;
    private String userId;
    private ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
    private LinearLayout linearRevenue, linearExpense;
    private ProgressBar progressBar;
    private String eventTitle;
    private Toolbar toolbar;
    private TextView title;
    private  LinearLayout linearLayout, linearRev, linearExp;
    private Button addRev, addExp;
    private String periondChecker = "empty", alterTotal="0";
    private Settings settings;
    private PieChart pieChart;
    String arrayExpenseString[]= new String[10000];
    float arrayExpenseFloat[] = new float[10000];

    String arrayRevenueString[]= new String[10000];
    float arrayRevenueFloat[] = new float[10000];
    int z=0;
    int q=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_setup__budget);

        settings = new Settings(this);
        title = findViewById(R.id.txtTitle);

        if(settings.getTYPE().equals("Revenue")){
            title.setText("REVENUE");
        }else if(settings.getTYPE().equals("Expense")){
            title.setText("EXPENSES");
        }
//        toolbar = findViewById(R.id.tabDetails);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle(settings.getTYPE());
        auth = FirebaseAuth.getInstance();
        budgetNumref = FirebaseDatabase.getInstance().getReference().child("data_identifiers");
        userId = auth.getCurrentUser().getUid();
        budgetRef = FirebaseDatabase.getInstance().getReference().child("budgets").child(userId).child("simple").child(settings.getCURRENTBUDGET());
        budgetId = FirebaseDatabase.getInstance().getReference().child("data_identifiers").child("ledger_identifiers").child("ledger_id");
        linearRevenue = findViewById(R.id.linRevnueList);
        linearExpense = findViewById(R.id.linExpenseList);

        linearExp = findViewById(R.id.linearExpenseDetails);
        linearRev = findViewById(R.id.linearRevenueDetails);
        addRev = findViewById(R.id.btnDetAddRev);
        addExp = findViewById(R.id.btnDetAddExp);
        pieChart = findViewById(R.id.pieDetails);



        configureSettings();
        getBudgetList();
        getButtons();
    }

    private void getButtons() {
        addRev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventTitle = "Revenue";
               addItem();
            }
        });

        addExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventTitle="Expense";
                addItem();
            }
        });
    }

    private void configureSettings() {

        if(settings.getTYPE().equals("Revenue")){
            linearRev.setVisibility(View.VISIBLE);
            linearExp.setVisibility(View.GONE);
        }else if(settings.getTYPE().equals("Expense")){
            linearRev.setVisibility(View.GONE);
            linearExp.setVisibility(View.VISIBLE);
        }
        //date is yyyy-mm-dd
        String currDate = Resource.getCurrentDate();
        if(settings.getCUSTOMDATEINDICATOR()){
            currDate = settings.getCUSTOMDATE();
        }else {
            currDate = Resource.getCurrentDate();
        }
        String cDate[] = currDate.split("-");
        switch (settings.getPERIOD()) {
            case "Day":
                periondChecker = currDate;
                break;
            case "Month":
                periondChecker = cDate[0] + cDate[1];
                break;
            case "Year":
                periondChecker = cDate[0];
                break;
        }

    }

    private void getBudgetList(){



        budgetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arrayList.clear();
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    HashMap<String, String> hashMap = new HashMap<>();

                    String itemPeriodChecker = "empty";
                    String itemPeriod = dataSnapshot1.child("date").getValue().toString();
                    String iPeriod[] = itemPeriod.split("-");

                    switch (settings.getPERIOD()) {
                        case "Day":
                            itemPeriodChecker = itemPeriod;
                            break;
                        case "Month":
                            itemPeriodChecker = iPeriod[0] + iPeriod[1];
                            break;
                        case "Year":
                            itemPeriodChecker = iPeriod[0];
                            break;
                    }

                    if(!dataSnapshot1.child("amount").getValue().toString().equals("0.00")  && itemPeriodChecker.equals(periondChecker)) {
                        hashMap.put("name", dataSnapshot1.child("item").getValue().toString());
                        hashMap.put("amount", dataSnapshot1.child("amount").getValue().toString());
                        hashMap.put("id", dataSnapshot1.child("id").getValue().toString());
                        hashMap.put("type", dataSnapshot1.child("type").getValue().toString());
                        hashMap.put("date", dataSnapshot1.child("date").getValue().toString());

                        arrayList.add(hashMap);
                    }


                }
                if(arrayList.size()>0){
                    linearRevenue.removeAllViews();
                    linearExpense.removeAllViews();
                    setBudgetList();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setBudgetList() {

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        for(int x=0;x<arrayList.size();x++){
            final HashMap<String, String> hashMap = arrayList.get(x);
            DecimalFormat df = new DecimalFormat("###,###,###,##0.00");


            if(hashMap.get("type").equals("Revenue")) {
                View view = layoutInflater.inflate(R.layout.layout_budget_list, linearRevenue, false);
                final TextView itemName =  view.findViewById(R.id.txtDTItemName);
                TextView amount =  view.findViewById(R.id.txtDTAmount);
                TextView date = view.findViewById(R.id.txtDTDate);
                LinearLayout linearList = view.findViewById(R.id.linDTDet);

                itemName.setText(hashMap.get("name"));
                amount.setText(settings.getCURRENCY()+hashMap.get("amount"));
                date.setText(hashMap.get("date"));
                view.setBackgroundColor(MyColors.JOYFUL_COLORS[q]);


                arrayRevenueString[q]=hashMap.get("name");
                arrayRevenueFloat[q] = Float.parseFloat(hashMap.get("amount"));
                q=q+1;


                //delete Revenue
                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        alterItem(hashMap.get("id"), hashMap.get("date"), hashMap.get("type"));
                        return false;
                    }
                });

                linearRevenue.addView(view);
            }

            if(hashMap.get("type").equals("Expense")) {

                View view = layoutInflater.inflate(R.layout.layout_budget_list, linearExpense, false);
                TextView itemName =  view.findViewById(R.id.txtDTItemName);
                final TextView amount =  view.findViewById(R.id.txtDTAmount);
                final LinearLayout linearList = view.findViewById(R.id.linDTDet);
                TextView date = view.findViewById(R.id.txtDTDate);


                itemName.setText(hashMap.get("name"));
                amount.setText(settings.getCURRENCY()+hashMap.get("amount"));
                date.setText(hashMap.get("date"));

                itemName.setTextColor(Color.WHITE);
                amount.setTextColor(Color.WHITE);
                date.setTextColor(Color.WHITE);
                if(x%2==0) {
                    view.setBackgroundResource(R.drawable.list_style);


                    // itemName.setBackgroundColor(ColorTemplate.JOYFUL_COLORS[z]);
                }else{
                    view.setBackgroundResource(R.drawable.list_style_other);
                }
                arrayExpenseString[z]=hashMap.get("name");
                arrayExpenseFloat[z] = Float.parseFloat(hashMap.get("amount"));
                z=z+1;

                //delete Expense
                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        alterItem(hashMap.get("id"), hashMap.get("date"), hashMap.get("type"));
                        return false;
                    }
                });






                linearExpense.addView(view);
            }


        }

        setPieChart();
    }

    private void setPieChart() {

        pieChart.clear();
        List<PieEntry> pieEntries = new ArrayList<>();

        if(settings.getTYPE().equals("Revenue")){
            for (float anArrayRevenueFloat : arrayRevenueFloat) {
                pieEntries.add(new PieEntry(anArrayRevenueFloat));
            }
        }else if(settings.getTYPE().equals("Expense")){
            for (float anArrayExpenseFloat : arrayExpenseFloat) {
                pieEntries.add(new PieEntry(anArrayExpenseFloat));
            }
        }


        //Log.d("cv", pieEntries.toString());

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(MyColors.JOYFUL_COLORS);
        pieDataSet.getSelectionShift();


        PieData pieData = new PieData(pieDataSet);

        //PieChart pieChart = (PieChart) findViewById(R.id.pieAttendance);



        //pieDataSet.setValueTextSize(0);
        pieChart.getDescription().setText("");
        pieChart.setData(pieData);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setHoleRadius(0);
        pieChart.setRotationEnabled(false);

        pieChart.setEntryLabelTextSize(0);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawEntryLabels(false);

        pieChart.setEnabled(false);


        pieChart.invalidate();
        z=0;
        q=0;
    }


    private void addItem(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = LayoutInflater.from(Setup_Budget.this);
        View view = layoutInflater.inflate(R.layout.layout_item_add, linearLayout, false);

        final Button submit = view.findViewById(R.id.btnChangeSubmit);
        final EditText item = view.findViewById(R.id.edtChangeItem);
        final EditText amount = view.findViewById(R.id.edtChangeAmount);
        final ImageView img = view.findViewById(R.id.imgDate);
        final EditText dob = view.findViewById(R.id.edtAddDate);
        final EditText amountQty = view.findViewById(R.id.edtChangeAmountNumber);
        final TextView amountTotal = view.findViewById(R.id.txtTotalAmount);
        TextView title = view.findViewById(R.id.txtTitle);

        if(eventTitle.equals("Revenue")){
            title.setText("ADD REVENUE");
        }else if(eventTitle.equals("Expense")){
            title.setText("ADD EXPENSE");
        }

        LinearLayout linear = findViewById(R.id.linDTDet);

        progressBar = view.findViewById(R.id.progressChange);

        final Spinner spinItem = view.findViewById(R.id.spinItems);

        List<String> listItem = new ArrayList<>();
        if(eventTitle.equals("Expense") ){

            listItem.add(0, "Select Items");
            listItem.add(1, "Food");
            listItem.add(2, "Transport");
            listItem.add(3, "Water");
            listItem.add(4, "Rent");
            listItem.add(5, "Electric Bill");
            listItem.add(6, "Water Bill");
            listItem.add(7, "Cloths");
        }

        if(eventTitle.equals("Revenue")) {
            //List<String> listItem = new ArrayList<>();
            listItem.add(0, "Select Items");
            listItem.add(1, "Salary");
            listItem.add(2, "Bonus");
            listItem.add(3, "Dividends");
            listItem.add(4, "Investment Returns");
            listItem.add(5, "Allowance");
            listItem.add(6, "Sales");
            listItem.add(7, "Gift");
        }

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.spiner_layout, listItem);
        arrayAdapter.setDropDownViewResource(R.layout.dropdown_layout);
        spinItem.setAdapter(arrayAdapter);
        spinItem.setVisibility(View.VISIBLE);



        spinItem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0){
                    item.setText(spinItem.getSelectedItem().toString());
                }else if(position==0){
                    item.setText("");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dob.setText(Resource.getCurrentDateFormat2());


        amountQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count==0){
                    amountTotal.setText("0");
                    alterTotal = "0";
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    Double newTotal = Double.parseDouble(amount.getText().toString()) * Double.parseDouble(s.toString());
                    amountTotal.setText("Total = "+settings.getCURRENCY()+newTotal);
                    alterTotal = String.valueOf(newTotal);
                }catch (Exception e){
                    alterTotal = "0";
                    e.printStackTrace();
                }

            }
        });

        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count==0){
                    amountTotal.setText("0");
                    alterTotal = "0";
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    Double newTotal = Double.parseDouble(amountQty.getText().toString()) * Double.parseDouble(s.toString());
                    amountTotal.setText("Total = "+settings.getCURRENCY()+newTotal);
                    alterTotal = String.valueOf(newTotal);
                }catch (Exception e){
                    alterTotal = "0";
                    e.printStackTrace();
                }
            }
        });


        alert.setView(view);
        //alert.setTitle("Add "+eventTitle);
        final AlertDialog closeDialogue = alert.create();
        //alert.show();
        closeDialogue.show();


        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date_Picker date_picker = new Date_Picker();
                date_picker.setEditTextDisplay(dob);
                date_picker.show(getFragmentManager(), null);
            }
        });



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(item.getText().toString().isEmpty()){
                    Toast.makeText(Setup_Budget.this, R.string.enterItem, Toast.LENGTH_SHORT).show();
                }else if(amount.getText().toString().isEmpty()){
                    Toast.makeText(Setup_Budget.this, R.string.enterAmount, Toast.LENGTH_SHORT).show();
                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    item.setEnabled(false);
                    amount.setEnabled(false);
                    submit.setEnabled(false);
                    img.setEnabled(false);
                    dob.setEnabled(false);
                    processData(item, amount, submit, dob, img, closeDialogue);
                }

            }
        });

    }

    private void processData(final EditText item, final EditText amount, final Button submit, final EditText setDate, final ImageView img, final AlertDialog alertDialog) {

        budgetId.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.equals(null)){
                    String newDate = Resource.getFormatDateAPI(setDate.getText().toString());
                    int bugId = Integer.parseInt(dataSnapshot.getValue().toString())+1;
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("type", eventTitle);
                    hashMap.put("id", String.valueOf(bugId));
                    hashMap.put("amount", alterTotal);
                    hashMap.put("item", item.getText().toString());
                    hashMap.put("date", newDate);

                    budgetId.setValue(bugId);
                    budgetRef.child(String.valueOf(bugId)).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(Setup_Budget.this, "New "+eventTitle+" added", Toast.LENGTH_SHORT).show();
                            item.setEnabled(true);
                            amount.setEnabled(true);
                            submit.setEnabled(true);
                            img.setEnabled(true);
                            setDate.setEnabled(true);
                            alertDialog.dismiss();
                            item.setText("");
                            amount.setText("");

                            progressBar.setVisibility(View.GONE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Setup_Budget.this, e.toString(), Toast.LENGTH_SHORT).show();
                            item.setEnabled(true);
                            amount.setEnabled(true);
                            submit.setEnabled(true);
                            img.setEnabled(true);
                            setDate.setEnabled(true);
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Setup_Budget.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });



    }

    private void alterItem(final String id, final String cDate, final String type) {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Delete");
        alert.setMessage("Are you sure you want to delete this Item?");


        alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteItem(id);
            }
        });

        alert.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               //dialog.dismiss();

                pieChart.clear();
            }
        });


        alert.show();
    }


    /*
    private void editItem(final String id, String cDate, final String type) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        LayoutInflater layoutInflater = LayoutInflater.from(Setup_Budget.this);
        View view = layoutInflater.inflate(R.layout.layout_item_add, linearLayout, false);

        final Button submit = view.findViewById(R.id.btnChangeSubmit);
        final EditText item = view.findViewById(R.id.edtChangeItem);
        final EditText amount = view.findViewById(R.id.edtChangeAmount);
        progressBar = view.findViewById(R.id.progressChange);

        submit.setText("Edit");
        final HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("date", cDate);
        hashMap.put("amount", amount.getText().toString());
        hashMap.put("item", item.getText().toString());
        hashMap.put("update_date", Resource.getCurrentDate());
        hashMap.put("type", type);
        hashMap.put("id", id);

        amount.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.d("fer", amount.getText().toString()+" "+item.getText().toString());
                return false;
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                budgetRef.child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        budgetRef.child(id).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Setup_Budget.this, "Successfully Updated", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                item.setEnabled(true);
                                amount.setEnabled(true);
                                submit.setEnabled(true);


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Setup_Budget.this, "Failed to update", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Setup_Budget.this, "Failed to Update", Toast.LENGTH_LONG).show();
                    }
                });

            }
        });


        alert.setView(view);
        alert.show();



    }
    */

    private void deleteItem(String id) {

        budgetRef.child(id).removeValue();
        //budgetRef.setValue("1");

    }
}
