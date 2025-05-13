package com.example.todoapp;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private ArrayList<Task> tasks;

    public TaskAdapter(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkbox;
        ImageButton deleteButton;

        ViewHolder(View itemView) {
            super(itemView);
            checkbox = itemView.findViewById(R.id.checkbox_task);
            deleteButton = itemView.findViewById(R.id.button_delete);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {
        Task task = tasks.get(pos);
        holder.checkbox.setText(task.getDescription());
        holder.checkbox.setChecked(task.isDone());
        holder.checkbox.setPaintFlags(task.isDone()
                ? holder.checkbox.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
                : holder.checkbox.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        holder.checkbox.setOnCheckedChangeListener((cb, isChecked) -> {
            task.setDone(isChecked);
            cb.setPaintFlags(isChecked ? cb.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
                    : cb.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        });
        holder.deleteButton.setOnClickListener(v -> {
            tasks.remove(holder.getAdapterPosition());
            notifyItemRemoved(holder.getAdapterPosition());
            if (holder.itemView.getContext() instanceof MainActivity) {
                ((MainActivity) holder.itemView.getContext()).onTaskDeleted();
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }
}
