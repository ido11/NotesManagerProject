package com.example.notesmanager.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesmanager.R;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private Context context;
    private ArrayList id, title;

    CustomAdapter(Context context, ArrayList id, ArrayList title){
        this.context = context;
        this.id = id;
        this.title = title;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.id_txt.setText(String.valueOf(id.get(position)));
        holder.title_txt.setText(String.valueOf(title.get(position)));
    }

    @Override
    public int getItemCount() {
        return id.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView id_txt, title_txt;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            id_txt = itemView.findViewById(R.id.note_id);
            title_txt = itemView.findViewById(R.id.note_title);
//            edit = itemView.findViewById(R.id.edit);
            title_txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, NoteActivity.class);
                    Bundle b = new Bundle();
                    b.putInt("key", Integer.parseInt(id_txt.getText().toString()));
                    intent.putExtras(b); //Put your id to your next Intent
                    context.startActivity(intent);
                    ((Activity) context).finish();
                }
            });
        }


    }
}
