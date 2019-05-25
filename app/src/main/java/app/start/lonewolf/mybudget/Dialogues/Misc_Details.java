package app.start.lonewolf.mybudget.Dialogues;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import app.start.lonewolf.mybudget.Budget_History;
import app.start.lonewolf.mybudget.MainActivity;
import app.start.lonewolf.mybudget.R;
import app.start.lonewolf.mybudget.Resources.Resource;
import app.start.lonewolf.mybudget.Resources.Settings;

public class Misc_Details {

    private static AlertDialog.Builder alert;
    private static AlertDialog dialogue;
    private  static Settings settings;
    public Misc_Details(){

    }

    public static void showDate(final Activity activity, LinearLayout linearLayout){
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View view = layoutInflater.inflate(R.layout.layout_my_date, linearLayout, false);

        settings = new Settings(activity);
        alert = new AlertDialog.Builder(activity);
        final Spinner spinMonth = view.findViewById(R.id.spinMonths);
        final Spinner spinYear = view.findViewById(R.id.spinYears);
        LinearLayout linYear = view.findViewById(R.id.linMonthSelect);
        LinearLayout linMonth = view.findViewById(R.id.linMonthSelect);

        Button submitDate = view.findViewById(R.id.btnDateSubmit);

        if(settings.getPERIOD().equals("Year")){
            spinMonth.setVisibility(View.GONE);
            linMonth.setVisibility(View.GONE);

        }else{
            spinMonth.setVisibility(View.VISIBLE);
            linMonth.setVisibility(View.VISIBLE);

        }


        List<String> listMonth = new ArrayList<>();
        listMonth.add(0, "Select Month");
        listMonth.add(1, "January");
        listMonth.add(2, "February");
        listMonth.add(3, "March");
        listMonth.add(4, "April");
        listMonth.add(5, "May");
        listMonth.add(6, "June");
        listMonth.add(7, "July");
        listMonth.add(8, "August");
        listMonth.add(9, "September");
        listMonth.add(10, "October");
        listMonth.add(11, "November");
        listMonth.add(12, "December");


        List<String> listYear = new ArrayList<>();
        listYear.add(0, "Select Year");
        listYear.add(1, "2010");
        listYear.add(2, "2011");
        listYear.add(3, "2012");
        listYear.add(4, "2013");
        listYear.add(5, "2014");
        listYear.add(6, "2015");
        listYear.add(7, "2016");
        listYear.add(8, "2017");
        listYear.add(9, "2018");
        listYear.add(10, "2019");
        listYear.add(11, "2020");
        listYear.add(12, "2021");
        listYear.add(13, "2022");
        listYear.add(14, "2023");
        listYear.add(15, "2024");
        listYear.add(16, "2010");
        listYear.add(17, "2026");
        listYear.add(18, "2027");
        listYear.add(19, "2029");
        listYear.add(20, "2030");


        ArrayAdapter<String> arrayAdapterMonth = new ArrayAdapter<>(activity, R.layout.spiner_layout, listMonth);
        arrayAdapterMonth.setDropDownViewResource(R.layout.dropdown_layout);
        spinMonth.setAdapter(arrayAdapterMonth);


        ArrayAdapter<String> arrayAdapterYear = new ArrayAdapter<>(activity, R.layout.spiner_layout, listYear);
        arrayAdapterYear.setDropDownViewResource(R.layout.dropdown_layout);
        spinYear.setAdapter(arrayAdapterYear);

        alert.setView(view);
        dialogue = alert.create();


        String currDate[] = settings.getCUSTOMDATE().split("-");

        for (int a = 0; a<listYear.size(); a++){
            if(currDate[0].equals(listYear.get(a))){
                spinYear.setSelection(a);
            }
        }

        for (int b = 0; b<listMonth.size(); b++){
            if(Integer.parseInt(currDate[1])==b){
                spinMonth.setSelection(b);
            }

        }

        submitDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String myMonth;
                if(settings.getPERIOD().equals("Year")){
                    myMonth = "1";
                }else{
                    myMonth = String.valueOf(spinMonth.getSelectedItemId());
                }

                String myYear = spinYear.getSelectedItem().toString();

                if(Integer.parseInt(myMonth)<10){
                    myMonth = "0"+myMonth;
                }

                settings.setCUSTOMDATE(myYear+"-"+myMonth+"-01");
                settings.setCUSTOMDATEINDICATOR(true);
                settings.setDATECHECK(true);
                //dialogue.dismiss();
                Intent intent = new Intent(activity, MainActivity.class);
                activity.startActivity(intent);
                activity.finish();
            }
        });

        dialogue.show();

    }




}
