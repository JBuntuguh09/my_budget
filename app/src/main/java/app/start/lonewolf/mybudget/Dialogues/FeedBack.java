package app.start.lonewolf.mybudget.Dialogues;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import app.start.lonewolf.mybudget.R;
import app.start.lonewolf.mybudget.Resources.Settings;

public class FeedBack {


    private static DatabaseReference databaseReference;
    private static Settings settings;
    private static String userId;
    private static AlertDialog dialog;

    public static void showFeedBack(final Activity activity, LinearLayout linearLayout){
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View view = layoutInflater.inflate(R.layout.layout_feedback, linearLayout, false);

        settings = new Settings(activity);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("ratingsInfo");



        final Button submit = view.findViewById(R.id.btnFeedbackSubmit);
        final EditText comment = view.findViewById(R.id.txtFeedbackComment);
        final RatingBar rate = view.findViewById(R.id.ratingBar);
        final ProgressBar progressBar = view.findViewById(R.id.progressFeedback);

        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setView(view);

        dialog = alert.create();
        dialog.show();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if((rate.getRating() == 0.00) &&  comment.getText().toString().isEmpty()){
                    Toast.makeText(activity, "Input feedback", Toast.LENGTH_SHORT).show();
                }else{
                    progressBar.setVisibility(View.VISIBLE);
                    rate.setEnabled(false);
                    comment.setEnabled(false);
                    submit.setEnabled(false);
                    rating(comment, rate, submit, progressBar, activity);
                }


            }
        });

    }


    private static void rating(final EditText editText, final RatingBar ratingBar, final Button button, final ProgressBar progressBar, final Activity activity){
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {


                    int ratingId = Integer.parseInt(dataSnapshot.child("rateId").getValue().toString());
                    final int newId = ratingId + 1;

                    final HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("rating_Id", String.valueOf(newId));
                    if (!editText.getText().toString().isEmpty()) {
                        hashMap.put("comment", editText.getText().toString());
                    } else {
                        hashMap.put("comment", "");
                    }
                    hashMap.put("stars", String.valueOf(ratingBar.getRating()));
                    hashMap.put("name", settings.getUSERNAME());

                    databaseReference.child("rateId").setValue(newId).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            databaseReference.child("ratings").child(userId).child(String.valueOf(newId)).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {


                                    Toast.makeText(activity, "Successfully sent your feedback. Thank you", Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    editText.setEnabled(true);
                                    progressBar.setVisibility(View.GONE);
                                    button.setEnabled(true);
                                    ratingBar.setEnabled(true);
                                    Toast.makeText(activity, "Sorry, feedback was not sent sent. Please try again", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            editText.setEnabled(true);
                            progressBar.setVisibility(View.GONE);
                            button.setEnabled(true);
                            ratingBar.setEnabled(true);
                            Toast.makeText(activity, "Sorry, feedback was not sent sent. Please try again. " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                editText.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                button.setEnabled(true);
                ratingBar.setEnabled(true);
                Toast.makeText(activity, "Sorry, feedback was not sent sent. Please try again. "+databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }
}
