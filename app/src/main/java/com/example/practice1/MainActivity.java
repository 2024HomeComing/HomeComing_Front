package com.example.practice1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.kakao.sdk.auth.AuthApiClient;
import com.kakao.sdk.auth.model.OAuthToken;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

public class MainActivity extends AppCompatActivity {

    private static final int DELAY_TIME_MILLIS = 3000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 첫 번째 ImageView 가져오기 (배경)
        ImageView blurImageView = findViewById(R.id.blurImageView);

        // 두 번째 ImageView 가져오기 (로고)
        ImageView loadinLogoImageView = findViewById(R.id.loadinlogo);

        // 로고 이미지 가져오기 (블러 처리하지 않은 원본 이미지)
        Bitmap originalLogoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);

        // 두 번째 ImageView에는 블러 처리하지 않은 로고 이미지 설정
        loadinLogoImageView.setImageBitmap(originalLogoBitmap);

        //앱 실행시 프리펀스에서 싱글톤으로 아이디 적재
        String userId = UserManager.getUserId(getApplicationContext());
        if (userId != null) {
            SingletonClass.getInstance().setUserId(userId);
        }

        // 배경 이미지 가져오기 (블러 처리 적용)
        new LoadBlurryBackgroundTask(blurImageView).execute(R.drawable.loadingscreen);

        // Delay for 3 seconds before transitioning to HomeActivity if user is logged in
        new Handler().postDelayed(() -> {
            // Check if user is logged in
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
                            Log.d("Token Refresh", "토큰 갱신 성공");
                        } else {
                            // 토큰 갱신 실패
                            navigateToLoginActivity(); // 예시로 로그인 화면으로 이동하는 처리를 함
                            // 로그 출력
                            Log.d("Token Refresh", "토큰 갱신 실패");
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