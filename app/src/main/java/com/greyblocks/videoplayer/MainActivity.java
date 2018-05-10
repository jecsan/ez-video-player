package com.greyblocks.videoplayer;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.DashPathEffect;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.mp4.Mp4Extractor;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.AssetDataSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.Window;
import android.widget.LinearLayout;


/*
* TODO
* Compress video before getting thumbnails
* Create cache for video
* Preload whole video if possible
* Turns out exoplayer doesn't support instant seek like what iOS does
* https://github.com/google/ExoPlayer/issues/1399
* https://github.com/google/ExoPlayer/issues/2882 - faster seek
*
* */
public class MainActivity extends AppCompatActivity {

    private SimpleExoPlayerView playerView;
    private SimpleExoPlayer player;
    private boolean playWhenReady = true;
    private long playbackPosition;
    private int currentWindow;
    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 160;
    private static final String TAG = MainActivity.class.getSimpleName();
    public ArrayList<Bitmap> framesArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}


        setContentView(R.layout.activity_main);
        playerView = findViewById(R.id.video_view);
    }

    private void prepareCache(){

        final String userAgent = Util.getUserAgent(getApplicationContext(), "Ez-video");
        final DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
//        final Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);

        Cache cache = new SimpleCache(getApplication().getCacheDir(), new LeastRecentlyUsedCacheEvictor(1024 * 1024 * 10));
//        DataSource dataSource = new DefaultUriDataSource(context, bandwidthMeter, userAgent);
        CacheDataSourceFactory cacheDataSource = new CacheDataSourceFactory(cache, new AssetDataSource.Factory() {
            @Override
            public DataSource createDataSource() {
                return new AssetDataSource(getApplicationContext());
            }
        });
//        ExtractorSampleSource sampleSource = new ExtractorSampleSource(uri
//                , cacheDataSource
//                , allocator
//                , BUFFER_SEGMENT_COUNT * BUFFER_SEGMENT_SIZE
//                , new Mp4Extractor());
    }

    private void initializePlayer() {
        player = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(this),
                new DefaultTrackSelector(), new DefaultLoadControl());
//        AspectRatioFrameLayou
//        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        playerView.setPlayer(player);
        player.setSeekParameters(SeekParameters.CLOSEST_SYNC);


        Uri uri = Uri.parse("assets:///base.mp4");
        MediaSource mediaSource = buildMediaSource(uri);

        Display dis = getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

//        DrawingController drawer = new DrawingController(dis);
//        drawer.drawKf2AtoK(new Point(169,141), new Point(212,157));
//        drawer.drawKf2KtoH(new Point(212,157), new Point(238,122));
//        drawer.drawKf2Line(new Point(238,122));
//        drawer.drawKf2AtoKAngle(new Point(169,141), new Point(212,157),new Point(238,122));
//
//        LinearLayout ll = (LinearLayout) findViewById(R.id.draw_area);
//        ll.setBackgroundDrawable(new BitmapDrawable(drawer.getBitmap()));

        player.prepare(mediaSource, false, false);
        player.setPlayWhenReady(playWhenReady);
        //player.seekTo(0,  2533);
        player.seekTo(0,  0);

    }

    public Point getMidPoint(Point p1, Point p2) {
        return new Point((p1.x+p2.x)/2, (p1.y+p2.y)/2);
    }


    public void drawCurvedArrow(int x1, int y1, int x2, int y2, int curveRadius, Canvas canvas) {

        Paint paint  = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);

        final Path path = new Path();
        int midX            = x1 + ((x2 - x1) / 2);
        int midY            = y1 + ((y2 - y1) / 2);
        float xDiff         = midX - x1;
        float yDiff         = midY - y1;
        double angle        = (Math.atan2(yDiff, xDiff) * (180 / Math.PI)) - 90;
        double angleRadians = Math.toRadians(angle);
        float pointX        = (float) (midX + curveRadius * Math.cos(angleRadians));
        float pointY        = (float) (midY + curveRadius * Math.sin(angleRadians));

        path.moveTo(x1, y1);
        path.cubicTo(x1,y1,310, 450, x2, y2);
        canvas.drawPath(path, paint);
    }

    private void drawDashedLines(Integer x, Integer y,Canvas canvas) {
        Paint mPaint = new Paint();
        mPaint.setARGB(255, 255, 255, 255);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setPathEffect(new DashPathEffect(new float[]{5, 5, 5, 5}, 0));
        canvas.drawLine(x,y-150,x,y+400,mPaint);
    }


    private Integer normaliseCords(Double value, Double screenRatio, Integer deviceRatio) {
        Integer newVal;
        newVal = (int)(value/screenRatio*(float)deviceRatio);
        if (screenRatio == 1280.0) {
            newVal = newVal+80;
        } else {
            newVal = newVal+190;
        }
        return newVal;
    }


    private String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }


    private MediaSource buildMediaSource(Uri uri) {
        Cache cache = new SimpleCache(getApplication().getCacheDir(), new LeastRecentlyUsedCacheEvictor(1024 * 1024 * 10));
//        DataSource dataSource = new DefaultUriDataSource(context, bandwidthMeter, userAgent);
        final CacheDataSourceFactory cacheDataSource = new CacheDataSourceFactory(cache, new AssetDataSource.Factory() {
            @Override
            public DataSource createDataSource() {
                return new AssetDataSource(getApplicationContext());
            }
        });

        return new ExtractorMediaSource.Factory(new DataSource.Factory() {
            @Override
            public DataSource createDataSource() {
                return cacheDataSource.createDataSource();
            }
        }).setExtractorsFactory(new DefaultExtractorsFactory())
                .createMediaSource(uri);


//        return new ExtractorMediaSource.Factory(new DataSource.Factory() {
//            @Override
//            public DataSource createDataSource() {
//                return new AssetDataSource(MainActivity.this);
//            }
//        },,
//                , new DefaultExtractorsFactory(), null, null);
    }



    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }
}
