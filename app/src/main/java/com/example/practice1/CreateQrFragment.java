package com.example.practice1;

import static android.content.ContentValues.TAG;

import android.os.AsyncTask;
import android.os.Bundle;
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


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CreateQrFragment extends Fragment {

    private EditText nameEditText, speciesEditText, hairEditText, locationEditText, likeDislikeEditText, phoneNumberEditText, manualEditText;
    private Button createQrButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_qr, container, false);

        nameEditText = view.findViewById(R.id.name);
        speciesEditText = view.findViewById(R.id.species);
        hairEditText = view.findViewById(R.id.hair);
        locationEditText = view.findViewById(R.id.location);
        likeDislikeEditText = view.findViewById(R.id.like_dislike);
        phoneNumberEditText = view.findViewById(R.id.phone_number);
        manualEditText = view.findViewById(R.id.manual);
        createQrButton = view.findViewById(R.id.btn_create_qr);

        createQrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 사용자 입력 내용 가져오기
                String name = nameEditText.getText().toString().trim();
                String species = speciesEditText.getText().toString().trim();
                String hairColor = hairEditText.getText().toString().trim();
                String location = locationEditText.getText().toString().trim();
                String likeDislike = likeDislikeEditText.getText().toString().trim();
                String phoneNumber = phoneNumberEditText.getText().toString().trim();
                String manual = manualEditText.getText().toString().trim();

                // 필수 정보 입력 여부 확인
                if (name.isEmpty() || species.isEmpty() || hairColor.isEmpty() || location.isEmpty() || likeDislike.isEmpty() || phoneNumber.isEmpty() || manual.isEmpty()) {
                    Toast.makeText(getActivity(), "모든 정보를 입력하세요.", Toast.LENGTH_SHORT).show();
                } else {
                    // 사용자 입력 정보를 JSON 형식으로 변환하여 서버로 전송
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("name", name);
                        jsonObject.put("species", species);
                        jsonObject.put("hairColor", hairColor);
                        jsonObject.put("location", location);
                        jsonObject.put("likeDislike", likeDislike);
                        jsonObject.put("phoneNumber", phoneNumber);
                        jsonObject.put("manual", manual);
                        // 카카오 user id 추가
                        String userId = UserManager.getInstance().getUserId();
                        jsonObject.put("userId", userId);
                        Log.i(TAG, "저장된 사용자 아이디: " + userId);
                        sendUserDataToServer(jsonObject.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return view;
    }


    private void sendUserDataToServer(String jsonData) {
        // AsyncTask를 사용하여 백그라운드에서 데이터를 서버로 전송
        new SendDataToServerTask().execute(jsonData);
    }

    private class SendDataToServerTask extends AsyncTask<String, Void, byte[]> {
        @Override
        protected byte[] doInBackground(String... params) {
            String jsonData = params[0];
            byte[] qrImage = null;
            String serverUrl = "https://homeskyul.store/api/qr/generate";

            try {
                URL url = new URL(serverUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setDoOutput(true);

                OutputStream os = urlConnection.getOutputStream();
                os.write(jsonData.getBytes());
                os.flush();
                os.close();

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                    BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    int read;
                    byte[] buffer = new byte[1024];
                    while ((read = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, read);
                    }
                    qrImage = outputStream.toByteArray();
                    inputStream.close();
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return qrImage;
        }

        @Override
        protected void onPostExecute(byte[] qrImage) {
            if (qrImage != null) {
                Bundle bundle = new Bundle();
                bundle.putByteArray("qr_image", qrImage);

                QRFragment qrFragment = new QRFragment();
                qrFragment.setArguments(bundle);

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, qrFragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                Toast.makeText(getActivity(), "QR 코드 생성에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}