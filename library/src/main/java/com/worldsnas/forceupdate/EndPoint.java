package com.worldsnas.forceupdate;

import okhttp3.HttpUrl;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface EndPoint {

    @FormUrlEncoded
    @POST
    Call<UpdateResponse> versionCheck(@Url HttpUrl url, @Field("version") int versionCode);

    @Streaming
    @GET
    Call<ResponseBody> downloadFile(@Url HttpUrl fileUrl);
}
