package com.example.first;

import android.content.Context;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class AddTodo extends AppCompatActivity {

    static public final int NEW_TODO = 1;
    static public final int NOTHING = 0;

    public List<Project> projects;
    int currentPosition;
    String selectedProject = "";
    private CustomAdapter mAdapter;
    EditText todoText;
    ListView projectList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("OpenSans-Light.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());


        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_todo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        projectList = findViewById(R.id.projectList);
        currentPosition = 0;
        mAdapter = new CustomAdapter(this);

        Bundle b = this.getIntent().getExtras();
        String[] projects_strings = b.getStringArray("projects_strings");

        for (int i = 0; i < projects_strings.length; i++) {
            mAdapter.addSectionHeaderItem(projects_strings[i]);
        }

        projectList.setAdapter(mAdapter);

        todoText = findViewById(R.id.todoText);

        todoText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {

                if (hasFocus) {

                    String text = todoText.getText().toString();

                    if (text.equals("Название задачи..."))
                        todoText.setText("");
                    else if (text.equals(""))
                        todoText.setText("Название задачи...");
                }
            }
        });

        projectList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                projectList.setSelection(position);

                if (currentPosition != position) {

                    ImageView icon = projectList.getChildAt(currentPosition).findViewById(R.id.selected_project_icon);
                    icon.setVisibility(View.INVISIBLE);

                    icon = view.findViewById(R.id.selected_project_icon);
                    icon.setVisibility(View.VISIBLE);

                    TextView selectedTextView = ((TextView) ((LinearLayout) view).getChildAt(0));
                    selectedTextView.setBackgroundColor(Color.TRANSPARENT);

                    selectedProject = selectedTextView.getText().toString();

                    currentPosition = position;
                }
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_todo_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add) {
            if (!selectedProject.equals("") & !todoText.getText().toString().equals("Название задачи...")) {
                JsonObject json = new JsonObject();
                json.addProperty("text", todoText.getText().toString());
                json.addProperty("project", selectedProject);

                Ion.with(getBaseContext())
                        .load(getString(R.string.CreateRequest))
                        .setJsonObjectBody(json)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {

                            @Override
                            public void onCompleted(Exception e, JsonObject result) {

                            }
                        });
                setResult(NEW_TODO, new Intent());
                finish();
                return super.onOptionsItemSelected(item);
            }
        }
        if(item.getItemId() == android.R.id.home)
        {   setResult(NOTHING, new Intent());
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

}
