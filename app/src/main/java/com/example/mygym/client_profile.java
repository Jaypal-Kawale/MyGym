package com.example.mygym;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

public class client_profile extends Fragment {
    public static final int IMG_REQUEST=777;
    private static final int RESULT_OK = 200;
    private Bitmap bitmap;
    Uri path;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    FirebaseStorage firebaseStorage= FirebaseStorage.getInstance();
    StorageReference storageReference=firebaseStorage.getReference();
    ProgressBar progressBar;
    ImageButton btn,editname;
    TextView name,pname,pmob,pemail;

    Button chagemob,chagepass,logout,share;
    CircleImageView imageView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_client_profile, container, false);

        imageView=view.findViewById(R.id.profileimage);
        name=view.findViewById(R.id.proname);
        editname=view.findViewById(R.id.editname);
        chagemob=view.findViewById(R.id.change_mob);
        chagepass=view.findViewById(R.id.change_pass);
        logout=view.findViewById(R.id.logout);
        share=view.findViewById(R.id.share);
        pname=view.findViewById(R.id.pname);
        pmob=view.findViewById(R.id.pmob);
        pemail=view.findViewById(R.id.pemail);
        chagepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                change_password();
            }
        });
        chagemob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                change_mobile();
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT,"https://play.google.com/store/apps/details?id=");
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent,"share via"));

            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user == null) {
                    Intent i = new Intent(getContext(), login.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                } else {
                    Log.d((String) TAG, "onAuthStateChanged:signed_out");
                }
            }
        });
        init();
        editname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call_editname();
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"Clicked",Toast.LENGTH_LONG).show();
                selectImage();
            }
        });
        return view;
    }
    private void init()
    {
        db.collection("user").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user=documentSnapshot.toObject(User.class);
                String str="Hello"+" "+user.getFullname();
                name.setText(str);
                pname.setText(user.getFullname());
                pmob.setText(user.getMob());
                pemail.setText(user.getEmail());
                if (user.link.equals("default"))
                {

                }
                else
                {
                    Picasso.get().load(user.link).into(imageView);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: "+e.toString());
            }
        });
    }
    private void call_editname() {
        AlertDialog.Builder builder
                = new AlertDialog.Builder(getContext());
        builder.setTitle("Enter Name");
        final View customLayout
                = getLayoutInflater()
                .inflate(
                        R.layout.alert_editname,
                        null);
        builder.setView(customLayout);
        builder
                .setPositiveButton(
                        "Update",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(
                                    DialogInterface dialog,
                                    int which)
                            {
                                EditText editText
                                        = customLayout
                                        .findViewById(
                                                R.id.editText);
                                if (editText.getText().toString()!=null&&editText.getText().toString().length()>0)
                                {
                                    sendDialogDatatoActivity(
                                            editText
                                                    .getText()
                                                    .toString());
                                }
                                else {

                                    Toast.makeText(getContext(),
                                            "Enter Name",
                                            Toast.LENGTH_SHORT)
                                            .show();

                                }

                            }
                        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog dialog
                = builder.create();
        dialog.show();


    }
    private void sendDialogDatatoActivity(String data)
    {
        db.collection("user").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update("fullname",data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                init();
                Toast.makeText(getContext(),
                        "Name updated Successfully",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    private void selectImage()
    {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMG_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode==RESULT_OK&&requestCode==IMG_REQUEST&&data!=null)
        {
            path=data.getData();
            upload_img(path);
            Log.d(TAG, "onActivityResult: "+path);
            try {
                bitmap= MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),path);
                imageView.setImageBitmap(bitmap);
                imageView.setVisibility(View.VISIBLE);
                //imag_title.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void upload_img(Uri path) {
        if (path != null){
            Log.d(TAG, "upload_img: " + "path is null");

            final ProgressDialog progressDialog
                    = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final StorageReference ref
                    = storageReference
                    .child(
                            "images/"
                                    + UUID.randomUUID().toString());


            ref.putFile(path)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot) {

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            db.collection("user").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update("link",uri.toString());
                                            Log.d(TAG, "onSuccess: "+"Url is"+ uri.toString());

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailure: "+"Url is not "+e.toString());
                                        }
                                    });
                                    progressDialog.dismiss();
                                    Toast
                                            .makeText(getContext(),
                                                    "Image Uploaded!!",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText(getContext(),
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int) progress + "%");
                                }
                            });
        }
    }

    private void change_mobile() {
        AlertDialog.Builder builder
                = new AlertDialog.Builder(getContext());
        builder.setTitle("Change Mob no.");

        // set the custom layout
        final View customLayout
                = getLayoutInflater()
                .inflate(
                        R.layout.alert_editname,
                        null);
        builder.setView(customLayout);
        builder
                .setPositiveButton(
                        "Update",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(
                                    DialogInterface dialog,
                                    int which)
                            {
                                final EditText editText
                                        = customLayout
                                        .findViewById(
                                                R.id.editText);
                                String str=editText.getText().toString();
                                if (str!=null&&editText.getText().toString().length()>0)
                                {
                                    db.collection("user").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update("mob",editText.getText().toString())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(getContext(), "Mobile no. updated successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(), "Failed to update Mobile no. ", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                else {

                                    Toast.makeText(getContext(), "Enter Mobile no. ", Toast.LENGTH_SHORT).show();
                                    change_mobile();
                                }

                            }
                        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog dialog
                = builder.create();
        dialog.show();
    }
    private void change_password() {
        AlertDialog.Builder builder
                = new AlertDialog.Builder(getContext());
        builder.setTitle("Enter current Password");
        final View customLayout
                = getLayoutInflater()
                .inflate(
                        R.layout.alert_editname,
                        null);
        builder.setView(customLayout);
        builder
                .setPositiveButton(
                        "login",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(
                                    DialogInterface dialog,
                                    int which)
                            {
                                EditText editText
                                        = customLayout
                                        .findViewById(
                                                R.id.editText);
                                if (editText.getText()!=null&&editText.getText().toString().length()>0)
                                {

                                    FirebaseAuth.getInstance().signInWithEmailAndPassword(FirebaseAuth.getInstance().getCurrentUser().getEmail().toString(),editText.getText().toString())
                                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                                @Override
                                                public void onSuccess(AuthResult authResult) {
                                                    callchage_password();
                                                    Log.d(TAG, "onFailure: "+"success i login");
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(), "Invalid Password,Failed to change password", Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "onFailure: "+e.toString());
                                        }
                                    });
                                    change_password();
                                }
                                else{
                                    Toast.makeText(getContext(), "Enter current password", Toast.LENGTH_SHORT).show();
                                    change_password();
                                }
                            }
                        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        // create and show
        // the alert dialog
        AlertDialog dialog
                = builder.create();
        dialog.show();
    }

    private void callchage_password() {
        AlertDialog.Builder builder
                = new AlertDialog.Builder(getContext());
        builder.setTitle("Enter new Password");

        // set the custom layout
        final View customLayout
                = getLayoutInflater()
                .inflate(
                        R.layout.alert_editname,
                        null);
        builder.setView(customLayout);

        // add a button
        builder
                .setPositiveButton(
                        "Change",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(
                                    DialogInterface dialog,
                                    int which)
                            {

                                // send data from the
                                // AlertDialog to the Activity
                                final EditText editText
                                        = customLayout
                                        .findViewById(
                                                R.id.editText);
                                String str=editText.getText().toString();
                                if (str!=null&&str.length()>0)
                                {
                                    FirebaseAuth.getInstance().getCurrentUser().updatePassword(str).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getContext(), "Password Changed", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailure: "+"fail to change password"+e.toString());
                                            Toast.makeText(getContext(), "Failed to change password. "+e.toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                else
                                {
                                    Toast.makeText(getContext(), "Enter  new password", Toast.LENGTH_SHORT).show();
                                    callchage_password();
                                }

                            }
                        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        // create and show
        // the alert dialog
        AlertDialog dialog
                = builder.create();
        dialog.show();
    }

}