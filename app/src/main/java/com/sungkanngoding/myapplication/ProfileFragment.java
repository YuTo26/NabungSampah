package com.sungkanngoding.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        // Temukan tombol llEditProfile dari layout fragment_profile
        LinearLayout llEditProfile = rootView.findViewById(R.id.llEditProfile);

        LinearLayout llEditPrivacy = rootView.findViewById(R.id.llEditPrivacy);

        // Tambahkan OnClickListener untuk tombol llEditProfile
        llEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Panggil Intent untuk membuka EditProfileActivity saat layar ditekan
                Intent gotoEditProfile = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(gotoEditProfile);
            }
        });
        llEditPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Panggil Intent untuk membuka EditProfileActivity saat layar ditekan
                Intent gotoeditprivacy = new Intent(getActivity(), PrivacySettingsActivity.class);
                startActivity(gotoeditprivacy);
            }
        });

        // Kembalikan rootView yang sudah diubah
        return rootView;
    }
}
