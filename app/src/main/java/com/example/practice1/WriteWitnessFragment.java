package com.example.practice1;

import android.content.Context;
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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import org.json.JSONObject;

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

public class WriteWitnessFragment extends Fragment {

    private Context context;
    private static final int GALLERY_REQUEST_CODE = 1;
    private static final int MAX_IMAGE_SELECTION = 5;
    private List<Uri> selectedImageUris = new ArrayList<>();
    private LinearLayout imageContainerLayout;
    private int imageWidth;

    private EditText wtitle, wbreed, wsize, wcolor, wcharacteristics, wlastSeenLocation, wlastSeenTime, wcontact, wadditionalInfo;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_write_witness, container, false);

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

        wtitle = view.findViewById(R.id.etwTitle);
        wbreed = view.findViewById(R.id.etwBreed);
        wsize = view.findViewById(R.id.etwSize);
        wcolor = view.findViewById(R.id.etwColor);
        wcharacteristics = view.findViewById(R.id.etwCharacteristics);
        wlastSeenLocation = view.findViewById(R.id.etwLastSeenLocation);
        wlastSeenTime = view.findViewById(R.id.etwLastSeenTime);
        wcontact = view.findViewById(R.id.etwContact);
        wadditionalInfo = view.findViewById(R.id.etwAdditionalInfo);

        Button btnSubmit = view.findViewById(R.id.btnwSubmit);
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

    private void submitPost() {
        try {
            JSONObject sightingReportJson = new JSONObject();
            sightingReportJson.put("wtitle", wtitle.getText().toString());
            sightingReportJson.put("wbreed", wbreed.getText().toString());
            sightingReportJson.put("wsize", wsize.getText().toString());
            sightingReportJson.put("wcolor", wcolor.getText().toString());
            sightingReportJson.put("wcharacteristics", wcharacteristics.getText().toString());
            sightingReportJson.put("wlastSeenLocation", wlastSeenLocation.getText().toString());
            sightingReportJson.put("wlastSeenTime", wlastSeenTime.getText().toString());
            sightingReportJson.put("wcontact", wcontact.getText().toString());
            sightingReportJson.put("wadditionalInfo", wadditionalInfo.getText().toString());
            String userId = SingletonClass.getInstance().getUserId();
            sightingReportJson.put("userId", userId);

            RequestBody sightingReportPart = RequestBody.create(MediaType.parse("application/json"), sightingReportJson.toString());

            List<MultipartBody.Part> imageParts = new ArrayList<>();
            for (Uri uri : selectedImageUris) {
                String filePath = getRealPathFromURI(uri);
                if (filePath != null) {
                    File file = new File(filePath);
                    RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
                    MultipartBody.Part part = MultipartBody.Part.createFormData("images", file.getName(), requestBody);
                    imageParts.add(part);
                } else {
                    Log.e("WriteWitnessFragment", "Image file path is null");
                }
            }

            sendPostToServer(sightingReportPart, imageParts);
        } catch (Exception e) {
            Log.e("WriteWitnessFragment", "submitPost: 오류 발생", e);
            Toast.makeText(getActivity(), "오류 발생: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void sendPostToServer(RequestBody sightingReportPart, List<MultipartBody.Part> imageParts) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://homeskyul.store/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);

        Call<ResponseBody> call = apiService.createSightingPost(sightingReportPart, imageParts);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("WriteWitnessFragment", "sendPostToServer: 게시글 작성 성공");
                    Toast.makeText(getActivity(), "게시글 작성이 완료 되었습니다!", Toast.LENGTH_SHORT).show();
                    navigateToWitnessReportFragment();
                } else {
                    Log.e("WriteWitnessFragment", "sendPostToServer: 게시글 작성 실패 - " + response.message());
                    Toast.makeText(getActivity(), "게시글 작성에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("WriteWitnessFragment", "sendPostToServer: 네트워크 오류 - " + t.getMessage());
                Toast.makeText(getActivity(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToWitnessReportFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, new DashboardFragment());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }
}