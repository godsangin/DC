package com.msproject.myhome.dailycloset;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class FSListViewAdapter extends BaseAdapter {
    Context context;
    ArrayList<SettingItem> items;

    public FSListViewAdapter(ArrayList<SettingItem> items, Context context){
        this.items = items;
        this.context = context;
    }


    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.setting_item,parent,false);
        TextView settingTitle = view.findViewById(R.id.item_name);
        TextView settingContent = view.findViewById(R.id.item_content);
        settingTitle.setText(items.get(position).getTitle());
        settingContent.setText(items.get(position).getContent());
        final CheckBox cb = view.findViewById(R.id.checkbox);
        cb.setVisibility(View.VISIBLE);

        final SharedPreferences sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        boolean push = sharedPreferences.getBoolean("push", false);
        if(push && position == 0){
            cb.setChecked(true);
        }
//        else if(background && position == 1){
//            cb.setChecked(true);
//        }
        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (position) {
                    case 0:
                        if (cb.isChecked()) {
                            //푸시 되게
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("push", true);
                            editor.commit();
                            Toast.makeText(parent.getContext(), "푸시 기능이 활성화됩니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            //안되게
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("push", false);
                            editor.commit();
                            Toast.makeText(parent.getContext(), "푸시 기능이 해제됩니다.", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        });
        return view;
    }
}