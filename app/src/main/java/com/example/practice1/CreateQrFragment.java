package com.example.practice1;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreateQrFragment extends Fragment {

    private static final int REQUEST_IMAGE_PICK = 1;
    private Uri selectedImageUri;

    private EditText nameEditText, speciesEditText, hairEditText, locationEditText, likeDislikeEditText, phoneNumberEditText, manualEditText;
    private Button createQrButton;
    private ImageButton postPetImgButton;
    private ImageView selectedImageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_qr, container, false);

        // UI 요소 초기화
        nameEditText = view.findViewById(R.id.name);
        speciesEditText = view.findViewById(R.id.species);
        hairEditText = view.findViewById(R.id.hair);
        locationEditText = view.findViewById(R.id.location);
        likeDislikeEditText = view.findViewById(R.id.like_dislike);
        phoneNumberEditText = view.findViewById(R.id.phone_number);
        manualEditText = view.findViewById(R.id.manual);
        createQrButton = view.findViewById(R.id.btn_create_qr);
        postPetImgButton = view.findViewById(R.id.postpetimg);
        selectedImageView = view.findViewById(R.id.selected_image_view);

        // 이미지 선택을 위한 갤러리 열기
        postPetImgButton.setOnClickListener(v -> openGallery());

        // QR 코드 생성 버튼 클릭 시 처리
        createQrButton.setOnClickListener(v -> {
            // 사용자 입력 수집
            String name = nameEditText.getText().toString().trim();
            String species = speciesEditText.getText().toString().trim();
            String hairColor = hairEditText.getText().toString().trim();
            String location = locationEditText.getText().toString().trim();
            String likeDislike = likeDislikeEditText.getText().toString().trim();
            String phoneNumber = phoneNumberEditText.getText().toString().trim();
            String manual = manualEditText.getText().toString().trim();

            // 필수 필드 검증
            if (name.isEmpty() || species.isEmpty() || hairColor.isEmpty() || location.isEmpty() || likeDislike.isEmpty() || phoneNumber.isEmpty() || manual.isEmpty()) {
                Toast.makeText(getActivity(), "모든 정보를 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 사용자 데이터를 JSON 형식으로 변환
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name", name);
                jsonObject.put("species", species);
                jsonObject.put("hairColor", hairColor);
                jsonObject.put("location", location);
                jsonObject.put("likeDislike", likeDislike);
                jsonObject.put("phoneNumber", phoneNumber);
                jsonObject.put("manual", manual);
                jsonObject.put("userId", SingletonClass.getInstance().getUserId());

                // JSON 객체 로그 출력
                Log.d("CreateQrFragment1", "JSON Object: " + jsonObject.toString());

                // 사용자 데이터와 이미지를 서버에 전송
                sendUserDataWithImageToServer(jsonObject.toString(), selectedImageUri);
            } catch (JSONException e) {
                Log.e("CreateQrFragment1", "JSON Object creation error: " + e.getMessage());
            }
        });
        return view;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == getActivity().RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            displaySelectedImage();
        }
    }

    private void displaySelectedImage() {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
            int buttonHeight = postPetImgButton.getHeight();
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, buttonHeight, buttonHeight, true);
            selectedImageView.setImageBitmap(scaledBitmap);
            selectedImageView.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            Log.e("CreateQrFragment1", "Image selection error: " + e.getMessage());
        }
    }

    private void sendUserDataWithImageToServer(String jsonData, Uri imageUri) {
        RequestBody jsonPart = RequestBody.create(MediaType.parse("application/json"), jsonData);
        MultipartBody.Part imagePart = null;

        if (imageUri != null) {
            String filePath = getRealPathFromURI(imageUri);
            File file = new File(filePath);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            imagePart = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        }

        // 서버에 데이터 전송
        sendPostToServer(jsonPart, imagePart);
    }

    private String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(columnIndex);
            cursor.close();
            return path;
        }
        return null;
    }

    private void sendPostToServer(RequestBody jsonPart, MultipartBody.Part imagePart) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://homeskyul.store/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<ResponseBody> call = apiService.createPetInfo(jsonPart, imagePart); // petId는 apiService에서 필요에 따라 처리합니다.

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("CreateQrFragment2", "Response Code: " + response.code()); // 응답 코드 로그
                if (response.isSuccessful()) {
                    Log.d("CreateQrFragment2", "Data sent successfully");
                    Toast.makeText(getActivity(), "QR 코드가 생성되었습니다!", Toast.LENGTH_SHORT).show();

                    // Navigate to MyQrFragment
                    navigateToMyQrFragment();
                } else {
                    Log.e("CreateQrFragment2", "Error sending data: " + response.message());
                    Toast.makeText(getActivity(), "데이터 전송 실패. 응답 코드: " + response.code(), Toast.LENGTH_SHORT).show(); // 오류 메시지에 응답 코드 추가
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("CreateQrFragment3", "Network error: " + t.getMessage());
                Toast.makeText(getActivity(), "네트워크 오류 발생.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToMyQrFragment() {
        MyQrFragment myQrFragment = new MyQrFragment();
        // Optionally, you can add a transition animation
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, myQrFragment) // Replace with the actual container ID
                .addToBackStack(null) // Optional: Add to back stack if you want to allow the user to navigate back
                .commit();
    }
}