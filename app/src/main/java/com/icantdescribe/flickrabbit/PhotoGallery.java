package com.icantdescribe.flickrabbit;

import android.content.Context;

import java.util.List;

// Singleton to hold our photos

public class PhotoGallery {
    private static PhotoGallery sPhotoGallery;

    private List<Photo> mPhotos;

    private PhotoGallery(Context context) {
        mPhotos = new FlickrFetcher().fetchItems("40786724@N00"); // my user - get from prefs later
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
