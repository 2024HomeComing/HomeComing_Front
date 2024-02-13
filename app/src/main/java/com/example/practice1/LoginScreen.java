package com.example.practice1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class LoginScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // ImageButton 인스턴스 가져오기
        ImageButton btnKLogin = findViewById(R.id.btnklogin);
        ImageButton btnGLogin = findViewById(R.id.btnglogin);

        // ImageButton에 클릭 이벤트 리스너 추가
        btnKLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 버튼 클릭 시 수행할 동작 정의
                // 예시: 카카오 로그인 처리 등

                // 홈 액티비티로 이동
                navigateToHomeActivity();
            }
        });

        btnGLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 버튼 클릭 시 수행할 동작 정의
                // 예시: 구글 로그인 처리 등

                // 홈 액티비티로 이동
                navigateToHomeActivity();
            }
        });
    }

    private void navigateToHomeActivity() {
        Intent intent = new Intent(LoginScreen.this, HomeActivity.class);
        startActivity(intent);
        // 현재 액티비티를 종료하려면 아래 코드 추가
        // finish();
    }
}

