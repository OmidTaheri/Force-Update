package com.worldsnas.forceupdate;

import okhttp3.HttpUrl;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface EndPoint {

    @GET
    Call<UpdateResponse> versionCheck(@Url HttpUrl url, @Query("version") int versionCode);
}
