package com.icantdescribe.flickrabbit;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.Date;
import java.util.UUID;


public class PhotoFragment extends Fragment {


    private static final String ARG_PHOTO_ID = "photo_id";

    private Photo mPhoto;

    public static PhotoFragment newInstance(long photoId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PHOTO_ID, photoId);
        PhotoFragment fragment = new PhotoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long photoId = (long) getArguments().getSerializable(ARG_PHOTO_ID);
        mPhoto = PhotoLab.get(getActivity()).getCrime(crimeId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo, container, false);

        return v;
    }

    public void returnResult() {
        getActivity().setResult(Activity.RESULT_OK, null);
    }
}
