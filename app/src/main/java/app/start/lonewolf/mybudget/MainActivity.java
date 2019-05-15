package app.start.lonewolf.mybudget;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.start.lonewolf.mybudget.Dialogues.Budget_History_Details;
import app.start.lonewolf.mybudget.Dialogues.FeedBack;
import app.start.lonewolf.mybudget.Dialogues.Help;
import app.start.lonewolf.mybudget.Resources.Date_Picker;
import app.start.lonewolf.mybudget.Resources.Resource;
import app.start.lonewolf.mybudget.Resources.Settings;

import static app.start.lonewolf.mybudget.R.layout.layout_item_add;

public class MainActivity extends AppCompatActivity {

    private PieChart pieChart;
    private DatabaseReference budgetRef, budgetId, settingsRef, userRef;
    private FirebaseAuth auth;
    private String currentUserId="";
    private Double totalRevenue;
    private Double totalExpense;
    private Button addRev, addExp, details;
    private LinearLayout linearLayout;
    private String eventTitle ="";
    private ProgressBar progressBar, progressBarMain;
    private TextView summary, current_period, spent, total, balance;
    private String periondChecker = "empty", alterTotal="0";

    private Settings settings;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle mToggle;
    private Toolbar toolbar;
    private RadioGroup radioMain;
    private RadioButton day, month, year;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_main);

        settings = new Settings(this);

        //settings.setPERIOD("Month");

        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        budgetRef = FirebaseDatabase.getInstance().getReference().child("budgets").child(currentUserId).child("simple");
        budgetId = FirebaseDatabase.getInstance().getReference().child("data_identifiers").child("ledger_identifiers").child("ledger_id");
        settingsRef = FirebaseDatabase.getInstance().getReference();
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId);
        pieChart = findViewById(R.id.pieMain);
        summary= findViewById(R.id.txtMainSummaray);
        drawerLayout = findViewById(R.id.mainDrawLayout);
        navigationView = findViewById(R.id.mainNavigate);
        linearLayout = findViewById(R.id.linMainStatus);

        balance = findViewById(R.id.txtMainBalance);
        spent = findViewById(R.id.txtMainSpent);
        total = findViewById(R.id.txtMainTotal);


        progressBarMain = findViewById(R.id.progressMain);
        current_period = findViewById(R.id.txtPeriodMain);
        toolbar = findViewById(R.id.tabMain);

        radioMain = findViewById(R.id.radioMain);
        day = findViewById(R.id.radioDay);
        month = findViewById(R.id.radioMonth);
        year = findViewById(R.id.radioYear);

        if(settings.getCHECKHELP().equals("")){
            settings.setCHECKHELP("1");
            Help.showHelp(MainActivity.this, linearLayout);
        }


        switch (settings.getPERIOD()) {
            case "Day":
                day.setChecked(true);


                break;
            case "Month":
                month.setChecked(true);
                break;
            case "Year":
                year.setChecked(true);
                break;
        }

        if(day.isChecked()){
            if(settings.getCUSTOMDATEINDICATOR()){
                String newYear[] = settings.getCUSTOMDATE().split("-");
                current_period.setText(Resource.getFormatDateAPP(settings.getCUSTOMDATE()));
            }else {
                current_period.setText(Resource.getCurrentDayMonthYear());

            }
        }else if(month.isChecked()){
            if(settings.getCUSTOMDATEINDICATOR()){
                String newYear[] = settings.getCUSTOMDATE().split("-");
                current_period.setText(newYear[1]+" Month");
            }else {
                current_period.setText(Resource.getCurrentMonthYear());
            }
        }else if(year.isChecked()){

            if(settings.getCUSTOMDATEINDICATOR()){
                String newYear[] = settings.getCUSTOMDATE().split("-");
                current_period.setText(newYear[0]);
            }else {
                current_period.setText(Resource.getCurrentYear());
            }
        }

        checkPeriod();
        getUsername();







        mToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        addRev = findViewById(R.id.btnMainAddRev);
        addExp = findViewById(R.id.btnMainAddExp);
        details = findViewById(R.id.btnMainDetails);




        getButtons();
        configureSettings();
        getSettings();
        //getBudeget(periondChecker);


        //setPieChart();
    }

    private void getButtons() {
        spent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settings.setTYPE("Expense");
                Intent intent = new Intent(MainActivity.this, Setup_Budget.class);
                startActivity(intent);
            }
        });

        balance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                settings.setTYPE("Revenue");
//                Intent intent = new Intent(MainActivity.this, Setup_Budget.class);
//                startActivity(intent);
            }
        });

        total.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settings.setVAR1(settings.getCURRENTBUDGET());
                Budget_History_Details.showHistory(MainActivity.this, linearLayout);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.dNewBudget){
                    drawerLayout.closeDrawer(Gravity.START);
                    getNewBudget();
                }

                if(item.getItemId()==R.id.dSettings){

                    Intent intent = new Intent(MainActivity.this, User_Settings.class);
                    startActivity(intent);
                    finish();

                }else if(item.getItemId()==R.id.dLogOut){
                    auth.signOut();
                    LoginManager.getInstance().logOut();
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivity(intent);
                    finish();

                }else if(item.getItemId()==R.id.dExpenseDetails){
                    drawerLayout.closeDrawer(Gravity.START);

                    settings.setTYPE("Expense");
                    Intent intent = new Intent(MainActivity.this, Setup_Budget.class);
                    startActivity(intent);

                }else if(item.getItemId()==R.id.dRevenueDetails){
                    drawerLayout.closeDrawer(Gravity.START);

                    settings.setTYPE("Revenue");
                    Intent intent = new Intent(MainActivity.this, Setup_Budget.class);
                    startActivity(intent);

                }else if(item.getItemId()==R.id.dBudgetHistory){
                    drawerLayout.closeDrawer(Gravity.START);
                    Intent intent = new Intent(MainActivity.this, Budget_History.class);
                    startActivity(intent);
                } else if(item.getItemId()==R.id.dFeedback){
                    drawerLayout.closeDrawer(Gravity.START);
                    FeedBack.showFeedBack(MainActivity.this, linearLayout);
                    //finish();
                }else if(item.getItemId()==R.id.dHelp){
                    drawerLayout.closeDrawer(Gravity.START);
                    Help.showHelp(MainActivity.this, linearLayout);
                }

                return false;
            }
        });

        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Setup_Budget.class);
                startActivity(intent);

            }
        });

        addRev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventTitle= "Revenue";

                addItem();
            }
        });

        addExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checkBox.setSelected(false);
                eventTitle= "Expense";
                //progressBar.setVisibility(View.VISIBLE);
                addItem();
            }
        });

    }

    private void checkPeriod(){


        radioMain.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.radioDay){
//                    month.setChecked(false);
//                    year.setChecked(false);
                    settings.setPERIOD("Day");
                    changeSetPeriod("Day");
                    configureSettings();
                    getBudeget(periondChecker);
                    //current_period.setText(Resource.getCurrentDayMonthYear());
                    // pieChart.animateXY(5000,6000);

                    if(settings.getCUSTOMDATEINDICATOR()){
                        String newYear[] = settings.getCUSTOMDATE().split("-");
                        current_period.setText(Resource.getFormatDateAPP(settings.getCUSTOMDATE()));
                    }else {
                        current_period.setText(Resource.getCurrentDayMonthYear());
                    }

                }else if(checkedId==R.id.radioMonth){
//                    day.setChecked(false);
//                    year.setChecked(false);
                    settings.setPERIOD("Month");
                    changeSetPeriod("Month");
                    configureSettings();
                    getBudeget(periondChecker);
                    //current_period.setText(Resource.getCurrentMonthYear());
                    if(settings.getCUSTOMDATEINDICATOR()){
                        String newYear[] = settings.getCUSTOMDATE().split("-");
                        current_period.setText(newYear[1]+" Month");
                    }else {
                        current_period.setText(Resource.getCurrentMonthYear());
                    }
                }else if(checkedId==R.id.radioYear){
//                    month.setChecked(false);
//                    day.setChecked(false);
                    settings.setPERIOD("Year");
                    changeSetPeriod("Year");
                    configureSettings();
                    getBudeget(periondChecker);

                    if(settings.getCUSTOMDATEINDICATOR()){
                        String newYear[] = settings.getCUSTOMDATE().split("-");
                        current_period.setText(newYear[0]);
                    }else {
                        current_period.setText(Resource.getCurrentYear());
                    }

                }
            }
        });

    }

    private void configureSettings() {
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
                day.setChecked(true);
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

    private void getSettings(){
        progressBarMain.setVisibility(View.VISIBLE);
        settingsRef.child("settings").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    settings.setCURRENCY(dataSnapshot.child("currency").getValue().toString());
                    settings.setPERIOD(dataSnapshot.child("period").getValue().toString());


                    switch (settings.getPERIOD()) {
                        case "Day":

                            //current_period.setText(Resource.getCurrentDayMonthYear());
                            if (settings.getCUSTOMDATEINDICATOR()) {
                                //String newYear[] = settings.getCUSTOMDATE().split("-");
                                String newYear = Resource.getFormatDateAPP(settings.getCUSTOMDATE());
                                current_period.setText(Resource.getCustomCurrentDayMonthYear(newYear));
                            } else {
                                //current_period.setText(Resource.getCurrentDayMonthYear());
                           /* if(settings.getCUSTOMDATEINDICATOR()){
                                String newYear[] = settings.getCUSTOMDATE().split("-");
                                current_period.setText(newYear[1]+" Month");
                            }else {
                                current_period.setText(Resource.getCurrentMonthYear());
                            }
                            */

                            }
                            getBudeget(periondChecker);
                            break;
                        case "Month":
//                            if (settings.getCUSTOMDATEINDICATOR()) {
//                                String newDate = Resource.getFormatDateAPP(settings.getCUSTOMDATE());
//                                String dateArray[] = newDate.split("/");
//                                current_period.setText(Resource.getFormatDateAPP(settings.getCUSTOMDATE()));
//                            } else {
                                current_period.setText(Resource.getCurrentMonthYear());
                                getBudeget(periondChecker);
                           // }
                            break;
                        case "Year":
                            //current_period.setText(Resource.getCurrentYear());
                            if (settings.getCUSTOMDATEINDICATOR()) {
                                String newYear[] = settings.getCUSTOMDATE().split("-");
                                current_period.setText(newYear[0]);
                            } else {
                                current_period.setText(Resource.getCurrentYear());
                            }
                            getBudeget(periondChecker);
                            break;
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getBudeget(String timePeriod) {

        progressBarMain.setVisibility(View.VISIBLE);
        //Toast.makeText(this, settings.getPERIOD() +" "+periondChecker, Toast.LENGTH_SHORT).show();
        budgetRef.child(settings.getCURRENTBUDGET()).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                totalRevenue=0.00;
                totalExpense =0.00;
                if(dataSnapshot.hasChildren()){

                    for(DataSnapshot child : dataSnapshot.getChildren() ) {
                        Double amountRevenue = 0.00;

                        String itemPeriodChecker = "empty";
                        String itemPeriod = child.child("date").getValue().toString();
                        String iPeriod[] = itemPeriod.split("-");

                        switch (settings.getPERIOD()) {
                            case "Day":
                                itemPeriodChecker = itemPeriod;
                                break;
                            case "Month":
                                Log.d("hope", itemPeriodChecker);
                                itemPeriodChecker = iPeriod[0] + iPeriod[1];
                                break;
                            case "Year":
                                itemPeriodChecker = iPeriod[0];
                                break;
                        }


                        if (child.child("type").getValue().toString().equals("Revenue") && itemPeriodChecker.equals(periondChecker))  {
                            amountRevenue = Double.parseDouble(child.child("amount").getValue().toString());
                            totalRevenue = totalRevenue + amountRevenue;

                        }
                        Double amountExpense = 0.00;
                        if (child.child("type").getValue().toString().equals("Expense") && itemPeriodChecker.equals(periondChecker)) {
                            amountExpense = Double.parseDouble(child.child("amount").getValue().toString());
                            totalExpense = totalExpense + amountExpense;
                        }




                    }
                }

                if(totalRevenue>=0 && totalExpense>=0){
                    setPieChart();
                }else{

                    progressBarMain.setVisibility(View.GONE);

                    }
                }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBarMain.setVisibility(View.GONE);
                //Log.d("jkl", databaseError.toString());
            }
        });

        //Log.d("currentz", String.valueOf(totalExpense)+"//"+String.valueOf(totalRevenue));
    }

    private void setPieChart(){




        float totalBudget = Float.parseFloat(totalRevenue.toString());
        final float tExp = Float.parseFloat(totalExpense.toString());
        final float tRev = Float.parseFloat(totalRevenue.toString())-tExp;

        total.setText( "Total\n"+String.valueOf(totalBudget));
        spent.setText("Spent\n"+String.valueOf(tExp));
        balance.setText("Balance\n"+String.valueOf(tRev));
        List<PieEntry> pieEntries = new ArrayList<>();
        if(totalBudget<tExp){

            summary.setText("You have a dificit of"+" "+settings.getCURRENCY() + String.valueOf(tRev*-1));
            float attendVal[] = {tRev*-1};
            String attendLabel[] = {"Deficit"};
            //pieChart.setEntryLabelColor(getColor(Color.GREEN));

            for(int y=0; y<attendVal.length; y++){
                pieEntries.add(new PieEntry(attendVal[y], attendLabel[y]));
            }
        }else {
            summary.setText("You have spent"+" " + settings.getCURRENCY() + String.valueOf(tExp) + " out of a budget of " + settings.getCURRENCY() + String.valueOf(totalBudget) + ". You have " + settings.getCURRENCY() + String.valueOf(tRev) + " remaining.");


            float attendVal[] = {tRev, tExp};
            String attendLabel[] = {"What you have left", "What you Spent"};

            for(int y=0; y<attendVal.length; y++){
                pieEntries.add(new PieEntry(attendVal[y], attendLabel[y]));
            }
        }



        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Pie Chart is in Percentage");
        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        pieDataSet.getSelectionShift();

        PieData pieData = new PieData(pieDataSet);

        //PieChart pieChart = (PieChart) findViewById(R.id.pieAttendance);
        pieChart.animateXY(2000, 2000);
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {


                if(h.getX()==0.0){
                    Toast.makeText(MainActivity.this, getString(R.string.youHave)+" "+settings.getCURRENCY()+tRev + " "+getString(R.string.remaining), Toast.LENGTH_LONG).show();
                }
                if(h.getX()==1.0){
                    Toast.makeText(MainActivity.this, getString(R.string.youHaveSpent)+" "+settings.getCURRENCY()+" "+tExp, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onNothingSelected() {

            }
        });
        pieDataSet.setValueTextSize(18);
        pieChart.getDescription().setText("");
        pieChart.setData(pieData);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setHoleRadius(0);

        //pieChart.setEntryLabelTextSize(0);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawEntryLabels(false);

        pieChart.invalidate();

        if(totalRevenue==0 && totalExpense==0) {
            switch (settings.getPERIOD()) {
                case "Day":
                    pieChart.clear();
                    pieChart.setNoDataText("You have no data for this day");
                    break;
                case "Month":
                    pieChart.clear();
                    pieChart.setNoDataText("You have no data for this month");
                    break;
                case "":
                    pieChart.clear();
                    pieChart.setNoDataText("You have no data for this year");
                    break;
            }
        }
        progressBarMain.setVisibility(View.GONE);

    }

    private void addItem(){
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        final View view = layoutInflater.inflate(R.layout.layout_item_add, linearLayout, false);

        final Button submit = view.findViewById(R.id.btnChangeSubmit);
        final EditText item = view.findViewById(R.id.edtChangeItem);
        final EditText amount = view.findViewById(R.id.edtChangeAmount);
        final ImageView img = view.findViewById(R.id.imgDate);
        final EditText dob = view.findViewById(R.id.edtAddDate);
        final EditText amountQty = view.findViewById(R.id.edtChangeAmountNumber);
        final TextView amountTotal = view.findViewById(R.id.txtTotalAmount);
        final Spinner spinItem = view.findViewById(R.id.spinItems);
        TextView title = view.findViewById(R.id.txtTitle);
        progressBar = view.findViewById(R.id.progressChange);


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
        final AlertDialog closedialog= alert.create();
        //alert.show();

        if(eventTitle.equals("Revenue")){
            title.setText("ADD REVENUE");
        }else if(eventTitle.equals("Expense")){
            title.setText("ADD EXPENSE");
        }
        closedialog.show();

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
                    Toast.makeText(MainActivity.this, R.string.enterItem, Toast.LENGTH_SHORT).show();
                }else if(amount.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, R.string.enterAmount, Toast.LENGTH_SHORT).show();
                }else {

                    progressBar.setVisibility(View.VISIBLE);
                    item.setEnabled(false);
                    amount.setEnabled(false);
                    submit.setEnabled(false);
                    img.setEnabled(false);
                    dob.setEnabled(false);

                    processData(item, amount, submit, dob, img, closedialog);

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
                    budgetRef.child(settings.getCURRENTBUDGET()).child(String.valueOf(bugId)).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(MainActivity.this, "New "+eventTitle+" added", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(MainActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
                item.setEnabled(true);
                amount.setEnabled(true);
                submit.setEnabled(true);
                img.setEnabled(true);
                setDate.setEnabled(true);

                progressBar.setVisibility(View.GONE);
            }
        });



    }


    private void getNewBudget(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.layout_item_add, linearLayout, false);
        final EditText name = view.findViewById(R.id.edtChangeItem);
        final EditText amount = view.findViewById(R.id.edtChangeAmount);
        final EditText date = view.findViewById(R.id.edtAddDate);
        final Button submit = view.findViewById(R.id.btnChangeSubmit);
        final ImageView imgDate = view.findViewById(R.id.imgDate);

        final EditText amountQty = view.findViewById(R.id.edtChangeAmountNumber);
        final TextView amountTotal = view.findViewById(R.id.txtTotalAmount);
        TextView X = view.findViewById(R.id.x);
        final ProgressBar progressBar = view.findViewById(R.id.progressChange);
        TextInputLayout nameText = view.findViewById(R.id.textInputLayout3);
        TextInputLayout amountText = view.findViewById(R.id.textInputLayout4);
        TextView title = view.findViewById(R.id.txtTitle);

        amountQty.setVisibility(View.GONE);
        amountTotal.setVisibility(View.GONE);
        X.setVisibility(View.GONE);


        nameText.setHint("Budget Name");
        amountText.setHint(getString(R.string.start_amount));
        date.setText(Resource.getCurrentDateFormat2());

        imgDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date_Picker date_picker = new Date_Picker();
                date_picker.setEditTextDisplay(date);
                date_picker.show(getFragmentManager(), null);
            }
        });

        alert.setView(view);
        //alert.setTitle();
        title.setText(R.string.addnewBudget);

        final AlertDialog dialog = alert.create();
        dialog.show();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name.setEnabled(false);
                amount.setEnabled(false);
                date.setEnabled(false);
                imgDate.setEnabled(false);
                submit.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);

                budgetRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        List list = new ArrayList();
                        for(DataSnapshot baby:dataSnapshot.getChildren()){

                            list.add(baby.getKey().toString());

                        }

                        if(list.contains(name.getText().toString())){
                            Toast.makeText(MainActivity.this, "This Budget name has already been use. Please enter a different budget name", Toast.LENGTH_SHORT).show();
                            name.setEnabled(true);
                            amount.setEnabled(true);
                            submit.setEnabled(true);
                            imgDate.setEnabled(true);
                            date.setEnabled(true);

                            progressBar.setVisibility(View.GONE);
                        }else{
                            settings.setCURRENTBUDGET(name.getText().toString().toUpperCase());
                            setNewBudget(name, amount, date, imgDate, submit, dialog, progressBar);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

    }

    private void setNewBudget(final EditText name, final EditText amount, final EditText date, final ImageView imgDate, final Button submit, final AlertDialog dialog, final ProgressBar progressBar) {



        budgetId.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.equals(null)) {
                    String newDate = Resource.getFormatDateAPI(date.getText().toString());
                    int bugId = Integer.parseInt(dataSnapshot.getValue().toString()) + 1;
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("type", "Revenue");
                    hashMap.put("id", String.valueOf(bugId));
                    hashMap.put("amount", amount.getText().toString());
                    hashMap.put("item", "Start Amount");
                    hashMap.put("date", newDate);
                    //hashMap.put("budget_name", name.getText().toString());
                    //settings.setCURRENTBUDGET(name.getText().toString());

                    budgetId.setValue(bugId);
                    budgetRef.child(settings.getCURRENTBUDGET()).child(String.valueOf(bugId)).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(MainActivity.this, "New Budget Created", Toast.LENGTH_SHORT).show();
                            name.setEnabled(true);
                            amount.setEnabled(true);
                            submit.setEnabled(true);
                            imgDate.setEnabled(true);
                            date.setEnabled(true);
                            dialog.dismiss();
                            name.setText("");
                            amount.setText("");
                            getBudeget(periondChecker);


                            progressBar.setVisibility(View.GONE);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                            name.setEnabled(true);
                            amount.setEnabled(true);
                            submit.setEnabled(true);
                            imgDate.setEnabled(true);
                            date.setEnabled(true);

                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }




    private void changeSetPeriod(String period){
        settingsRef.child("settings").child(currentUserId).child("period").setValue(period);

    }


    private void getUsername(){
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    String fname = dataSnapshot.child("first_name").getValue().toString();
                    String full_name = dataSnapshot.child("first_name").getValue().toString()+" "+dataSnapshot.child("last_name").getValue().toString();

                    String refCode = dataSnapshot.child("reference_code").getValue().toString();
                    String ref = dataSnapshot.child("references").getValue().toString();

                    settings.setUSERNAME(full_name);
                    settings.setREFERENCECODE(refCode);
                    settings.setREFERENCES(ref);

                    if(fname.contains(" ")){
                        String[] splitFname = fname.split(" ");
                        fname = splitFname[0];
                    }

                    getSupportActionBar().setTitle("Welcome "+Resource.capitalize(fname.toLowerCase()));

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    protected void onResume() {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        super.onResume();
    }
}
