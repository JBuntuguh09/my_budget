package app.start.lonewolf.mybudget.Dialogues;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import app.start.lonewolf.mybudget.R;
import app.start.lonewolf.mybudget.Resources.Settings;
import app.start.lonewolf.mybudget.User_Settings;

public class Create_Template {
    private static Settings settings;
    private static AlertDialog.Builder alert;
    private static AlertDialog dialog;


    public Create_Template(){

    }

    public static void create_list_temp(Activity activity, LinearLayout linearLayout){
        settings = new Settings(activity);

        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View view = layoutInflater.inflate(R.layout.layout_exp_rev_list, linearLayout, false);

        Button submit = view.findViewById(R.id.btnListSubmit);
        LinearLayout linear = view.findViewById(R.id.linListMain);
        alert = new AlertDialog.Builder(activity);

        dialog = alert.create();
        dialog.setView(view);

        dialog.show();


        getList(activity, linear);

    }

    private static void getList(Activity activity, LinearLayout linear) {
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View view = layoutInflater.inflate(R.layout.layout_edit, linear, false);
    }
}
