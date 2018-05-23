package api;

import android.app.Application;

import com.greyblocks.videoplayer.BuildConfig;

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
        sparrowApi = new Retrofit.Builder()
                .client(new OkHttpClient.Builder().addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).build())
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build().create(SparrowApi.class);
    }
}
