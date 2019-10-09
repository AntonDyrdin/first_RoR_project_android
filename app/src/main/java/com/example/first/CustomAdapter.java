package com.example.first;

import java.util.ArrayList;
import java.util.TreeSet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

class CustomAdapter extends BaseAdapter {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;

    private ArrayList<Todo> mData = new ArrayList<Todo>();
    private TreeSet<Integer> sectionHeader = new TreeSet<Integer>();

    private LayoutInflater mInflater;

    public CustomAdapter(Context context) {
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addItem(Todo todo) {

        mData.add(todo);
        notifyDataSetChanged();
    }

    public void turnState(final String text) {
        for (int i = 0; i < mData.size(); i++) {

            if (mData.get(i).text == text)
                turnState(i);
        }
    }

    public void turnState(final int position) {
        if (mData.get(position).isCompleted == true)
            setState(position, false);
        else
            setState(position, true);
    }

    public void setState(final int position, boolean state) {
        mData.get(position).isCompleted = state;
        notifyDataSetChanged();
    }

    public void addSectionHeaderItem(final String text) {
        Todo sectionItem = new Todo(text, false, "section_item");
        mData.add(sectionItem);
        sectionHeader.add(mData.size() - 1);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return sectionHeader.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Todo getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        int rowType = getItemViewType(position);

        if (convertView == null) {
            holder = new ViewHolder();
            switch (rowType) {
                case TYPE_ITEM:
                    convertView = mInflater.inflate(R.layout.snippet_item1, null);

                    holder.checkBox = (CheckBox) convertView.findViewById(R.id.chb);
                    holder.textView = (TextView) convertView.findViewById(R.id.text);

                    break;
                case TYPE_SEPARATOR:
                    convertView = mInflater.inflate(R.layout.snippet_item2, null);
                    holder.textView = (TextView) convertView.findViewById(R.id.textSeparator);
                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textView.setText(mData.get(position).text);
        if (rowType == 0) {
            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView tv = (TextView) (view);
                    turnState(tv.getText().toString());

                }
            });

            holder.checkBox.setChecked(mData.get(position).isCompleted);
/*
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                Todo totoInFocus = (Todo) buttonView.getTag();
                if (totoInFocus.isCompleted == isChecked) return;

            }
        });*/
        }
        return convertView;
    }

    public static class ViewHolder {
        public TextView textView;
        public CheckBox checkBox;
    }

}
