package com.example.assignment2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;
import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoViewHolder> {

    private static final String TAG = "TodoAdapter";
    private final List<Todo> todos;
    private final MainActivity mainAct;

    public TodoAdapter(List<Todo> todos, MainActivity mainAct) {
        this.todos = todos;
        this.mainAct = mainAct;
    }

    private String trimDescription(String description) {
        int tail = 80;

        if (description.length() <= 80) {
            return description;
        } else {
            return description.substring(0, 80) + "...";
        }
    }


    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.todo_item_list_view, parent, false);

        view.setOnClickListener(mainAct);
        view.setOnLongClickListener(mainAct);

        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: ");

        Todo todo = todos.get(position);

        holder.course_id.setText(todo.getCourseId());
        holder.title.setText(todo.getTitle());
        holder.description.setText(trimDescription( todo.getDescription() ));
        holder.updateAt.setText(todo.getUpdateAt().toString());
    }

    @Override
    public int getItemCount() {
        return todos.size();
    }


}
