package app.start.lonewolf.mybudget.Dialogues;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import app.start.lonewolf.mybudget.R;
import app.start.lonewolf.mybudget.Resources.Settings;

public class Help {
    private static Settings settings;

    public static void  showHelp(Activity activity, LinearLayout linearLayout){
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View view = layoutInflater.inflate(R.layout.dialogue_help, linearLayout, false);

        settings = new Settings(activity);

        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setView(view);
        alert.show();

        if(settings.getCHECKHELP().equals("")){
            alert.setPositiveButton("Next", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    settings.setCHECKHELP("1");
                    dialog.dismiss();

                }
            });
        }
        alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                settings.setCHECKHELP("1");
            }
        });

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                @Override
//                public void onDismiss(DialogInterface dialog) {
//
//                }
//            });
//        }
    }
}
