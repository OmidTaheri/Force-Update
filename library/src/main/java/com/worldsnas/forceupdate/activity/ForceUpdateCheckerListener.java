package com.worldsnas.forceupdate.activity;

import android.support.annotation.Nullable;

import com.worldsnas.forceupdate.UpdateResponse;

public interface ForceUpdateCheckerListener {

    void checked(boolean needsUpdate, @Nullable UpdateResponse response);
}
