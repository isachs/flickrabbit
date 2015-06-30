package com.icantdescribe.flickrabbit;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

public class MainFragment extends Fragment {

    private static final String TAG = "FlickRabbit";

    private static final int REQUEST_PHOTO = 1;

    private RecyclerView mPhotoRecyclerView;
    private PhotoAdapter mAdapter;
    private String mGridType;
    private String mActualGridType = "STAGGERED";
    private String mLongPressAction;

    private final int[] mSizes = new int[]{100, 240, 320, 500, 640, 800};
    private int mPhotoSize = mSizes[4]; // defaults to 640px

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisk(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getActivity())
                .defaultDisplayImageOptions(defaultOptions)
                .diskCacheSize(100 * 1024 * 1024).build(); // get from prefs later

        ImageLoader.getInstance().init(config);

        View view = inflater.inflate(R.layout.fragment_photo_grid, container, false);
        mPhotoRecyclerView = (RecyclerView) view.findViewById(R.id.photo_recycler_view);

        mPhotoRecyclerView.addItemDecoration(new SpacesItemDecoration(5));

        setPhotoSize();

        setLayoutManager();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setHasOptionsMenu(true);

        updateUI();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = SettingsActivity.newIntent(getActivity(), null);
                startActivityForResult(intent, REQUEST_PHOTO);
                return true;
            case R.id.action_help:
                DialogFragment newFragment = new HelpFragment();
                newFragment.show(getFragmentManager(), "help");
                return true;
            case R.id.action_refresh:
                PhotoGallery photoTool = PhotoGallery.get(getActivity());
                photoTool.intializePhotos(getActivity());
                mAdapter = new PhotoAdapter(photoTool.getPhotos());
                mPhotoRecyclerView.setAdapter(mAdapter);
                setLayoutManager();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        setLayoutManager();

        updateUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        PhotoGallery photoTool = PhotoGallery.get(getActivity());
        List<Photo> photos = photoTool.getPhotos();

        if (mAdapter == null) {
            mAdapter = new PhotoAdapter(photos);
            mPhotoRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }

        Log.i(TAG, "updateUI " + Integer.toString(photos.size()));
    }

    private class PhotoHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
        private Photo mPhoto;
        private ImageView mImageView;
        private FrameLayout mFrameLayout;
        private ProgressBar mProgressBar;

        public PhotoHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            mImageView = (ImageView) itemView.findViewById(R.id.grid_item_photo_image_view);
            mFrameLayout = (FrameLayout) itemView.findViewById(R.id.grid_item_photo_layout);
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.progress_image);
        }

        public void bindImage(Photo photo, int prefSize) {
            mPhoto = photo;

            mProgressBar.setVisibility(View.VISIBLE);

            ImageLoader imageLoader = ImageLoader.getInstance();
            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisc(true).resetViewBeforeLoading(true)
                    .build();

            String mUri = (prefSize < 0) ? mPhoto.getImageUri(4) : mPhoto.getImageUri(prefSize);

            mImageView.getLayoutParams().width = largestImageSize();
            Log.d(TAG, "mPhotoSize  " + mPhotoSize);
            ViewGroup.LayoutParams lp = mFrameLayout.getLayoutParams();
            lp.width = largestImageSize();
            mFrameLayout.setLayoutParams(lp);

            if (mActualGridType.equals("REGULAR")) {
                mImageView.setMaxHeight(mPhotoSize);
            }

            imageLoader.displayImage(mUri, mImageView, options, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    mProgressBar.setVisibility(View.GONE);
                }
            });
        }

        @Override
        public void onClick(View v) {
            new FetchItemsTask(mPhoto.getOwner()).execute();
        }

        @Override
        public boolean onLongClick(View v) {
            SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(getActivity());
            mLongPressAction = shared.getString("pref_long_click", getString(R.string.pref_long_click_photo_view));

            if (mLongPressAction.equals(getString(R.string.pref_long_click_flickr))) {
                String uri = new FlickrFetcher().fetchPhotoPageUri(mPhoto.getId());

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(browserIntent);
            } else {
                Intent intent = PhotoActivity.newIntent(getActivity(), mPhoto.getId());
                startActivityForResult(intent, REQUEST_PHOTO);
            }
            return true;
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {
        private List<Photo> mPhotos;
        private int mSizePref = 4;

        public PhotoAdapter(List<Photo> photos) {
            mPhotos = photos;

            SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(getActivity());
            mSizePref = Integer.parseInt(shared.getString("pref_grid_image_size", "-1"));
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.grid_item_photo, parent, false);
            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            Photo photo = mPhotos.get(position);
            holder.bindImage(photo, mSizePref);
        }
        @Override
        public int getItemCount() {
            return mPhotos.size();
        }
    }

    public int getNumColumns(int imageSize) {
        final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return (int) Math.max(Math.floor(displayMetrics.widthPixels / imageSize), 2); // at least 2 columns
    }

    public int largestImageSize() {
        final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return  displayMetrics.widthPixels / getNumColumns(mPhotoSize);
    }

    public void setPhotoSize() {
        final DisplayMetrics displayMetrics=getResources().getDisplayMetrics();
        int largestImageSize = (int) Math.floor((Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels) - 5)/2);
        Log.d(TAG, "setPhotoSize " + Integer.toString(largestImageSize));

        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int prefSize = Integer.parseInt(shared.getString("pref_grid_image_size", "-1"));

        Log.d(TAG, "prefSize " + Integer.toString(prefSize));

        if (prefSize < 0) { // auto size images
            mPhotoSize = (mSizes[4] > largestImageSize) ? largestImageSize : mSizes[4];
        } else {
            mPhotoSize = (mSizes[prefSize] > largestImageSize) ? largestImageSize : mSizes[prefSize];
        }
    }

    private void setLayoutManager() {
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mGridType = shared.getString("pref_grid_type", getString(R.string.pref_grid_type_auto));

        if (mGridType.equals(getString(R.string.pref_grid_type_staggered))) {
            StaggeredGridLayoutManager lm = new StaggeredGridLayoutManager(getNumColumns(mPhotoSize), StaggeredGridLayoutManager.VERTICAL);
            lm.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
            mPhotoRecyclerView.setLayoutManager(lm);
            mActualGridType = "STAGGERED";
        } else if (mGridType.equals(getString(R.string.pref_grid_type_regular))) {
            GridLayoutManager lm = new GridLayoutManager(getActivity(), getNumColumns(mPhotoSize));
            mPhotoRecyclerView.setLayoutManager(lm);
            mActualGridType = "REGULAR";
        } else {
            int currentapiVersion = android.os.Build.VERSION.SDK_INT;

            Log.d(TAG, "apiversion " + Integer.toString(currentapiVersion));

            if (currentapiVersion >= android.os.Build.VERSION_CODES.KITKAT) { // need KitKat to avoid issues
                StaggeredGridLayoutManager lm = new StaggeredGridLayoutManager(getNumColumns(mPhotoSize), StaggeredGridLayoutManager.VERTICAL);
                lm.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
                mPhotoRecyclerView.setLayoutManager(lm);
                mActualGridType = "STAGGERED";
            } else {
                GridLayoutManager lm = new GridLayoutManager(getActivity(), getNumColumns(mPhotoSize));
                mPhotoRecyclerView.setLayoutManager(lm);
                mActualGridType = "REGULAR";
            }
        }
    }

    private class FetchItemsTask extends AsyncTask<String,Void,List<Photo>> {

        private String mUser = new String();

        public FetchItemsTask(String user) {
            super();
            mUser = user;
        }

        @Override
        protected void onPreExecute() {
            View pb = getActivity().findViewById(R.id.progress_bar);
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Photo> doInBackground(String... params) {
            SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(getActivity());
            int num = Integer.parseInt(shared.getString("pref_grid_num", "10"));
            int fetchPoolSize = Math.min(Integer.parseInt(shared.getString("pref_fetch_num", "250")), 500);

            List<Photo> photos = new FlickrFetcher().fetchItems(mUser, num, fetchPoolSize);
            return photos;
        }

        @Override
        protected void onPostExecute(List<Photo> photos) {
            PhotoGallery photoTool = PhotoGallery.get(getActivity());

            Log.d(TAG, "onClick " + Integer.toString(photos.size()));

            for (int i = 0; i < photos.size(); i++) {
                photoTool.addPhoto(photos.get(i));
            }

            updateUI();

            View pb = getActivity().findViewById(R.id.progress_bar);

            mPhotoRecyclerView.smoothScrollToPosition(0);
            mPhotoRecyclerView.smoothScrollToPosition(photoTool.getNumPhotos());

            Log.d(TAG, "smoothScroll " + Integer.toString(photoTool.getNumPhotos()));

            pb.setVisibility(View.GONE);
        }
    }

}
