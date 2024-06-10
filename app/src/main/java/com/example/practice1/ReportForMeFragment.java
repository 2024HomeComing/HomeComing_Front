package com.example.practice1;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.practice1.dto.PetInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportForMeFragment extends Fragment {
    private ApiService apiService;
    private RecyclerView recyclerViewPets;
    private PetAdapter petAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_for_me, container, false);

        recyclerViewPets = view.findViewById(R.id.recyclerViewPets);
        recyclerViewPets.setLayoutManager(new LinearLayoutManager(getContext()));

        apiService = ApiClient.createService();

        String userId = SingletonClass.getInstance().getUserId();
        // 유저 ID로 반려동물 정보 조회
        getPetsByUserId(userId);

        return view;
    }
    private void getPetsByUserId(String userId) {
        Call<List<PetInfo>> call = apiService.getPetsByUserId(userId);
        call.enqueue(new Callback<List<PetInfo>>() {
            @Override
            public void onResponse(Call<List<PetInfo>> call, Response<List<PetInfo>> response) {
                if (response.isSuccessful()) {
                    List<PetInfo> pets = response.body();
                    petAdapter = new PetAdapter(getContext(), pets, petInfo -> openReportFragment(petInfo.getId()));
                    recyclerViewPets.setAdapter(petAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<PetInfo>> call, Throwable t) {
                Log.e("ReportForMe", "Error: " + t.getMessage());
            }
        });
    }

    private void openReportFragment(Long petId) {
        ReportFragment reportFragment = new ReportFragment();
        Bundle args = new Bundle();
        args.putLong("petInfoId", petId);
        reportFragment.setArguments(args);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, reportFragment)
                .addToBackStack(null)
                .commit();
    }
}