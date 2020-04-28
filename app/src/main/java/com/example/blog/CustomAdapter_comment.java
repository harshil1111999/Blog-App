package com.example.blog;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter_comment extends ArrayAdapter<Comment> {

    private ImageView imageView;
    private TextView name,comment;

    public CustomAdapter_comment(@NonNull Context context, int resource, @NonNull ArrayList<Comment> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = (LinearLayout)inflater.inflate(R.layout.custom_adapter_comment,null);
        }

        imageView = convertView.findViewById(R.id.comment_photo);
        name = convertView.findViewById(R.id.comment_name_txt);
        comment = convertView.findViewById(R.id.comment_txt);

        Picasso.get().load(Uri.parse(getItem(position).getPhoto())).into(imageView);
        name.setText(getItem(position).getName()+" : ");
        comment.setText(getItem(position).getComment());

        return convertView;

    }
}
