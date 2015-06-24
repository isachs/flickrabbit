package com.icantdescribe.flickrabbit;

public class Photo {

    private String mId;
    private String mOwner;
    private String mSecret;
    private String mServer;
    private String mFarm;
    private final String[] mSizes = new String[]{"_t", "_m", "_n", "", "_z", "_c", "_b"};

    public Photo(String id, String owner, String secret, String server, String farm) {
        mId = id;
        mOwner = owner;
        mSecret = secret;
        mServer = server;
        mFarm = farm;
    }

    public String getImageUri(int size) {
        return "http://farm" + mFarm + ".staticflickr.com/" + mServer + "/" + mId + "_" + mSecret + mSizes[size] + ".jpg";
    }

    public String getPhotoPageUri() {
        return "http://www.flickr.com/photos/" + mOwner + "/" + mId;
    }

    public String getId() {
        return mId;
    }

    public String getOwner() {
        return mOwner;
    }

}
