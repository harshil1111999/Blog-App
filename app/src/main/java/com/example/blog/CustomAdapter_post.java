package com.example.blog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter_post extends RecyclerView.Adapter<CustomAdapter_post.CustomViewHolder> {

    private ArrayList<Post> post_list;
    private Context context;
    private String opt;

    public CustomAdapter_post(Context ct,ArrayList<Post> p,String operation){
        context = ct;
        post_list = p;
        opt = operation;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.custom_adapter_post_updated,parent,false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        holder.title.setText(post_list.get(position).getTitle());
        holder.desc.setText(post_list.get(position).getDescription());
        Picasso.get().load(Uri.parse(post_list.get(position).getPhoto())).into(holder.post_photo);
        Picasso.get().load(Uri.parse(post_list.get(position).getPerson_photo())).into(holder.person_photo);
        holder.name.setText(post_list.get(position).getEmail());

        holder.post_linear_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.i("check2",Long.toString(post_list.get(position).getTime()));
                Intent i = new Intent(context,post_comments.class);
                i.putExtra("title",post_list.get(position).getTitle());
                i.putExtra("description",post_list.get(position).getDescription());
                i.putExtra("photo",post_list.get(position).getPhoto());
                i.putExtra("email",post_list.get(position).getEmail());
                i.putExtra("person_photo",post_list.get(position).getPerson_photo());
                i.putExtra("time",Long.toString(post_list.get(position).getTime()));
                if(opt.equals("Home"))
                    ((Home)context).finish();
                else
                    ((My_posts)context).finish();
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return post_list.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{
        ImageView post_photo,person_photo;
        TextView name,title,desc;
        LinearLayout post_linear_layout;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            person_photo = itemView.findViewById(R.id.post_person_photo);
            post_photo = itemView.findViewById(R.id.post_photo);
            name = itemView.findViewById(R.id.post_person_name);
            title = itemView.findViewById(R.id.post_title);
            desc = itemView.findViewById(R.id.post_description);
            post_linear_layout = itemView.findViewById(R.id.post_linear_layout);
        }
    }


}
