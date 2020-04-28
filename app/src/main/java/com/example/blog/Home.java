package com.example.blog;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Home extends AppCompatActivity implements View.OnClickListener{

    /////////
    private RecyclerView recyclerView;
    /////////

//    private ListView Post_list_view;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference ref,db;
//    private ProgressDialog progressDialog;
    private ArrayList<Post> post_list;
//    private CustomAdapter_post customAdapter;
//    private ArrayList<String> friend_list;
    private ProgressBar progressBar;
//    private ObjectAnimator objectAnimator;
//
    private Dialog dialog;
    private Button Follow;
    private TextView close,name,followers_count,post_count;
    private ImageView image;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        /////////////
        dialog = new Dialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference("User_friends");
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView = (RecyclerView) findViewById(R.id.post_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ////////////



//        Post_list_view = (ListView) findViewById(R.id.post_list);
//        firebaseAuth = FirebaseAuth.getInstance();
        post_list = new ArrayList<>();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.child(firebaseAuth.getCurrentUser().getEmail().split("@")[0]).exists()){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Home.this, "You have no friend yet!!",Toast.LENGTH_LONG).show();
                }else{

                    Long x = dataSnapshot.child(firebaseAuth.getCurrentUser().getEmail().split("@")[0]).getChildrenCount();
                    int c = 0;
                    for(DataSnapshot ds : dataSnapshot.child(firebaseAuth.getCurrentUser().getEmail().split("@")[0]).getChildren()){
                        c++;
                        final DatabaseReference post_table = FirebaseDatabase.getInstance().getReference("User_posts/"+ds.getValue(String.class));
                        int finalC = c;
                        post_table.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                for(DataSnapshot ds1: dataSnapshot.getChildren()){
                                    Post p = ds1.getValue(Post.class);
                                    post_list.add(p);
                                }
                                if(finalC == x){
                                    if(post_list.isEmpty()){
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(Home.this,"No posts",Toast.LENGTH_LONG).show();
                                    }else {
//                                        Log.i("check",Integer.toString(post_list.size()));
                                        t.start();
                                        CustomAdapter_post customAdapter_post = new CustomAdapter_post(Home.this,post_list,"Home");
                                        recyclerView.setAdapter(customAdapter_post);
//                                            Log.i("reach","1");
//                                        Collections.sort(post_list, (p1, p2) -> Long.compare(p1.getTime(), p2.getTime()));
//                                        Collections.reverse(post_list);
//                                        customAdapter = new CustomAdapter_post(Home.this, R.layout.post_custom_adapter, post_list,"Home");
//                                        Post_list_view.setAdapter(customAdapter);
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
//                                        Log.i("size",Integer.toString(post_list.size()));
                                post_table.removeEventListener(this);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }

                        });
                    }

                }
                ref.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void showPopUp(String p) {

        dialog.setContentView(R.layout.custom_popup);
        Follow = (Button) dialog.findViewById(R.id.follow_btn);
        Follow.setVisibility(View.GONE);
        Follow.setEnabled(false);
//        if(friend_list.contains(p))
//            Follow.setText("Connected");
        close = (TextView) dialog.findViewById(R.id.close);
        name = (TextView) dialog.findViewById(R.id.Person_name);
        followers_count = (TextView) dialog.findViewById(R.id.followers_count);
        post_count = (TextView) dialog.findViewById(R.id.post_count);
        image = (ImageView) dialog.findViewById(R.id.profile_photo);
//
        db = FirebaseDatabase.getInstance().getReference("User_friends/" + p);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

//                Toast.makeText(search_friend.this,Long.toString(dataSnapshot.getChildrenCount()),Toast.LENGTH_LONG).show();
                followers_count.setText(Long.toString(dataSnapshot.getChildrenCount()));
                db.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        db = FirebaseDatabase.getInstance().getReference("User_posts/"+p);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                post_count.setText(Long.toString(dataSnapshot.getChildrenCount()));
                db.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        db = FirebaseDatabase.getInstance().getReference("users");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User u1 = dataSnapshot.child(p).getValue(User.class);
                Log.i("check3",u1.getName());
                name.setText(u1.getName());
                if(!u1.getPhoto().equals(""))
                    Picasso.get().load(Uri.parse(u1.getPhoto())).into(image);

                db.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        close.setOnClickListener(this);
        dialog.show();

    }
//
    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.home_menu, menu);

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

            case R.id.Me:
                showPopUp(firebaseAuth.getCurrentUser().getEmail().split("@")[0]);
                return true;

            case R.id.addPost:
                finish();
                startActivity(new Intent(this, AddPost_page.class));
                return true;

            case R.id.profile:
                finish();
                startActivity(new Intent(this, profile.class));
//                Toast.makeText(this,"profile",Toast.LENGTH_LONG).show();
                return true;

            case R.id.search:
                Intent i1 = new Intent(this, search_friend.class);
                i1.putExtra("Operation","search");
                finish();
                startActivity(i1);
//                Toast.makeText(this,"serach",Toast.LENGTH_LONG).show();
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
//
    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference ref = firebaseDatabase.getReference("users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User u1 = dataSnapshot.child(firebaseAuth.getCurrentUser().getEmail().split("@")[0]).getValue(User.class);
                String name = u1.getName();
                MenuItem item = menu.findItem(R.id.profile);
                item.setTitle(name);
//                msg.setText("Welcome "+name);
//                progressDialog.dismiss();
                ref.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
        return super.onPrepareOptionsMenu(menu);
    }

    Thread t = new Thread(){
        @Override
        public void run() {
            Collections.sort(post_list, (p1, p2) -> Long.compare(p1.getTime(), p2.getTime()));
            Collections.reverse(post_list);
        }
    };

    @Override
    public void onClick(View view) {
        if(view == close){
            dialog.dismiss();
        }
    }
}
