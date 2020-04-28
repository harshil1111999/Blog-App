package com.example.blog;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class My_posts extends AppCompatActivity implements View.OnClickListener{

//    private ListView My_post_list;
    private RecyclerView recyclerView;
    private ArrayList<Post> postArrayList;
//    private CustomAdapter_post adapter;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference db;
    private ProgressBar progressBar;
//    private Dialog dialog;

//    private Button Follow;
//    private TextView close,name,followers_count,post_count;
//    private ImageView image;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_posts);

//        My_post_list = (ListView) findViewById(R.id.my_post_list);
        postArrayList = new ArrayList<>();
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView = (RecyclerView) findViewById(R.id.my_post_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        dialog = new Dialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference("User_posts/"+firebaseAuth.getCurrentUser().getEmail().split("@")[0]);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    postArrayList.add(ds.getValue(Post.class));
                }
                if(postArrayList.isEmpty()){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(My_posts.this, "You Have Not Posted Any Blog Yet!!", Toast.LENGTH_LONG).show();
                }else{
//                    adapter = new CustomAdapter_post(My_posts.this, R.layout.post_custom_adapter, postArrayList,"My_posts");
//                    My_post_list.setAdapter(adapter);
                    CustomAdapter_post customAdapter_post = new CustomAdapter_post(My_posts.this,postArrayList,"My_posts");
                    recyclerView.setAdapter(customAdapter_post);
                    progressBar.setVisibility(View.GONE);
                }
                db.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(this, profile.class));

    }

    @Override
    public void onClick(View view) {
//        if(view == close){
//            dialog.dismiss();
//        }
    }

}
