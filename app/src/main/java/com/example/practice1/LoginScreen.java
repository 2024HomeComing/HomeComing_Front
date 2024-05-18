package com.example.practice1;

import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;

import java.io.IOException;

import okhttp3.*;

public class LoginScreen extends AppCompatActivity {

    private static final String TAG = "LoginScreen";
    private String accessToken; // 토큰을 저장할 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // 카카오 로그인 버튼 클릭 이벤트 설정
        findViewById(R.id.kakao_login_button).setOnClickListener(view -> {
            // 카카오 로그인 가능 여부 확인
            if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(LoginScreen.this)) {
                // 카카오 로그인 시작
                UserApiClient.getInstance().loginWithKakaoTalk(LoginScreen.this, (token, error) -> {
                    if (error != null) {
                        // 카카오 로그인 실패
                        Log.e(TAG, "로그인 실패", error);
                    } else if (token != null) {
                        // 카카오 로그인 성공
                        Log.i(TAG, "로그인 성공(토큰) : " + token.getAccessToken());

                        // 토큰 저장
                        accessToken = token.getAccessToken();

                        // 사용자 정보 요청
                        requestUserInfo();
                    }
                    return null;
                });
            } else {
                // 카카오톡이 설치되어 있지 않은 경우 처리
                showWebLoginDialog();
            }
        });
    }
    private void showWebLoginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("카카오톡이 설치되어 있지 않습니다. 웹 로그인을 하시겠습니까?")
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startWebLogin();
                    }
                })
                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void startWebLogin() {
        String webLoginUrl = "https://kauth.kakao.com/oauth/authorize?client_id=b0239c075a7f76386a2f57b70f094558&redirect_uri=https://homeskyul.store/login/oauth2/code/kakao&response_type=code";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(webLoginUrl));
        startActivity(intent);
    }



    // 사용자 정보 요청
    private void requestUserInfo() {
        UserApiClient.getInstance().me((user, error) -> {
            if (error != null) {
                // 사용자 정보 요청 실패
                Log.e(TAG, "사용자 정보 요청 실패", error);
            } else if (user != null) {
                // 사용자 정보 요청 성공
                Log.i(TAG, "사용자 정보 요청 성공");
                Log.i(TAG, "사용자 아이디: " + user.getId());
                Log.i(TAG, "사용자 이름: " + user.getKakaoAccount().getName());
                Log.i(TAG, "사용자 전화번호: " + user.getKakaoAccount().getPhoneNumber());
                Log.i(TAG, "사용자 이메일: " + user.getKakaoAccount().getEmail());


                // 서버로 사용자 정보 전송
                sendUserInfoToServer(String.valueOf(user.getId()), user.getKakaoAccount().getPhoneNumber(), user.getKakaoAccount().getName(), user.getKakaoAccount().getEmail());

                // 홈 액티비티로 이동
                moveToHomeActivity();
            }
            return null;
        });
    }

    // 서버로 사용자 정보 전송
    private void sendUserInfoToServer(String userId, String phoneNumber, String name, String email) {
        OkHttpClient client = new OkHttpClient();

        // JSON 형식으로 사용자 정보 구성
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String jsonBody = "{\"userId\": \"" + userId + "\", \"phoneNumber\": \"" + phoneNumber + "\", \"name\": \"" + name + "\", \"email\": \"" + email + "\"}";
        RequestBody requestBody = RequestBody.create(JSON, jsonBody);

        // HTTP 요청 생성
        Request request = new Request.Builder()
                .url("https://homeskyul.store/api/login/kakao") // 서버 URL 설정
                .post(requestBody)
                .build();

        // 서버에 요청 보내기
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "서버 요청 실패", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.i(TAG, "서버 응답 성공2");
                    // 서버 응답에 따른 처리
                } else {
                    Log.e(TAG, "서버 응답 실패: " + response.code());
                }
            }
        });
    }


    // 홈 액티비티로 이동
    private void moveToHomeActivity() {
        Intent intent = new Intent(LoginScreen.this, HomeActivity.class);
        startActivity(intent);
        finish(); // 로그인 액티비티 종료
    }
}