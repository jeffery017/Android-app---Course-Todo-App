package com.example.assignment2;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TodoViewHolder extends RecyclerView.ViewHolder {
    TextView course_id;
    TextView title;
    TextView description;
    TextView updateAt;

    public TodoViewHolder(@NonNull View view) {
        super(view);
        course_id = view.findViewById(R.id.course_id);
        title = view.findViewById(R.id.title);
        description = view.findViewById(R.id.description);
        updateAt = view.findViewById(R.id.updateAt);
    }
}
