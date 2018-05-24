package com.greyblocks.videoplayer;

import android.app.Application;

import com.greyblocks.videoplayer.BuildConfig;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import api.SparrowApi;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class VideoPlayerApp extends Application {
    private SparrowApi sparrowApi;

    public SparrowApi getSparrowApi() {
        return sparrowApi;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Type type = Types.newParameterizedType(Map.class, String.class, Double.class);
        Moshi moshi = new Moshi.Builder().build();
        //Define adapter for Maps with the following type
        JsonAdapter<Map<String, Double>> adapter = moshi.adapter(type);

        sparrowApi = new Retrofit.Builder()
                .client(new OkHttpClient.Builder().addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).build())
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(new Moshi.Builder().add(type, adapter).build()))
                .build().create(SparrowApi.class);
    }
}
