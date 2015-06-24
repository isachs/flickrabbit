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
        // fill below - in future from API
        mPhotos.add(new Photo(18884532229L, "38637224@N00", "f499655622", 271, 1));
        mPhotos.add(new Photo(16691719663L, "19689679@N00", "5730b331c5", 8723, 9));
        mPhotos.add(new Photo(18680638705L, "50677694@N08", "a385c23e08", 374, 1));
        mPhotos.add(new Photo(18743915248L, "9980652@N05", "4384938ec0", 3755, 4));
        mPhotos.add(new Photo(17321763174L, "60005435@N08", "ac66c5cfe0", 8872, 9));
        mPhotos.add(new Photo(17914330865L, "60005435@N08", "807cf83463", 7733, 8));
        mPhotos.add(new Photo(17724503100L, "62113916@N02", "91e4f08dc4", 7700, 8));
        mPhotos.add(new Photo(16046829431L, "49248828@N06", "02ef362c53", 8620, 9));
        mPhotos.add(new Photo(17268185784L, "88626385@N03", "56d0d73cac", 5444, 6));
        mPhotos.add(new Photo(17293869844L, "9980652@N05", "d83228d95d", 8762, 9));
        mPhotos.add(new Photo(17727407358L, "9980652@N05", "4217615687", 5327, 6));
        mPhotos.add(new Photo(17867644032L, "45598648@N00", "97600ce1ce", 5322, 6));
        mPhotos.add(new Photo(17685275448L, "41449558@N06", "1653f45b3d", 5322, 6));
        mPhotos.add(new Photo(17252898304L, "38596480@N02", "fe914f93c7", 7668, 8));
        mPhotos.add(new Photo(17819203856L, "22488303@N05", "57a3464eae", 5453, 6));
        mPhotos.add(new Photo(17818930806L, "30437523@N06", "827ed9c9b1", 5463, 6));
        mPhotos.add(new Photo(15335115036L, "7510391@N07", "829e8c84f0", 2941, 3));
        mPhotos.add(new Photo(17772535056L, "71902268@N00", "9e364763b5", 5452, 6));
        mPhotos.add(new Photo(17772010146L, "9980652@N05", "7c58ce5bc6", 5442, 6));
        mPhotos.add(new Photo(17786413211L, "82787602@N00", "6a1e1e6e24", 8767, 9));
        mPhotos.add(new Photo(5303105812L, "52674600@N02", "03937fffe7", 5006, 6));
        mPhotos.add(new Photo(17752250865L, "60005435@N08", "11423f932b", 8791, 9));
        mPhotos.add(new Photo(17757581881L, "92159930@N02", "6fde540006", 7696, 8));
        mPhotos.add(new Photo(17546346750L, "82987375@N08", "a93b992c00", 8858, 9));
        mPhotos.add(new Photo(17545469878L, "22488303@N05", "2d77577013", 5338, 6));
        mPhotos.add(new Photo(17112919993L, "39643460@N00", "5a47087b89", 7660, 8));
        mPhotos.add(new Photo(17721624091L, "130742953@N02", "20b4b26964", 8759, 9));
    }

    public void addPhoto(Photo photo) {
        mPhotos.add(photo);
    }

    public List<Photo> getPhotos() {
        return mPhotos;
    }

    public Photo getPhoto(long id) {
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
