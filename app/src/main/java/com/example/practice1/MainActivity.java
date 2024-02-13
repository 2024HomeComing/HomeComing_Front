package com.example.practice1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

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

        // 배경 이미지 가져오기 (블러 처리 적용)
        new LoadBlurryBackgroundTask(blurImageView).execute(R.drawable.loadingscreen);

        // Delay for 3 seconds before transitioning to LoginScreen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Intent로 LoginActivity 시작
                Intent intent = new Intent(MainActivity.this, LoginScreen.class);
                startActivity(intent);

                // Optional: Close the current activity if needed
                // finish();  // 주석 처리 또는 삭제
            }
        }, DELAY_TIME_MILLIS);
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