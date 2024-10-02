package com.example.practice1;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practice1.dto.Comment;
import com.example.practice1.dto.Scomment; // Scomment 클래스를 임포트

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context context;
    private ApiService apiService;
    private List<?> commentList;  // 변경된 필드, Comment와 Scomment를 모두 처리할 수 있도록
    private String userId;  // 사용자 ID 필드

    // 생성자 수정
    public CommentAdapter(Context context, ApiService apiService, List<?> commentList, String userId) {
        this.context = context;
        this.apiService = apiService;
        this.commentList = commentList;
        this.userId = userId;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        // 댓글 종류에 따라 처리
        if (commentList.get(position) instanceof Comment) {
            Comment comment = (Comment) commentList.get(position);
            holder.writerTextView.setText(comment.getWriter());
            holder.commentTextView.setText(comment.getContent());
            holder.commentTimeTextView.setText(comment.getTime());

            // 로그에 댓글 정보 출력
            Log.d("CommentAdapter", "Comment at position " + position + ":");
            Log.d("CommentAdapter", "Writer: " + comment.getWriter());
            Log.d("CommentAdapter", "Content: " + comment.getContent());
            Log.d("CommentAdapter", "Time: " + comment.getTime());

            // Handle delete button click
            holder.deleteButton.setOnClickListener(v -> {
                Long commentId = comment.getId();
                deleteComment(commentId, position, userId);
            });
        } else if (commentList.get(position) instanceof Scomment) {
            Scomment scomment = (Scomment) commentList.get(position);
            holder.writerTextView.setText(scomment.getWriter());
            holder.commentTextView.setText(scomment.getContent());
            holder.commentTimeTextView.setText(scomment.getTime());

            // 로그에 댓글 정보 출력
            Log.d("CommentAdapter", "Sighting Comment at position " + position + ":");
            Log.d("CommentAdapter", "Writer: " + scomment.getWriter());
            Log.d("CommentAdapter", "Content: " + scomment.getContent());
            Log.d("CommentAdapter", "Time: " + scomment.getTime());


            // Handle delete button click
            holder.deleteButton.setOnClickListener(v -> {
                Long commentId = scomment.getId();
                deleteSightingComment(commentId, position, userId);
            });
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    // ViewHolder class
    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView writerTextView;
        TextView commentTextView;
        TextView commentTimeTextView;
        Button deleteButton;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            writerTextView = itemView.findViewById(R.id.writer);
            commentTextView = itemView.findViewById(R.id.comment);
            commentTimeTextView = itemView.findViewById(R.id.comment_time);
            deleteButton = itemView.findViewById(R.id.btn_delete);

            if (deleteButton == null) {
                Log.e("CommentAdapter", "Delete button not found in layout");
            }
        }
    }

    // Method to delete a comment
    private void deleteComment(Long commentId, int position, String userId) {
        apiService.deleteCommentById(commentId, userId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("deleteComment", "Response code: " + response.code());
                Log.d("deleteComment", "Response message: " + response.message());

                if (response.isSuccessful()) {
                    commentList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, commentList.size());
                    Log.d("deleteComment", "Comment deleted successfully.");
                } else {
                    Log.e("deleteComment", "Failed to delete comment: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("deleteComment", "Error: " + t.getMessage());
            }
        });
    }

    // Method to delete a sighting comment
    private void deleteSightingComment(Long commentId, int position, String userId) {
        apiService.deleteSightingCommentById(commentId, userId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("deleteSightingComment", "Response code: " + response.code());
                Log.d("deleteSightingComment", "Response message: " + response.message());

                if (response.isSuccessful()) {
                    commentList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, commentList.size());
                    Log.d("deleteSightingComment", "Sighting comment deleted successfully.");
                } else {
                    Log.e("deleteSightingComment", "Failed to delete sighting comment: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("deleteSightingComment", "API call error", t);
            }
        });
    }
}