package com.example.assignment2;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import android.window.OnBackInvokedDispatcher;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Date;
import java.util.Objects;

public class EditTodoActivity extends AppCompatActivity {
    private static final String TAG = "EditTodoActivity";
    EditText courseId;
    EditText title;
    EditText description;

    Toolbar toolbar;
    Intent resultIntent = new Intent();
    Todo originalTodo = new Todo("", "", "", new Date());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_todo);

        courseId = findViewById(R.id.courseId);
        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        description.setMovementMethod(new ScrollingMovementMethod());

        toolbar = findViewById(R.id.toolbarEdit);

        toolbar.setTitle(getString(R.string.app_name));
        toolbar.setTitleTextColor(Color.WHITE);
        Objects.requireNonNull(toolbar.getOverflowIcon()).setTint(Color.WHITE);
        setSupportActionBar(toolbar);



        Intent intent = getIntent();
        if (intent.hasExtra("TODO")) {
            originalTodo = (Todo) intent.getSerializableExtra("TODO");
            if (originalTodo != null) {
                courseId.setText(originalTodo.getCourseId());
                title.setText(originalTodo.getTitle());
                description.setText(originalTodo.getDescription());
            }
            resultIntent.putExtra("ACTION", "EDIT");
        }
        else {
            resultIntent.putExtra("ACTION", "ADD");
        }


        // when back button is pressed, call `backInvoked()` method
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getOnBackInvokedDispatcher().registerOnBackInvokedCallback(
                    OnBackInvokedDispatcher.PRIORITY_DEFAULT,
                    this::backInvoked
            );
        } else {
            getOnBackPressedDispatcher().addCallback(
                    new OnBackPressedCallback(true) {
                        @Override
                        public void handleOnBackPressed() {
                            backInvoked();
                        }
                    }
            );
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuItemSave) {
            save();
        }
        return super.onOptionsItemSelected(item);
    }

    private void backInvoked() {
        Log.d(TAG, "backInvoked: ");
        String courseId = this.courseId.getText().toString().trim();
        String title = this.title.getText().toString().trim();
        String description = this.description.getText().toString().trim();


        if (courseId.isEmpty() && title.isEmpty() && description.isEmpty()) {
            // all fields are empty, drop it.
             dropEmptyTodo();
        }
        // alert user to save without id or title
        else if (courseId.isEmpty() || title.isEmpty()) {
            alertSaveWithoutIdOrTitle();
        }
        // without edited, cancel editing and exit
        else if (
                originalTodo.getCourseId().equals(courseId) &&
                originalTodo.getTitle().equals(title) &&
                originalTodo.getDescription().equals(description)
        ) {
            Toast.makeText(this, getString(R.string.toast_no_change_made), Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK, resultIntent);
            finish();
        }
        else {
            askIfSaveChange();
        }
    }

    private void save() {
        Log.d(TAG, "save: ");
        String courseId = this.courseId.getText().toString().trim();
        String title = this.title.getText().toString().trim();
        String description = this.description.getText().toString().trim();

        // all fields are empty, drop it
        if ( description.isEmpty() && courseId.isEmpty() && title.isEmpty()) {
            dropEmptyTodo();
        }
        // alert user to save without id or title
        else if (courseId.isEmpty() || title.isEmpty()) {
            alertSaveWithoutIdOrTitle();
        }
        else {
            Todo newTodo = new Todo( courseId,  title, description, new Date());
            resultIntent.putExtra("TODO", newTodo);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }

    private void askIfSaveChange() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(String.format("Your note is not saved!\nSave note '%s'?", title.getText().toString() ));
        builder.setPositiveButton("Yes", (dialog, which) -> save());
        builder.setNegativeButton("No", (dialog, which) -> {
            setResult(RESULT_CANCELED, resultIntent);
            finish();
        });
        builder.show();
    }
    private void alertSaveWithoutIdOrTitle() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Leave without Titles?");
        builder.setMessage("Course ID and Title cannot be empty. If you click OK, the change will be discarded.");

        builder.setPositiveButton("OK", (dialog, which) -> {
            //Toast.makeText(this, "Cannot save item without course id and title", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED, resultIntent);
            finish();});
        builder.setNegativeButton("Cancel", (dialog, which) -> {});
        builder.show();
    }

    private void dropEmptyTodo() {
        Toast.makeText(this, getString(R.string.toast_save_empty_todo), Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}