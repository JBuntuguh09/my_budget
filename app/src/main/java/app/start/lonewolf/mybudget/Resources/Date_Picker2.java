package app.start.lonewolf.mybudget.Resources;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import app.start.lonewolf.mybudget.MainActivity;

public class Date_Picker2 extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private EditText setData;
    private Boolean isDate;

    public void setEditTextDisplay(EditText editText){
        setData = editText;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            isDate = getArguments().getBoolean("isDate");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //return super.onCreateDialog(savedInstanceState);
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);



        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
        return datePickerDialog;




    }

    @Override
    public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if(setData!=null){
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, monthOfYear);
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            setData.setText(simpleDateFormat.format(cal.getTime()));
            Intent intent = new Intent(getActivity(), MainActivity.class);
            getActivity().startActivity(intent);
            getActivity().finish();
        }

    }
}
