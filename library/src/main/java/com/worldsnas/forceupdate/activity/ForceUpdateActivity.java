package com.worldsnas.forceupdate.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.URLUtil;

import com.worldsnas.forceupdate.R;
import com.worldsnas.forceupdate.UpdateResponse;

import java.io.File;
import java.io.IOException;

import static com.worldsnas.forceupdate.Constants.RESULT_DOWNLOAD_COMPLETE;
import static com.worldsnas.forceupdate.Constants.RESULT_DOWNLOAD_FAILED;
import static com.worldsnas.forceupdate.Constants.RESULT_INVALID_ENTRY;
import static com.worldsnas.forceupdate.Constants.RESULT_PERMISSION_DENIED;
import static com.worldsnas.forceupdate.Constants.RESULT_UPTODATE;
import static com.worldsnas.forceupdate.Constants.RESULT_USER_CANCELED;
import static com.worldsnas.forceupdate.ForceUpdateService.CURRENT_VERSION;
import static com.worldsnas.forceupdate.ForceUpdateService.VERSION_CHECK_URL;

public class ForceUpdateActivity extends AppCompatActivity implements ForceUpdateCheckerListener, VersionDownloadListener {

    VersionCheckTask mVersionCheckTask;
    ProgressDialog pDialog;
    String mDownloadUrl;
    DownloadVersionTask mDownloadVersionTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras == null || !extras.containsKey(VERSION_CHECK_URL) || !extras.containsKey(CURRENT_VERSION) ||
                extras.getString(VERSION_CHECK_URL, null) == null) {
            closeWithResult(RESULT_INVALID_ENTRY);
        }
        if (hasPermission()) {
            //noinspection ConstantConditions
            mVersionCheckTask = new VersionCheckTask(extras.getString(VERSION_CHECK_URL, ""),
                    extras.getInt(CURRENT_VERSION, 0), this);

            mVersionCheckTask.execute();
        }else{
            closeWithResult(RESULT_PERMISSION_DENIED);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVersionCheckTask != null) {
            mVersionCheckTask.cancel(true);
            mVersionCheckTask = null;
        }
        if (mDownloadVersionTask != null) {
            mDownloadVersionTask.cancel(true);
            mDownloadVersionTask = null;
        }

        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

    @Override
    public void checked(boolean needsUpdate, @Nullable UpdateResponse response) {
        if (needsUpdate && response != null) {
            mDownloadUrl = response.getApkUrl();
            File downloadFile = getDownloadFileAddress(mDownloadUrl, response.getLatestVersion());
            manageDownloadFile(downloadFile);

            mDownloadVersionTask = new DownloadVersionTask(mDownloadUrl, downloadFile, this);
            showNeedsUpdateDialog();
        } else {
            closeWithResult(RESULT_UPTODATE);
        }
    }

    private void showNeedsUpdateDialog() {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        @SuppressLint("InflateParams") View mView = layoutInflaterAndroid.inflate(R.layout.dialog_download, null);

        new AlertDialog.Builder(this)
                .setView(mView)
                .setCancelable(false)
                .setPositiveButton("دانلود", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        dialogBox.dismiss();
                        download();
                    }
                })
                .setNegativeButton("لغو",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                // do nothing
                                closeEntireApp();
                            }
                        })
                .show();

//        versionCheckDialog.setOnShowListener(new DialogInterface.OnShowListener() {
//            @Override
//            public void onShow(DialogInterface dialogInterface) {
//
//                Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/shabnam.ttf");
//                versionCheckDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTypeface(typeface);
//                versionCheckDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTypeface(typeface);
//
//            }
//        });
    }

    private void download() {
        createProgressDialog();
        mDownloadVersionTask.execute();
    }

    private void createProgressDialog() {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("درحال دانلود. لطفا منتظر بمانید...");
        pDialog.setIndeterminate(false);

        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void closeEntireApp() {
        closeWithResult(RESULT_USER_CANCELED);
    }

    private void closeWithResult(int resultCode) {
        setResult(resultCode);
        finish();
    }

    @Override
    public void progressUpdate(int progress) {
        pDialog.setProgress(progress);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void downloadComplete(boolean success, File downloadedFile) {
        if (success) {
            completeDownload(downloadedFile);
            closeWithResult(RESULT_DOWNLOAD_COMPLETE);
        } else {
            if (downloadedFile.exists())
                downloadedFile.delete();
            closeWithResult(RESULT_DOWNLOAD_FAILED);
        }
    }

    private void completeDownload(File downloadedFile) {
        Intent promptInstall = new Intent(Intent.ACTION_VIEW).setDataAndType(Uri.fromFile(downloadedFile), "application/vnd.android.package-archive");
        startActivity(promptInstall);
    }

    private File getDownloadFileAddress(String url, int version) {
        String fileName = URLUtil.guessFileName(url, null, "application/vnd.android.package-archive");
        if (fileName != null) {
            fileName = "downloaded";
        }
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

    private boolean hasPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
}
