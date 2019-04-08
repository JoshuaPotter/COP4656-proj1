package edu.fsu.cs.mobile.project1;

import android.os.Parcel;
import android.os.Parcelable;

public class Post implements Parcelable {
    private String title;
    private String message;
    private String latitude;
    private String longitude;
    private String timestamp;
    private String userid;

    public Post() {

    }

    public Post(String title, String message, String latitude, String longitude,
                String timestamp, String userid) {
        this.title = title;
        this.message = message;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
        this.userid = userid;
    }

    protected Post(Parcel in) {
        this.title = in.readString();
        this.message = in.readString();
        this.latitude = in.readString();
        this.longitude = in.readString();
        this.timestamp = in.readString();
        this.userid = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(message);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(timestamp);
        dest.writeString(userid);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
