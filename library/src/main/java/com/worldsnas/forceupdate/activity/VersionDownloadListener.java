package com.worldsnas.forceupdate.activity;

import java.io.File;

public interface VersionDownloadListener {

    void progressUpdate(int progress);

    void downloadComplete(boolean success, File downloadFile);
}
