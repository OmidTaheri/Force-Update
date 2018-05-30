package com.worldsnas.forceupdate.activity;

import android.os.AsyncTask;

import com.worldsnas.forceupdate.EndPoint;

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

public class DownloadVersionTask extends AsyncTask<String, Integer, Boolean> {

    private final String mDownloadUrl;
    private VersionDownloadListener mDownloadListener;
    private final File downloadFile;

    DownloadVersionTask(String downloadUrl, File downloadFile, VersionDownloadListener downloadListener) {
        mDownloadUrl = downloadUrl;
        mDownloadListener = downloadListener;
        this.downloadFile = downloadFile;
    }

    @Override
    protected Boolean doInBackground(String... strings) {

        try {
            Response<ResponseBody> downloadResponse = prepareDownloadRequest(mDownloadUrl).execute();

            return isDownloadResponseValid(downloadResponse) && download(downloadResponse.body());

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        mDownloadListener.downloadComplete(aBoolean, downloadFile);

        mDownloadListener = null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        mDownloadListener.progressUpdate(values[0]);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        mDownloadListener = null;
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

    private boolean download(ResponseBody body) {
        try {
            InputStream inputStream = null;
            OutputStream outputStream = null;
            long fileSizeDownloaded = 0;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(downloadFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    if (isCancelled())
                        return false;

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;
                    final int percent = (int) ((fileSizeDownloaded * 100) / fileSize);
                    onProgressUpdate(percent);
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
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isDownloadResponseValid(Response<ResponseBody> downloadResponse) {
        return downloadResponse.isSuccessful() && downloadResponse.body() != null;
    }
}
