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
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;


public class profile extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextView hos_name,abt_hos,abt_hos_desc,hos_location,hos_lbl,rerating,reperson;
    ImageView img,starimg;
    String cname,lname,nam;
    Toolbar toolbar;
    Button btn,btnrat,btnlogout;
    public static final int IMG_REQUEST=777;
    private Bitmap bitmap;
    Uri path;
    CardView cardView;
    private static final String TAG = "Medicine_center_details";
    RecyclerView recyclerView;
    List<gym_model> hospitals=new ArrayList<>();
    gym_model h;
    String url="Hello";
    Bitmap photo;
    Button addpackage;
    FirebaseStorage firebaseStorage= FirebaseStorage.getInstance();
    StorageReference storageReference=firebaseStorage.getReference();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_profile, container, false);
        btn=view.findViewById(R.id.showmap);
        // btnrat=findViewById(R.id.showrate);
        cardView=view.findViewById(R.id.showrate);
        hos_name=view.findViewById(R.id.name_hospital);
        abt_hos=view.findViewById(R.id.about_hospital);
        hos_lbl=view.findViewById(R.id.hos_treatment_lbl);
        abt_hos_desc=view.findViewById(R.id.about_hospital_desc);
        hos_location=view.findViewById(R.id.location_hospital);
        rerating=view.findViewById(R.id.revrat);
        reperson=view.findViewById(R.id.revperson);
        starimg=view.findViewById(R.id.revstar);
        img=view.findViewById(R.id.img_hospital);
        btnlogout=view.findViewById(R.id.btnlogout);
        addpackage=view.findViewById(R.id.addpackage);
        getdata();

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                capture();
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str= h.getName()+" "+ h.getLocation();
                Uri gmmIntentUri = Uri.parse("geo:0,0?q="+str);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(),reviewfinal.class);
                intent.putExtra("name", h.getName());
                startActivity(intent);
            }
        });
         btnlogout.setOnClickListener(new View.OnClickListener() {
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

        recyclerView=view.findViewById(R.id.hospital_details_recycler);
         addpackage.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 call_name();
             }
         });
        return view;
    }
    private void call_name() {
           String s;
        AlertDialog.Builder builder
                = new AlertDialog.Builder(getContext());
        builder.setTitle("Enter Package");
        final View customLayout
                = getLayoutInflater()
                .inflate(
                        R.layout.add_package,
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
                                                R.id.editText_m);
                                EditText editTextq
                                        = customLayout
                                        .findViewById(
                                                R.id.editText_q);
                                if (editText.getText().toString()!=null&&editText.getText().toString().length()>0)
                                {
                                    Map<String,String>map=new HashMap<>();
                                    map.put("monthly",editText.getText().toString());
                                    map.put("quaterly",editTextq.getText().toString());
                                    db.collection("gyms").document(FirebaseAuth.getInstance().getUid()).update("list",map);
                                }
                                else {

                                    Toast.makeText(getContext(),
                                            "Enter package",
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
    private void getdata() {
        db.collection("gyms").document(FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
           h=documentSnapshot.toObject(gym_model.class);
                hos_name.setText(h.getName());
                abt_hos.setText("About "+h.getName());
                String[]split=h.getAbout().split(",");
                if(h.getLink()!="image link")
                {
                    Picasso.get().load(h.link).into(img);
                }
                abt_hos_desc.setText(h.getAbout());
                hos_location.setText(h.getLocation());
                Log.d(TAG, "onSuccess: here is the list"+h.getList());
                packages p=new packages("Monthly","30",h.getList().get("monthly"));
                packages q=new packages("Quaterly","120",h.getList().get("quaterly"));
                List<packages>plist=new ArrayList<>();
                plist.add(p);
                plist.add(q);
                package_adapter adapter=new package_adapter(getContext(),plist);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false) );
                recyclerView.setAdapter(adapter);
                ratingfunction();
            }
        });
    }
    private void ratingfunction() {
        db.collection("reviews").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int k=0;
                float sum=0;
                int no=0;
                for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots)
                {

                    user_model m=documentSnapshot.toObject(user_model.class);
                    // Log.d(TAG, "onSuccess: "+m.getHospital()+" "+h.getName());
                    if (m.getHospital().equals(h.getName()))
                    {
                        sum+=m.getRating();
                        no++;
                    }
                }
                if (no>0)
                {
                    sum=sum/no;
                    String st=String.format("%.1f",sum);
                    rerating.setText(st);
                    reperson.setText(String.valueOf(no)+".0");
                }

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
        Log.d(TAG, "selectImage: "+"in profile");
    }
    private void capture()
    {
        Intent intent1=new Intent();
        intent1.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent1.resolveActivity(getContext().getPackageManager())!=null)
            startActivityForResult(intent1,200);
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
                img.setImageBitmap(bitmap);
                img.setVisibility(View.VISIBLE);
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
            img.setImageBitmap(bitmap);
            img.setVisibility(View.VISIBLE);
            //upload_img(path);
            submit();
            Log.d(TAG, "onActivityResult: "+"camera selected");

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
                = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        byte[] b = stream.toByteArray();
        final StorageReference ref
                = storageReference
                .child(
                        "images/"
                                + UUID.randomUUID().toString());

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("documentImages").child("noplateImg");
        ref.putBytes(b).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                // Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Toast.makeText(getContext(), "uploaded", Toast.LENGTH_SHORT).show();
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        db.collection("gyms").document(FirebaseAuth.getInstance().getUid()).update("link",uri.toString());
                        Log.d(TAG, "onSuccess: "+"path set image uploaded");
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
                        .makeText(getContext(),
                                "Image Uploaded!!",
                                Toast.LENGTH_SHORT)
                        .show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"failed",Toast.LENGTH_LONG).show();


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
                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            db.collection("gyms").document(FirebaseAuth.getInstance().getUid()).update("link",uri.toString());
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
            Toast.makeText(getContext(),"path is null",Toast.LENGTH_SHORT).show();
    }

}