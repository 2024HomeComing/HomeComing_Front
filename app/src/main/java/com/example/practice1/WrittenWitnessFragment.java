package com.example.practice1;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;

import com.example.practice1.dto.SightingBoard;


import java.io.IOException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WrittenWitnessFragment extends Fragment {

    private TextView wtitle, wbreed, wsize, wcolor, wcharacteristics,
            wlastSeenLocation, wlastSeenTime, wcontact, wadditionalInfo;
    private ImageView wpetImageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_written_witness, container, false);

        wtitle = view.findViewById(R.id.wtitleTextView);
        wbreed = view.findViewById(R.id.wbreedTextView);
        wsize = view.findViewById(R.id.wsizeTextView);
        wcolor = view.findViewById(R.id.wcolorTextView);
        wcharacteristics = view.findViewById(R.id.wcharacteristicsTextView);
        wlastSeenLocation = view.findViewById(R.id.wlastSeenLocationTextView);
        wlastSeenTime = view.findViewById(R.id.wlastSeenTimeTextView);
        wcontact = view.findViewById(R.id.wcontactTextView);
        wadditionalInfo = view.findViewById(R.id.wadditionalInfoTextView);
        wpetImageView = view.findViewById(R.id.wpetimg);

        // 아이디 가져오기
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey("sightingId")) {
            String sightingId = arguments.getString("sightingId");
            // 서버에서 데이터를 가져와 UI에 채워넣는 메서드 호출
            fetchBoardData(sightingId);
        } else {
            Log.e("onCreateView", "No boardId found in arguments.");
        }

        return view;
    }

    // newInstance 메서드를 사용하여 WrittenWitnessFragment를 생성하고 아이디를 전달
    public static WrittenWitnessFragment newInstance(String sightingId) {
        WrittenWitnessFragment fragment = new WrittenWitnessFragment();
        Bundle args = new Bundle();
        args.putString("sightingId", sightingId);
        fragment.setArguments(args);
        return fragment;
    }

    private void fetchBoardData(String sightingId) {
        // Retrofit을 사용하여 서버에서 데이터를 가져오는 코드 작성
        ApiService service = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);
        Call<SightingBoard> call = service.getSightingBoardById(Long.parseLong(sightingId)); // 예시: 실제로는 필요한 Endpoint와 호출 방식에 따라 달라질 수 있습니다.

        // Retrofit 호출 전 로그 추가
        Log.d("fetchBoardData", "Fetching board data...");

        call.enqueue(new Callback<SightingBoard>() {
            @Override
            public void onResponse(Call<SightingBoard> call, Response<SightingBoard> response) {
                // Retrofit 호출 후 로그 추가
                Log.d("fetchBoardData", "Response received");

                if (response.isSuccessful()) {
                    SightingBoard board = response.body();
                    // 가져온 데이터를 UI에 채워넣기
                    wtitle.setText(board.getTitle());
                    wbreed.setText("품종 : " + board.getwBreed());
                    wsize.setText("크기 : " + board.getwSize());
                    wcolor.setText("털색 : " + board.getwColor());
                    wcharacteristics.setText("특징 : " + board.getwCharacteristics());
                    wlastSeenLocation.setText("마지막 확인 위치 : " + board.getwLastSeenLocation());
                    wlastSeenTime.setText("확인 시기 : " + board.getwLastSeenTime());
                    wcontact.setText("연락처: " + board.getwContact());
                    wadditionalInfo.setText("추가적인 특징 : " + board.getwAdditionalInfo());
                    // 이미지 로드
                    Glide.with(requireContext())
                            .load(board.getImageUrl())
                            .into(wpetImageView);
                } else {
                    // 서버 응답이 실패한 경우 처리
                    Log.e("fetchBoardData", "Response not successful: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<SightingBoard> call, Throwable t) {
                // 실패 처리
                if (t instanceof IOException) {
                    // IOException은 네트워크 관련 예외를 나타냅니다.
                    Log.e("fetchBoardData", "Network error", t);
                } else {
                    // 네트워크 이외의 다른 예외 처리
                    Log.e("fetchBoardData", "Unexpected error", t);
                }
            }
        });
    }
}
