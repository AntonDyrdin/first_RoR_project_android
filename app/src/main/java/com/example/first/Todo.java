package com.example.first;

public class Todo {
    public String id;
    public String project_id;
    public String text;
    public boolean isCompleted;


    public Todo(String text, boolean isCompleted, String project_id,String id) {
        this.text = text;
        this.isCompleted = isCompleted;
        this.project_id = project_id;
        this.id=id;
    }
}
