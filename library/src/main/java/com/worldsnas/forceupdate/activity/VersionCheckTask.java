package com.worldsnas.forceupdate.activity;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.worldsnas.forceupdate.EndPoint;
import com.worldsnas.forceupdate.UpdateResponse;

import java.io.IOException;

import okhttp3.HttpUrl;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VersionCheckTask extends AsyncTask<String, String, Response<UpdateResponse>> {

    private final String mCheckUrl;
    private final int mCurrentVersion;
    private ForceUpdateCheckerListener mListener;

    public VersionCheckTask(@NonNull String checkUrl, int currentVersion, ForceUpdateCheckerListener listener) {
        mCheckUrl = checkUrl;
        mCurrentVersion = currentVersion;
        mListener = listener;
    }


    @Override
    protected Response<UpdateResponse> doInBackground(String... strings) {

        Response<UpdateResponse> checkVersionResponse = null;
        try {
            checkVersionResponse = prepareCheckVersion(mCheckUrl, mCurrentVersion).execute();
            return checkVersionResponse;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Response<UpdateResponse> updateResponseResponse) {
        super.onPostExecute(updateResponseResponse);

        if (forceUpdate(updateResponseResponse)) {
            mListener.checked(true, updateResponseResponse.body());
        }else{
            mListener.checked(false, null);
        }

        mListener = null;
    }

    private Call<UpdateResponse> prepareCheckVersion(String checkUrl, int currentVersion) {
        HttpUrl url = HttpUrl.parse(checkUrl);
        if (url != null) {
            url = url.newBuilder().addQueryParameter("version", String.valueOf(currentVersion)).build();
        }
        return new Retrofit.Builder()
                .baseUrl("http://google.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(EndPoint.class)
                .versionCheck(url, currentVersion);
    }

    @SuppressWarnings("ConstantConditions")
    private boolean forceUpdate(Response<UpdateResponse> response) {
        return response != null && response.isSuccessful() && response.body() != null && response.body().getForce_update() == 1;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        mListener = null;
    }
}