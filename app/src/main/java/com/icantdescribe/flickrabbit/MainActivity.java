package com.icantdescribe.flickrabbit;


import android.support.v4.app.Fragment;

public class MainActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new MainFragment();
    }

}