package com.example.first;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;


import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public CustomAdapter mAdapter;
    private ListView listView;
    public List<Project> projects;
    public List<Todo> todos;
    String[] projects_strings;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("OpenSans-Light.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar_main_activity);
        setSupportActionBar(toolbar);


        listView = findViewById(R.id.list);

        dounloadDataAndSetupView();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (projects_strings != null) {
                    Intent intent = new Intent(MainActivity.this, AddTodo.class);

                    Bundle b = new Bundle();
                    b.putStringArray("projects_strings", projects_strings);
                    intent.putExtras(b);


                    startActivityForResult(intent, 0);
                    TextView todoText_view = findViewById(R.id.todoText);


                }
            }
        });

    }

    void dounloadDataAndSetupView() {

        if (mAdapter != null) {
            mAdapter.mData.clear();
            mAdapter.notifyDataSetChanged();
            projects = null;
            todos = null;
        }
        mAdapter = new CustomAdapter(this);


        Ion.with(this)

                .load(getString(R.string.apiGetProjects))

                .asJsonArray()

                .setCallback(new FutureCallback<JsonArray>() {

                    @Override

                    public void onCompleted(Exception e, JsonArray result) {

                        if (result != null) {

                            projects = new ArrayList<Project>();

                            for (final JsonElement projectJsonElement : result) {

                                projects.add(new Gson().fromJson(projectJsonElement, Project.class));

                            }
                            projects_strings = new String[projects.size()];

                            for (int i = 0; i < projects.size(); i++)
                                projects_strings[i] = projects.get(i).title;
                            showAllTodos();
                        }
                    }
                });

        Ion.with(this)

                .load(getString(R.string.apiGetTodos))

                .asJsonArray()

                .setCallback(new FutureCallback<JsonArray>() {

                    @Override

                    public void onCompleted(Exception e, JsonArray result) {

                        if (result != null) {

                            todos = new ArrayList<Todo>();

                            for (final JsonElement todoJsonElement : result) {

                                todos.add(new Gson().fromJson(todoJsonElement, Todo.class));

                            }

                            showAllTodos();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            if (resultCode == AddTodo.NEW_TODO) {
                dounloadDataAndSetupView();
            }
        }
    }

    public void showAllTodos() {
        mAdapter.mData.clear();
        mAdapter.notifyDataSetChanged();
        if (projects != null & todos != null) {

            for (int i = 0; i < projects.size(); i++) {

                mAdapter.addSectionHeaderItem(projects.get(i).title);
                for (int j = 0; j < todos.size(); j++) {
                    if (projects.get(i).id.equals(todos.get(j).project_id))
                        mAdapter.addItem(todos.get(j));
                }
            }
            listView.setAdapter(mAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
}

