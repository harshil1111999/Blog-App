package com.example.blog;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class search_friend extends AppCompatActivity implements View.OnClickListener{

    private ListView Friend_list_view;
    public ArrayList<String> person_list , temp1,temp2;
    private HashMap<String,Person> map;
    private CustomAdapter_friend customAdapter_friend;
    public ArrayList<String> friend_list;
    private ArrayList<User> user_list;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference ref,db;
    private Dialog dialog;
    private String p;
    private SearchView Search_view_friend;

    private ProgressBar progressBar;

    private TextView name,followers_count,post_count,close;
    private ImageView image;
    private Button Follow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_friend);

        friend_list = new ArrayList<>();
        person_list = new ArrayList<>();
        temp1 = new ArrayList<>();
        temp2 = new ArrayList<>();
        map = new HashMap<>();
        dialog = new Dialog(this);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
//        Search_view_friend = (SearchView) findViewById(R.id.search_view_frined);
        Friend_list_view = (ListView) findViewById(R.id.friend_list_view);
        firebaseAuth = FirebaseAuth.getInstance();

        Friend_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("clicked","1");
//                adapterView.getItemAtPosition(i).getClass().getName();
                p = (String)adapterView.getItemAtPosition(i);
                showPopUp(view);
            }
        });

//        Log.i("users",Integer.toString(friend_list.size()));

        String me = firebaseAuth.getCurrentUser().getEmail().split("@")[0];
//        Log.i("ME",me);
        ref = FirebaseDatabase.getInstance().getReference("users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    if(!ds.getKey().equals(me)) {
                        User u1 = ds.getValue(User.class);
                        String name = u1.getName();
                        String photo = u1.getPhoto();
                        Person p1 = new Person(name, photo);
                        map.put(ds.getKey(),p1);
                        person_list.add(ds.getKey());
                    }

                }
                temp1.addAll(person_list);
                ref.removeEventListener(this);
                makeList_users();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void showPopUp(View v){

        dialog.setContentView(R.layout.custom_popup);
        Follow = (Button) dialog.findViewById(R.id.follow_btn);
        if(friend_list.contains(p))
            Follow.setText("Connected");
        close = (TextView) dialog.findViewById(R.id.close);
        name = (TextView) dialog.findViewById(R.id.Person_name);
        followers_count = (TextView) dialog.findViewById(R.id.followers_count);
        post_count = (TextView) dialog.findViewById(R.id.post_count);
        image = (ImageView) dialog.findViewById(R.id.profile_photo);

        db = FirebaseDatabase.getInstance().getReference("User_friends/"+p);
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
//
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
        Follow.setOnClickListener(this);
        dialog.show();

    }

    public void make_friend(){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("User_friends/"+firebaseAuth.getCurrentUser().getEmail().split("@")[0]);
        if(Follow.getText().toString().equals("Connected")){
            Follow.setText("Follow Me");
            friend_list.remove(p);
            temp2.remove(p);
            db.child(p).removeValue();
        }else {
            db.child(p).setValue(p);
            Follow.setText("Connected");
            friend_list.add(p);
            temp2.add(p);
        }
        customAdapter_friend.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {

        if(view == close){
            dialog.dismiss();
        }
        if(view == Follow){
            make_friend();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
        startActivity(new Intent(this, Home.class));

    }

    public void show(){

        String v = getIntent().getStringExtra("Operation");
        if(v.equals("search")) {
            TextView textView = (TextView) findViewById(R.id.textView);
            textView.setText("Available users");
            customAdapter_friend = new CustomAdapter_friend(this, R.layout.custom_adapter_friend, person_list, map, friend_list);
        }
        else if(v.equals("My_friends")) {
            TextView textView = (TextView) findViewById(R.id.textView);
            textView.setText("Following");
            if(friend_list.isEmpty())
                Toast.makeText(this, "You are not following anyone!!",Toast.LENGTH_LONG).show();
            customAdapter_friend = new CustomAdapter_friend(this, R.layout.custom_adapter_friend, friend_list, map, friend_list);
        }
        Friend_list_view.setAdapter(customAdapter_friend);
        progressBar.setVisibility(View.GONE);

    }

    public void makeList_users(){

        ref = FirebaseDatabase.getInstance().getReference("User_friends");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(firebaseAuth.getCurrentUser().getEmail().split("@")[0]).exists()) {

                    DatabaseReference table_user_friend = FirebaseDatabase.getInstance().getReference("User_friends/"+firebaseAuth.getCurrentUser().getEmail().split("@")[0]);
                    table_user_friend.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for(DataSnapshot ds: dataSnapshot.getChildren()){
                                friend_list.add(ds.getValue(String.class));
                            }
                            temp2.addAll(friend_list);
                            table_user_friend.removeEventListener(this);
//                            Log.i("users",Integer.toString(friend_list.size()));
//                            Log.i("users",Integer.toString(person_list.size()));
                            show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }else{
//                    Log.i("users",Integer.toString(friend_list.size()));
//                    Log.i("users",Integer.toString(person_list.size()));
                    show();
                }

                ref.removeEventListener(this);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_menu,menu);

        MenuItem item = menu.findItem(R.id.search_item);
        SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

//                if(s.equals("")){
//                    person_list.addAll(temp);
//                    customAdapter_friend.notifyDataSetChanged();
//                    return false;
//                }

                ArrayList<String> result = new ArrayList<>();
                String v = getIntent().getStringExtra("Operation");
                if(v.equals("search")){
                    person_list.clear();
                    person_list.addAll(temp1);
                    for(String t : person_list){
                        if(t.contains(s)){
                            result.add(t);
                        }
                    }
                    person_list.clear();
                    person_list.addAll(result);
                }else if(v.equals("My_friends")){
                    friend_list.clear();
                    friend_list.addAll(temp2);
                    for(String t : friend_list){
                        if(t.contains(s)){
                            result.add(t);
                        }
                    }
                    friend_list.clear();
                    friend_list.addAll(result);
                }
                customAdapter_friend.notifyDataSetChanged();
//                ((CustomAdapter_friend)Friend_list_view.getAdapter()).update(result);

                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);

    }
}
