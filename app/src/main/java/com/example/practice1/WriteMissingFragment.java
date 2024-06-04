package com.example.practice1;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import java.io.File;
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

public class WriteMissingFragment extends Fragment {

    private static final int GALLERY_REQUEST_CODE = 1;
    private static final int MAX_IMAGE_SELECTION = 5;
    private List<Uri> selectedImageUris = new ArrayList<>();
    private LinearLayout imageContainerLayout;
    private int imageWidth;

    private EditText title, breed, name, size, age, color, characteristics, lastSeenLocation, lastSeenTime, contact, additionalInfo;

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

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        imageWidth = screenWidth / 5;

        title = view.findViewById(R.id.etTitle);
        breed = view.findViewById(R.id.etBreed);
        name = view.findViewById(R.id.etName);
        size = view.findViewById(R.id.etSize);
        age = view.findViewById(R.id.etAge);
        color = view.findViewById(R.id.etColor);
        characteristics = view.findViewById(R.id.etCharacteristics);
        lastSeenLocation = view.findViewById(R.id.etLastSeenLocation);
        lastSeenTime = view.findViewById(R.id.etLastSeenTime);
        contact = view.findViewById(R.id.etContact);
        additionalInfo = view.findViewById(R.id.etAdditionalInfo);

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
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == getActivity().RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                int count = Math.min(data.getClipData().getItemCount(), MAX_IMAGE_SELECTION);
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    selectedImageUris.add(imageUri);
                }
            } else if (data.getData() != null) {
                if (selectedImageUris.size() < MAX_IMAGE_SELECTION) {
                    Uri imageUri = data.getData();
                    selectedImageUris.add(imageUri);
                }
            }
            displaySelectedImages();
        }
    }

    private void displaySelectedImages() {
        imageContainerLayout.removeAllViews();
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
        try {
            String titleText = title.getText().toString();
            String breedText = breed.getText().toString();
            String nameText = name.getText().toString();
            String sizeText = size.getText().toString();
            String ageText = age.getText().toString();
            String colorText = color.getText().toString();
            String characteristicsText = characteristics.getText().toString();
            String lastSeenLocationText = lastSeenLocation.getText().toString();
            String lastSeenTimeText = lastSeenTime.getText().toString();
            String contactText = contact.getText().toString();
            String additionalInfoText = additionalInfo.getText().toString();

            List<MultipartBody.Part> imageParts = new ArrayList<>();
            for (Uri uri : selectedImageUris) {
                String filePath = getRealPathFromURI(uri);
                if (filePath != null) {
                    File file = new File(filePath);
                    RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
                    MultipartBody.Part part = MultipartBody.Part.createFormData("images", file.getName(), requestBody);
                    imageParts.add(part);
                } else {
                    Log.e("WriteMissingFragment", "Image file path is null");
                }
            }

            String kakaoId = "123456789";
            sendPostToServer(titleText, breedText, nameText, sizeText, ageText, colorText, characteristicsText, lastSeenLocationText, lastSeenTimeText, contactText, additionalInfoText, kakaoId, imageParts);
        } catch (Exception e) {
            Log.e("WriteMissingFragment", "submitPost: 오류 발생", e);
            Toast.makeText(getActivity(), "오류 발생: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        String filePath = null;
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(contentUri, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
            cursor.close();
        }
        return filePath;
    }

    private void sendPostToServer(String title, String breed, String name, String size, String age, String color, String characteristics, String lastSeenLocation, String lastSeenTime, String contact, String additionalInfo, String kakaoId, List<MultipartBody.Part> image) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://homeskyul.store/boards/") // 서버 주소 변경 필요
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);

        RequestBody titlePart = RequestBody.create(MediaType.parse("text/plain"), title);
        RequestBody breedPart = RequestBody.create(MediaType.parse("text/plain"), breed);
        RequestBody namePart = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody sizePart = RequestBody.create(MediaType.parse("text/plain"), size);
        RequestBody agePart =         RequestBody.create(MediaType.parse("text/plain"), age);
        RequestBody colorPart = RequestBody.create(MediaType.parse("text/plain"), color);
        RequestBody characteristicsPart = RequestBody.create(MediaType.parse("text/plain"), characteristics);
        RequestBody lastSeenLocationPart = RequestBody.create(MediaType.parse("text/plain"), lastSeenLocation);
        RequestBody lastSeenTimePart = RequestBody.create(MediaType.parse("text/plain"), lastSeenTime);
        RequestBody contactPart = RequestBody.create(MediaType.parse("text/plain"), contact);
        RequestBody additionalInfoPart = RequestBody.create(MediaType.parse("text/plain"), additionalInfo);
        RequestBody kakaoIdPart = RequestBody.create(MediaType.parse("text/plain"), kakaoId);

        Call<ResponseBody> call = apiService.createPost(titlePart, breedPart, namePart, sizePart, agePart, colorPart, characteristicsPart, lastSeenLocationPart, lastSeenTimePart, contactPart, additionalInfoPart, kakaoIdPart, image);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("WriteMissingFragment", "sendPostToServer: 게시글 작성 성공");
                    Toast.makeText(getActivity(), "게시글 작성이 완료 되었습니다!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("WriteMissingFragment", "sendPostToServer: 게시글 작성 실패 - " + response.message());
                    Toast.makeText(getActivity(), "게시글 작성에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("WriteMissingFragment", "sendPostToServer: 네트워크 오류 - " + t.getMessage());
                Toast.makeText(getActivity(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}