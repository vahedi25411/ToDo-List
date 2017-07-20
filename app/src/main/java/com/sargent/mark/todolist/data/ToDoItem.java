package com.sargent.mark.todolist.data;

/**
 * Created by mark on 7/4/17.
 */

public class ToDoItem {
    private String description;
    private String dueDate;
    private int category; // this variable has been created to save the category value
    private boolean isDone; // this variable has been created to save the status of a todo item

    public ToDoItem(String description, String dueDate, int category, boolean isDone) {
        this.description = description;
        this.dueDate = dueDate;
        this.category = category;
        this.isDone = isDone;
    }

    public boolean isDone() { return isDone; }

    public void setDone(boolean done) { isDone = done; }

    public int getCategory() { return category; }

    public void setCategory(int category) { this.category = category; }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }
}
