package com.example.blog;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomAdapter_friend extends ArrayAdapter<String> {

    private ArrayList<String> friend_list,person_list;
    private HashMap<String, Person> map;
    private ImageView imageView;
    private TextView textView,follow_txt;
//    private Button follow_btn;
    private String name;
    private FirebaseAuth firebaseAuth;
    private Context mcontext;
//
    public CustomAdapter_friend(@NonNull Context context, int resource, @NonNull ArrayList<String> objects, HashMap<String,Person> map, ArrayList<String> friend_list) {
        super(context, resource, objects);
        this.friend_list = friend_list;
        this.person_list = objects;
        this.mcontext = context;
        this.map = map;
    }

//    public class ViewHolder {
//
//        TextView name;
//        ImageView image;
//
//    }

    @Override
    public int getCount() {
        return person_list.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null)
        {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView  = (LinearLayout)inflater.inflate(R.layout.custom_adapter_friend, null);
        }
//        LayoutInflater inflater = LayoutInflater.from(getContext());
//        View customView = inflater.inflate(R.layout.custom_adapter_friend,parent,false);

        imageView = (ImageView) convertView.findViewById(R.id.person_photo);
        textView = (TextView) convertView.findViewById(R.id.person_name);
        follow_txt = (TextView) convertView.findViewById(R.id.follow_txt);
//        follow_btn = (Button) convertView.findViewById(R.id.follow_btn);
        Log.i("position",Integer.toString(position));
        String email = getItem(position);

//        String name = map.get(email).getName();
        name = email;
        String photo = map.get(email).getPhoto();

        Picasso.get().load(Uri.parse(photo)).into(imageView);
        textView.setText(name);
        if(friend_list.contains(email))
            follow_txt.setText("Connected");
        else
            follow_txt.setText("Follow");
        return convertView;

    }

}
