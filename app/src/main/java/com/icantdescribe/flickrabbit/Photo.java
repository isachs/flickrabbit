package com.icantdescribe.flickrabbit;

public class Photo {

    private long mId;
    private String mOwner;
    private String mSecret;
    private int mServer;
    private int mFarm;
    private final String[] mSizes = new String[]{"_t", "_m", "_n", "", "_z", "_c", "_b"};

    public Photo(long id, String owner, String secret, int server, int farm) {
        mId = id;
        mOwner = owner;
        mSecret = secret;
        mServer = server;
        mFarm = farm;
    }

    public String getImageUri(int size) {
        return "http://farm" + Integer.toString(mFarm) + ".staticflickr.com/" + mServer + "/" + Long.toString(mId) + "_" + mSecret + mSizes[size] + ".jpg";
    }

    public String getPhotoPageUri() {
        return "http://www.flickr.com/photos/" + mOwner + "/" + Long.toString(mId);
    }

    public long getId() {
        return mId;
    }

}
