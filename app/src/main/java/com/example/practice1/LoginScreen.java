package com.example.practice1;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.practice1.Login.ApiService;
import com.example.practice1.Login.RetrofitClient;
import com.example.practice1.Login.TokenManager;
import com.example.practice1.Login.TokenResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginScreen extends AppCompatActivity {

    private WebView webView;
    private ImageButton btnklogin, btnglogin, btnnlogin;

    private TokenManager tokenManager; // TokenManager 객체 선언
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // TokenManager 객체 초기화
        tokenManager = new TokenManager(this);

        // Retrofit 인스턴스 생성
        apiService = RetrofitClient.getApiService();

        webView = new WebView(this); // WebView 객체 생성
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // 자바스크립트 활성화

        // XML에서 정의한 ImageButton 참조
        btnklogin = findViewById(R.id.btnklogin);
        btnglogin = findViewById(R.id.btnglogin);
        btnnlogin = findViewById(R.id.btnnlogin);

        // 버튼 클릭 이벤트 처리
        btnklogin.setOnClickListener(v -> {
            String url = "https://www.naver.com/"; // btnklogin 이미지 버튼에 대한 URL
            loadWebView(url);
        });

        btnglogin.setOnClickListener(v -> {
            String url = "https://www.naver.com/"; // btnglogin 이미지 버튼에 대한 URL
            loadWebView(url);
        });

        btnnlogin.setOnClickListener(v -> {
            String url = "https://www.naver.com/"; // btnnlogin 이미지 버튼에 대한 URL
            loadWebView(url);
        });
    }

    // 웹뷰를 로드하는 메서드
    private void loadWebView(String url) {
        setContentView(webView); // 웹뷰를 화면에 보이도록 설정

        // WebViewClient를 설정하여 웹뷰에서 페이지를 로드할 때 현재 액티비티에서 처리하도록 함
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url); // 웹뷰에 URL 로딩
                return true; // true 반환하여 WebViewClient가 URL 처리를 맡도록 함
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.equals("https://m.comic.naver.com/index")) {
                    // 로그인이 성공했으므로 서버에서 토큰 요청
                    requestTokensFromServer();
                }
            }
        });

        // 지정된 URL 로드
        webView.loadUrl(url);
    }

    // 서버에서 토큰을 요청하는 메서드
    private void requestTokensFromServer() {
        // 서버에서 토큰을 받아오는 API 호출
        Call<TokenResponse> call = apiService.getTokens();
        call.enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (response.isSuccessful()) {
                    // 토큰 요청 성공 시
                    TokenResponse tokenResponse = response.body();
                    if (tokenResponse != null) {
                        // 받아온 토큰을 저장
                        tokenManager.saveAccessToken(tokenResponse.getAccessToken());
                        tokenManager.saveRefreshToken(tokenResponse.getRefreshToken());

                        // 로그인 성공 메시지 및 화면 전환
                        Toast.makeText(LoginScreen.this, "로그인 되었습니다.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginScreen.this, HomeActivity.class));
                        finish(); // 로그인 액티비티 종료
                    }
                } else {
                    // 토큰 요청 실패 시
                    Toast.makeText(LoginScreen.this, "토큰 요청 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                // 네트워크 오류 등의 실패 시
                Toast.makeText(LoginScreen.this, "네트워크 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 뒤로 가기 버튼 눌렀을 때 웹뷰에서 뒤로 가기 처리
    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}