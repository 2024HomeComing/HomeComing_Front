package com.example.practice1;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class MyQrViewModel extends ViewModel {
    private MutableLiveData<List<String>> qrCodes = new MutableLiveData<>();
    private ApiService apiService;

    public MyQrViewModel() {
        apiService = ApiClient.createService();
    }

    public LiveData<List<String>> getQrCodes() {
        return qrCodes;
    }

    public void fetchQrCodes(String userId) {
        apiService.getQrCodes(userId).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful()) {
                    qrCodes.setValue(response.body());
                } else {
                    qrCodes.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                // 오류 로그 출력
                Log.e("MyQrViewModel", "Failed to fetch QR codes", t);
                // qrCodes.setValue(null);
            }

        });
    }
}