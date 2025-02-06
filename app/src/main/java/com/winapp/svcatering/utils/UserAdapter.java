package com.winapp.svcatering.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.winapp.svcatering.R;
import com.winapp.svcatering.model.UserListModel;

import java.util.ArrayList;

public class UserAdapter extends BaseAdapter {
    Context context;
    int flags[];
    public ArrayList<UserListModel> userList;
    LayoutInflater inflter;

    public UserAdapter(Context applicationContext, ArrayList<UserListModel> userList) {
        this.context = applicationContext;
        this.userList = userList;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return this.userList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.custom_spinner_items, null);
        TextView names =view.findViewById(R.id.textView);
        names.setTextColor(Color.parseColor("#000000"));
        names.setText(userList.get(i).getUserName());
        return view;
    }
}