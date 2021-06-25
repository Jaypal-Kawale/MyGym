package com.example.mygym;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
    private static final String TAG ="MainActivity" ;
    private Button btn;
    Bitmap photo;
    EditText name;
    EditText money;
    EditText total;
    EditText mob;
    EditText rno;
    TextView fdate,jdate;
    CircleImageView imageView;
    DatePicker datePicker,datePicker2;
    private RadioGroup radioGroup;
    public static final int IMG_REQUEST=777;
    private Bitmap bitmap;
    Uri path;
    String gymname,uid;
    String url="Hello";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage firebaseStorage= FirebaseStorage.getInstance();
    StorageReference storageReference=firebaseStorage.getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn=findViewById(R.id.add);
        total=findViewById(R.id.totalfee);
        fdate=findViewById(R.id.fdate);
        jdate=findViewById(R.id.jdate);
        name=findViewById(R.id.name);
        money=findViewById(R.id.money);
        rno=findViewById(R.id.rno);
        imageView=findViewById(R.id.profileimage);
        mob=findViewById(R.id.mob);
        gymname=getIntent().getStringExtra("name");
        uid=getIntent().getStringExtra("uid");
        //batch=findViewById(R.id.batch);
        radioGroup = (RadioGroup)findViewById(R.id.groupradio);
        jdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callDialog(jdate);
            }
        });
        fdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callDialog(fdate);
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton
                        = (RadioButton)radioGroup
                        .findViewById(selectedId);
                Timestamp timestamp=Timestamp.now();
                String sp=timestamp.toString();
                Log.d(TAG, "onClick: "+timestamp.getSeconds());
              //  SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy kk:mm");

                String dateString = formatter.format(new Date(timestamp.toDate().toString()));
              //  client c=new client(("Jaypal"));
                Log.d(TAG, "onClick: "+dateString);
                String sdl="0";
                if(money.getText().toString().equals(""))
                {
                   sdl="0";
                }else {
                    sdl=money.getText().toString();

                }

                Client c=new Client(name.getText().toString(),url,jdate.getText().toString(),fdate.getText().toString(),sdl
                ,radioButton.getText().toString(),mob.getText().toString(),dateString,total.getText().toString(),rno.getText().toString());
             //   applytogym(c);
                if(uid==null)
                {
                    db.collection(gymname).add(c).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "onSuccess: "+"adding document from gym owner");
                            Toast.makeText(MainActivity.this,"Document added Successfully",Toast.LENGTH_SHORT).show();
                        }
                    });
                    finish();
                    Log.d(TAG, "onClick: "+"Inside uid is null");
                }else{
                    db.collection(gymname).document(uid).set(c).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(MainActivity.this,"Document added Successfully",Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onSuccess: "+"user Added Successfully");
                        }
                    });
                    finish();
                    Log.d(TAG, "onClick: "+"outside uid is not null");

                }


//                Log.d(TAG, "onClick: "+getdate());
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 capture();
                selectImage();
            }
        });
    }

//    private void applytogym(Client c) {
//        List<Client> list=new ArrayList<>();
//        list.add(c);
//        db.collection("gyms").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                for (DocumentSnapshot documentSnapshot :queryDocumentSnapshots)
//                {
//                    gym_model m=documentSnapshot.toObject(gym_model.class);
//                    if(m.user!=null)
//                    {
//                        if(m.user.isEmpty())
//                        {
//                            Log.d(TAG, "onSuccess:checking ulist "+"is empty");
//                        }
//                        else{
//                            Log.d(TAG, "onSuccess:checking ulist "+"is not empty");
//                            Log.d(TAG, "onSuccess: "+m.getUser());
//                            list.addAll(m.getUser());
//                        }
//
//                    }
//                    if(m.getName().equals(gymname))
//                    {
//                        documentSnapshot.getReference().update("user",list).addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void unused) {
//                                Log.d(TAG, "onSuccess: "+"updated successfully");
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure( Exception e) {
//                                Log.d(TAG, "onFailure: failed to upload"+e.toString());
//                            }
//                        });
//                    }
//                }
//            }
//        });
//
//
//    }

    private void callDialog(TextView textView) {
        AlertDialog.Builder builder
                = new AlertDialog.Builder(this);
        builder.setTitle("Select Date");

        // set the custom layout
        final View customLayout
                = getLayoutInflater()
                .inflate(
                        R.layout.date,
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
                                DatePicker datePicker=customLayout.findViewById(R.id.datepicker);
                                String str=getdate(datePicker);
                                textView.setText(str);

                            }
                        });
        AlertDialog dialog
                = builder.create();
        dialog.show();
    }

    String getdate(DatePicker datePicker)
    {
        StringBuilder builder=new StringBuilder();;
        builder.append(datePicker.getDayOfMonth()+"/");
        builder.append((datePicker.getMonth() + 1)+"/");//month is 0 based
        builder.append(datePicker.getYear());
        return builder.toString();

    }   private void selectImage()
    {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMG_REQUEST);
    }
    private void capture()
    {
        Intent intent1=new Intent();
        intent1.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent1.resolveActivity(getPackageManager())!=null)
            startActivityForResult(intent1,200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode==RESULT_OK&&requestCode==IMG_REQUEST&&data!=null)
        {
            path=data.getData();
            upload_img(path);
            Log.d(TAG, "onActivityResult: "+path);
            try {
                bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),path);
                imageView.setImageBitmap(bitmap);
                imageView.setVisibility(View.VISIBLE);
                //imag_title.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }  else if (requestCode==200&&resultCode==RESULT_OK)
        {
            path=data.getData();
            Bundle extras=data.getExtras();
            Bitmap bitmap= (Bitmap) extras.get("data");
            photo=bitmap;
            imageView.setImageBitmap(bitmap);
            imageView.setVisibility(View.VISIBLE);
            //upload_img(path);
            submit();

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void submit(){

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap resized = Bitmap.createScaledBitmap(photo, 800, 1100, true);

        resized.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//        photo.setHeight(500);
//        photo.setWidth(400);
        Bitmap yourBitmap;

        final ProgressDialog progressDialog
                = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        byte[] b = stream.toByteArray();
        final StorageReference ref
                = storageReference
                .child(
                        "images/"
                                + UUID.randomUUID().toString());

        StorageReference storageReference =FirebaseStorage.getInstance().getReference().child("documentImages").child("noplateImg");
        //StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_images").child(userID);
        ref.putBytes(b).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

               // Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Toast.makeText(MainActivity.this, "uploaded", Toast.LENGTH_SHORT).show();
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        url=uri.toString();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: "+"Url is not "+e.toString());
                    }
                });
                progressDialog.dismiss();
                Toast
                        .makeText(MainActivity.this,
                                "Image Uploaded!!",
                                Toast.LENGTH_SHORT)
                        .show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,"failed",Toast.LENGTH_LONG).show();


            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress
                        = (100.0
                        * snapshot.getBytesTransferred()
                        / snapshot.getTotalByteCount());
                progressDialog.setMessage(
                        "Uploaded "
                                + (int) progress + "%");
            }
        });

    }
    private void upload_img(Uri path) {
        if (path != null){
            Log.d(TAG, "upload_img: " + "path is null");

            final ProgressDialog progressDialog
                    = new ProgressDialog(this);
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
//                                            db.collection("user").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update("link",uri.toString());
//                                            Log.d(TAG, "onSuccess: "+"Url is"+ uri.toString());
                                            url=uri.toString();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailure: "+"Url is not "+e.toString());
                                        }
                                    });
                                    progressDialog.dismiss();
                                    Toast
                                            .makeText(MainActivity.this,
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
                                    .makeText(MainActivity.this,
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
        else
            Toast.makeText(MainActivity.this,"path is null",Toast.LENGTH_SHORT).show();
    }


}