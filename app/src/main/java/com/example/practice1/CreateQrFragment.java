package com.example.practice1;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kakao.sdk.user.UserApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
                        String kakaoUserId = getKakaoUserId(); // 카카오 SDK를 사용하여 user id 가져오기
                        jsonObject.put("kakaoUserId", kakaoUserId);

                        sendUserDataToServer(jsonObject.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return view;
    }

    private String getKakaoUserId() {
        String[] kakaoUserId = {null};
        UserApiClient.getInstance().me((user, error) -> {
            if (error != null) {
                // 사용자 정보 가져오기 실패
                return null;
            } else {
                // 사용자 정보 가져오기 성공
                kakaoUserId[0] = user.getId() + "";
                return null;
            }
        });
        return kakaoUserId[0];
    }

    private void sendUserDataToServer(String jsonData) {
        // AsyncTask를 사용하여 백그라운드에서 데이터를 서버로 전송
        new SendDataToServerTask().execute(jsonData);
    }

    // AsyncTask를 사용하여 백그라운드에서 데이터를 서버로 전송
    // CreateQrFragment.java

    // AsyncTask를 사용하여 백그라운드에서 데이터를 서버로 전송
    private class SendDataToServerTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];
            String qrImageUrl = null;

            // 서버 URL
            String serverUrl = "https://homeskyul.store/api/qr/generate";

            try {
                URL url = new URL(serverUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setDoOutput(true);

                // 데이터 전송
                OutputStream os = urlConnection.getOutputStream();
                os.write(jsonData.getBytes());
                os.flush();
                os.close();

                // 응답 코드 확인
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // 응답 데이터를 읽어서 qrImageUrl 설정
                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // response에서 qrImageUrl 추출
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    qrImageUrl = jsonResponse.getString("qr_image_url");
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return qrImageUrl;
        }

        @Override
        protected void onPostExecute(String qrImageUrl) {
            // GetQrFragment로 이동하여 QR 코드 이미지 표시
            GetQrFragment getQrFragment = new GetQrFragment();
            Bundle bundle = new Bundle();
            bundle.putString("qr_image_url", qrImageUrl);
            getQrFragment.setArguments(bundle);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, getQrFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}