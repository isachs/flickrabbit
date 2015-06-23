package com.icantdescribe.flickrabbit;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

// Singleton to hold our photos

public class PhotoGallery {
    private static PhotoGallery sPhotoGallery;

    private List<Photo> mPhotos;

    private PhotoGallery(Context context) {
        mPhotos = new ArrayList<>();
        // fill below
        Photo photo = new Photo(18884532229L, "38637224@N00", "f499655622", 271, 1);
        mPhotos.add(photo);
    }

    public void addPhoto(Photo photo) {
        mPhotos.add(photo);
    }

    public List<Photo> getPhotos() {
        return mPhotos;
    }

    public Photo getPhoto(int id) {
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
