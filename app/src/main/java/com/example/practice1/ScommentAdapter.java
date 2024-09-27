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

import com.example.practice1.dto.Scomment;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScommentAdapter extends RecyclerView.Adapter<ScommentAdapter.ScommentViewHolder> {

    private Context context;
    private RecyclerView commentsRecyclerView;
    private ApiService apiService;
    private List<Scomment> commentsList;  // 변경된 필드
    private String userId;  // 사용자 ID 필드

    // 생성자 수정
    public ScommentAdapter(Context context, ApiService apiService, List<Scomment> commentsList, String userId, RecyclerView commentsRecyclerView) {
        this.context = context;
        this.apiService = apiService;
        this.commentsList = commentsList;  // 수정된 필드
        this.userId = userId;
        this.commentsRecyclerView = commentsRecyclerView;
    }

    public void setComments(List<Scomment> comments) {
        this.commentsList = comments;  // 수정된 필드
        notifyDataSetChanged(); // RecyclerView가 데이터 변경을 인식하도록 갱신
    }

    @NonNull
    @Override
    public ScommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new ScommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScommentViewHolder holder, int position) {
        Scomment scomment = commentsList.get(position);  // 수정된 필드
        holder.writerTextView.setText(scomment.getWriter());
        holder.commentTextView.setText(scomment.getContent());
        holder.commentTimeTextView.setText(scomment.getTime());

        // Handle delete button click
        holder.deleteButton.setOnClickListener(v -> {
            Long commentId = scomment.getId();
            deleteSightingComment(commentId, position);
        });
    }

    @Override
    public int getItemCount() {
        return commentsList.size();  // 수정된 필드
    }

    public void addComment(Scomment comment) {
        commentsList.add(comment);
        notifyItemInserted(commentsList.size() - 1);
        commentsRecyclerView.smoothScrollToPosition(commentsList.size() - 1); // 스크롤 처리
    }
    // ViewHolder class
    public static class ScommentViewHolder extends RecyclerView.ViewHolder {
        TextView writerTextView;
        TextView commentTextView;
        TextView commentTimeTextView;
        Button deleteButton;

        public ScommentViewHolder(@NonNull View itemView) {
            super(itemView);
            writerTextView = itemView.findViewById(R.id.writer);
            commentTextView = itemView.findViewById(R.id.comment);
            commentTimeTextView = itemView.findViewById(R.id.comment_time);
            deleteButton = itemView.findViewById(R.id.btn_delete);
        }
    }

    // Method to delete a sighting comment
    private void deleteSightingComment(Long commentId, int position) {
        apiService.deleteSightingCommentById(commentId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    commentsList.remove(position);  // 수정된 필드
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, commentsList.size());  // 수정된 필드
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