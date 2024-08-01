package com.example.practice1;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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

import com.example.practice1.dto.ProfileUpdateDto;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.gson.Gson;
import com.kakao.sdk.user.UserApiClient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileEditFragment extends Fragment {

    private static final String TAG = "ProfileEditFragment";
    private static final int GALLERY_REQUEST_CODE = 1;
    private static final String PROFILE_UPDATE_URL = "https://homeskyul.store/api/";

    private Context context;
    private List<Uri> selectedImageUris = new ArrayList<>();

    private EditText edNickname, edRegion, edDetails;
    private ShapeableImageView profileImg;
    private Button btnSave;

    @Override
    public void onAttach(@NonNull Context context) {
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

    public String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(columnIndex);
            cursor.close();
            return path;
        }
        return null;
    }

    private void getUserIdAndSaveProfile() {
        UserApiClient.getInstance().me((user, error) -> {
            if (error != null) {
                Log.e(TAG, "사용자 정보 요청 실패", error);
                Toast.makeText(context, "사용자 정보를 가져오는 데 실패했습니다", Toast.LENGTH_SHORT).show();
            } else if (user != null) {
                saveProfile(String.valueOf(user.getId()));
            }
            return null;
        });
    }

    private void saveProfile(String userId) {
        String nickname = edNickname.getText().toString().trim();
        String region = edRegion.getText().toString().trim();
        String details = edDetails.getText().toString().trim();

        if (nickname.isEmpty() || region.isEmpty() || details.isEmpty()) {
            Toast.makeText(context, "모든 필드를 채워주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "User ID 전송 확인: " + userId);

        // ProfileUpdateDto 객체 생성
        ProfileUpdateDto profileUpdateDto = new ProfileUpdateDto(userId, nickname, region, details, null);

        // 선택된 이미지가 있는 경우에만 전송
        if (!selectedImageUris.isEmpty()) {
            Uri imageUri = selectedImageUris.get(0);
            sendProfileToServer(profileUpdateDto, imageUri);
        } else {
            sendProfileToServer(profileUpdateDto, null);
        }
    }

    private void sendProfileToServer(ProfileUpdateDto profileUpdateDto, Uri imageUri) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PROFILE_UPDATE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        MultipartBody.Part imagePart = null;
        if (imageUri != null) {
            String imagePath = getRealPathFromURI(imageUri);
            Log.d(TAG, "Selected image path: " + imagePath); // 이미지 경로를 로그로 출력
            if (imagePath != null) {
                File file = new File(imagePath);
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                imagePart = MultipartBody.Part.createFormData("images", file.getName(), requestFile);
                Log.d(TAG, "Image attached: " + file.getName());
            } else {
                Log.e(TAG, "이미지 파일 경로가 null입니다");
            }
        }

        // Convert ProfileUpdateDto to JSON
        Gson gson = new Gson();
        String profileUpdateJson = gson.toJson(profileUpdateDto);
        RequestBody profileUpdateBody = RequestBody.create(MediaType.parse("application/json"), profileUpdateJson);

        // Log the data to be sent
        Log.d(TAG, "ProfileUpdateDto JSON: " + profileUpdateJson);
        if (imagePart != null) {
            Log.d(TAG, "Image file name: " + imagePart.headers().get("Content-Disposition"));
        } else {
            Log.d(TAG, "No image attached");
        }

        Call<ResponseBody> call = apiService.updateProfile(imagePart, profileUpdateBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        Log.d(TAG, "Profile update successful: " + responseBody);
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(context, "프로필 업데이트 성공", Toast.LENGTH_SHORT).show();
                            getActivity().getSupportFragmentManager().popBackStack();
                        });
                    } catch (IOException e) {
                        Log.e(TAG, "Response body parsing error", e);
                    }
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e(TAG, "Profile update error: " + errorBody);
                        getActivity().runOnUiThread(() ->
                                Toast.makeText(context, "프로필 업데이트 오류: " + errorBody, Toast.LENGTH_SHORT).show()
                        );
                    } catch (IOException e) {
                        Log.e(TAG, "Error response parsing error", e);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(TAG, "Profile update failed", t);
                getActivity().runOnUiThread(() ->
                        Toast.makeText(context, "프로필 업데이트 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        });
    }
}