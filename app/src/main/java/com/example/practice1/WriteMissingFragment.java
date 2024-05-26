package com.example.practice1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class WriteMissingFragment extends Fragment {

    private static final int GALLERY_REQUEST_CODE = 1;
    private static final int MAX_IMAGE_SELECTION = 5; // 최대 이미지 선택 개수
    private List<Uri> selectedImageUris = new ArrayList<>();
    private LinearLayout imageContainerLayout;
    private int imageWidth;

    private EditText etTitle, etBreed, etName, etSize, etAge, etColor, etCharacteristics, etLastSeenLocation, etLastSeenTime, etContact, etAdditionalInfo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_write_miss, container, false);

        ImageButton btnOpenGallery = view.findViewById(R.id.postimg);
        btnOpenGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        imageContainerLayout = view.findViewById(R.id.image_container_layout);

        // 화면 너비의 5분의 1을 이미지의 가로세로 크기로 사용
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        imageWidth = screenWidth / 5;

        // EditText 변수 초기화
        etTitle = view.findViewById(R.id.etTitle);
        etBreed = view.findViewById(R.id.etBreed);
        etName = view.findViewById(R.id.etName);
        etSize = view.findViewById(R.id.etSize);
        etAge = view.findViewById(R.id.etAge);
        etColor = view.findViewById(R.id.etColor);
        etCharacteristics = view.findViewById(R.id.etCharacteristics);
        etLastSeenLocation = view.findViewById(R.id.etLastSeenLocation);
        etLastSeenTime = view.findViewById(R.id.etLastSeenTime);
        etContact = view.findViewById(R.id.etContact);
        etAdditionalInfo = view.findViewById(R.id.etAdditionalInfo);

        Button btnSubmit = view.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPost();
            }
        });

        return view;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // 다중 이미지 선택 가능하도록 설정
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == getActivity().RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                // 다중 이미지 선택
                int count = Math.min(data.getClipData().getItemCount(), MAX_IMAGE_SELECTION); // 최대 선택 개수만큼만 추가
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    selectedImageUris.add(imageUri);
                }
            } else if (data.getData() != null) {
                // 단일 이미지 선택
                if (selectedImageUris.size() < MAX_IMAGE_SELECTION) {
                    Uri imageUri = data.getData();
                    selectedImageUris.add(imageUri);
                }
            }

            // 이미지 뷰에 선택한 이미지들을 표시
            displaySelectedImages();
        }
    }

    private void displaySelectedImages() {
        imageContainerLayout.removeAllViews(); // 이미지 추가 전에 기존 이미지를 모두 제거

        for (Uri uri : selectedImageUris) {
            ImageView imageView = new ImageView(getActivity());
            imageView.setLayoutParams(new LinearLayout.LayoutParams(imageWidth, imageWidth));
            imageView.setImageURI(uri);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setAdjustViewBounds(true);
            imageContainerLayout.addView(imageView);
        }
    }

    private void submitPost() {
        String title = etTitle.getText().toString();
        String breed = etBreed.getText().toString();
        String name = etName.getText().toString();
        String size = etSize.getText().toString();
        String age = etAge.getText().toString();
        String color = etColor.getText().toString();
        String characteristics = etCharacteristics.getText().toString();
        String lastSeenLocation = etLastSeenLocation.getText().toString();
        String lastSeenTime = etLastSeenTime.getText().toString();
        String contact = etContact.getText().toString();
        String additionalInfo = etAdditionalInfo.getText().toString();

        // 선택된 이미지들을 URI 문자열 리스트로 변환
        List<String> imageUris = new ArrayList<>();
        for (Uri uri : selectedImageUris) {
            imageUris.add(uri.toString());
        }

        // 서버로 데이터를 전송
        sendPostToServer(title, breed, name, size, age, color, characteristics, lastSeenLocation, lastSeenTime, contact, additionalInfo, imageUris);
    }

    private void sendPostToServer(String title, String breed, String name, String size, String age, String color, String characteristics, String lastSeenLocation, String lastSeenTime, String contact, String additionalInfo, List<String> imageUris) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://172.30.1.72:9080/boards") // 서버 주소 변경 필요
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Post post = new Post(title, breed, name, size, age, color, characteristics, lastSeenLocation, lastSeenTime, contact, additionalInfo, imageUris);

        Call<ResponseBody> call = apiService.createPost(post);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // 성공적으로 전송됨
                    Log.d("WritingPostFragment", "게시글 작성 성공");
                    Toast.makeText(getActivity(), "게시글 작성이 완료 되었습니다!", Toast.LENGTH_SHORT).show();
                } else {
                    // 오류 발생
                    Log.e("WritingPostFragment", "게시글 작성 실패: " + response.code());
                    Toast.makeText(getActivity(), "작성 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // 네트워크 오류 발생
                Log.e("WritingPostFragment", "네트워크 오류: " + t.getMessage(), t);
                Toast.makeText(getActivity(), "네트워크 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public interface ApiService {
        @POST("posts")
        Call<ResponseBody> createPost(@Body Post post);
    }

    public class Post {
        private String title;
        private String breed;
        private String name;
        private String size;
        private String age;
        private String color;
        private String characteristics;
        private String lastSeenLocation;
        private String lastSeenTime;
        private String contact;
        private String additionalInfo;
        private List<String> imageUris;

        public Post(String title, String breed, String name, String size, String age, String color, String characteristics, String lastSeenLocation, String lastSeenTime, String contact, String additionalInfo, List<String> imageUris) {
            this.title = title;
            this.breed = breed;
            this.name = name;
            this.size = size;
            this.age = age;
            this.color = color;
            this.characteristics = characteristics;
            this.lastSeenLocation = lastSeenLocation;
            this.lastSeenTime = lastSeenTime;
            this.contact = contact;
            this.additionalInfo = additionalInfo;
            this.imageUris = imageUris;
        }

        // getter/setter 생략 가능
    }
}