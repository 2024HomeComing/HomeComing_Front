package com.example.practice1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class WrittenMissingFragment extends Fragment {

    private TextView titleTextView;
    private ImageView petImageView;
    private TextView breedTextView;
    private TextView nameTextView;
    private TextView sizeTextView;
    private TextView ageTextView;
    private TextView colorTextView;
    private TextView characteristicsTextView;
    private TextView lastSeenLocationTextView;
    private TextView lastSeenTimeTextView;
    private TextView contactTextView;
    private TextView additionalInfoTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_written_missing, container, false);

        // UI 요소 초기화
        titleTextView = view.findViewById(R.id.titleTextView);
        petImageView = view.findViewById(R.id.petimg);
        breedTextView = view.findViewById(R.id.breedTextView);
        nameTextView = view.findViewById(R.id.nameTextView);
        sizeTextView = view.findViewById(R.id.sizeTextView);
        ageTextView = view.findViewById(R.id.ageTextView);
        colorTextView = view.findViewById(R.id.colorTextView);
        characteristicsTextView = view.findViewById(R.id.characteristicsTextView);
        lastSeenLocationTextView = view.findViewById(R.id.lastSeenLocationTextView);
        lastSeenTimeTextView = view.findViewById(R.id.lastSeenTimeTextView);
        contactTextView = view.findViewById(R.id.contactTextView);
        additionalInfoTextView = view.findViewById(R.id.additionalInfoTextView);

        // 서버에서 게시글 내용을 가져오는 작업 수행
        int postId = getArguments().getInt("postId"); // 게시글 ID를 번들에서 가져옴
        loadPostContent(postId);

        return view;
    }

    private void loadPostContent(int postId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://homeskyul.store/boards") // 서버 주소 변경 필요
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<Post> call = apiService.getPostById(postId);
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if (response.isSuccessful()) {
                    Post post = response.body();
                    if (post != null) {
                        setPostContent(post);
                    }
                } else {
                    Toast.makeText(getActivity(), "Failed to load post", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 선택된 게시글의 내용을 설정하는 메소드
    private void setPostContent(Post post) {
        titleTextView.setText(post.getTitle());
        Glide.with(this).load(post.getImageUrl()).into(petImageView); // 이미지 로드
        breedTextView.setText("품종: " + post.getBreed());
        nameTextView.setText("이름: " + post.getName());
        sizeTextView.setText("크기: " + post.getSize());
        ageTextView.setText("나이: " + post.getAge());
        colorTextView.setText("털색: " + post.getColor());
        characteristicsTextView.setText("특징: " + post.getCharacteristics());
        lastSeenLocationTextView.setText("마지막 확인 위치: " + post.getLastSeenLocation());
        lastSeenTimeTextView.setText("확인 시기: " + post.getLastSeenTime());
        contactTextView.setText("연락처: " + post.getContact());
        additionalInfoTextView.setText("추가적인 특징: " + post.getAdditionalInfo());
    }

    public interface ApiService {
        @GET("posts/{id}")
        Call<Post> getPostById(@Path("id") int postId);
    }

    public class Post {
        private int id;
        private String title;
        private String imageUrl; // 이미지 URL
        private String breed;
        private String name;
        private String size;
        private String age;
        private String color;
        private String characteristics;
        private String lastSeenLocation;
        private String lastSeenTime;
        private String contact;
        private String additionalInfo;

        // getter 및 setter
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getBreed() {
            return breed;
        }

        public void setBreed(String breed) {
            this.breed = breed;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getCharacteristics() {
            return characteristics;
        }

        public void setCharacteristics(String characteristics) {
            this.characteristics = characteristics;
        }

        public String getLastSeenLocation() {
            return lastSeenLocation;
        }

        public void setLastSeenLocation(String lastSeenLocation) {
            this.lastSeenLocation = lastSeenLocation;
        }

        public String getLastSeenTime() {
            return lastSeenTime;
        }

        public void setLastSeenTime(String lastSeenTime) {
            this.lastSeenTime = lastSeenTime;
        }

        public String getContact() {
            return contact;
        }

        public void setContact(String contact) {
            this.contact = contact;
        }

        public String getAdditionalInfo() {
            return additionalInfo;
        }

        public void setAdditionalInfo(String additionalInfo) {
            this.additionalInfo = additionalInfo;
        }
    }
}