package com.example.blog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;

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

public class profile extends AppCompatActivity implements View.OnClickListener{

    private ImageView imageView;
    private TextView Profile_gender;
    private TextView Profile_name;
    private TextView Profile_age;
    private TextView Profile_number;
    private TextView Profile_email;
    private FirebaseAuth firebaseAuth;
    private int galary_intent = 1;
    private Uri uri;
    private String profile_photo_uri;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private DatabaseReference profile_photo_ref;
    private ProgressDialog progressDialog;
    private Dialog dialog;

    private Button Name_edit_btn, Gender_edit_btn, Number_edit_btn, Age_edit_btn;
    private Button submit_btn;
    private TextView close_btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        imageView = (ImageView) findViewById(R.id.profile_img);
        Profile_email = (TextView) findViewById(R.id.profile_email);
        Profile_age = (TextView) findViewById(R.id.profile_age);
        Profile_name = (TextView) findViewById(R.id.profile_name);
        Profile_number = (TextView) findViewById(R.id.profile_number);
        Profile_gender = (TextView) findViewById(R.id.profile_gender);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        dialog = new Dialog(this);
        Name_edit_btn = (Button) findViewById(R.id.name_edit_btn);
        Number_edit_btn = (Button) findViewById(R.id.number_edit_btn);
        Age_edit_btn = (Button) findViewById(R.id.age_edit_btn);
        Gender_edit_btn = (Button) findViewById(R.id.gender_edit_btn);

        imageView.setOnClickListener(this);
        Age_edit_btn.setOnClickListener(this);
        Gender_edit_btn.setOnClickListener(this);
        Number_edit_btn.setOnClickListener(this);
        Name_edit_btn.setOnClickListener(this);

        storageReference = FirebaseStorage.getInstance().getReference("Photos/"+firebaseAuth.getCurrentUser().getEmail().split("@")[0]);
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User u1 = dataSnapshot.child(firebaseAuth.getCurrentUser().getEmail().split("@")[0]).getValue(User.class);

                if(u1.getPhoto()!=""){
                    String url = u1.getPhoto();
                    Picasso.get().load(Uri.parse(url)).into(imageView);
//                    Log.i("exist",url);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final String [] key = firebaseAuth.getCurrentUser().getEmail().split("@");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User u1 = dataSnapshot.child(key[0]).getValue(User.class);
                String name = u1.getName();
                String age = u1.getAge();
                String gender = u1.getGender();
                String number = u1.getNumber();
                String email = firebaseAuth.getCurrentUser().getEmail();
                Profile_email.setText("Email : "+email);
                Profile_gender.setText("Gender : "+gender);
                Profile_name.setText("Name : "+name);
                Profile_age.setText("Age : "+age);
                Profile_number.setText("Number : "+number);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i = new Intent(this, Home.class);
        finish();
        startActivity(i);

    }

    public void showPopup(String s){

        dialog.setContentView(R.layout.custom_edit_popup);
        TextView txt = dialog.findViewById(R.id.update_property_txt);
        txt.setText(s);
        EditText e_txt = dialog.findViewById(R.id.property_edittxt);
        dialog.show();
        close_btn = dialog.findViewById(R.id.close);
        submit_btn = dialog.findViewById(R.id.submit_btn);

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s1 = e_txt.getText().toString();
                if(s.equals("Name : "))
                    Profile_name.setText(s+s1);
                else if(s.equals("Number : "))
                    Profile_number.setText(s+s1);
                else if(s.equals("Gender : "))
                    Profile_gender.setText(s+s1);
                else if(s.equals("Age : "))
                    Profile_age.setText(s+s1);
                dialog.dismiss();
                DatabaseReference db = FirebaseDatabase.getInstance().getReference("users");
                db.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        User u1 = dataSnapshot.child(firebaseAuth.getCurrentUser().getEmail().split("@")[0]).getValue(User.class);

                        if(s.equals("Name : "))
                            u1.setName(s1);
                        else if(s.equals("Number : "))
                            u1.setNumber(s1);
                        else if(s.equals("Gender : "))
                            u1.setGender(s1);
                        else if(s.equals("Age : "))
                            u1.setAge(s1);

                        db.removeEventListener(this);
                        db.child(firebaseAuth.getCurrentUser().getEmail().split("@")[0]).setValue(u1);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    @Override
    public void onClick(View view) {

        if(view == imageView){
            upload();
        }

        if(view == Name_edit_btn){
            showPopup("Name : ");
        }

        if(view == Gender_edit_btn){
            showPopup("Gender : ");
        }

        if(view == Number_edit_btn){
            showPopup("Number : ");
        }

        if(view == Age_edit_btn){
            showPopup("Age : ");
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

            progressDialog.setMessage("Uploading...");
            progressDialog.show();
            uri = data.getData();
            Picasso.get().load(uri).into(imageView);
            upload_photo();

        }

    }

    private String getFileExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
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
            StorageReference fileReference = storageReference.child(Long.toString(d.getTime()));

//            User_uri user_uri = new User_uri(uri.toString());

//            profile_photo_ref.child(firebaseAuth.getCurrentUser().getEmail().split("@")[0]).setValue(uri);
            fileReference.putBytes(fileInBytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            progressDialog.dismiss();
                            Toast.makeText(profile.this,"Upload successful..",Toast.LENGTH_LONG).show();
                            profile_photo_uri = uri.toString();
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
                            ref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    User u1 = dataSnapshot.child(firebaseAuth.getCurrentUser().getEmail().split("@")[0]).getValue(User.class);
                                    u1.setPhoto(profile_photo_uri);
                                    ref.child(firebaseAuth.getCurrentUser().getEmail().split("@")[0]).setValue(u1);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(profile.this,e.getMessage(),Toast.LENGTH_LONG).show();
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menu,menu);

        if(menu instanceof MenuBuilder){
            MenuBuilder m = (MenuBuilder) menu;
            //noinspection RestrictedApi
            m.setOptionalIconsVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.my_posts:
                finish();
                startActivity(new Intent(this, My_posts.class));
                return true;

            case R.id.me_following:
                Intent i = new Intent(this, search_friend.class);
                i.putExtra("Operation","My_friends");
                finish();
                startActivity(i);
                return true;
            case R.id.logout:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(this, MainActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
