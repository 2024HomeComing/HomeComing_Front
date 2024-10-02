package com.example.practice1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practice1.dto.AllboardDto;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private final List<AllboardDto> posts;
    private final OnPostClickListener listener;

    public interface OnPostClickListener {
        void onPostClick(AllboardDto post);
    }

    public PostAdapter(List<AllboardDto> posts, OnPostClickListener listener) {
        this.posts = posts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(viewType == 0 ? R.layout.item_todaycounts : R.layout.item_todayscounts, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        AllboardDto post = posts.get(position);
        holder.bind(post, listener);
    }

    @Override
    public int getItemViewType(int position) {
        return posts.get(position).getKind().equals("missing") ? 0 : 1; // kind에 따라 뷰 타입 결정
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.todayboard);
        }

        public void bind(AllboardDto post, OnPostClickListener listener) {
            titleTextView.setText(post.getTitle());
            itemView.setOnClickListener(v -> listener.onPostClick(post));
        }
    }
}
