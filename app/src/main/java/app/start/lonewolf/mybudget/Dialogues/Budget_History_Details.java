package app.start.lonewolf.mybudget.Dialogues;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import app.start.lonewolf.mybudget.R;
import app.start.lonewolf.mybudget.Resources.Settings;

public class Budget_History_Details {

    private static FirebaseAuth auth;
    private static DatabaseReference databaseReference;
    private static Settings settings;
    private static String userId;
    private static ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
    //private double revTotal,


    public Budget_History_Details(){

    }


    public static void showHistory(Activity activity, LinearLayout linearLayout){
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View view = layoutInflater.inflate(R.layout.dialogue_budgets_detail, linearLayout, false);

        settings = new Settings(activity);
        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        TextView totalRev = view.findViewById(R.id.txtDBHTotalRev);
        TextView totalExp = view.findViewById(R.id.txtDBHTotalExp);
        TextView totalBal = view.findViewById(R.id.txtTotalBalance);
        LinearLayout linearRev = view.findViewById(R.id.lindialogueHistoryRev);
        LinearLayout linearExp = view.findViewById(R.id.lindialogueHistoryExp);


        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setView(view);

        AlertDialog dialog = alert.create();
        dialog.show();

        setBudget(totalRev, totalExp, linearRev, linearExp, activity, totalBal);

    }

    private static void setBudget(final TextView totalRev, final TextView totalExp, final LinearLayout linearRev, final LinearLayout linearExp, final Activity activity, final TextView totalBal) {

        databaseReference.child("budgets").child(userId).child("simple").child(settings.getVAR1()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                arrayList.clear();
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    HashMap<String, String> hashMap = new HashMap<>();

                    String itemPeriodChecker = "empty";
                    String itemPeriod = dataSnapshot1.child("date").getValue().toString();
                    String iPeriod[] = itemPeriod.split("-");


                    if(!dataSnapshot1.child("amount").getValue().toString().equals("0.00")  ) {
                        hashMap.put("name", dataSnapshot1.child("item").getValue().toString());
                        hashMap.put("amount", dataSnapshot1.child("amount").getValue().toString());
                        hashMap.put("id", dataSnapshot1.child("id").getValue().toString());
                        hashMap.put("type", dataSnapshot1.child("type").getValue().toString());
                        hashMap.put("date", dataSnapshot1.child("date").getValue().toString());

                        arrayList.add(hashMap);
                    }


                }
                if(arrayList.size()>0){
                    linearRev.removeAllViews();
                    linearExp.removeAllViews();
                    setBudgetList(totalRev, totalExp, linearRev, linearExp, activity, totalBal);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private static void setBudgetList(TextView totalRev, TextView totalExp, LinearLayout linearRev, LinearLayout linearExp, Activity activity, TextView totalBal) {

        Double revTotal=0.00, expTotal =0.00;
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        for(int x=0;x<arrayList.size();x++){
            final HashMap<String, String> hashMap = arrayList.get(x);
            DecimalFormat df = new DecimalFormat("###,###,###,##0.00");


            if(hashMap.get("type").equals("Revenue")) {
                View view = layoutInflater.inflate(R.layout.layout_budget_details, linearRev, false);
                TextView itemName =  view.findViewById(R.id.txtBHDItemName);
                final TextView amount =  view.findViewById(R.id.txtBHDAmount);
                TextView date = view.findViewById(R.id.txtBHDDate);


                itemName.setText(hashMap.get("name"));
                amount.setText(settings.getCURRENCY()+hashMap.get("amount"));
                date.setText(hashMap.get("date"));


                Double revString = Double.parseDouble(hashMap.get("amount"));
                revTotal = revString + revTotal;



                //arrayRevenueString[z]=hashMap.get("name");
                //arrayRevenueFloat[z] = Float.parseFloat(hashMap.get("amount"));
                //q=q+1;




                linearRev.addView(view);
            }

            if(hashMap.get("type").equals("Expense")) {

                View view = layoutInflater.inflate(R.layout.layout_budget_details, linearExp, false);
                TextView itemName = (TextView) view.findViewById(R.id.txtBHDItemName);
                final TextView amount = (TextView) view.findViewById(R.id.txtBHDAmount);
                TextView date = (TextView)view.findViewById(R.id.txtBHDDate);



                itemName.setText(hashMap.get("name"));
                amount.setText(settings.getCURRENCY()+hashMap.get("amount"));
                date.setText(hashMap.get("date"));
                // itemName.setBackgroundColor(ColorTemplate.JOYFUL_COLORS[z]);

                //arrayExpenseString[z]=hashMap.get("name");
                //arrayExpenseFloat[z] = Float.parseFloat(hashMap.get("amount"));
                //z=z+1;

                Double expString = Double.parseDouble(hashMap.get("amount"));
                expTotal = expString + expTotal;


                linearExp.addView(view);
            }


        }


        totalRev.setText(String.valueOf(revTotal));
        totalExp.setText(String.valueOf(expTotal));
        Double totBal = revTotal-expTotal;
        totalBal.setText("Total Balance "+settings.getCURRENCY()+String.valueOf(totBal));
    }


}
