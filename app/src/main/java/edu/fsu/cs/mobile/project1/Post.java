package edu.fsu.cs.mobile.project1;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Post implements Parcelable {
    private String title;
    private String message;
    private double latitude;
    private double longitude;
    private String userid;

    @ServerTimestamp
    private Date timestamp;

    public Post() {

    }

    public Post(String title, String message, double latitude, double longitude,
                Date timestamp, String userid) {
        this.title = title;
        this.message = message;
        this.userid = userid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    protected Post(Parcel in) {
        this.title = in.readString();
        this.message = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.timestamp = (Date) in.readSerializable();
        this.userid = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(message);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeSerializable(timestamp);
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
