package com.example.practice1;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Notification extends Fragment {
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<NotificationModel> notificationList;
    private FirebaseFirestore db;
    private static final String TAG = "NotificationFragment";
    private String userId; // 현재 로그인한 사용자의 ID

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        notificationList = new ArrayList<>();
        adapter = new NotificationAdapter(notificationList, getContext());
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        // UserManager를 사용해 userId 가져오기
        userId = UserManager.getUserId(getContext()); // userId 가져오기

        // 조회한 userId 로그로 출력
        if (userId != null) {
            Log.d(TAG, "Retrieved userId: " + userId); // userId 출력
            loadNotifications(userId); // userId가 존재할 때만 알림 로드
        } else {
            Log.w(TAG, "userId is null");
        }

        // 알림 아이템 클릭 리스너 설정
        adapter.setOnItemClickListener(notification -> {
            Log.d(TAG, "Clicked Notification reportId: " + notification.getReportId()); // reportId 로그 출력
            openReportDetailFragment(Long.valueOf(notification.getReportId())); // ReportDetailFragment로 이동
        });

        // ItemTouchHelper 설정
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false; // 이동 기능은 사용하지 않음
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                NotificationModel notification = notificationList.get(position);
                deleteNotification(notification.getId()); // Firestore에서 삭제
                notificationList.remove(position); // 목록에서 삭제
                adapter.notifyItemRemoved(position); // UI 업데이트
            }
        }).attachToRecyclerView(recyclerView);

        return view;
    }

    private void loadNotifications(String userId) {
        db.collection("notifications")
                .whereEqualTo("providerId", userId) // Firestore 쿼리에서 userId와 일치하는 문서만 가져오기
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w(TAG, "Error fetching notifications", error);
                            return;
                        }

                        notificationList.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            NotificationModel notification = doc.toObject(NotificationModel.class);
                            notification.setId(doc.getId()); // 문서 ID 설정
                            notificationList.add(notification);
                            Log.d(TAG, "Notification ID: " + doc.getId() + " => Data: " + doc.getData());
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void deleteNotification(String id) {
        db.collection("notifications").document(id)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Notification successfully deleted!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting notification", e));
    }

    private void openReportDetailFragment(Long reportId) {
        FragmentManager fragmentManager = getParentFragmentManager();
        Fragment fragment = new ReportDetailFragment();

        Bundle args = new Bundle();
        args.putLong("reportId", reportId);
        fragment.setArguments(args);

        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment) // Ensure `fragment_container` is the ID of your container view
                .addToBackStack(null)
                .commit();
    }
}