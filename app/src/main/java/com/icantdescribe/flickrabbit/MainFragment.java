package com.icantdescribe.flickrabbit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.List;

public class MainFragment extends Fragment {

    private static final int REQUEST_PHOTO = 1;

    private RecyclerView mPhotoRecyclerView;
    private PhotoAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getActivity())
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);

        View view = inflater.inflate(R.layout.fragment_photo_grid, container, false);
        mPhotoRecyclerView = (RecyclerView) view.findViewById(R.id.photo_recycler_view);
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2)); // 2 = columns (set using method later)

        updateUI();

        return view;
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
    }

    private class PhotoHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
        private Photo mPhoto;

        private ImageView mImageView;

        public PhotoHolder(View itemView) {
            super(itemView);
            itemView.setOnLongClickListener(this);

            mImageView = (ImageView) itemView.findViewById(R.id.grid_item_photo_image_view);
        }

        public void bindImage(Photo photo) {
            mPhoto = photo;

            ImageLoader imageLoader = ImageLoader.getInstance();
            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisc(true).resetViewBeforeLoading(true)
                    .build();

            String mUri = mPhoto.getImageUri(4); // 4 for 500px - get from config

            imageLoader.displayImage(mUri, mImageView, options);

        }

        @Override
        public void onClick(View v) {
            // fetch more images
        }

        @Override
        public boolean onLongClick(View v) {
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


//    final List<Photo> photosList = new ArrayList<Photo>();
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
//                .cacheOnDisc(true).cacheInMemory(true)
//                .imageScaleType(ImageScaleType.EXACTLY).build();
//
//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
//                .defaultDisplayImageOptions(defaultOptions)
//                .memoryCache(new WeakMemoryCache())
//                .discCacheSize(100 * 1024 * 1024).build();
//
//        ImageLoader.getInstance().init(config);
//
//        setContentView(R.layout.activity_main);
//
//        Context context = this;
//        final ListAdapter adapter = new ImageAdapter(context);
//
//        getNewImages("");
//
//        final GridView gridview = (GridView) findViewById(R.id.gridView);
//        gridview.setNumColumns(getNumColumns());
//        final int cellSize = getColumnWidth();
//        GridView.LayoutParams layout = new GridView.LayoutParams(cellSize, cellSize);
//        gridview.setAdapter(adapter);
//
//        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                getNewImages();
//                int newPosition = adapter.getCount() + 1;
//                gridview.updateViewLayout(view, new GridView.LayoutParams(cellSize, cellSize));
//                gridview.smoothScrollToPositionFromTop(newPosition, 0); // TODO get workaround for https://code.google.com/p/android/issues/detail?id=36062
//            }
//        });
//
//        gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(MainActivity.this, "" + position, Toast.LENGTH_SHORT).show();
//                return true;
//            }
//        });
//    }
//
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//
//        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        GridView gridview = (GridView) findViewById(R.id.gridView);
//
//        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            gridview.setNumColumns(getNumColumns());
//        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            gridview.setNumColumns(getNumColumns());
//        }
//    }
//
//    public int getColumnWidth() {
//        return 525;
//    }
//
//    public int getNumColumns() {
//        final DisplayMetrics displayMetrics=getResources().getDisplayMetrics();
//        return (int) Math.floor(displayMetrics.widthPixels / 525);
//    }
//
//    public void getNewImages(String user) throws IllegalArgumentException{
//        Field[] IDFields = R.drawable.class.getFields();
//        try {
//            for(int i = 0; i < IDFields.length; i++){
//                int IDint = IDFields[i].getInt(null);
//                String name = String.valueOf(getResources().getResourceEntryName(IDint));
//                if (name.startsWith("img_1"+count)) {
//                    photosList.add(IDint);
//                }
//            }
//        } catch (Exception e) {
//            throw new IllegalArgumentException();
//        }
//        count++;
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    public class ImageAdapter implements ListAdapter {
//
//        // create a new ImageView for each item referenced by the Adapter
//        public View getView(int position, View convertView, ViewGroup parent) {
//            ImageView imageView;
//            if (convertView == null) {  // if it's not recycled, initialize some attributes
//                imageView = new ImageView(imageContext);
//                int cellSize = getColumnWidth();
//                imageView.setLayoutParams(new GridView.LayoutParams(cellSize, cellSize));
//                imageView.setPadding(10, 10, 10, 10);
//            } else {
//                imageView = (ImageView) convertView;
//            }
//
//            ImageLoader imageLoader = ImageLoader.getInstance();
//            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
//                    .cacheOnDisc(true).resetViewBeforeLoading(true)
//                    .build();
//
//            String mUri = "http://farm1.staticflickr.com/271/18884532229_f499655622.jpg";
//
//            imageLoader.displayImage(mUri, imageView, options);
//
//            return imageView;
//        }
//    }
}
