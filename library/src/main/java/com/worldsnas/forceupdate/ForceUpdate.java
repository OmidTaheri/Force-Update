package com.worldsnas.forceupdate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class ForceUpdate {

    public static void start(Context context, String url, int currentVersion){
        Intent firstCheckDownload = new Intent(Intent.ACTION_SYNC, null, context, ForceUpdateService.class);
        Bundle bundle = new Bundle();

        bundle.putString(ForceUpdateService.VERSION_CHECK_URL, url);
        bundle.putInt(ForceUpdateService.CURRENT_VERSION, currentVersion);

        firstCheckDownload.putExtras(bundle);
        context.startService(firstCheckDownload);
    }
}
