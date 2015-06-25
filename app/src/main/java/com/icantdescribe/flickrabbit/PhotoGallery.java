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
    private String mUser;

    private PhotoGallery(Context context) {
        intializePhotos(context);
    }

    public void intializePhotos(Context context) {
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(context);
        mUser = shared.getString("pref_userid", "40786724@N00");
        Log.d(TAG, "userid " + mUser);
        mPhotos = new FlickrFetcher().fetchItems(mUser);
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
