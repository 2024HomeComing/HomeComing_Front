package com.example.practice1;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ProfileEditFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_edit, container, false);

        // Find the ImageView by its ID
        ImageView profileImageView = view.findViewById(R.id.profileImg);

        // Set the drawable resource to the ImageView
        profileImageView.setImageResource(R.drawable.basic_profile);

        return view;
    }
}