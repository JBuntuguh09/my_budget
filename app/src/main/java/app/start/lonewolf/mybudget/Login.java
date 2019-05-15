package app.start.lonewolf.mybudget;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.Arrays;

import app.start.lonewolf.mybudget.Dialogues.SecondaryLogin;
import app.start.lonewolf.mybudget.Resources.Settings;

public class Login extends AppCompatActivity {

    private TextView register;
    private EditText email, password;
    private Button login;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Settings settings;
    private CheckBox keepLoggedIn;
    private LoginButton facebook;
    private CallbackManager callbackManager;
    private LinearLayout linearLayout;
    private SignInButton google;
    private  static int RC_SIGN_IN=1;
    //GoogleSignInOptions mGoogleSignInClient;
    private GoogleSignInClient mGoogleSignInClient;
    private ImageView imgPassword;
    private int passCheck=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        setContentView(R.layout.activity_login);

        linearLayout = findViewById(R.id.linRegister);
        register = findViewById(R.id.txtLoginRegiter);
        email = findViewById(R.id.edtLoginEmail);
        password = findViewById(R.id.edtLoginPassword);
        progressBar = findViewById(R.id.progressLogin);
        settings = new Settings(this);
        keepLoggedIn = findViewById(R.id.checkLoggedin);
        imgPassword = findViewById(R.id.imgPassVisibility);

        facebook = findViewById(R.id.login_button);
        callbackManager = CallbackManager.Factory.create();
        facebook.setReadPermissions(Arrays.asList("email"));

        google = findViewById(R.id.btnGoogleSignin);


        login =findViewById(R.id.btnLoginLogin);
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        if(settings.getCURRENTBUDGET().equals("")){
            settings.setCURRENTBUDGET("First Budget");
        }

        getButtons();
    }

    private void getButtons() {

        imgPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(passCheck==0){
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    imgPassword.setImageResource(R.drawable.hide_password);
                    passCheck=1;
                }else {
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    imgPassword.setImageResource(R.drawable.show_password);
                    passCheck=0;
                }
            }
        });


        if(settings.getLofinIndicator()==true){
            keepLoggedIn.setChecked(true);
        }else {
            keepLoggedIn.setChecked(false);
        }

        if(auth.getCurrentUser()!=null && settings.getLofinIndicator()){
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            finish();
        }else {
            LoginManager.getInstance().logOut();
        }
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        email.setEnabled(false);
                        password.setEnabled(false);
                        login.setEnabled(false);
                        register.setEnabled(false);
                        progressBar.setVisibility(View.VISIBLE);
                        handleFacebookToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        email.setEnabled(true);
                        password.setEnabled(true);
                        login.setEnabled(true);
                        register.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(Login.this, R.string.cancelled, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        email.setEnabled(true);
                        password.setEnabled(true);
                        login.setEnabled(true);
                        register.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(Login.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });


        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email.setEnabled(false);
                password.setEnabled(false);
                login.setEnabled(false);
                register.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                googleSignIn();
            }
        });






        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
                //SecondaryLogin.showLogin(Login.this, linearLayout);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().toString().isEmpty()){
                    Toast.makeText(Login.this, "Enter an Email", Toast.LENGTH_SHORT).show();
                }else if(password.getText().toString().isEmpty()){
                    Toast.makeText(Login.this, "Enter a password", Toast.LENGTH_SHORT).show();
                }else{
                    email.setEnabled(false);
                    password.setEnabled(false);
                    login.setEnabled(false);
                    register.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);
                    loginToApp();
                }
            }
        });
    }

    private void handleFacebookToken(AccessToken accessToken) {
        settings.setLogiIndicator(true);
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        auth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChildren()){
                           if(dataSnapshot.getValue().toString().contains(auth.getCurrentUser().getUid())){
                               //Toast.makeText(Login.this, "Yes", Toast.LENGTH_SHORT).show();
                               progressBar.setVisibility(View.GONE);
                               checkDatabase();
                           }else
                           {
                               progressBar.setVisibility(View.GONE);
                               //Toast.makeText(Login.this, "No", Toast.LENGTH_SHORT).show();
                               setupAccount();
                           }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private void setupAccount() {
        if(settings.getPERIOD().equals("")) {
            settings.setPERIOD("Month");
        }

        settings.setLogiIndicator(true);
        SecondaryLogin.showLogin(Login.this, linearLayout);
    }

    private void loginToApp() {
        String eEmail = email.getText().toString();
        String pPassword = password.getText().toString();
        auth.signInWithEmailAndPassword(eEmail, pPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {


                if(keepLoggedIn.isChecked()){
                    settings.setLogiIndicator(true);
                }else {
                    settings.setLogiIndicator(false);
                }
                checkDatabase();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                email.setEnabled(true);
                password.setEnabled(true);
                login.setEnabled(true);
                register.setEnabled(true);
                Toast.makeText(Login.this, e.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void checkDatabase() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(auth.getCurrentUser().getUid());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("budget_name").exists()){
                    progressBar.setVisibility(View.GONE);
                    email.setEnabled(true);
                    password.setEnabled(true);
                    login.setEnabled(true);
                    register.setEnabled(true);
                    settings.setCURRENTBUDGET(dataSnapshot.child("budget_name").getValue().toString());

                    Intent intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    progressBar.setVisibility(View.GONE);
                    email.setEnabled(true);
                    password.setEnabled(true);
                    login.setEnabled(true);
                    register.setEnabled(true);
                    Toast.makeText(Login.this, "Unable to login", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Login.this, databaseError.toString(), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                email.setEnabled(true);
                password.setEnabled(true);
                login.setEnabled(true);
                register.setEnabled(true);
            }
        });
    }

    private void googleSignIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        //Log.d("tag", "firebaseAuthWithGoogle:" + acct.getId());
        settings.setLogiIndicator(true);
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("tag", "signInWithCredential:success");
                            //FirebaseUser user = auth.getCurrentUser();
                            //SecondaryLogin.showLogin(Login.this, linearLayout);
                            databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChildren()){
                                        if(dataSnapshot.getValue().toString().contains(auth.getCurrentUser().getUid())){
                                            //Toast.makeText(Login.this, "Yes", Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                            checkDatabase();
                                        }else
                                        {
                                            progressBar.setVisibility(View.GONE);
                                            //Toast.makeText(Login.this, "No", Toast.LENGTH_SHORT).show();
                                            setupAccount();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    email.setEnabled(true);
                                    password.setEnabled(true);
                                    login.setEnabled(true);
                                    register.setEnabled(true);
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(Login.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
//                            Log.w("tag", "signInWithCredential:failure", task.getException());
                            //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            email.setEnabled(true);
                            password.setEnabled(true);
                            login.setEnabled(true);
                            register.setEnabled(true);
                            progressBar.setVisibility(View.GONE);
                            setupAccount();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {


            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);


            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                // ...
                e.printStackTrace();
                email.setEnabled(true);
                password.setEnabled(true);
                login.setEnabled(true);
                register.setEnabled(true);
                progressBar.setVisibility(View.GONE);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


}
