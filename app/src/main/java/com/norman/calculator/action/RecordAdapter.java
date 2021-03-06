package com.norman.calculator.action;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.norman.calculator.R;

import java.util.ArrayList;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder> {
    private final ArrayList<String> mArrayList;

    RecordAdapter(ArrayList<String> arrayList) {
        mArrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.answer_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.list.setText(mArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    void addString(String str) {
        mArrayList.add(str);
        notifyItemInserted(mArrayList.size() - 1);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView list;

        ViewHolder(View view) {
            super(view);
            list = view.findViewById(R.id.list);
        }
    }

    void insertString(int position, String str) {
        mArrayList.add(position, str);
        notifyItemInserted(position);
    }

    void removeString(int position) {
        mArrayList.remove(position);
        notifyItemRemoved(position);
    }

    void clearString() {
        mArrayList.clear();
        notifyDataSetChanged();
    }

    ArrayList<String> getArrayList() {
        return mArrayList;
    }

}