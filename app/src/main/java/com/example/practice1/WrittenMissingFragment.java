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
import com.example.practice1.dto.Board;
import java.io.IOException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WrittenMissingFragment extends Fragment {

    private TextView title, breed, name, size, age, color, characteristics,
            lastSeenLocation, lastSeenTime, contact, additionalInfo;
    private ImageView petImageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_written_missing, container, false);

        title = view.findViewById(R.id.titleTextView);
        breed = view.findViewById(R.id.breedTextView);
        name = view.findViewById(R.id.nameTextView);
        size = view.findViewById(R.id.sizeTextView);
        age = view.findViewById(R.id.ageTextView);
        color = view.findViewById(R.id.colorTextView);
        characteristics = view.findViewById(R.id.characteristicsTextView);
        lastSeenLocation = view.findViewById(R.id.lastSeenLocationTextView);
        lastSeenTime = view.findViewById(R.id.lastSeenTimeTextView);
        contact = view.findViewById(R.id.contactTextView);
        additionalInfo = view.findViewById(R.id.additionalInfoTextView);
        petImageView = view.findViewById(R.id.petimg);

        // 아이디 가져오기
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey("boardId")) {
            String boardId = arguments.getString("boardId");
            // 서버에서 데이터를 가져와 UI에 채워넣는 메서드 호출
            fetchBoardData(boardId);
        } else {
            Log.e("onCreateView", "No boardId found in arguments.");
        }

        return view;
    }

    // newInstance 메서드를 사용하여 WrittenMissingFragment를 생성하고 아이디를 전달
    public static WrittenMissingFragment newInstance(String boardId) {
        WrittenMissingFragment fragment = new WrittenMissingFragment();
        Bundle args = new Bundle();
        args.putString("boardId", boardId);
        fragment.setArguments(args);
        return fragment;
    }

    private void fetchBoardData(String boardId) {
        // Retrofit을 사용하여 서버에서 데이터를 가져오는 코드 작성
        ApiService service = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);
        Call<Board> call = service.getBoardData(Long.parseLong(boardId)); // 예시: 실제로는 필요한 Endpoint와 호출 방식에 따라 달라질 수 있습니다.

        // Retrofit 호출 전 로그 추가
        Log.d("fetchBoardData", "Fetching board data...");

        call.enqueue(new Callback<Board>() {
            @Override
            public void onResponse(Call<Board> call, Response<Board> response) {
                // Retrofit 호출 후 로그 추가
                Log.d("fetchBoardData", "Response received");

                if (response.isSuccessful()) {
                    Board board = response.body();
                    // 가져온 데이터를 UI에 채워넣기
                    title.setText(board.getTitle());
                    breed.setText("품종 : " + board.getBreed());
                    name.setText("이름 : " + board.getName());
                    size.setText("크기 : " + board.getSize());
                    age.setText("나이 : " + board.getAge());
                    color.setText("털색 : " + board.getColor());
                    characteristics.setText("특징 : " + board.getCharacteristics());
                    lastSeenLocation.setText("마지막 확인 위치 : " + board.getLastSeenLocation());
                    lastSeenTime.setText("확인 시기 : " + board.getLastSeenTime());
                    contact.setText("연락처: " + board.getContact());
                    additionalInfo.setText("추가적인 특징 : " + board.getAdditionalInfo());
                    // 이미지 로드
                    Glide.with(requireContext())
                            .load(board.getImageUrl())
                            .into(petImageView);
                } else {
                    // 서버 응답이 실패한 경우 처리
                    Log.e("fetchBoardData", "Response not successful: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Board> call, Throwable t) {
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