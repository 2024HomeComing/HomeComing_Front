package com.example.practice1;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practice1.dto.PetInfo;
import com.kakao.sdk.user.UserApiClient;

import java.util.ArrayList;

public class MyQrFragment extends Fragment {
    private MyQrViewModel viewModel;
    private MyQrAdapter myQrAdapter;

    public MyQrFragment() {
        super(R.layout.fragment_my_qr);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout ifHaveQrLayout = view.findViewById(R.id.ifHaveQr);
        TextView ifNoQrTextView = view.findViewById(R.id.ifNoQr);
        Button btnGetQr = view.findViewById(R.id.btnGetQr);
        RecyclerView qrRecyclerView = view.findViewById(R.id.qrRecyclerView);

        viewModel = new ViewModelProvider(this).get(MyQrViewModel.class);

        // MyQrAdapter 초기화
        myQrAdapter = new MyQrAdapter(
                new ArrayList<>(),
                this::showLargeQr,        // QR 클릭 리스너
                this::deleteQrCode,       // 삭제 리스너
                this::onPetInfoClick      // PetInfo 클릭 리스너
        );

        qrRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        qrRecyclerView.setAdapter(myQrAdapter);

        // 카카오 사용자 정보를 통해 PetInfo 로드
        UserApiClient.getInstance().me((user, error) -> {
            if (error != null) {
                ifHaveQrLayout.setVisibility(View.GONE);
                ifNoQrTextView.setVisibility(View.VISIBLE);
            } else if (user != null) {
                String userId = SingletonClass.getInstance().getUserId();
                viewModel.fetchPetInfo(userId);

                viewModel.getPetInfoList().observe(getViewLifecycleOwner(), petInfoList -> {
                    if (petInfoList == null || petInfoList.isEmpty()) {
                        ifHaveQrLayout.setVisibility(View.GONE);
                        ifNoQrTextView.setVisibility(View.VISIBLE);
                    } else {
                        ifHaveQrLayout.setVisibility(View.VISIBLE);
                        ifNoQrTextView.setVisibility(View.GONE);
                        myQrAdapter.updatePetInfoList(petInfoList);
                    }
                });
            }
            return null;
        });

        // QR 생성 버튼 클릭 이벤트 처리
        btnGetQr.setOnClickListener(v -> {
            CreateQrFragment createQrFragment = new CreateQrFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, createQrFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void showLargeQr(Bitmap qrBitmap) {
        FragmentManager fragmentManager = getParentFragmentManager();
        LargeQrDialogFragment dialogFragment = LargeQrDialogFragment.newInstance(qrBitmap);
        dialogFragment.show(fragmentManager, "large_qr");
    }

    private void deleteQrCode(Long petId) {
        viewModel.deleteQr(petId);
    }

    private void onPetInfoClick(PetInfo petInfo) {
        Log.d("MyQrFragment", "onPetInfoClick: PetInfo clicked with ID " + petInfo.getId());
        showQrInfo(petInfo);
    }

    private void showQrInfo(PetInfo petInfo) {
        Log.d("MyQrFragment", "showQrInfo: Displaying QrInfoFragment for PetInfo ID " + petInfo.getId());

        QrInfoFragment qrInfoFragment = new QrInfoFragment();
        Bundle args = new Bundle();
        args.putLong("PET_INFO_ID", petInfo.getId()); // PetInfo ID 전달
        qrInfoFragment.setArguments(args);

        // 전달하는 ID를 로그로 확인
        Log.d("MyQrFragment", "showQrInfo: 전달된 petInfoId = " + petInfo.getId());

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, qrInfoFragment)
                .addToBackStack(null)
                .commit();

        Log.d("MyQrFragment", "showQrInfo: QrInfoFragment transaction committed");
    }
}