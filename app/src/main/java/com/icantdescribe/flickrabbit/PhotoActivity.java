package com.icantdescribe.flickrabbit;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

public class PhotoActivity extends SingleFragmentActivity {

    private static final String EXTRA_PHOTO_ID = "com.icantdescribe.flickrabbit";
    public static Intent newIntent(Context packageContext, String photoId) {
        Intent intent = new Intent(packageContext, PhotoActivity.class);
        intent.putExtra(EXTRA_PHOTO_ID, Integer.parseInt(photoId));
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        long photoId = (long) getIntent().getSerializableExtra(EXTRA_PHOTO_ID);
        return PhotoFragment.newInstance(photoId);
    }

}
