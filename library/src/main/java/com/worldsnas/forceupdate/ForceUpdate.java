package com.worldsnas.forceupdate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.URLUtil;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;
import java.io.IOException;

import okhttp3.HttpUrl;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ForceUpdate implements Runnable {

    private static final String TAG = "ForceUpdate";

    private final int version;
    private final String checkUrl;
    private Context mContext;

    /**
     * after instatiating the class call .run() to start downloading
     *
     * @param version  current version code of the app
     * @param checkUrl url to check the latest version of the app
     * @param context  only application context should ne provided
     */
    public ForceUpdate(int version, String checkUrl, Context context) {
        this.version = version;
        this.checkUrl = checkUrl;
        this.mContext = context;
    }

    @SuppressLint("LogNotTimber")
    @SuppressWarnings("ConstantConditions")
    @Override
    public void run() {
        HttpUrl url = HttpUrl.parse(checkUrl);

        Call<UpdateResponse> request = new Retrofit.Builder()
                .baseUrl("http://google.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(EndPoint.class)
                .versionCheck(url, version);

        try {
            Response<UpdateResponse> response = request.execute();
            if (checkResponse(response)) {
                if (response.body().getNeedsUpdate() == 1) {
                    downloadApk(response.body());
                } else {
                    Log.d(TAG, "check update needs upadte: " + response.body().getNeedsUpdate());
                    mContext = null;
                }
            } else {
                Log.d(TAG, "check update response failed: " + response.toString());
                mContext = null;
            }
        } catch (IOException e) {
            Log.e(TAG, "updating error", e);
            mContext = null;
        }
    }

    private boolean checkResponse(Response<UpdateResponse> response) {
        if (response.isSuccessful()) {
            if (response.body() != null) {
                return false;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private void downloadApk(UpdateResponse response) {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        String appName = URLUtil.guessFileName(response.getApkUrl(), null, null);
        path = path + File.separator + appName;

        FileDownloader.getImpl()
                .create(response.getApkUrl())
                .setAutoRetryTimes(1000)
                .setTag(response)
                .setPath(path, false)
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(new File(task.getPath())), "application/vnd.android.package-archive");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (mContext != null)
                            mContext.startActivity(intent);
                        mContext = null;
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        mContext = null;
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        mContext = null;
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {

                    }
                })
                .start();
    }
}
