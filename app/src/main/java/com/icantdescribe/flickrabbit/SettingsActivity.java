package com.icantdescribe.flickrabbit;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;

public class SettingsActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context packageContext, String photoId) {
        Intent intent = new Intent(packageContext, SettingsActivity.class);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return new SettingsFragment();
    }
}
