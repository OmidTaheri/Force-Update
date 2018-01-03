package com.worldsnas.forceupdate;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class UpdateResponse implements Parcelable {
    @SerializedName("uptodate")
    private int needsUpdate;
    @SerializedName("latest_version")
    private int latestVersion;
    @SerializedName("apk_url")
    private String apkUrl;
    @SerializedName("your_version")
    private int your_version;

    public UpdateResponse() {
    }

    public UpdateResponse(int needsUpdate, int latestVersion, String apkUrl, int your_version) {
        this.needsUpdate = needsUpdate;
        this.latestVersion = latestVersion;
        this.apkUrl = apkUrl;
        this.your_version = your_version;
    }

    String getApkUrl() {
        return apkUrl;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    int getNeedsUpdate() {
        return needsUpdate;
    }

    public void setNeedsUpdate(int needsUpdate) {
        this.needsUpdate = needsUpdate;
    }

    public int getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(int latestVersion) {
        this.latestVersion = latestVersion;
    }

    public int getYour_version() {
        return your_version;
    }

    public void setYour_version(int your_version) {
        this.your_version = your_version;
    }

    @Override
    public String toString() {
        return "UpdateResponse{" +
                "needsUpdate=" + needsUpdate +
                ", latestVersion=" + latestVersion +
                ", apkUrl='" + apkUrl + '\'' +
                ", your_version=" + your_version +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.needsUpdate);
        dest.writeInt(this.latestVersion);
        dest.writeString(this.apkUrl);
        dest.writeInt(this.your_version);
    }

    private UpdateResponse(Parcel in) {
        this.needsUpdate = in.readInt();
        this.latestVersion = in.readInt();
        this.apkUrl = in.readString();
        this.your_version = in.readInt();
    }

    public static final Creator<UpdateResponse> CREATOR = new Creator<UpdateResponse>() {
        public UpdateResponse createFromParcel(Parcel source) {
            return new UpdateResponse(source);
        }

        public UpdateResponse[] newArray(int size) {
            return new UpdateResponse[size];
        }
    };
}
