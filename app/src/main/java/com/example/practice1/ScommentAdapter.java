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
    private ApiService apiService;
    private List<Scomment> commentList;  // 변경된 필드, Scomment를 처리
    private String userId;  // 사용자 ID 필드

    // 생성자 수정
    public ScommentAdapter(Context context, ApiService apiService, List<Scomment> commentList, String userId) {
        this.context = context;
        this.apiService = apiService;
        this.commentList = commentList;
        this.userId = userId;
    }


    public void setComments(List<Scomment> comments) {
        this.commentList = comments;
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
        Scomment scomment = commentList.get(position);
        holder.writerTextView.setText(scomment.getWriter());
        holder.commentTextView.setText(scomment.getContent());
        holder.commentTimeTextView.setText(scomment.getTime());

        // 로그에 댓글 정보 출력
        Log.d("ScommentAdapter", "Sighting Comment at position " + position + ":");
        Log.d("ScommentAdapter", "Writer: " + scomment.getWriter());
        Log.d("ScommentAdapter", "Content: " + scomment.getContent());
        Log.d("ScommentAdapter", "Time: " + scomment.getTime());

        // Handle delete button click
        holder.deleteButton.setOnClickListener(v -> {
            Long commentId = scomment.getId();
            deleteSightingComment(commentId, position);
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
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

            if (deleteButton == null) {
                Log.e("ScommentAdapter", "Delete button not found in layout");
            }
        }
    }

    // Method to delete a sighting comment
    private void deleteSightingComment(Long commentId, int position) {
        apiService.deleteSightingCommentById(commentId).enqueue(new Callback<ResponseBody>() {
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