package com.icantdescribe.flickrabbit;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Fragment;
import android.preference.PreferenceManager;
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
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.List;

public class MainFragment extends Fragment {

    private static final String TAG = "FlickRabbit";

    private static final int REQUEST_PHOTO = 1;

    private RecyclerView mPhotoRecyclerView;
    private PhotoAdapter mAdapter;

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

        StaggeredGridLayoutManager lm = new StaggeredGridLayoutManager(getNumColumns(), StaggeredGridLayoutManager.VERTICAL);
        lm.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);

        mPhotoRecyclerView.setLayoutManager(lm);

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

                StaggeredGridLayoutManager lm = new StaggeredGridLayoutManager(getNumColumns(), StaggeredGridLayoutManager.VERTICAL);
                lm.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);

                mPhotoRecyclerView.setLayoutManager(lm);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        StaggeredGridLayoutManager lm = new StaggeredGridLayoutManager(getNumColumns(), StaggeredGridLayoutManager.VERTICAL);
        lm.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);

        mPhotoRecyclerView.setLayoutManager(lm);

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

        public PhotoHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            mImageView = (ImageView) itemView.findViewById(R.id.grid_item_photo_image_view);
        }

        public void bindImage(Photo photo) {
            mPhoto = photo;

            ImageLoader imageLoader = ImageLoader.getInstance();
            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisc(true).resetViewBeforeLoading(true)
                    .build();

            String mUri = mPhoto.getImageUri(4); // 4 for 500px - get from config in future

            imageLoader.displayImage(mUri, mImageView, options);

        }

        @Override
        public void onClick(View v) {
            new FetchItemsTask(mPhoto.getOwner()).execute();
        }

        @Override
        public boolean onLongClick(View v) {
            // disable long click for now
            Intent intent = PhotoActivity.newIntent(getActivity(), mPhoto.getId());
            startActivityForResult(intent, REQUEST_PHOTO);
            return true;
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {
        private List<Photo> mPhotos;
        public PhotoAdapter(List<Photo> photos) {
            mPhotos = photos;
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
            holder.bindImage(photo);
        }
        @Override
        public int getItemCount() {
            return mPhotos.size();
        }
    }

    public int getNumColumns() {
        final DisplayMetrics displayMetrics=getResources().getDisplayMetrics();
        return (int) Math.floor(displayMetrics.widthPixels / 520);
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
            List<Photo> photos = new FlickrFetcher().fetchItems(mUser);
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
