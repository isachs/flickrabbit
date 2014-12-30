package com.icantdescribe.flickrabbit;

import android.content.Context;
import android.content.res.Configuration;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    final List<Integer> imagesList = new ArrayList<Integer>();

    int count = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context = this;
        final ListAdapter adapter = new ImageAdapter(context);

        getInitialImages();

        final GridView gridview = (GridView) findViewById(R.id.gridView);
        gridview.setNumColumns(getNumColumns());
        final int cellSize = getColumnWidth();
        GridView.LayoutParams layout = new GridView.LayoutParams(cellSize, cellSize);
        gridview.setAdapter(adapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getNewImages();
                int newPosition = adapter.getCount() + 1;
                gridview.updateViewLayout(view, new GridView.LayoutParams(cellSize, cellSize));
                gridview.smoothScrollToPositionFromTop(newPosition, 0); // TODO get workaround for https://code.google.com/p/android/issues/detail?id=36062
            }
        });

        gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "" + position, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        GridView gridview = (GridView) findViewById(R.id.gridView);

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridview.setNumColumns(getNumColumns());
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridview.setNumColumns(getNumColumns());
        }
    }

    public int getColumnWidth() {
        return 500;
    }

    public int getNumColumns() {
        final DisplayMetrics displayMetrics=getResources().getDisplayMetrics();
        return (int) Math.floor(displayMetrics.widthPixels / 525);
    }

    private void getInitialImages() throws IllegalArgumentException{ // right now just the same as getNewImages
        Field[] IDFields = R.drawable.class.getFields();
        try {
            for(int i = 0; i < IDFields.length; i++){
                int IDint = IDFields[i].getInt(null);
                String name = String.valueOf(getResources().getResourceEntryName(IDint));
                if (name.startsWith("img_1"+count)) {
                    imagesList.add(IDint);
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }

        count++;
    }

    public void getNewImages() throws IllegalArgumentException{
        Field[] IDFields = R.drawable.class.getFields();
        try {
            for(int i = 0; i < IDFields.length; i++){
                int IDint = IDFields[i].getInt(null);
                String name = String.valueOf(getResources().getResourceEntryName(IDint));
                if (name.startsWith("img_1"+count)) {
                    imagesList.add(IDint);
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
        count++;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class ImageAdapter implements ListAdapter {
        private Context imageContext;

        public ImageAdapter(Context c) {
            imageContext = c;
        }

        public int getCount() {
            return imagesList.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public boolean hasStableIds() {
            return false;
        }

        public boolean isEnabled(int position) {
            return true;
        }

        public boolean areAllItemsEnabled() {
            return true;
        }

        public boolean isEmpty() {
            if(imagesList.size() == 0) {
                return true;
            } else {
                return false;
            }
        }

        public int getItemViewType(int position) {
            return 0;
        }

        public int getViewTypeCount() {
            return 1;
        }

        public void registerDataSetObserver(DataSetObserver observer) {
            //
        }

        public void unregisterDataSetObserver(DataSetObserver observer) {
            //
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {  // if it's not recycled, initialize some attributes
                imageView = new ImageView(imageContext);
                int cellSize = getColumnWidth();
                imageView.setLayoutParams(new GridView.LayoutParams(cellSize, cellSize));
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setPadding(0, 0, 0, 0);
            } else {
                imageView = (ImageView) convertView;
            }

            Bitmap bm = BitmapFactory.decodeResource(getResources(), getArrayFromIntList(imagesList)[position]);
            imageView.setImageBitmap(bm);


            return imageView;
        }

        private int[] getArrayFromIntList(List<Integer> inputList) {
            int[] intArray = new int[inputList.size()];
            for(int i = 0; i < inputList.size(); i++) intArray[i] = imagesList.get(i);
            return intArray;
        }
    }
}