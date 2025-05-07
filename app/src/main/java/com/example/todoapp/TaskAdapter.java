package com.example.todoapp;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private ArrayList<Task> tasks;
    private OnItemLongClickListener longClickListener;

    public interface OnItemLongClickListener {
        void onItemLongClicked(int position);
    }
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    public TaskAdapter(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkbox;
        ViewHolder(View itemView, OnItemLongClickListener longClickListener) {
            super(itemView);
            checkbox = itemView.findViewById(R.id.checkbox_task);
            itemView.setOnLongClickListener(v -> {
                if (longClickListener != null) {
                    longClickListener.onItemLongClicked(getAdapterPosition());
                    return true;
                }
                return false;
            });
        }
    }

    @NonNull
    @Override
    public TaskAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new ViewHolder(v, longClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.ViewHolder holder, int pos) {
        Task task = tasks.get(pos);
        holder.checkbox.setText(task.getDescription());
        holder.checkbox.setChecked(task.isDone());
        holder.checkbox.setPaintFlags(
                task.isDone()
                        ? holder.checkbox.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
                        : holder.checkbox.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG
        );
        holder.checkbox.setOnCheckedChangeListener((cb, isChecked) -> {
            task.setDone(isChecked);
            cb.setPaintFlags(
                    isChecked
                            ? cb.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
                            : cb.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG
            );
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }
}
