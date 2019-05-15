package app.start.lonewolf.mybudget.Resources;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.start.lonewolf.mybudget.R;

public class Resource {

    public static final String DATEWITHTIME = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String DATEFORMATDDMMYYYY = "dd/MM/yyyy";
    public static final String DATEFORMATYYYYMMDD = "yyyy-MM-dd";

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static boolean isValidPhone(String strPhone) {
        if (strPhone == null || strPhone.trim().length() == 0) {
            return false;
        }

        boolean isValid = strPhone.trim().length() >= 9 && strPhone.trim().length() <= 12 ? true : false;
        return isValid;
    }

    public static String toCamelCase(final String init) {
        if (init == null)
            return null;

        final StringBuilder ret = new StringBuilder(init.length());

        for (final String word : init.split(" ")) {
            if (!word.isEmpty()) {
                ret.append(word.substring(0, 1).toUpperCase());
                ret.append(word.substring(1).toLowerCase());
            }
            if (!(ret.length() == init.length()))
                ret.append(" ");
        }

        return ret.toString();
    }

    public static boolean isValidDate(CharSequence target) {
        // \+[0-9]+
        Pattern mPattern = Pattern.compile("^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[1,3-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$");
        Matcher matcher = mPattern.matcher(target);
        return matcher.matches();
    }

    public static class DecimalDigitsInputFilter implements InputFilter {

        Pattern mPattern;

        public DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
            mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Matcher matcher = mPattern.matcher(dest);
            if (!matcher.matches())
                return "";
            return null;
        }

    }

    public static boolean isThisDateValid(String dateToValidate) {

        if (dateToValidate == null) {
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMATYYYYMMDD);

        try {
            //if not valid, it will throw ParseException
            Date date = sdf.parse(dateToValidate);
            System.out.println(date);
            return true;
        } catch (ParseException e) {

            e.printStackTrace();
            return false;
        }


    }

    public static boolean isThisDateValid2(String dateToValidate) {
        if (dateToValidate == null || dateToValidate.trim().length() == 0) {
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMATDDMMYYYY);
        try {
            Date date = sdf.parse(dateToValidate);
            System.out.println(date);
            return true;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String sendDateToApi(String str) {
        if (str != null && !str.equalsIgnoreCase("null") && str.trim().length() != 0) {

            SimpleDateFormat sdf1 = new SimpleDateFormat(DATEFORMATDDMMYYYY);
            SimpleDateFormat sdf2 = new SimpleDateFormat(DATEFORMATYYYYMMDD);

            try {

                Date date = sdf1.parse(str);
                return sdf2.format(date);

            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }


        } else {
            return "";
        }
    }

    public static String getDateTimeForAPI(String dateFormatted) {
        Calendar apiDate = Calendar.getInstance();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMATDDMMYYYY);
            apiDate.setTime(dateFormat.parse(dateFormatted));
            Calendar corrTime = Calendar.getInstance();
            apiDate.set(Calendar.HOUR_OF_DAY, corrTime.get(Calendar.HOUR_OF_DAY));
            apiDate.set(Calendar.MINUTE, corrTime.get(Calendar.MINUTE));
            apiDate.set(Calendar.SECOND, corrTime.get(Calendar.SECOND));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //2014-03-15T21:04:43.162Z
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATEWITHTIME);
        return dateFormat.format(apiDate.getTime());
    }

    public static String getDateForAPP(String strDate) {
        if (strDate != null && !strDate.equalsIgnoreCase("null") && strDate.trim().length() != 0) {

            SimpleDateFormat sdf1 = new SimpleDateFormat(DATEWITHTIME);
            SimpleDateFormat sdf2 = new SimpleDateFormat(DATEFORMATDDMMYYYY);

            try {

                Date date = sdf1.parse(strDate);
                return sdf2.format(date);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }


        } else {
            return "";
        }
    }

    public static String getDateForAPI(String strDate) {
        if (strDate != null && !strDate.equalsIgnoreCase("null") && strDate.trim().length() != 0) {

            SimpleDateFormat sdf1 = new SimpleDateFormat(DATEWITHTIME);
            SimpleDateFormat sdf2 = new SimpleDateFormat(DATEFORMATYYYYMMDD);

            try {

                Date date = sdf1.parse(strDate);
                return sdf2.format(date);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }


        } else {
            return "";
        }
    }


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public static boolean chkDatePassed(String str1) {
        try {
            String strToday = getCurrentDate();

            SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMATDDMMYYYY);
            SimpleDateFormat sdf1 = new SimpleDateFormat(DATEFORMATYYYYMMDD);

            Date date1 = sdf.parse(str1);
            Date date2 = sdf1.parse(strToday);

            System.out.println(sdf.format(date1));
            System.out.println(sdf.format(date2));

            if (date1.before(date2)) {
                return true;
            }


        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static String getFormatDateAPI(String str) {
        if (str != null && !str.equalsIgnoreCase("null") && str.trim().length() != 0) {

            SimpleDateFormat sdf1 = new SimpleDateFormat(DATEFORMATDDMMYYYY);
            SimpleDateFormat sdf2 = new SimpleDateFormat(DATEFORMATYYYYMMDD);

            try {

                Date date = sdf1.parse(str);
                return sdf2.format(date);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }


        } else {
            return "";
        }
    }

    public static String getFormatDateAPP(String str) {
        if (str != null && !str.equalsIgnoreCase("null") && str.trim().length() != 0) {

            SimpleDateFormat sdf1 = new SimpleDateFormat(DATEFORMATYYYYMMDD);
            SimpleDateFormat sdf2 = new SimpleDateFormat(DATEFORMATDDMMYYYY);

            try {

                Date date = sdf1.parse(str);
                return sdf2.format(date);

            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }


        } else {
            return "";
        }
    }

    public static boolean chkDateisBefore(String str1, String str2) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMATDDMMYYYY);
            Date date1 = sdf.parse(str1);
            Date date2 = sdf.parse(str2);

            System.out.println(sdf.format(date1));
            System.out.println(sdf.format(date2));

            if (date1.before(date2)) {
                return true;
            }


        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMATYYYYMMDD, Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getCurrentDateFormat2() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMATDDMMYYYY, Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getCurrentDayMonthYear() {

        Calendar c = Calendar.getInstance();
        int currMonth = c.get(Calendar.MONTH);
        int currYear = c.get(Calendar.YEAR);
        int curDay = c.get(Calendar.DAY_OF_MONTH);


        if(currMonth==0){

            return "January "+String.valueOf(curDay)+", "+String.valueOf(currYear);
        }else if(currMonth==1){
            return "February "+String.valueOf(curDay)+", "+String.valueOf(currYear);
        }else if(currMonth==2){
            return "March "+String.valueOf(curDay)+", "+String.valueOf(currYear);
        }else if(currMonth==3){
            return "April "+String.valueOf(curDay)+", "+String.valueOf(currYear);
        }else if(currMonth==4){
            return "May "+String.valueOf(curDay)+", "+String.valueOf(currYear);
        }else if(currMonth==5){
            return "June "+String.valueOf(curDay)+", "+String.valueOf(currYear);
        }else if(currMonth==6){
            return "July "+String.valueOf(curDay)+", "+String.valueOf(currYear);
        }else if(currMonth==7){
            return "August "+String.valueOf(curDay)+", "+String.valueOf(currYear);
        }else if(currMonth==8){
            return "September "+String.valueOf(curDay)+", "+String.valueOf(currYear);
        }else if(currMonth==9){
            return "October "+String.valueOf(curDay)+", "+String.valueOf(currYear);
        }else if(currMonth==10){
            return "November "+String.valueOf(curDay)+", "+String.valueOf(currYear);
        }else if(currMonth==11){
            return "December "+String.valueOf(curDay)+", "+String.valueOf(currYear);
        }
        else {
            return "";
        }

    }

    public static String getCustomCurrentDayMonthYear(String newDate) {

        Calendar c = Calendar.getInstance();
        String myDate[] = newDate.split("/");
        int currYear = Integer.parseInt(myDate[2]);
        int currMonth = Integer.parseInt(myDate[1])-1;
        int curDay = Integer.parseInt(myDate[0]);


        if(currMonth==0){

            return "January "+String.valueOf(curDay)+", "+String.valueOf(currYear);
        }else if(currMonth==1){
            return "February "+String.valueOf(curDay)+", "+String.valueOf(currYear);
        }else if(currMonth==2){
            return "March "+String.valueOf(curDay)+", "+String.valueOf(currYear);
        }else if(currMonth==3){
            return "April "+String.valueOf(curDay)+", "+String.valueOf(currYear);
        }else if(currMonth==4){
            return "May "+String.valueOf(curDay)+", "+String.valueOf(currYear);
        }else if(currMonth==5){
            return "June "+String.valueOf(curDay)+", "+String.valueOf(currYear);
        }else if(currMonth==6){
            return "July "+String.valueOf(curDay)+", "+String.valueOf(currYear);
        }else if(currMonth==7){
            return "August "+String.valueOf(curDay)+", "+String.valueOf(currYear);
        }else if(currMonth==8){
            return "September "+String.valueOf(curDay)+", "+String.valueOf(currYear);
        }else if(currMonth==9){
            return "October "+String.valueOf(curDay)+", "+String.valueOf(currYear);
        }else if(currMonth==10){
            return "November "+String.valueOf(curDay)+", "+String.valueOf(currYear);
        }else if(currMonth==11){
            return "December "+String.valueOf(curDay)+", "+String.valueOf(currYear);
        }
        else {
            return "";
        }

    }

    public static String getCurrentMonthYear() {

        Calendar c = Calendar.getInstance();
        int currMonth = c.get(Calendar.MONTH);
        int currYear = c.get(Calendar.YEAR);


        if(currMonth==0){
           return "January, "+String.valueOf(currYear);
        }else if(currMonth==1){
            return "February, "+String.valueOf(currYear);
        }else if(currMonth==2){
            return "March, "+String.valueOf(currYear);
        }else if(currMonth==3){
            return "April, "+String.valueOf(currYear);
        }else if(currMonth==4){
            return "May, "+String.valueOf(currYear);
        }else if(currMonth==5){
            return "June, "+String.valueOf(currYear);
        }else if(currMonth==6){
            return "July, "+String.valueOf(currYear);
        }else if(currMonth==7){
            return "August, "+String.valueOf(currYear);
        }else if(currMonth==8){
            return "September, "+String.valueOf(currYear);
        }else if(currMonth==9){
            return "October, "+String.valueOf(currYear);
        }else if(currMonth==10){
            return "November, "+String.valueOf(currYear);
        }else if(currMonth==11){
            return "December, "+String.valueOf(currYear);
        }
        else {
            return "";
        }

    }

    public static String getCurrentYear() {
        Calendar c = Calendar.getInstance();
        //int currMonth = c.get(Calendar.MONTH);
        int currYear = c.get(Calendar.YEAR);

        return String.valueOf(currYear);
    }

    public static int getWeek(String strDate) {
        try {
            Calendar now = Calendar.getInstance();

            SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMATDDMMYYYY, Locale.getDefault());
            Date date = dateFormat.parse(strDate);
            now.setTime(date);
            return now.get(Calendar.WEEK_OF_MONTH);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static String getCurrentTimeAndDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static void showToast(Context context, String strMsg) {
        Toast toast = Toast.makeText(context, strMsg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void showAlert(Context context, String title, String msg) {
        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        adb.setTitle(title);
        adb.setMessage(msg);
        adb.setNegativeButton("Ok", null);
        adb.create().show();
    }

    public static String subtract30Years(String strDate) {
        String strDate30 = "";
        SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMATDDMMYYYY);
        try {
            Date date = sdf.parse(strDate);
            System.out.println(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.YEAR, -30);
            strDate30 = (String.format("%02d", cal.get(Calendar.DATE)) + "/" + String.format("%02d", cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return strDate30;
    }

    public static String getLastDateofMonth() {

        try {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));

            Date lastDayOfMonth = cal.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMATDDMMYYYY);
            return sdf.format(lastDayOfMonth);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getFirstDateofMonth() {

        try {

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            Date firstDayOfMonth = calendar.getTime();


            SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMATDDMMYYYY);
            return sdf.format(firstDayOfMonth);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String capitalize(String capString){
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
        while (capMatcher.find()){
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }

        return capMatcher.appendTail(capBuffer).toString();
    }

}
