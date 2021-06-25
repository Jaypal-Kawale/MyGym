package com.example.mygym;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class signup extends AppCompatActivity {
    private static final String TAG = "sign up";
    TextInputLayout name,reg_username,reg_password,reg_email,phoneNo;
    Button signUp;
    String email,password;
    AlertDialog.Builder builder;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    FirebaseStorage firebaseStorage= FirebaseStorage.getInstance();
    StorageReference storageReference=firebaseStorage.getReference();
    //ProgressBar pBar;
    private FirebaseAuth mAuth;
    int mode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mode=getIntent().getIntExtra("mode",0);
        reg_email = (TextInputLayout) findViewById(R.id.eMail);
        reg_password = (TextInputLayout) findViewById(R.id.passWord);
        name=(TextInputLayout)findViewById(R.id.fullName);
        phoneNo=(TextInputLayout)findViewById(R.id.mobileNumber);
        if (mode==1)
        {
            name.setHint("Gym Name");
            phoneNo.setHint("Location");


        }

    }
    public void Signup(View view)
    {
        email = reg_email.getEditText().getText().toString();
        password = reg_password.getEditText().getText().toString();
        //final String username = reg_username.getEditText().getText().toString();
        final String fname=name.getEditText().getText().toString();
        final String mo=phoneNo.getEditText().getText().toString();
        mAuth = FirebaseAuth.getInstance();
        if(password.length() <= 6)
        {
            reg_password.setError("Password too short");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(signup.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful())
                        {

                            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        if(mode==0)
                                        {
                                            User user=new User("default",email,fname,mo,"default");
                                            db.collection("user").document(mAuth.getUid()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    refreshtoken token=new refreshtoken();
                                                    FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                                                        @Override
                                                        public void onComplete( Task<String> task) {
                                                            Log.d(TAG, "onComplete:token "+task.getResult());
                                                            token.setToken(task.getResult());
                                                            db.collection("token").document(FirebaseAuth.getInstance().getUid()).set(token).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    Log.d(TAG, "onSuccess: "+"token Added Successfully");
                                                                }
                                                            });
                                                        }
                                                    });

                                                    Toast.makeText(signup.this, "User data Added", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(signup.this, e.toString(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }else{
                                            gym_model m=new gym_model(fname,mo,"image link",null,"It is nice gym",email,null);
                                            db.collection("gyms")
                                                    .document(mAuth.getUid()).set(m).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(signup.this, "User data Added", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure( Exception e) {
                                                    Toast.makeText(signup.this, e.toString(), Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        }

                                        reg_password.getEditText().setText("");
                                        Toast.makeText(getApplicationContext(),"Registration Successfull... Please Verify Your Email",Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(signup.this,login.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else
                                    {
                                        Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                        else
                        {
                            reg_password.getEditText().setText("");
                            Toast.makeText(getApplicationContext(),"Registration Not Successfull",Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }
}