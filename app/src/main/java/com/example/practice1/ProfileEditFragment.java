package com.example.practice1;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.imageview.ShapeableImageView;
import com.kakao.sdk.user.UserApiClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

public class ProfileEditFragment extends Fragment {

    private static final String TAG = "ProfileEditFragment";
    private static final int GALLERY_REQUEST_CODE = 1;
    private static final String PROFILE_UPDATE_URL = "https://homeskyul.store/api/users/profile_update";

    private Context context;
    private List<Uri> selectedImageUris = new ArrayList<>();

    private EditText edNickname, edRegion, edDetails;
    private ShapeableImageView profileImg;
    private Button btnSave;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_edit, container, false);

        // UI 컴포넌트 초기화
        profileImg = view.findViewById(R.id.profileImg);
        Button btnOpenGallery = view.findViewById(R.id.change_prof);
        edNickname = view.findViewById(R.id.ednickname);
        edRegion = view.findViewById(R.id.edregion);
        edDetails = view.findViewById(R.id.eddetails);
        btnSave = view.findViewById(R.id.save);

        btnOpenGallery.setOnClickListener(v -> openGallery());
        btnSave.setOnClickListener(v -> getUserIdAndSaveProfile());

        return view;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == getActivity().RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                profileImg.setImageURI(selectedImageUri);
                selectedImageUris.clear();
                selectedImageUris.add(selectedImageUri);
            }
        }
    }

    private void getUserIdAndSaveProfile() {
        UserApiClient.getInstance().me((user, error) -> {
            if (error != null) {
                Log.e(TAG, "사용자 정보 요청 실패", error);
                Toast.makeText(context, "사용자 정보를 가져오는 데 실패했습니다", Toast.LENGTH_SHORT).show();
            } else if (user != null) {
                saveProfile(user.getId());
            }
            return null;
        });
    }

    private void saveProfile(Long userId) {
        String nickname = edNickname.getText().toString().trim();
        String region = edRegion.getText().toString().trim();
        String details = edDetails.getText().toString().trim();

        if (nickname.isEmpty() || region.isEmpty() || details.isEmpty()) {
            Toast.makeText(context, "모든 필드를 채워주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "User ID: " + userId);

        OkHttpClient client = new OkHttpClient();

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("P_update[user_id]", String.valueOf(userId))
                .addFormDataPart("P_update[nickname]", nickname)
                .addFormDataPart("P_update[region]", region)
                .addFormDataPart("P_update[details]", details);

        // 이미지 파일 추가
        if (!selectedImageUris.isEmpty()) {
            Uri imageUri = selectedImageUris.get(0);
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
                if (inputStream != null) {
                    builder.addFormDataPart("P_update[image]", "image.jpg", new RequestBody() {
                        @Override
                        public MediaType contentType() {
                            return MediaType.parse("image/jpeg"); // or image/png
                        }

                        @Override
                        public void writeTo(BufferedSink sink) throws IOException {
                            try (InputStream in = inputStream) {
                                sink.writeAll(Okio.source(in));
                            }
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "이미지 파일을 열 수 없습니다", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(PROFILE_UPDATE_URL)
                .put(requestBody) // PUT 요청으로 변경
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "Profile update failed", e);
                getActivity().runOnUiThread(() ->
                        Toast.makeText(context, "프로필 업데이트 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    Log.d(TAG, "Profile update successful: " + responseBody);
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(context, "프로필 업데이트 성공", Toast.LENGTH_SHORT).show()
                    );
                } else {
                    Log.e(TAG, "Profile update error: " + responseBody);
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(context, "프로필 업데이트 오류: " + responseBody, Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }
}