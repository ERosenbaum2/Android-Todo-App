package com.example.todoapp;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import android.content.Intent;
import android.view.MenuItem;
import androidx.annotation.NonNull;


public class MainActivity extends AppCompatActivity {

    private ArrayList<Task> tasks;
    private TaskAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean nightMode = prefs.getBoolean("night_mode", false);
        AppCompatDelegate.setDefaultNightMode(
                nightMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Drawable overflow = toolbar.getOverflowIcon();
        if (overflow != null) {
            overflow.setTint(getResources().getColor(android.R.color.white, getTheme()));
        }
        tasks = new ArrayList<>();
        adapter = new TaskAdapter(tasks);
        RecyclerView rv = findViewById(R.id.recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        boolean autoSave = prefs.getBoolean("auto_save", false);
        if (autoSave) {
            loadTasksFromPreferences();
        }
        findViewById(R.id.fab).setOnClickListener(v -> {
            final EditText input = new EditText(MainActivity.this);
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Add Task")
                    .setMessage("Enter task description:")
                    .setView(input)
                    .setPositiveButton("Add", (dialog, which) -> {
                        String desc = input.getText().toString().trim();
                        if (!desc.isEmpty()) {
                            tasks.add(new Task(desc));
                            adapter.notifyItemInserted(tasks.size() - 1);
                            Snackbar.make(
                                    findViewById(R.id.coordinator_layout),
                                    "Task added",
                                    Snackbar.LENGTH_SHORT
                            ).show();
                            saveTasksToPreferences();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
        adapter.setOnItemLongClickListener(position -> {
            tasks.remove(position);
            adapter.notifyItemRemoved(position);
            Snackbar.make(
                    findViewById(R.id.coordinator_layout),
                    "Task deleted",
                    Snackbar.LENGTH_SHORT
            ).show();
            saveTasksToPreferences();
        });
    }

    private void saveTasksToPreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        Set<String> taskDescriptions = new HashSet<>();
        for (Task task : tasks) {
            taskDescriptions.add(task.getDescription());
        }
        editor.putStringSet("tasks", taskDescriptions);
        editor.apply();
    }

    private void loadTasksFromPreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> taskDescriptions = prefs.getStringSet("tasks", new HashSet<>());
        if (taskDescriptions != null) {
            tasks.clear();
            for (String desc : taskDescriptions) {
                tasks.add(new Task(desc));
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_about) {
            new AlertDialog.Builder(this)
                    .setTitle("About")
                    .setMessage("Simple To-Do App\nVersion 1.0")
                    .setPositiveButton("OK", null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}