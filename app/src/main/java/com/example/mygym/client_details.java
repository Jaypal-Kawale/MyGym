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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class client_details extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Client details";
    private Button btn;
    String gymname;
    TextView name,money,batch,remain;
    TextView fdate,jdate,mob,udate,drno;
    CircleImageView imageView;
    ArrayList<Client>list=new ArrayList<>();
    Client c;
    public static final int IMG_REQUEST=777;
    private Bitmap bitmap;
    Uri path;
    Bitmap photo;
    String url="Hello";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage firebaseStorage= FirebaseStorage.getInstance();
    StorageReference storageReference=firebaseStorage.getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_details);
        btn=findViewById(R.id.dadd);
        mob=findViewById(R.id.dmob);
        fdate=findViewById(R.id.dfdate);
        jdate=findViewById(R.id.djdate);
        name=findViewById(R.id.dname);
        money=findViewById(R.id.dmoney);
        batch=findViewById(R.id.dbatch);
        drno=findViewById(R.id.drno);
        remain=findViewById(R.id.dremain);
        udate=findViewById(R.id.dudate);
        imageView=findViewById(R.id.pimage);
        Bundle bundle=getIntent().getExtras();
        c=bundle.getParcelable("client");
        gymname=bundle.getString("name");
        String receipt=bundle.getString("receipt");
        Log.d("List", "onCreate: "+c.getName()+c.getBatch());
       // if (list!=null)
        {
            name.setText(c.getName().toString());
            money.setText(c.getMoney());
            batch.setText(c.getBatch());
            jdate.setText(c.getJoining());
            fdate.setText(c.getFinish());
            udate.setText(c.getTimestamp());
            mob.setText(c.getMob());
            drno.setText(receipt);
          //  Log.d("rno receipt", "onCreate: "+c.getRno()+" "+c.getPnomber());
            remain.setText(String.valueOf(Integer.parseInt(c.getTotal())-Integer.parseInt(c.getMoney())));
            if (c.getUrl().equals("Hello"))
            {
                imageView.setImageResource(R.drawable.add);
            }
            else
            Picasso.get().load(c.getUrl()).into(imageView);
        }
     name.setOnClickListener(this);
        batch.setOnClickListener(this);
        mob.setOnClickListener(this);
        money.setOnClickListener(this);
        drno.setOnClickListener(this);
        jdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callDialog(jdate,"joining");
            }
        });
        fdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callDialog(fdate,"finish");
            }
        });
       btn.setOnClickListener(this);
       imageView.setOnClickListener(this);
    }
    private void callDialog(TextView textView,String st) {
        AlertDialog.Builder builder
                = new AlertDialog.Builder(this);
        builder.setTitle("select new "+st);

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
                                sendDialogDatatoActivity(str,st);

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

    }

    @Override
    public void onClick(View view) {
          switch (view.getId())
          {
              case R.id.dname :
                  call_name("name",name);
                  break;
              case R.id.dbatch :
                  call_name("batch",batch);
                  break;
              case R.id.dmob :
                  call_name("mob",mob);
                  break;
              case R.id.dmoney :
                  call_name("money",money);
                  break;
              case R.id.pimage :
                  call_image();
                  break;
              case R.id.drno:
                  call_name("rno",drno);
                  break;
              case R.id.dadd:
                  Timestamp timestamp=Timestamp.now();
                 // SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                  SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy kk:mm");

                  String dateString = formatter.format(new Date(timestamp.toDate().toString()));
                  sendDialogDatatoActivity(dateString,"timestamp");
                  udate.setText(dateString);
          }
    }

    private void call_image() {

        AlertDialog.Builder builder
                = new AlertDialog.Builder(this);
        builder.setTitle("Profile Pic");
        final View customLayout
                = getLayoutInflater()
                .inflate(
                        R.layout.item_image,
                        null);
        builder.setView(customLayout);
        ImageView pimageView=customLayout.findViewById(R.id.bimage);
        Picasso.get().load(c.getUrl()).into(pimageView);
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        }).setPositiveButton("update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                   capture();
            }
        });
        AlertDialog dialog
                = builder.create();
        dialog.show();

    }

    private void call_name(String s,TextView t) {

        AlertDialog.Builder builder
                = new AlertDialog.Builder(this);
        builder.setTitle("Enter "+s);
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
                                    t.setText(editText.getText());
                                    sendDialogDatatoActivity(
                                            editText
                                                    .getText()
                                                    .toString(),s);
                                }
                                else {

                                    Toast.makeText(getApplicationContext(),
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
    private void capture()
    {
        Intent intent1=new Intent();
        intent1.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent1.resolveActivity(getPackageManager())!=null)
            startActivityForResult(intent1,200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode==200&&resultCode==RESULT_OK)
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
                Toast.makeText(client_details.this, "uploaded", Toast.LENGTH_SHORT).show();
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        url=uri.toString();
                        sendDialogDatatoActivity(url,"url");

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("client", "onFailure: "+"Url is not "+e.toString());
                    }
                });
                progressDialog.dismiss();
                Toast
                        .makeText(client_details.this,
                                "Image Uploaded!!",
                                Toast.LENGTH_SHORT)
                        .show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(client_details.this,"failed",Toast.LENGTH_LONG).show();


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
    private void sendDialogDatatoActivity(String data,String s)
    {
        Log.d(TAG, "sendDialogDatatoActivity: "+data+s);
       db.collection(gymname).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
           @Override
           public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
               for (DocumentSnapshot doc:queryDocumentSnapshots)
               {
                   Client d=doc.toObject(Client.class);
                   if (c.getName().equals(d.getName())&&c.getTimestamp().equals(d.getTimestamp())&&c.getMob().equals(d.getMob()))
                   {
                       db.collection(gymname).document(doc.getId()).update(s,data).addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void aVoid) {
                               Log.d(TAG, "onSuccess: "+"updated successfully");
                               Toast.makeText(client_details.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                           }
                       });
                   }
               }
           }
       });
       finish();
    }
}