package com.example.blog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

public class AddPost_page extends AppCompatActivity implements View.OnClickListener{

    private ImageView Post_pic;
    private EditText title, description;
    private int galary_intent = 1;
    private Uri uri;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private Button Upload_btn;
    private String title_str,description_str;
    private String person_photo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addpost_page);

        Post_pic = (ImageView) findViewById(R.id.post_pic);
        title = (EditText) findViewById(R.id.title_editText);
        description = (EditText) findViewById(R.id.description_editText);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        Upload_btn = (Button) findViewById(R.id.upload_btn);

        databaseReference = FirebaseDatabase.getInstance().getReference("User_posts/"+firebaseAuth.getCurrentUser().getEmail().split("@")[0]);
        storageReference = FirebaseStorage.getInstance().getReference("Photos/"+firebaseAuth.getCurrentUser().getEmail().split("@")[0]);

        Post_pic.setOnClickListener(this);
        Upload_btn.setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
        startActivity(new Intent(this, Home.class));

    }

    @Override
    public void onClick(View view) {
        if(view == Post_pic){
            upload();
        }
        if(view == Upload_btn){
            title_str = title.getText().toString();
            description_str = description.getText().toString();

            if(title_str.isEmpty()){
                title.setError("Please enter title");
                title.requestFocus();
            }else if(description_str.isEmpty()){
                description.setError("Please enter description");
                description.requestFocus();
            }else {
                upload_photo();
                progressDialog.setMessage("Uploading...");
                progressDialog.show();
            }
        }
    }

    private void upload() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,galary_intent);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==galary_intent && resultCode==RESULT_OK && data!=null && data.getData()!=null){

            uri = data.getData();
            Picasso.get().load(uri).into(Post_pic);

        }

    }

    private void upload_photo() {

        if(uri!=null){

            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            //here you can choose quality factor in third parameter(ex. i choosen 25)
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] fileInBytes = baos.toByteArray();

            Date d = new Date();
            long t = d.getTime();
            StorageReference fileReference = storageReference.child(Long.toString(t));

//            User_uri user_uri = new User_uri(uri.toString());

//            profile_photo_ref.child(firebaseAuth.getCurrentUser().getEmail().split("@")[0]).setValue(uri);
            fileReference.putBytes(fileInBytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                            DatabaseReference db = FirebaseDatabase.getInstance().getReference("users/"+firebaseAuth.getCurrentUser().getEmail().split("@")[0]);
                            db.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    person_photo = dataSnapshot.child("photo").getValue(String.class);
                                    Post p = new Post(title_str, description_str, uri.toString(), t, firebaseAuth.getCurrentUser().getEmail().split("@")[0],person_photo);
                                    databaseReference.child(Long.toString(t)).setValue(p);
//                            DatabaseReference db = FirebaseDatabase.getInstance().getReference("Posts");
//                            db.child(Long.toString(t)).setValue(p);
                                    progressDialog.dismiss();
                                    Toast.makeText(AddPost_page.this,"Upload successful..",Toast.LENGTH_LONG).show();
                                    finish();
                                    startActivity(new Intent(AddPost_page.this, Home.class));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
//                            Log.i("photo",person_photo);
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(AddPost_page.this,e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    progressDialog.setProgress((int)progress);
                }
            });
        }else{
            Toast.makeText(this,"Phtot is not selected",Toast.LENGTH_LONG).show();
        }

    }

}
