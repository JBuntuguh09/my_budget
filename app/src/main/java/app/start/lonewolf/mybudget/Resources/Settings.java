package app.start.lonewolf.mybudget.Resources;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Settings {

    private final String SET_NAME = "storage variable";
    private SharedPreferences sharedPreferences;
    private Settings settings;
    private final String USERNAME = "yourName";
    private final String PERIOD = "period";
    private final String CURRENCY = "currency";
    private final String LOGININDICATOR = "loginindicator";
    private final String TYPE = "bType";
    private final String CURRENTBUDGET = "currentBudget";
    private final String CUSTOMDATEINDICATOR = "Customdateindicato";
    private final String CUSTOMDATE = "customDate";
    private final String REFERENCECODE = "refCode";
    private final String REFERENCES = "references";
    private final String CHECKHELP = "check help";
    private final String VAR1 = "var1";
    private final String VAR2 = "var2";
    private final String VAR3 = "var3";
    private final String VAR4 = "var4";
    private final String VAR5 = "var5";
    private final String VAR6 = "var6";
    private final String DATECHECK = "date check";



    public Settings(Context context){
        sharedPreferences = context.getSharedPreferences(SET_NAME, Context.MODE_PRIVATE);
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public Editor getEditor(){
        Editor editor = sharedPreferences.edit();
        return editor;
    }

    public String getUSERNAME(){
        return sharedPreferences.getString(USERNAME, "");
    }

    public void setUSERNAME(String username){
        Editor editor = getEditor();
        editor.putString(USERNAME, username);
        editor.commit();
    }

    public String getCURRENCY() {
        return sharedPreferences.getString( CURRENCY, "");
    }

    public void setCURRENCY(String currency){
        Editor editor = getEditor();
        editor.putString(CURRENCY, currency);
        editor.commit();
    }

    public String getPERIOD() {
        return sharedPreferences.getString( PERIOD,"");
    }

    public void setPERIOD(String period){
        Editor editor = getEditor();
        editor.putString(PERIOD, period);
        editor.commit();
    }

    public boolean getLofinIndicator(){
        return sharedPreferences.getBoolean(LOGININDICATOR, false);
    }

    public void setLogiIndicator(boolean logiIndicator){
        Editor editor = getEditor();
        editor.putBoolean(LOGININDICATOR, logiIndicator);
        editor.commit();
    }

    public String getTYPE() {
        return sharedPreferences.getString(TYPE, "");
    }

    public void setTYPE(String type){
        Editor editor = getEditor();
        editor.putString(TYPE, type);
        editor.commit();
    }

    public String getCURRENTBUDGET(){
        return sharedPreferences.getString(CURRENTBUDGET, "");
    }

    public void setCURRENTBUDGET(String currentbudget){
        Editor editor = getEditor();
        editor.putString(CURRENTBUDGET, currentbudget);
        editor.commit();
    }

    public boolean getCUSTOMDATEINDICATOR(){
        return sharedPreferences.getBoolean(CUSTOMDATEINDICATOR, false);
    }

    public void setCUSTOMDATEINDICATOR(boolean customdateindicator){
        Editor editor = getEditor();
        editor.putBoolean(CUSTOMDATEINDICATOR, customdateindicator);
        editor.commit();
    }


    public String getCUSTOMDATE(){
        return sharedPreferences.getString(CUSTOMDATE, "");
    }

    public void setCUSTOMDATE(String customdate){
        Editor editor = getEditor();
        editor.putString(CUSTOMDATE, customdate);
        editor.commit();
    }

    public String getREFERENCECODE(){
        return sharedPreferences.getString(REFERENCECODE, "");
    }

    public void setREFERENCECODE(String referencecode){
        Editor editor = getEditor();
        editor.putString(REFERENCECODE, referencecode);
        editor.commit();
    }

    public String getREFERENCES(){
        return sharedPreferences.getString(REFERENCES, "");
    }

    public void setREFERENCES(String references){
        Editor editor = getEditor();
        editor.putString(REFERENCES, references);
        editor.commit();
    }

    public String getCHECKHELP(){
        return sharedPreferences.getString(CHECKHELP, "");
    }

    public void setCHECKHELP(String checkhelp){
        Editor editor = getEditor();
        editor.putString(CHECKHELP, checkhelp);
        editor.commit();
    }


    /////////Temporary values   ////////////////////////////
    public String getVAR1(){
        return sharedPreferences.getString(VAR1, "");
    }

    public void setVAR1(String var1){
        Editor editor = getEditor();
        editor.putString(VAR1, var1);
        editor.commit();
    }

    public String getVAR2(){
        return sharedPreferences.getString(VAR2, "");
    }

    public void setVAR2(String var2){
        Editor editor = getEditor();
        editor.putString(VAR2, var2);
        editor.commit();
    }

    public String getVAR3(){
        return sharedPreferences.getString(VAR3, "");
    }

    public void setVAR3(String var3){
        Editor editor = getEditor();
        editor.putString(VAR3, var3);
        editor.commit();
    }

    public String getVAR4(){
        return sharedPreferences.getString(VAR4, "");
    }

    public void setVAR4(String var4){
        Editor editor = getEditor();
        editor.putString(VAR4, var4);
        editor.commit();
    }

    public String getVAR5(){
        return sharedPreferences.getString(VAR5, "");
    }

    public void setVAR5(String var5){
        Editor editor = getEditor();
        editor.putString(VAR5, var5);
        editor.commit();
    }

    public String getVAR6(){
        return sharedPreferences.getString(VAR6, "");
    }

    public void setVAR6(String var6){
        Editor editor = getEditor();
        editor.putString(VAR6, var6);
        editor.commit();
    }

    public boolean getDATECHECK(){
        return sharedPreferences.getBoolean(DATECHECK, false);
    }

    public void setDATECHECK(boolean datecheck){
        Editor editor = getEditor();
        editor.putBoolean(DATECHECK, datecheck);
        editor.commit();
    }

////////////////////////////////////////////////////////////////////
}
