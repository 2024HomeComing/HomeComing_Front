package com.example.practice1;

import android.nfc.Tag;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.practice1.dto.PetInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class MyQrViewModel extends ViewModel {

    private MutableLiveData<List<PetInfo>> petInfoList = new MutableLiveData<>();
    private ApiService apiService;

    public MyQrViewModel() {
        apiService = ApiClient.createService();
    }

    public LiveData<List<PetInfo>> getPetInfoList() {
        return petInfoList;
    }

    public void fetchPetInfo(String userId) {
        apiService.getPetsByUserId(userId).enqueue(new Callback<List<PetInfo>>() {
            @Override
            public void onResponse(Call<List<PetInfo>> call, Response<List<PetInfo>> response) {
                if (response.isSuccessful()) {
                    Log.d("MyQrViewModel", "Received response from server");
                    petInfoList.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<PetInfo>> call, Throwable t) {
                // 오류 로그 출력
                Log.e("MyQrViewModel", "Failed to fetch pet info", t);
            }


        });
    }
}