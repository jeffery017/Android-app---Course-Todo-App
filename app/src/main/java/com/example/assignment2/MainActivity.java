package com.example.assignment2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.window.OnBackInvokedDispatcher;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener{
    private static final String TAG = "MainActivity";


    Toolbar toolbar;
    RecyclerView recyclerView;

    private final List<Todo> todos = new ArrayList<>();
    private int selectedPosition = -1;
    private TodoAdapter todoAdapter;
    private ActivityResultLauncher<Intent> activityResultLauncherEdit;
    private final LinearLayoutManager layoutManager = new LinearLayoutManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set up recycler view
        recyclerView = findViewById(R.id.recyclerView);
        todoAdapter = new TodoAdapter(todos, this);
        recyclerView.setAdapter(todoAdapter);
        recyclerView.setLayoutManager( layoutManager);

        // set up toolbar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        toolbar.setTitleTextColor( Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        Objects.requireNonNull(toolbar.getOverflowIcon()).setTint(Color.WHITE);
        setSupportActionBar(toolbar);

        // load data from file when app first opened
        loadTodoFile();
        updateSubtitle();


        // activity launcher
        activityResultLauncherEdit = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleEditResult
        );

        // back button callback
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
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuItemAdd) {
            Intent intent = new Intent(this, EditTodoActivity.class);
            activityResultLauncherEdit.launch(intent);
        } else if (item.getItemId() == R.id.menuItemInfo) {
            Intent intent = new Intent(this, InfoActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void handleEditResult(ActivityResult result) {
        Log.d(TAG, "handleEditResult: ");
        if (result == null || result.getData() == null) {return;}
        
        Intent data = result.getData();
        if (result.getResultCode() == RESULT_OK) {
            String action = data.getStringExtra("ACTION");
            Todo todo = (Todo) data.getSerializableExtra("TODO");
            if (action != null && todo != null) {
                if (action.equals("ADD")) {addTodo(todo);}
                else if (action.equals("EDIT")) {editTodo(todo);}
            }
        }
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: ");
        selectedPosition = recyclerView.getChildLayoutPosition(view);
        Todo todo = todos.get(selectedPosition);

        Intent intent = new Intent(this, EditTodoActivity.class);
        intent.putExtra("TODO", todo);
        activityResultLauncherEdit.launch(intent);
    }

    @Override
    public boolean onLongClick(View view) {
        Log.d(TAG, "onLongClick: ");
        selectedPosition = recyclerView.getChildLayoutPosition(view);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Todo");
        builder.setMessage(String.format("Delete Note '%s: %s'?", todos.get(selectedPosition).getCourseId(), todos.get(selectedPosition).getTitle()));
        builder.setPositiveButton("Yes", (dialog, which) -> deleteTodo(selectedPosition));
        builder.setNegativeButton("No", (dialog, which) -> {});
        builder.show();
        return true;
    }
    
    private void backInvoked() { 
        Log.d(TAG, "backInvoked: ");
        finish();
    }

    void editTodo(Todo todo){
        todos.remove(selectedPosition);
        todoAdapter.notifyItemRemoved(selectedPosition);
        todos.add(0, todo);
        todoAdapter.notifyItemInserted(0);
        layoutManager.scrollToPosition(0);
        saveTodoFile();
    }

    void addTodo(Todo todo){
        todos.add(0, todo);
        todoAdapter.notifyItemInserted(0);
        layoutManager.scrollToPosition(0);
        updateSubtitle();
        saveTodoFile();
    }

    void deleteTodo(int position) {
        todos.remove(position);
        todoAdapter.notifyItemRemoved(position);
        updateSubtitle();
        saveTodoFile();
    }

    private void saveTodoFile() {
        Log.d(TAG, "saveTodo: ");
        try {
            FileOutputStream fos = getApplicationContext().openFileOutput(getString(R.string.filename), Context.MODE_PRIVATE);
            PrintWriter writer = new PrintWriter(fos);
            writer.print(todos.toString());
            writer.close();
            fos.close();
        }
        catch (Exception e) {
            e.getStackTrace();
        }
    }

    private void loadTodoFile() {
        try {
            InputStream fis = getApplicationContext().openFileInput(getString(R.string.filename));
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            if (sb.length() > 0) {
                Gson gson = new Gson();
                Todo[] todolist = gson.fromJson(sb.toString(), Todo[].class);
                todos.addAll(List.of(todolist));
            }

        } catch (FileNotFoundException e) {
            Log.d(TAG, "No File Found");
        } catch (Exception e) {
            Log.d(TAG, "Error Loading File");
        }
    }





    @SuppressLint("DefaultLocale")
    void updateSubtitle() {
        toolbar.setSubtitle(String.format("Todo: %d", todos.size()));
    }
}