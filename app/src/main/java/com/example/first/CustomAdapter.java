package com.example.first;

import java.util.ArrayList;
import java.util.TreeSet;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.first.R;
import com.example.first.Todo;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

class CustomAdapter extends BaseAdapter {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;

    private ArrayList<Todo> mData = new ArrayList<Todo>();
    private TreeSet<Integer> sectionHeader = new TreeSet<Integer>();

    private LayoutInflater mInflater;
    Context baseContext;

    public CustomAdapter(Context context) {
        this.baseContext = context;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addItem(Todo todo) {
        mData.add(todo);
        notifyDataSetChanged();
    }


    public void addSectionHeaderItem(final String text) {
        Todo sectionItem = new Todo(text, false, "section_item", "section_item");
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

        Paint paint = new Paint();
        paint.setStrikeThruText(mData.get(position).isCompleted);
        holder.textView.setPaintFlags(paint.getFlags());

        if (rowType == 0) {


            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    turnState(position, view);

                }
            });

            holder.checkBox.setChecked(mData.get(position).isCompleted);
            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    turnState(position, view);

                }
            });
        } else {
            paint.setStrikeThruText(false);
            paint.setFakeBoldText(true);
            holder.textView.setPaintFlags(paint.getFlags());
        }
        return convertView;
    }

    public void turnState(int position, View view) {
        mData.get(position).isCompleted = !mData.get(position).isCompleted;

        TextView tv = (TextView) (view);
        Paint paint = new Paint();
        paint.setStrikeThruText(mData.get(position).isCompleted);
        tv.setPaintFlags(paint.getFlags());
        notifyDataSetChanged();

        JsonObject json = new JsonObject();
        json.addProperty("todo_id", mData.get(position).id);
        if (mData.get(position).isCompleted)
            json.addProperty("isCompleted", "true");
        else
            json.addProperty("isCompleted", "false");

        Ion.with(baseContext)
                .load(baseContext.getString(R.string.UpdateRequest))
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                    }
                });
    }

    public static class ViewHolder {
        public TextView textView;
        public CheckBox checkBox;
    }

}
