package com.example.practice1;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import com.kakao.sdk.user.UserApiClient;

public class LoginScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        ImageButton kakaoLoginButton = findViewById(R.id.kakao_login_button);
        kakaoLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(LoginScreen.this)) {
                    login();
                } else {
                    // 카카오톡이 설치되어 있지 않은 경우 처리
                }
            }
        });
    }

    private void login() {
        String TAG = "login()";
        UserApiClient.getInstance().loginWithKakaoTalk(LoginScreen.this, (oAuthToken, error) -> {
            if (error != null) {
                Log.e(TAG, "로그인 실패", error);
            } else if (oAuthToken != null) {
                Log.i(TAG, "로그인 성공(토큰) : " + oAuthToken.getAccessToken());
                getUserInfo();
            }
            return null;
        });
    }

    private void getUserInfo() {
        String TAG = "getUserInfo()";
        UserApiClient.getInstance().me((user, meError) -> {
            if (meError != null) {
                Log.e(TAG, "사용자 정보 요청 실패", meError);
            } else {
                Log.i(TAG, "로그인 완료");
                Log.i(TAG, "사용자 정보 요청 성공" +
                        "\n회원번호: " + user.getId() +
                        "\n이메일: " + user.getKakaoAccount().getEmail());

                // 로그인 성공 후 HomeActivity로 이동
                moveToHomeActivity();
            }
            return null;
        });
    }

    private void moveToHomeActivity() {
        Intent intent = new Intent(LoginScreen.this, HomeActivity.class);
        startActivity(intent);
        finish(); // 현재 액티비티 종료
    }
}
