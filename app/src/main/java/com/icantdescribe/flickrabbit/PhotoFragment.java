package com.icantdescribe.flickrabbit;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class PhotoFragment extends Fragment {

    private static final String ARG_PHOTO_ID = "photo_id";

    private ImageView mImageView;
    private String mPhotoId;

    public static PhotoFragment newInstance(String photoId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PHOTO_ID, photoId);
        PhotoFragment fragment = new PhotoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPhotoId = (String) getArguments().getSerializable(ARG_PHOTO_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo, container, false);

        mImageView = (ImageView) v.findViewById(R.id.full_photo_view);

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getActivity())
                .defaultDisplayImageOptions(defaultOptions)
                .discCacheSize(100 * 1024 * 1024).build(); // get from prefs later

        ImageLoader.getInstance().init(config);

        ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .build();

        FlickrFetcher flickrFetcher = new FlickrFetcher();
        String mUri = flickrFetcher.fetchBiggestPhotoUri(mPhotoId); // 4 for 500px - get from config in future

        imageLoader.displayImage(mUri, mImageView, options);

        return v;
    }

    public void returnResult() {
        getActivity().setResult(Activity.RESULT_OK, null);
    }
}
