package com.example.todoapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Task> tasks;
    private TaskAdapter adapter;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;

    private static final String KEY_SAVED_TASKS = "saved_tasks";
    private static final String KEY_SAVED_DONE = "saved_done";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean nightMode = prefs.getBoolean("night_mode", false);
        AppCompatDelegate.setDefaultNightMode(nightMode ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_SAVED_TASKS)) {
            ArrayList<String> savedDescs = savedInstanceState.getStringArrayList(KEY_SAVED_TASKS);
            boolean[] savedDones = savedInstanceState.getBooleanArray(KEY_SAVED_DONE);
            tasks = new ArrayList<>();
            for (int i = 0; i < savedDescs.size(); i++) {
                Task t = new Task(savedDescs.get(i));
                if (savedDones != null && i < savedDones.length) {
                    t.setDone(savedDones[i]);
                }
                tasks.add(t);
            }
        } else {
            tasks = new ArrayList<>();
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Drawable whiteOverflow = ContextCompat.getDrawable(this,
                R.drawable.ic_more_vert_white_24dp);
        if (whiteOverflow != null) {
            toolbar.setOverflowIcon(whiteOverflow);
        }
        adapter = new TaskAdapter(tasks);
        RecyclerView rv = findViewById(R.id.recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        boolean autoSave = prefs.getBoolean("auto_save", false);
        if (savedInstanceState == null && autoSave) {
            loadTasksFromPreferences();
            adapter.notifyDataSetChanged();
        }
        findViewById(R.id.fab).setOnClickListener(v -> {
            EditText input = new EditText(this);
            new AlertDialog.Builder(this).setTitle("Add Task")
                    .setMessage("Enter task description:").setView(input)
                    .setPositiveButton("Add", (dlg, which) -> {
                        String desc = input.getText().toString().trim();
                        if (!desc.isEmpty()) {
                            tasks.add(new Task(desc));
                            adapter.notifyItemInserted(tasks.size() - 1);
                            Snackbar.make(findViewById(R.id.coordinator_layout), "Task added",
                                    Snackbar.LENGTH_SHORT).show();
                            saveTasksToPreferences();
                        }
                    }).setNegativeButton("Cancel", null).show();
        });
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navView = findViewById(R.id.nav_view);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable()
                .setColor(ContextCompat.getColor(this, android.R.color.white));
        navView.setNavigationItemSelectedListener(item -> {
            drawer.closeDrawer(GravityCompat.START);
            if (item.getItemId() == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
            } else if (item.getItemId() == R.id.nav_about) {
                Utils.showInfoDialog(this, "About", "To Do App");
            }
            return true;
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<String> descs = new ArrayList<>();
        for (Task t : tasks) {
            descs.add(t.getDescription());
        }
        outState.putStringArrayList(KEY_SAVED_TASKS, descs);
        boolean[] dones = new boolean[tasks.size()];
        for (int i = 0; i < tasks.size(); i++) {
            dones[i] = tasks.get(i).isDone();
        }
        outState.putBooleanArray(KEY_SAVED_DONE, dones);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void saveTasksToPreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> taskDescriptions = new HashSet<>();
        for (Task task : tasks) {
            taskDescriptions.add(task.getDescription());
        }
        prefs.edit().putStringSet("tasks", taskDescriptions).apply();
    }

    private void loadTasksFromPreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> stored = prefs.getStringSet("tasks", new HashSet<>());
        tasks.clear();
        for (String desc : stored) {
            tasks.add(new Task(desc));
        }
    }

    public void onTaskDeleted() {
        Snackbar.make(
                findViewById(R.id.coordinator_layout),
                "Task deleted",
                Snackbar.LENGTH_SHORT
        ).show();
        saveTasksToPreferences();
    }
}
