package com.example.practice1;

import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;


import androidx.appcompat.app.AppCompatActivity;

import com.kakao.sdk.auth.AuthApiClient;
import com.kakao.sdk.auth.model.OAuthToken;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class MainActivity extends AppCompatActivity {

    private static final int DELAY_TIME_MILLIS = 3000;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imageView = findViewById(R.id.mainactivity);

        // 핸드폰의 전체 세로 길이 가져오기
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        // 이미지뷰의 크기를 세로 길이의 5분의 2로 설정
        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        params.width = screenHeight * 2 / 5;  // 세로 길이의 5분의 2로 가로, 세로 설정
        params.height = screenHeight * 2 / 5;
        imageView.setLayoutParams(params);


        // 앱 실행 시 프리퍼런스에서 사용자 아이디를 싱글톤에 적재
        String userId = UserManager.getUserId(getApplicationContext());
        if (userId != null) {
            SingletonClass.getInstance().setUserId(userId);
        }


        // 3초 후에 로그인 상태 확인 후 HomeActivity로 이동
        new Handler().postDelayed(() -> {
            // 로그인 상태 확인 및 네비게이션 처리
            checkLoginStatusAndNavigate();
        }, DELAY_TIME_MILLIS);
    }

    private void checkLoginStatusAndNavigate() {
        // 카카오 SDK를 이용하여 로그인 상태 확인
        if (AuthApiClient.getInstance().hasToken()) {
            // 사용자가 로그인된 상태
            refreshAccessTokenAndNavigate();
        } else {
            // 사용자가 로그인되지 않은 상태
            navigateToLoginActivity();
        }
    }

    private void refreshAccessTokenAndNavigate() {
        // 토큰 갱신 요청
        AuthApiClient.getInstance().refreshAccessToken(
                new Function2<OAuthToken, Throwable, Unit>() {
                    @Override
                    public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                        // 토큰 갱신 성공
                        if (throwable == null) {
                            navigateToHomeActivity();
                            // 로그 출력
                            Log.d("KaKao Token Refresh", "토큰 갱신 성공");
                        } else {
                            // 토큰 갱신 실패
                            navigateToLoginActivity(); // 예시로 로그인 화면으로 이동하는 처리를 함
                            // 로그 출력
                            Log.d("KaKao Token Refresh", "토큰 갱신 실패");
                        }
                        return null;
                    }
                }
        );
    }

    private void navigateToHomeActivity() {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginScreen.class);
        startActivity(intent);
        finish();
    }

    private static class LoadBlurryBackgroundTask extends AsyncTask<Integer, Void, Bitmap> {

        private final ImageView imageView;

        public LoadBlurryBackgroundTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Integer... resourceIds) {
            if (resourceIds.length > 0) {
                int resourceId = resourceIds[0];
                Bitmap originalBackgroundBitmap = BitmapFactory.decodeResource(imageView.getResources(), resourceId);
                return BlurBuilder.blur(imageView.getContext(), originalBackgroundBitmap);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap blurredBackgroundBitmap) {
            if (blurredBackgroundBitmap != null) {
                imageView.setImageBitmap(blurredBackgroundBitmap);
            }
        }
    }
}