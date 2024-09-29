package com.example.practice1;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.practice1.dto.PetInfo;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyQrViewModel extends ViewModel {

    private MutableLiveData<List<PetInfo>> petInfoList = new MutableLiveData<>();
    private ApiService apiService;

    public MyQrViewModel() {
        apiService = ApiClient.createService(); // 매개변수 없이 호출
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
                } else {
                    Log.e("MyQrViewModel", "Response was not successful: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<PetInfo>> call, Throwable t) {
                // 오류 로그 출력
                Log.e("MyQrViewModel", "Failed to fetch pet info", t);
            }
        });
    }

    public void deleteQr(Long petId) {
        apiService.deleteQr(petId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("MyQrViewModel", "QR code deleted successfully");
                    updatePetInfoListAfterDelete(petId);
                } else {
                    Log.e("MyQrViewModel", "Failed to delete QR code: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("MyQrViewModel", "Error deleting QR code", t);
            }
        });
    }

    private void updatePetInfoListAfterDelete(Long petId) {
        List<PetInfo> currentList = petInfoList.getValue();
        if (currentList != null) {
            currentList.removeIf(petInfo -> petId != null && petId.equals(petInfo.getId())); // petId에 해당하는 항목 삭제
            petInfoList.setValue(currentList); // 업데이트된 목록 설정
        }
    }
}