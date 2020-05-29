package com.example.tictactoe.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tictactoe.ChoosePlayerActivity;
import com.example.tictactoe.R;
import com.example.tictactoe.model.User;

import java.util.ArrayList;
import java.util.List;

//public class UserAdapter extends ArrayAdapter {
//
//    public UserAdapter(Context context, ArrayList<User> users)
//    {
//        super(context, 0, users);
//    }
//
//    @NonNull
//    @Override
//    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        User user = (User) getItem(position);
//        if (convertView == null) {
//            convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_list, parent,false);
//        }
//        TextView txt1 = convertView.findViewById(R.id.user_id);
//        TextView txt2 = convertView.findViewById(R.id.name);
//        TextView txt3 = convertView.findViewById(R.id.emailAddress);
//
//        txt1.setText(user.myid);
//        txt2.setText(user.name);
//        txt3.setText(user.email);
//        return convertView;
//    }
//}
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {
    private int[] myid;
    private String[] email, name;
    Context context;

    public UserAdapter(Context ct, int[] myid, String[] name, String[] email) {
        this.myid = myid;
        this.name = name;
        this.email = email;
        context = ct;
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textView;
        public MyViewHolder(TextView v) {
            super(v);
            textView = v;
        }
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}