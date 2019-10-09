package com.example.first;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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
        toolbar.setTitle("Новая задача");
        setSupportActionBar(toolbar);


        Button okButton = findViewById(R.id.ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedProject != "") {
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
                }
                finish();
            }
        });

        Button cancelButton = findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        projectList = findViewById(R.id.projectList);
        currentPosition = 0;
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
                            for (int i = 0; i < projects.size(); i++) {
                                mAdapter.addSectionHeaderItem(projects.get(i).title);
                            }

                            projectList.setAdapter(mAdapter);
                        }
                    }
                });

        todoText = findViewById(R.id.todoText);

        todoText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {

                if (hasFocus) {

                    String text = todoText.getText().toString();

                    if (text.equals("Текст задачи..."))
                        todoText.setText("");
                    else if (text.equals(""))
                        todoText.setText("Текст задачи...");
                }
            }
        });

        projectList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                projectList.setSelection(position);

                if (currentPosition != position) {

                    projectList.getChildAt(currentPosition).setBackgroundColor(getResources().getColor(R.color.backgroundColor));
                    view.setBackgroundColor(getResources().getColor(R.color.accentColor));

                    selectedProject = ((TextView) ((LinearLayout) view).getChildAt(0)).getText().toString();

                    currentPosition = position;
                }
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

}
