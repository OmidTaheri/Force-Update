package com.worldsnas.forceupdate;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.HttpUrl;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by itparsa on 1/8/18.
 */

public class ForceUpdateService extends IntentService {

    public static final String VERSION_CHECK_URL = "version_check_url";
    public static final String CURRENT_VERSION = "current_version";

    public static final String FORCE_UPDATE_SERVICE = "force update service";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public ForceUpdateService() {
        super(FORCE_UPDATE_SERVICE);
    }


    @SuppressLint("LogNotTimber")
    @SuppressWarnings({"ConstantConditions", "UnnecessaryReturnStatement"})
    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras == null || !extras.containsKey(VERSION_CHECK_URL) || !extras.containsKey(CURRENT_VERSION))
            stopSelf();

        String checkUrl = extras.getString(VERSION_CHECK_URL);
        int currentVersion = extras.getInt(CURRENT_VERSION);
        String appName = getString(R.string.app_name);

        try {
            Response<UpdateResponse> checkVersionResponse = prepareCheckVersion(checkUrl, currentVersion).execute();

            if (!isUpToDate(checkVersionResponse)) {
                stopSelf();
                return;

            } else {
                Response<ResponseBody> downloadResponse = prepareDownloadRequest(checkVersionResponse.body().getApkUrl()).execute();

                if (!isDownloadResponseValid(downloadResponse)) {
                    stopSelf();
                    Log.d(FORCE_UPDATE_SERVICE, "downlaoding response failed");
                    return;
                } else {
                    File downloadFile = getDownloadFile(appName, checkVersionResponse.body().getLatestVersion());
                    manageDownloadFile(downloadFile);

                    if (downloadAPK(downloadResponse.body(), downloadFile))
                        installAPK(downloadFile);
                    else{
                        stopSelf();
                        return;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(FORCE_UPDATE_SERVICE, "service stopped with error");
            Log.e(FORCE_UPDATE_SERVICE, e.toString());
        }
    }

    @SuppressWarnings("ConstantConditions")
    private boolean isUpToDate(Response<UpdateResponse> response) {
        return response.isSuccessful() && response.body() != null && response.body().getNeedsUpdate() == 1;
    }

    private boolean isDownloadResponseValid(Response<ResponseBody> downloadResponse) {
        return downloadResponse.isSuccessful() && downloadResponse.body() != null;

    }

    private Call<UpdateResponse> prepareCheckVersion(String checkUrl, int currentVersion) {
        HttpUrl url = HttpUrl.parse(checkUrl);

        return new Retrofit.Builder()
                .baseUrl("http://google.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(EndPoint.class)
                .versionCheck(url, currentVersion);
    }

    private Call<ResponseBody> prepareDownloadRequest(String url) {
        HttpUrl httpUrl = HttpUrl.parse(url);

        return new Retrofit.Builder()
                .baseUrl("http://google.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(EndPoint.class)
                .downloadFile(httpUrl);

    }

    private File getDownloadFile(String fileName, int version) {
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                "/" + fileName + "." + version + ".apk");
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void manageDownloadFile(File downloadFile) {
        if (downloadFile.exists()) {
            downloadFile.delete();
            try {
                downloadFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean downloadAPK(ResponseBody body, File downloadFile){
        try {
            InputStream inputStream = null;
            OutputStream outputStream = null;


            try {
                byte[] fileReader = new byte[4096];

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(downloadFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    private void installAPK(File downloadFile){
        Intent promptInstall = new Intent(Intent.ACTION_VIEW).setDataAndType(Uri.fromFile(downloadFile), "application/vnd.android.package-archive");
        startActivity(promptInstall);
        stopSelf();
    }
}
