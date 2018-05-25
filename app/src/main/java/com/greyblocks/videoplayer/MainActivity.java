package com.greyblocks.videoplayer;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
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

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
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
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
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
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Element;

import api.DefaultState;
import api.ErrorState;
import api.MyViewModelFactory;
import api.VideoState;
import api.models.Assessments;
import api.models.Frames;
import api.models.Texttips;
import okio.Buffer;
import okio.BufferedSource;
import viewmodel.VideoViewModel;


/*
* TODO
* Compress video before getting thumbnails
* Create cache for video
* Preload whole video if possible
* Turns out exoplayer doesn't support instant seek like what iOS does
* https://github.com/google/ExoPlayer/issues/1399
* https://github.com/google/ExoPlayer/issues/2882 - faster seek
*
* TODO
* Expose a method to set proshot image
* Set proshot/video player to consumer half of the screen
* Compute dimensions programmatically
*
* */
public class MainActivity extends AppCompatActivity {

    private CustomExoPlayerView playerView;
    private SimpleExoPlayer player;
    private boolean playWhenReady = true;
    private long playbackPosition;
    private int currentWindow;
    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 160;
    private ProShotView proShotView;
    private CustomPlayerControlView customerPlayerView;
    public Display dis;
    private DrawAction drawAction;

    private static final String TAG = MainActivity.class.getSimpleName();
    public ArrayList<Bitmap> framesArray = new ArrayList<>();
    Frames framesData;
    private VideoViewModel videoViewModel;
    Moshi moshi = new Moshi.Builder().build();
    public Integer kf2Hp;

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


        videoViewModel = ViewModelProviders.of(this, new MyViewModelFactory(getApplication())).get(VideoViewModel.class);
        //Observer the live data for changes
        videoViewModel.getMutableLiveData().observe(this, new Observer<VideoState>() {
            @Override
            public void onChanged(@Nullable VideoState videoState) {
                if(videoState instanceof DefaultState){
                    if(((DefaultState) videoState).component1() != null){

                        framesData = videoState.getData().getFrames();
                        List<Assessments> assessments = videoState.getData().getAssessments();
                        List<Texttips> textips = videoState.getData().getTextips();

                        Double hp = framesData.getKeyframe2().getKickleg().getHp().get(0);
                        kf2Hp = (int)(((hp*.8)+hp));
                        playerView.setFrameData(framesData,assessments,textips);

//                        Type type = Types.newParameterizedType(Map.class, Object.class, Map.class);
//                        JsonAdapter<Map<Object,Map>> adapter = moshi.adapter(type);


//                        try {
//                            String json = ((DefaultState) videoState).component1().source().readUtf8();
//                            Map<Object,Map> map = adapter.fromJson(json);
//                            map.get("frames")
//                            JsonReader jsonReader = JsonReader.of(new Buffer().writeUtf8(json));
//                            Log.d("Joed",((DefaultState) videoState).component1().getFrames().getKeyframe4().getKickleg().getAngles().toString());

                            //TODO use the data obtained from the API and pass to CustomExoPlayerView, maybe delay some parts of player initialization
                            //until we have the data?
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }

                    }

                }
                else if (videoState instanceof ErrorState){

                }
            }
        });
        //Actually call the api to get the video data
        videoViewModel.loadVideoData();
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
        player.setSeekParameters(SeekParameters.EXACT);
        proShotView = playerView.findViewById(R.id.pro_shot_view);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        AspectRatioFrameLayout aspectRatioFrameLayout = playerView.findViewById(R.id.exo_content_frame);
        final int collapsedWidth = (getResources().getDisplayMetrics().widthPixels- getResources().getDisplayMetrics().widthPixels/10);
        aspectRatioFrameLayout.getLayoutParams().width = collapsedWidth;
        aspectRatioFrameLayout.getLayoutParams().height = (displayMetrics.heightPixels-(displayMetrics.heightPixels/6));
        aspectRatioFrameLayout.requestLayout();

        final View contentFrame = playerView.findViewById(R.id.exo_content_frame);

        proShotView.getLayoutParams().height = (displayMetrics.heightPixels-(displayMetrics.heightPixels/6));

        proShotView.setProShotAction(new ProShotView.ProShotAction() {
            @Override
            public void onExpand() {


                ResizeWidthAnimation resizeWidthAnimation = new ResizeWidthAnimation(playerView.findViewById(R.id.exo_content_frame), playerView.getWidth()/2);
                resizeWidthAnimation.setDuration(200);

                Integer deviceWidth = getResources().getDisplayMetrics().widthPixels;
                contentFrame.setX(-kf2Hp);
                //proShotView.setX(1300);
                Log.d("ZZ","zzz="+proShotView.getWidth());
                //proShotView.getLayoutParams().width = 800;
                proShotView.setX(deviceWidth-kf2Hp);
                //proShotView.startAnimation(resizeWidthAnimation);
            }

            @Override
            public void onCollapse() {
                ResizeWidthAnimation resizeWidthAnimation = new ResizeWidthAnimation(playerView.findViewById(R.id.exo_content_frame),
                        collapsedWidth);
                resizeWidthAnimation.setDuration(200);
                Double hp = framesData.getKeyframe2().getKickleg().getHp().get(0);
                final Double translationWidth = ((hp*.8)+hp)/2;
                Integer deviceWidth = getResources().getDisplayMetrics().widthPixels;
                View view = playerView.findViewById(R.id.exo_content_frame);
                view.setX(0);
                proShotView.setX(deviceWidth-239);

                Log.d("ZZZ","ZZ="+proShotView.getX());
                //proShotView.startAnimation(resizeWidthAnimation);

            }
        });

        proShotView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


//        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        },2000);


   //     Uri uri = Uri.parse("assets:///sample.mp4");

        //player.setSeekParameters(SeekParameters.CLOSEST_SYNC);


        Uri uri = Uri.parse("assets:///base_5.mp4");

        MediaSource mediaSource = buildMediaSource(uri);

//        dis = getWindowManager().getDefaultDisplay();
//        DisplayMetrics metrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(metrics);
//
//        DrawingController drawer = new DrawingController(dis);
//        drawer.drawKf2AtoK(new Point(385,234), new Point(371,188));
//        drawer.drawKf2KtoH(new Point(371,188), new Point(334,151));
//        drawer.drawKf2Line(new Point(334,151));
//        drawer.drawKf2AtoKAngle(new Point(385,234), new Point(371,188),new Point(334,151));
//
//        LinearLayout ll = (LinearLayout) findViewById(R.id.draw_area);
//        ll.setBackgroundDrawable(new BitmapDrawable(drawer.getBitmap()));

//        dis = getWindowManager().getDefaultDisplay();
//        DrawingController kf2Drawer = new DrawingController(dis);
//        kf2Drawer.drawKf2AtoK(new Point(385, 234), new Point(371, 188));
//        kf2Drawer.drawKf2KtoH(new Point(371, 188), new Point(334, 151));
//        kf2Drawer.drawKf2Line(new Point(334, 151));
//
//        kf2Drawer.drawKf2AtoK(new Point(398, 235), new Point(393, 187));
//        kf2Drawer.drawKf2KtoH(new Point(393, 187), new Point(360, 157));
//        kf2Drawer.drawKf2Line(new Point(355, 158));
//
//        kf2Drawer.drawKf2AtoK(new Point(393, 235), new Point(403, 187));
//        kf2Drawer.drawKf2KtoH(new Point(403, 187), new Point(374, 158));
//        kf2Drawer.drawKf2Line(new Point(355, 158));
//
//        LinearLayout ll = (LinearLayout) findViewById(R.id.draw_area);
//        ll.setBackgroundDrawable(new BitmapDrawable(kf2Drawer.getBitmap()));
        //ll.setVisibility(LinearLayout.INVISIBLE);

        //kf2Drawer.drawKf2AtoKAngle(new Point(385, 234), new Point(371, 188), new Point(334, 151));

//        DrawingController kf3Drawer = new DrawingController(dis);
//        kf3Drawer.drawKf2AtoK(new Point(398, 235), new Point(393, 187));
//        kf3Drawer.drawKf2KtoH(new Point(393, 187), new Point(360, 157));
//        kf3Drawer.drawKf2Line(new Point(355, 158));
//        kf3Drawer.drawKf2AtoKAngle(new Point(261, 170), new Point(300, 193), new Point(355, 151));
//        LinearLayout kf3 = (LinearLayout) findViewById(R.id.draw_area);
//        kf3.setBackgroundDrawable(new BitmapDrawable(kf3Drawer.getBitmap()));
//
//        //kf3.setVisibility(LinearLayout.INVISIBLE);
//
//        DrawingController kf4Drawer = new DrawingController(dis);
//        kf4Drawer.drawKf2AtoK(new Point(393, 235), new Point(403, 187));
//        kf4Drawer.drawKf2KtoH(new Point(403, 187), new Point(374, 158));
//        kf4Drawer.drawKf2Line(new Point(355, 158));
//        //kf3Drawer.drawKf2AtoKAngle(new Point(261, 170), new Point(300, 193), new Point(355, 151));
//        LinearLayout kf4 = (LinearLayout) findViewById(R.id.draw_area);
//        kf4.setBackgroundDrawable(new BitmapDrawable(kf4Drawer.getBitmap()));


        BubbleDrawable myBubble = new BubbleDrawable(BubbleDrawable.CENTER);
//        myBubble.setCornerRadius(20);
//        myBubble.setPointerAlignment(BubbleDrawable.RIGHT);
//        myBubble.setPadding(25, 25, 25, 25);
//
//        LinearLayout dialogLayout = (LinearLayout) findViewById(R.id.draw_area);
//        dialogLayout.setBackgroundDrawable(myBubble);
//
//        //LinearLayout dialogLayout = new LinearLayout(this);
//        dialogLayout.setBackgroundColor(Color.CYAN);
//        dialogLayout.setOrientation(LinearLayout.VERTICAL);
//
//        RelativeLayout.LayoutParams LLParams = new RelativeLayout.LayoutParams(new FrameLayout.LayoutParams(500,200));
//        LLParams.topMargin = 421;
//        LLParams.leftMargin = 693;
//        dialogLayout.setLayoutParams(LLParams);
//        TextView rowTextView = new TextView(this);
//        rowTextView.setText("When winding up, pull your leg back 50° + Bend at the knee to 90°.");
//        rowTextView.setTextColor(Color.WHITE);
//        rowTextView.setWidth(200);
//        dialogLayout.setBackgroundDrawable(myBubble);
//        dialogLayout.addView(rowTextView);
       // playerView.addView(dialogLayout);

        player.prepare(mediaSource, false, false);
        //player.setPlayWhenReady(playWhenReady);
        //player.seekTo(0, 3610);
        player.setVolume(0);
        player.seekTo(0,0);
        //player.seekTo(0,  0);


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
//
//    public void drawKeyframe(Long position) {
//        if (position == 2533) {
//            dis = getWindowManager().getDefaultDisplay();
//            DrawingController drawer = new DrawingController(dis);
//            drawer.drawKf2AtoK(new Point(169,141), new Point(212,157));
//            drawer.drawKf2KtoH(new Point(212,157), new Point(238,122));
//            drawer.drawKf2Line(new Point(238,122));
//            //drawer.drawKf2AtoKAngle(new Point(169,141), new Point(212,157),new Point(238,122));
//
//            LinearLayout ll = (LinearLayout) findViewById(R.id.draw_area);
//            ll.setBackgroundDrawable(new BitmapDrawable(drawer.getBitmap()));
//        }
//    }
//

    public void setDrawing(MainActivity.DrawAction drawer) {
        this.drawAction = drawer;
    }

    public interface DrawAction {
        void drawFrame(Long position);
    }

}
