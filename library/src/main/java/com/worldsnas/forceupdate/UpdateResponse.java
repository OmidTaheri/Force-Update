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
    @SerializedName("force_update")
    private int force_update;

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

    public int getForce_update() {
        return force_update;
    }

    public void setForce_update(int force_update) {
        this.force_update = force_update;
    }

    @Override
    public String toString() {
        return "UpdateResponse{" +
                "needsUpdate=" + needsUpdate +
                ", latestVersion=" + latestVersion +
                ", apkUrl='" + apkUrl + '\'' +
                ", your_version=" + your_version +
                ", force_update=" + force_update +
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
        dest.writeInt(this.force_update);
    }

    private UpdateResponse(Parcel in) {
        this.needsUpdate = in.readInt();
        this.latestVersion = in.readInt();
        this.apkUrl = in.readString();
        this.your_version = in.readInt();
        this.force_update = in.readInt();
    }

    public static final Parcelable.Creator<UpdateResponse> CREATOR = new Parcelable.Creator<UpdateResponse>() {
        @Override
        public UpdateResponse createFromParcel(Parcel source) {
            return new UpdateResponse(source);
        }

        @Override
        public UpdateResponse[] newArray(int size) {
            return new UpdateResponse[size];
        }
    };
}
