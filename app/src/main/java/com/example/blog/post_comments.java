package com.example.blog;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class post_comments extends AppCompatActivity {

    private String email,photo,description,title;
    private Long time;
    private ImageView Post_person_photo, Post_photo;
    private TextView Post_person_name, Post_title, Post_description;
    private EditText Comment_edittxt;
    private Button Comment_post_btn;
    private ListView Comment_list;
    private ArrayList<Comment> arrayList;
    private CustomAdapter_comment customAdapter_comment;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference db;

    private String comment_person_name;
    private String comment_words,person_photo;
    private User u1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_comments);

        email = getIntent().getStringExtra("email");
        photo = getIntent().getStringExtra("photo");
        person_photo = getIntent().getStringExtra("person_photo");
        time = Long.parseLong(getIntent().getStringExtra("time"));
//        Log.i("check1",time);
        description = getIntent().getStringExtra("description");
        title = getIntent().getStringExtra("title");
//        comments = getIntent().getStringExtra("comments");

        Post_person_photo = (ImageView) findViewById(R.id.post_person_photo);
        Post_person_name = (TextView) findViewById(R.id.post_person_name);
        Post_photo = (ImageView) findViewById(R.id.post_photo);
        Post_title = (TextView) findViewById(R.id.post_title);
        Post_description = (TextView) findViewById(R.id.post_description);
        Comment_list = (ListView) findViewById(R.id.comment_list);
        Comment_edittxt = (EditText) findViewById(R.id.comment_edittxt);
        Comment_post_btn = (Button) findViewById(R.id.comment_post_btn);
        arrayList = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();

        comment_person_name = firebaseAuth.getCurrentUser().getEmail().split("@")[0];

        customAdapter_comment = new CustomAdapter_comment(this, R.layout.custom_adapter_comment, arrayList);
        Comment_list.setAdapter(customAdapter_comment);

        Post_person_name.setText(email);
        Picasso.get().load(Uri.parse(photo)).into(Post_photo);
        Picasso.get().load(Uri.parse(person_photo)).into(Post_person_photo);
        Post_title.setText(title);
        Post_description.setText(description);
//        Post_comments.setText(comments);

        db = FirebaseDatabase.getInstance().getReference("User_posts/"+email+"/"+time+"/comments");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final int[] c = {0};
                final Long x = dataSnapshot.getChildrenCount();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
//                    Log.i("reach","2");
                    String name = ds.getKey();
                    String comment = ds.getValue(String.class);
//                    Log.i("reach",comment);
                    final DatabaseReference db1 = FirebaseDatabase.getInstance().getReference("users");
                    db1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User u1 = dataSnapshot.child(name).getValue(User.class);
                            arrayList.add(new Comment(name,u1.getPhoto(),comment));
                            c[0]++;
//                            Log.i("c", Long.toString(c[0]));
                            if(c[0] == x) {
//                                Log.i("reach1", Long.toString(arrayList.size()));
                                show();
                            }
//                            Log.i("reach1",Integer.toString(arrayList.size()));
                            db1.removeEventListener(this);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
//                Log.i("reach", Long.toString(dataSnapshot.getChildrenCount()));
//                customAdapter_comment.notifyDataSetChanged();
                db.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Comment_post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                comment_words = Comment_edittxt.getText().toString();
                if(comment_words.isEmpty()){
                    Comment_edittxt.setError("Please enter comment first");
                    Comment_edittxt.requestFocus();
                }else{
                    DatabaseReference db1 = FirebaseDatabase.getInstance().getReference("users");
                    db1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            u1 = dataSnapshot.child(comment_person_name).getValue(User.class);
                            addPost();
//                        Collections.reverse(arrayList);
//                        arrayList.add(new Comment(name, u1[0].getPhoto(),comment));
//                        Collections.reverse(arrayList);
//                        customAdapter_comment.notifyDataSetChanged();
                            db1.removeEventListener(this);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

            }
        });
        Post_person_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_detail();
            }
        });

    }

    public void addPost(){
        final int[] flag = {0};
        DatabaseReference db3 = FirebaseDatabase.getInstance().getReference("User_posts/"+email+"/"+time);
        db = FirebaseDatabase.getInstance().getReference("User_posts/"+email+"/"+time+"/comments");
        db3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("comments").exists()){
                    db.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.child(comment_person_name).exists()) {
                                arrayList.remove(new Comment(comment_person_name, u1.getPhoto(), dataSnapshot.child(comment_person_name).getValue(String.class)));
                                arrayList.add(new Comment(comment_person_name, u1.getPhoto(), dataSnapshot.child(comment_person_name).getValue(String.class) + "\n" + comment_words));
                                comment_words = dataSnapshot.child(comment_person_name).getValue(String.class) + "\n" + comment_words;
                                Log.i("check",dataSnapshot.child(comment_person_name).getValue(String.class));
                                db.removeEventListener(this);
                                add_comment();
//                                db2.child(comment_person_name).setValue(dataSnapshot.child(comment_person_name).getValue(String.class) + "\n" + comment_words);
                            } else {
                                arrayList.add(new Comment(comment_person_name, u1.getPhoto(), comment_words));
                                db.removeEventListener(this);
                                add_comment();
//                                db2.child(comment_person_name).setValue(comment_words);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }else{
                    arrayList.add(new Comment(comment_person_name, u1.getPhoto(), comment_words));
//                    db.removeEventListener(this);
                    add_comment();
//                    db2.child(comment_person_name).setValue(comment_words);
                }
//                customAdapter_comment.notifyDataSetChanged();
                Comment_edittxt.setText("");
                Comment_edittxt.clearFocus();
                db3.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void show_detail(){
        Dialog dialog = new Dialog(this);
        Button Follow;
        TextView close,name,followers_count,post_count;
        ImageView image;
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
        db = FirebaseDatabase.getInstance().getReference("User_friends/" + email);
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
        db = FirebaseDatabase.getInstance().getReference("User_posts/"+email);
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
//        Log.i("check3",u1.getName());
        name.setText(email);
        Picasso.get().load(Uri.parse(person_photo)).into(image);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void add_comment(){
        DatabaseReference db2 = FirebaseDatabase.getInstance().getReference("User_posts/"+email+"/"+time+"/comments");
        db2.child(comment_person_name).setValue(comment_words);
        finish();
        startActivity(getIntent());
    }

    public void show(){
//        Log.i("reach",Integer.toString(arrayList.size()));
        customAdapter_comment = new CustomAdapter_comment(this, R.layout.custom_adapter_comment, arrayList);
        Comment_list.setAdapter(customAdapter_comment);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
        startActivity(new Intent(this, Home.class));

    }
}
