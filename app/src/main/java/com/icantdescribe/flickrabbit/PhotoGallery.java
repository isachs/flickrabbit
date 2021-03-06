package com.icantdescribe.flickrabbit;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.List;

// Singleton to hold our photos

public class PhotoGallery {
    private static PhotoGallery sPhotoGallery;

    private final static String TAG = "Flickrabbit";

    private List<Photo> mPhotos;

    private PhotoGallery(Context context) {
        intializePhotos(context);
    }

    public void intializePhotos(Context context) {
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(context);
        String user = shared.getString("pref_userid", "40786724@N00");
        int num = Integer.parseInt(shared.getString("pref_grid_num", "10"));
        int fetchPoolSize = Math.max(Integer.parseInt(shared.getString("pref_fetch_num", "250")), 500);

        Log.d(TAG, "userid " + user);
        mPhotos = new FlickrFetcher().fetchItems(user, num, fetchPoolSize);
    }

    public void addPhoto(Photo photo) {
        mPhotos.add(photo);
    }

    public List<Photo> getPhotos() {
        return mPhotos;
    }

    public int getNumPhotos() {
        return mPhotos.size();
    }

    public Photo getPhoto(String id) {
        for (Photo photo : mPhotos) {
            if (photo.getId() == id) {
                return photo;
            }
        }
        return null;
    }

    public static PhotoGallery get(Context context) {
        if (sPhotoGallery == null) {
            sPhotoGallery = new PhotoGallery(context);
        }
        return sPhotoGallery;
    }

}
