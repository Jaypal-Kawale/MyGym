package com.example.mygym;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class login extends AppCompatActivity implements FirebaseAuth.AuthStateListener {
    private static final String TAG = "login";
    Button callSignUp,Signin,fgtPass;
    TextInputLayout reg_password,reg_email;
    private FirebaseAuth mAuth;
    boolean doubleBackToExitPressedOnce = false;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    FirebaseStorage firebaseStorage= FirebaseStorage.getInstance();
    StorageReference storageReference=firebaseStorage.getReference();
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "onCreate: "+"Hello ");
        Signin = (Button) findViewById(R.id.ssignIn);
        callSignUp = (Button) findViewById(R.id.sbtnSignup);
        reg_email = (TextInputLayout) findViewById(R.id.slogin_email);
        reg_password = (TextInputLayout) findViewById(R.id.slogin_password);
        fgtPass = (Button) findViewById(R.id.sbtn_fgt);
        mAuth = FirebaseAuth.getInstance();

//        user = mAuth.getCurrentUser();
        Log.d(TAG, "onCreate: "+"inside");
        Log.d(TAG, "onCreate: "+"My name is jay");
        callSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(login.this,check.class);
                startActivity(intent);
            }
        });
    }

    public void SignIn(View view)
    {
        Log.d(TAG, "onCreate: "+"Hello ");
        Log.d((String) TAG, "onAuthStateChanged:signed_out");
        String email = reg_email.getEditText().getText().toString();
        String password = reg_password.getEditText().getText().toString();
        // Initialize Firebase Auth

        Log.d(TAG, "SignIn: "+FirebaseAuth.getInstance().getUid());
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            Log.d(TAG, "onComplete: "+"login is successful");
                            if(mAuth.getCurrentUser().isEmailVerified()) {
                                final int[] check = {0};
                                final int[] make = {0};
                                reg_email.getEditText().setText("");
                                reg_password.getEditText().setText("");
                                Log.d(TAG, "SignIn: "+FirebaseAuth.getInstance().getUid());
                                db.collection("user").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                       for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots)
                                       {
                                           if(FirebaseAuth.getInstance().getUid().equals(documentSnapshot.getId()))
                                           {
                                               Log.d(TAG, "onSuccess: "+documentSnapshot.getId());
                                               Toast.makeText(getApplicationContext(),"Login Successfull",Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(login.this,client_home.class);
                                             startActivity(intent);
                                             finish();
                                             break;

                                           }
                                       }
                                    }
                                });

                                db.collection("gyms").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots)
                                        {
                                            if(FirebaseAuth.getInstance().getUid().equals(documentSnapshot.getId()))
                                            {
                                                Log.d(TAG, "onSuccess: "+documentSnapshot.getId());
                                                Toast.makeText(getApplicationContext(),"Login Successfull",Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(login.this,bottom.class);
                                                startActivity(intent);
                                                finish();
                                                break;

                                            }
                                        }
                                    }
                                });
                            }
                        }
                        else
                        {
                            reg_email.getEditText().setText("");
                            reg_password.getEditText().setText("");
                            Toast.makeText(getApplicationContext(),"Login Not Successfull",Toast.LENGTH_LONG).show();

                        }

                        // ...
                    }
                });


    }

    public void resetPassword(View view) {
        final String email = reg_email.getEditText().getText().toString();
        if (email.length()==0){
            Toast.makeText(login.this,"Enter Email",Toast.LENGTH_SHORT).show();
        }
        else {
            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();
            final EditText resetPass = new EditText(view.getContext());
            final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
            passwordResetDialog.setTitle("Reset Password ?");
            passwordResetDialog.setMessage("Enter New Password(Greater than 6)");
            passwordResetDialog.setView(resetPass);
            db.collection("user").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    int i=0;
                    for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments())
                    {
                        User user=documentSnapshot.toObject(User.class);
                        if (email.toLowerCase().trim().equals(user.getEmail().toLowerCase().trim()))
                        {
                            i=1;
                            FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getApplicationContext(),"Password Reset Link sent on email",Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(),"Password Reset failed",Toast.LENGTH_LONG).show();
                                }
                            });
                            break;
                        }
                    }
                    if (i==0)
                    {
                        Toast.makeText(login.this,"your email is not registered or verified",Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

        }


    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            if(mAuth.getCurrentUser().isEmailVerified()) {
             //   finish();
                db.collection("user").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots)
                        {
                            if(FirebaseAuth.getInstance().getUid().equals(documentSnapshot.getId()))
                            {

                                Log.d(TAG, "onSuccess: "+documentSnapshot.getId());
                                Toast.makeText(getApplicationContext(),"Login Successfull",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(login.this,client_home.class);
                                startActivity(intent);
                                finish();
                                break;

                            }
                        }
                    }
                });

                db.collection("gyms").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots)
                        {
                            if(FirebaseAuth.getInstance().getUid().equals(documentSnapshot.getId()))
                            {
                                Log.d(TAG, "onSuccess: "+documentSnapshot.getId());
                                Toast.makeText(getApplicationContext(),"Login Successfull",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(login.this,bottom.class);
                                startActivity(intent);
                                finish();
                                break;

                            }
                        }
                    }
                });



            }
            else
            {
            }
        }
        else
        {
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}