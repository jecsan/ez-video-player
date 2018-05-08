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

        playerView.setPlayer(player);
        player.setSeekParameters(SeekParameters.CLOSEST_SYNC);


        Uri uri = Uri.parse("assets:///test8.mov");
        MediaSource mediaSource = buildMediaSource(uri);


        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#01ee18"));
        paint.setStrokeWidth(4);


        Display dis = getWindowManager().getDefaultDisplay();
        int width = dis.getWidth();
        int height = dis.getHeight();


        Bitmap bg = Bitmap.createBitmap(720, 1280, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bg);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);


//        canvas.translate(0,canvas.getHeight());   // reset where 0,0 is located
//        canvas.scale(1,-1);

        Log.d(TAG,"HEIGHT"+height);
        Log.d(TAG,"WIDTH"+width);
        paint.setAntiAlias(true);

        Integer x1 = normaliseCords(235.83656311035156,1280.0,height);
        Integer y1 = normaliseCords(170.23597717285156,720.0,width);

        Integer x2 = normaliseCords(282.8143310546875,1280.0,height);
        Integer y2 = normaliseCords(179.19447326660156,720.0,width);

        Integer x3 = normaliseCords(354.7802734375,1280.0,height);
        Integer y3 = normaliseCords(153.3143768310547,720.0,width);

        Integer hpX = normaliseCords(354.7802734375,1280.0,height);
        Integer hpY = normaliseCords(79.65565490722656,720.0,width);




        canvas.drawLine(x1,y1,x2,y2,paint);
        canvas.drawLine(x2,y2,x3,y3,paint);
        drawDashedLines(hpX,hpY,canvas);
        //canvas.drawLine(x2,y2,x3,y3,paint);

//        canvas.drawLine(x4,y4,x5,y5,paint);
//        canvas.drawLine(x5,y5,x6,y6,paint);
//
//        canvas.drawLine(x7,y7,x8,y8,paint);
//        canvas.drawLine(x8,y8,x9,y9,paint);
        //drawCircle(canvas);


//        canvas.drawLine(x10,y10,x11,y11,paint);
//        canvas.drawLine(x12,y12,x13,y13,paint);


//        RectF rectF = new RectF(50, 20, 100, 80);
//        canvas.drawArc(rectF, 0F, 90F, true, paint);
//        drawCircle(canvas,(float)50,(float)50,paint);

        Integer mWidth = 300;
        Integer mHeight = 300;
        Integer mRadius = 150;
        Paint paintArc = new Paint();
        paintArc.setColor(Color.parseColor("#CD5C5C"));
        paintArc.setStrokeWidth(2);
        paintArc.setAntiAlias(true);
        //paintArc.setStrokeCap(Paint.Cap.ROUND);
        paintArc.setStyle(Paint.Style.STROKE);

        Integer newY =normaliseCords(170.23597717285156,720.0,width);
        Integer newX =  normaliseCords(235.83656311035156,1280.0,height);
        Integer newX1 = normaliseCords(282.8143310546875,1280.0,height);
        Integer newY1 = normaliseCords(179.19447326660156,720.0,width);

        Point p1 = new Point(x1, y1);
        Point p2 = new Point(x2, y2);
        Point p3 = new Point(x3, y3);

        Point midPoint = getMidPoint(p1,p2);
        Point midPoint2 = getMidPoint(p2,p3);

        drawCurvedArrow(midPoint.x,midPoint.y,midPoint2.x,midPoint2.y,30,canvas);

//        int startAngle = (int) (180 / Math.PI * Math.atan2(newY - newY1, newX -newX1));
//        paintArc.setColor(Color.RED);
//        Double dist = Math.sqrt((newX - newX1) * (newX - newX1) + (newY - newY1) * (newY - newY1));
//        float radius = 50;
//        final RectF oval = new RectF();
//        oval.set(newX, 50, newX1, 50);
//        Path myPath = new Path();
//
//        Double sweepAngle = 180- (dist/2)/radius;
//
//        Log.d(TAG,"ANGLE:"+startAngle);
//        myPath.arcTo(oval, startAngle, -(float) 360, false);
//        canvas.drawPath(myPath,paintArc);
        //canvas.drawArc (oval, 0, 90, false, paintArc);


        //canvas.drawLine(343,177,335,230,paint);

        Log.d(TAG,"TEST X:"+x1);
        Log.d(TAG,"TEST Y:"+y1);
        //canvas.drawLine(338,153,282,179,paint);
        LinearLayout ll = (LinearLayout) findViewById(R.id.draw_area);
        ll.setBackgroundDrawable(new BitmapDrawable(bg));


//        MediaMetadataRetriever mMMR = new MediaMetadataRetriever();
//        mMMR.setDataSource("assets:///stream.mp4");
//        Log.d(TAG, "---------");
//        //api time unit is microseconds
//        currentTime = 1000000;
//        while (timeInMs > currentTime ) {
//            framesArray.add(mMMR.getFrameAtTime(currentTime, MediaMetadataRetriever.OPTION_CLOSEST));
//            Log.d(TAG, Integer.toString(currentTime));
//            currentTime = currentTime+300000;
//        }

        player.prepare(mediaSource, false, false);


        //player.setPlayWhenReady(playWhenReady);
        player.seekTo(0, 3460);

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

    private void drawCircle(Canvas canvas, Float x, Float y, Paint paint) {
//        float radius = 50;
//        Path path = new Path();
//        path.addCircle(100 / 2,
//                100 / 2, 50,
//                Path.Direction.CW);
//
//        Paint paint = new Paint();
//        paint.setColor(Color.BLACK);
//        paint.setStrokeWidth(5);
//        paint.setStyle(Paint.Style.FILL);
//
//        float center_x, center_y;
//        final RectF oval = new RectF();
//        paint.setStyle(Paint.Style.STROKE);
//
//        center_x = 100 / 2;
//        center_y = 100 / 2;
//
//        oval.set(center_x - radius,
//                center_y - radius,
//                center_x + radius,
//                center_y + radius);
//        canvas.drawArc(oval, 90, 180, false, paint);
        float radius = 20;
        final RectF oval = new RectF();
        oval.set(x - radius, y - radius, x + radius,y+ radius);
        Path myPath = new Path();
        int startAngle = (int) (180 / Math.PI * Math.atan2(y - y, x - x));
        myPath.arcTo(oval, startAngle, -(float) 90, true);
        canvas.drawArc(oval, 90, 180, false, paint);
    }


    private Integer normaliseCords(Double value, Double screenRatio, Integer deviceRatio) {
        Integer newVal;
//        if (screenRatio == 1280.0) {
//            newVal = value-30;
//        } else {
//            newVal = value-17;
//        }


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
