package com.example.practice1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class LoginScreen extends AppCompatActivity {

    private RequestQueue requestQueue;
    private static final String TOKEN_ENDPOINT = "https://localhost:8080/login/oauth2/code/google"; // 서버의 토큰 발급 엔드포인트 URL
    private static final String GOOGLE_LOGIN_URL = "https://accounts.google.com/o/oauth2/v2/auth/oauthchooseaccount?response_type=code&client_id=152376215926-les79fdmimjjkisn4jgqs1583td65ql0.apps.googleusercontent.com&scope=profile%20email&state=8dqSJcQj2izXK6_VqLeBj1P4bJp-AyvfKIUSjrNwSS0%3D&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Flogin%2Foauth2%2Fcode%2Fgoogle&service=lso&o2v=2&theme=mn&ddm=0&flowName=GeneralOAuthFlow";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // Volley 요청 큐 초기화
        requestQueue = Volley.newRequestQueue(this);

        // ImageButton 인스턴스 가져오기
        ImageButton btnGLogin = findViewById(R.id.btnglogin);

        // ImageButton에 클릭 이벤트 리스너 추가
        btnGLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGoogleLoginLink();
            }
        });
    }

    private void openGoogleLoginLink() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(GOOGLE_LOGIN_URL));
        startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleGoogleLoginRedirect(intent.getData());
    }

    private void handleGoogleLoginRedirect(Uri uri) {
        if (uri != null && uri.toString().startsWith("http://localhost:8080/login/oauth2/code/google")) {
            String code = uri.getQueryParameter("code");
            if (code != null) {
                requestToken(code);
            } else {
                showLoginFailureToast();
            }
        }
    }

    private void requestToken(String code) {
        StringRequest tokenRequest = new StringRequest(Request.Method.POST, TOKEN_ENDPOINT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String token = jsonObject.getString("token");
                            saveToken(token);
                            navigateToHomeActivity();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handleTokenRequestError(error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("code", code);
                return params;
            }
        };
        requestQueue.add(tokenRequest);
    }

    private void saveToken(String token) {
        // 토큰을 SharedPreferences 또는 안전한 곳에 저장
    }

    private void navigateToHomeActivity() {
        Intent intent = new Intent(LoginScreen.this, HomeActivity.class);
        startActivity(intent);
        finish(); // 현재 액티비티를 종료
    }

    private void showLoginFailureToast() {
        Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show();
    }

    private void handleTokenRequestError(VolleyError error) {
        Toast.makeText(LoginScreen.this, "토큰 발급 실패", Toast.LENGTH_SHORT).show();
    }
}