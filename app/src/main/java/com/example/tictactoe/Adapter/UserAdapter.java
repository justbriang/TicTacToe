package com.example.tictactoe.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tictactoe.ChoosePlayerActivity;
import com.example.tictactoe.R;
import com.example.tictactoe.model.User;

import java.util.ArrayList;
import java.util.List;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {
    static ArrayList<User> users;
    Context context;

    public UserAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView myid;
        public TextView email;
        public TextView name;
//        private EditText etInviteEMail;

        public MyViewHolder(View v) {
            super(v);
            this.myid = (TextView) v.findViewById(R.id.user_id);
            this.email = (TextView) v.findViewById(R.id.emailAddress);
            this.name = (TextView) v.findViewById(R.id.name);
//            etInviteEMail = (EditText)v. findViewById(R.id.InviteEmail);
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    // get position
                    int pos = getAdapterPosition();

                    // check if item still exists
                    if(pos != RecyclerView.NO_POSITION){
                        User clickedDataItem = users.get(pos);
//                        etInviteEMail.setText(email.getText());
                        Toast.makeText(v.getContext(), "You clicked " + email.getText(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.user_list, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.MyViewHolder holder, int position) {
        TextView myid = holder.myid;
        TextView email = holder.email;
        TextView name = holder.name;
        myid.setText(users.get(position).myid + "");
        email.setText(users.get(position).email + "");
        name.setText(users.get(position).name + "");
    }

    @Override
    public int getItemCount() {
        return users.size();

    }
}
