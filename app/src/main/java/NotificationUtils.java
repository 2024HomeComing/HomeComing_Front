import android.util.Log;

import com.example.practice1.NotificationModel;
import com.google.firebase.firestore.FirebaseFirestore;
import android.util.Log;

public class NotificationUtils {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void addNotification(String title, String message, String providerId) {
        // 새로운 생성자를 사용하여 providerId를 포함한 NotificationModel 객체를 생성
        NotificationModel notification = new NotificationModel(title, message, System.currentTimeMillis(), providerId);

        db.collection("notifications")
                .add(notification)
                .addOnSuccessListener(documentReference -> {
                    // 성공적으로 저장된 경우 로그 출력
                    Log.d("Firestore", "Notification added with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    // 저장 실패 시 로그 출력
                    Log.w("Firestore", "Error adding notification", e);
                });
    }
}