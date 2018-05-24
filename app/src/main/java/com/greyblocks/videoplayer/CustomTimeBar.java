package com.greyblocks.videoplayer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;
import android.widget.LinearLayout;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ui.TimeBar;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArraySet;

public class CustomTimeBar extends View implements TimeBar {

    /**
     * Default height for the time bar, in dp.
     */
    public static final int DEFAULT_BAR_HEIGHT_DP = 4;
    /**
     * Default height for the touch target, in dp.
     */
    public static final int DEFAULT_TOUCH_TARGET_HEIGHT_DP = 26;
    /**
     * Default width for ad markers, in dp.
     */
    public static final int DEFAULT_AD_MARKER_WIDTH_DP = 4;
    /**
     * Default diameter for the scrubber when enabled, in dp.
     */
    public static final int DEFAULT_SCRUBBER_ENABLED_SIZE_DP = 12;
    /**
     * Default diameter for the scrubber when disabled, in dp.
     */
    public static final int DEFAULT_SCRUBBER_DISABLED_SIZE_DP = 0;
    /**
     * Default diameter for the scrubber when dragged, in dp.
     */
    public static final int DEFAULT_SCRUBBER_DRAGGED_SIZE_DP = 16;
    /**
     * Default color for the played portion of the time bar.
     */
    public static final int DEFAULT_PLAYED_COLOR = 0xFFFFFFFF;
    /**
     * Default color for ad markers.
     */
    public static final int DEFAULT_AD_MARKER_COLOR = 0xB2FFFF00;

    /**
     * The threshold in dps above the bar at which touch events trigger fine scrub mode.
     */
    private static final int FINE_SCRUB_Y_THRESHOLD_DP = -50;
    /**
     * The ratio by which times are reduced in fine scrub mode.
     */
    private static final int FINE_SCRUB_RATIO = 0;
    /**
     * The time after which the scrubbing listener is notified that scrubbing has stopped after
     * performing an incremental scrub using key input.
     */
    private static final long STOP_SCRUBBING_TIMEOUT_MS = 0;
    private static final int DEFAULT_INCREMENT_COUNT = 1;

    private final Rect seekBounds;
    private final Rect progressBar;
    private final Rect bufferedBar;
    private final Rect scrubberBar;
    private final Paint playedPaint;
    private final Paint bufferedPaint;
    private final Paint unplayedPaint;
    private final Paint adMarkerPaint;
    private final Paint playedAdMarkerPaint;
    private final Paint scrubberPaint;
    private final Drawable scrubberDrawable;
    private final int barHeight;
    private final int touchTargetHeight;
    private final int adMarkerWidth;
    private final int scrubberEnabledSize;
    private final int scrubberDisabledSize;
    private final int scrubberDraggedSize;
    private final int scrubberPadding;
    private final int fineScrubYThreshold;
    private final StringBuilder formatBuilder;
    private final Formatter formatter;
    private final Runnable stopScrubbingRunnable;
    private final CopyOnWriteArraySet<OnScrubListener> listeners;

    private final Integer imagesOffset = 160;
    private final Integer thumbWidth = 170;
    private final Integer framesMsSkip = 200000;
    private final Integer thumbHeight = 170;

    private int keyCountIncrement;
    private long keyTimeIncrement;
    private int lastCoarseScrubXPosition;
    private int[] locationOnScreen;
    private Point touchPosition;

    private boolean scrubbing;
    private long scrubPosition;
    private long duration;
    private long position;
    private long bufferedPosition;
    private int adGroupCount;
    private long[] adGroupTimesMs;
    private boolean[] playedAdGroups;

    private Bitmap sampleBitmap;
    private List<Bitmap> samepleBitmaps = new ArrayList<>();
    private static final String TAG = MainActivity.class.getSimpleName();

    public static Bitmap drawableToBitmap (Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }


    private void initTimebarBitmaps(Context context){
//        sampleBitmap = drawableToBitmap(ContextCompat.getDrawable(getContext(),R.drawable.exo_controls_fastforward));
//        for(int i=0;i<10;i++){
//            samepleBitmaps.add(sampleBitmap);
//        }

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        final AssetFileDescriptor afd;
        try {
            afd = context.getAssets().openFd("stream.mp4");
            retriever.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            Integer timeInMs = Integer.parseInt(time)*1000;
            Log.d(TAG, "Video Time:"+ Integer.toString(timeInMs));

//            Integer currentTime = 0;
//            while (timeInMs > currentTime ) {
//                Bitmap tmpBtm = retriever.getFrameAtTime(currentTime, MediaMetadataRetriever.OPTION_CLOSEST);
//                if (tmpBtm  != null) {
//                    Bitmap newBtmp = Bitmap.createScaledBitmap(tmpBtm, thumbWidth, thumbHeight, false);
//                    samepleBitmaps.add(newBtmp);
//                }
//                //currentTime = 99999999;
//                //currentTime = currentTime+framesMsSkip;
//            }
            Log.d(TAG, "Thumbnails count: "+Integer.toString(samepleBitmaps.size()));
        } catch(IOException ex) {

        }

    }

    /**
     * Creates a new time bar.
     */
    public CustomTimeBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        seekBounds = new Rect();
        progressBar = new Rect();
        bufferedBar = new Rect();
        scrubberBar = new Rect();
        playedPaint = new Paint();
        bufferedPaint = new Paint();
        unplayedPaint = new Paint();
        adMarkerPaint = new Paint();
        playedAdMarkerPaint = new Paint();
        scrubberPaint = new Paint();
        scrubberPaint.setAntiAlias(true);
        listeners = new CopyOnWriteArraySet<>();

        initTimebarBitmaps(context);

        // Calculate the dimensions and paints for drawn elements.
        Resources res = context.getResources();
        DisplayMetrics displayMetrics = res.getDisplayMetrics();
        fineScrubYThreshold = dpToPx(displayMetrics, FINE_SCRUB_Y_THRESHOLD_DP);
        int defaultBarHeight = dpToPx(displayMetrics, DEFAULT_BAR_HEIGHT_DP);
        int defaultTouchTargetHeight = dpToPx(displayMetrics, DEFAULT_TOUCH_TARGET_HEIGHT_DP);
        int defaultAdMarkerWidth = dpToPx(displayMetrics, DEFAULT_AD_MARKER_WIDTH_DP);
        int defaultScrubberEnabledSize = dpToPx(displayMetrics, DEFAULT_SCRUBBER_ENABLED_SIZE_DP);
        int defaultScrubberDisabledSize = dpToPx(displayMetrics, DEFAULT_SCRUBBER_DISABLED_SIZE_DP);
        int defaultScrubberDraggedSize = dpToPx(displayMetrics, DEFAULT_SCRUBBER_DRAGGED_SIZE_DP);
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DefaultTimeBar, 0,
                    0);
            try {
                scrubberDrawable = a.getDrawable(R.styleable.DefaultTimeBar_scrubber_drawable);
                if (scrubberDrawable != null) {
                    setDrawableLayoutDirection(scrubberDrawable, getLayoutDirection());
                    defaultTouchTargetHeight =
                            Math.max(scrubberDrawable.getMinimumHeight(), defaultTouchTargetHeight);
                }
                barHeight = a.getDimensionPixelSize(R.styleable.DefaultTimeBar_bar_height,
                        defaultBarHeight);
                touchTargetHeight = a.getDimensionPixelSize(R.styleable.DefaultTimeBar_touch_target_height,
                        defaultTouchTargetHeight);
                adMarkerWidth = a.getDimensionPixelSize(R.styleable.DefaultTimeBar_ad_marker_width,
                        defaultAdMarkerWidth);
                scrubberEnabledSize = a.getDimensionPixelSize(
                        R.styleable.DefaultTimeBar_scrubber_enabled_size, defaultScrubberEnabledSize);
                scrubberDisabledSize = a.getDimensionPixelSize(
                        R.styleable.DefaultTimeBar_scrubber_disabled_size, defaultScrubberDisabledSize);
                scrubberDraggedSize = a.getDimensionPixelSize(
                        R.styleable.DefaultTimeBar_scrubber_dragged_size, defaultScrubberDraggedSize);
                int playedColor = a.getInt(R.styleable.DefaultTimeBar_played_color, DEFAULT_PLAYED_COLOR);
                int scrubberColor = a.getInt(R.styleable.DefaultTimeBar_scrubber_color,
                        getDefaultScrubberColor(playedColor));
                int bufferedColor = a.getInt(R.styleable.DefaultTimeBar_buffered_color,
                        getDefaultBufferedColor(playedColor));
                int unplayedColor = a.getInt(R.styleable.DefaultTimeBar_unplayed_color,
                        getDefaultUnplayedColor(playedColor));
                int adMarkerColor = a.getInt(R.styleable.DefaultTimeBar_ad_marker_color,
                        DEFAULT_AD_MARKER_COLOR);
                int playedAdMarkerColor = a.getInt(R.styleable.DefaultTimeBar_played_ad_marker_color,
                        getDefaultPlayedAdMarkerColor(adMarkerColor));
                playedPaint.setColor(Color.TRANSPARENT);
                scrubberPaint.setColor(Color.GRAY);
                bufferedPaint.setColor(Color.TRANSPARENT);
                unplayedPaint.setColor(Color.TRANSPARENT);
                adMarkerPaint.setColor(adMarkerColor);
                playedAdMarkerPaint.setColor(playedAdMarkerColor);
            } finally {
                a.recycle();
            }
        } else {
            barHeight = defaultBarHeight;
            touchTargetHeight = defaultTouchTargetHeight;
            adMarkerWidth = defaultAdMarkerWidth;
            scrubberEnabledSize = defaultScrubberEnabledSize;
            scrubberDisabledSize = defaultScrubberDisabledSize;
            scrubberDraggedSize = defaultScrubberDraggedSize;
            playedPaint.setColor(DEFAULT_PLAYED_COLOR);
            scrubberPaint.setColor(getDefaultScrubberColor(DEFAULT_PLAYED_COLOR));
            bufferedPaint.setColor(getDefaultBufferedColor(DEFAULT_PLAYED_COLOR));
            unplayedPaint.setColor(getDefaultUnplayedColor(DEFAULT_PLAYED_COLOR));
            adMarkerPaint.setColor(DEFAULT_AD_MARKER_COLOR);
            scrubberDrawable = null;
        }
        formatBuilder = new StringBuilder();
        formatter = new Formatter(formatBuilder, Locale.getDefault());
        stopScrubbingRunnable = new Runnable() {
            @Override
            public void run() {
                stopScrubbing(false);
            }
        };
        if (scrubberDrawable != null) {
            scrubberPadding = (scrubberDrawable.getMinimumWidth() + 1) / 2;
        } else {
            scrubberPadding =
                    (Math.max(scrubberDisabledSize, Math.max(scrubberEnabledSize, scrubberDraggedSize)) + 1)
                            / 2;
        }
        duration = C.TIME_UNSET;
        keyTimeIncrement = C.TIME_UNSET;
        keyCountIncrement = DEFAULT_INCREMENT_COUNT;
        setFocusable(true);
        if (Util.SDK_INT >= 16) {
            maybeSetImportantForAccessibilityV16();
        }


    }

    @Override
    public void addListener(OnScrubListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(OnScrubListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void setKeyTimeIncrement(long time) {
        Assertions.checkArgument(time > 0);
        keyCountIncrement = C.INDEX_UNSET;
        keyTimeIncrement = time;
    }

    @Override
    public void setKeyCountIncrement(int count) {
        Assertions.checkArgument(count > 0);
        keyCountIncrement = count;
        keyTimeIncrement = C.TIME_UNSET;
    }

    @Override
    public void setPosition(long position) {
        this.position = position;
        setContentDescription(getProgressText());
        update();
    }

    @Override
    public void setBufferedPosition(long bufferedPosition) {
        this.bufferedPosition = bufferedPosition;
        update();
    }

    @Override
    public void setDuration(long duration) {
        this.duration = duration;
        if (scrubbing && duration == C.TIME_UNSET) {
            stopScrubbing(true);
        }
        update();
    }

    @Override
    public void setAdGroupTimesMs(@Nullable long[] adGroupTimesMs, @Nullable boolean[] playedAdGroups,
                                  int adGroupCount) {
        Assertions.checkArgument(adGroupCount == 0
                || (adGroupTimesMs != null && playedAdGroups != null));
        this.adGroupCount = adGroupCount;
        this.adGroupTimesMs = adGroupTimesMs;
        this.playedAdGroups = playedAdGroups;
        update();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (scrubbing && !enabled) {
            stopScrubbing(true);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.save();
        drawTimeBar(canvas);
        drawPlayhead(canvas);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled() || duration <= 0) {
            return false;
        }
        Point touchPosition = resolveRelativeTouchPosition(event);
        int x = touchPosition.x;
        int y = touchPosition.y;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isInSeekBar(x, y)) {
                    startScrubbing();
                    positionScrubber(x);
                    scrubPosition = getScrubberPosition();
                    update();
                    invalidate();
                    setScrubPosition();
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (scrubbing) {
                    if (y < fineScrubYThreshold) {
                        int relativeX = x - lastCoarseScrubXPosition;
                        positionScrubber(lastCoarseScrubXPosition + relativeX / FINE_SCRUB_RATIO);
                    } else {
                        lastCoarseScrubXPosition = x;
                        positionScrubber(x);
                    }
                    scrubPosition = getScrubberPosition();
                    for (OnScrubListener listener : listeners) {
                        listener.onScrubMove(this, scrubPosition);
                    }
                    update();
                    invalidate();
                    setScrubPosition();
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (scrubbing) {
                    scrubbing = false;
//                    stopScrubbing(event.getAction() == MotionEvent.ACTION_CANCEL);
                    return true;
                }
                break;
            default:
                // Do nothing.
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isEnabled()) {
            long positionIncrement = getPositionIncrement();
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    positionIncrement = -positionIncrement;
                    // Fall through.
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (scrubIncrementally(positionIncrement)) {
                        removeCallbacks(stopScrubbingRunnable);
                        postDelayed(stopScrubbingRunnable, STOP_SCRUBBING_TIMEOUT_MS);
                        return true;
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_ENTER:
                    if (scrubbing) {
                        removeCallbacks(stopScrubbingRunnable);
                        stopScrubbingRunnable.run();
                        return true;
                    }
                    break;
                default:
                    // Do nothing.
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        updateDrawableState();
    }

    @Override
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if (scrubberDrawable != null) {
            scrubberDrawable.jumpToCurrentState();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int height = heightMode == MeasureSpec.UNSPECIFIED ? touchTargetHeight
                : heightMode == MeasureSpec.EXACTLY ? heightSize : Math.min(touchTargetHeight, heightSize);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height);
        updateDrawableState();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = right - left;
        int height = bottom - top;
        int barY = (height - touchTargetHeight) / 2;
        int seekLeft = getPaddingLeft();
        int seekRight = width - getPaddingRight();
        int progressY = barY + (touchTargetHeight - barHeight) / 2;
        seekBounds.set(seekLeft, barY, seekRight, barY + touchTargetHeight);
        progressBar.set(seekBounds.left + scrubberPadding, progressY,
                seekBounds.right - scrubberPadding, progressY + barHeight);
        update();
    }

    @Override
    public void onRtlPropertiesChanged(int layoutDirection) {
        if (scrubberDrawable != null && setDrawableLayoutDirection(scrubberDrawable, layoutDirection)) {
            invalidate();
        }
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_SELECTED) {
            event.getText().add(getProgressText());
        }
        event.setClassName(CustomTimeBar.class.getName());
    }

    @TargetApi(21)
    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(CustomTimeBar.class.getCanonicalName());
        info.setContentDescription(getProgressText());
        if (duration <= 0) {
            return;
        }
        if (Util.SDK_INT >= 21) {
            info.addAction(AccessibilityAction.ACTION_SCROLL_FORWARD);
            info.addAction(AccessibilityAction.ACTION_SCROLL_BACKWARD);
        } else if (Util.SDK_INT >= 16) {
            info.addAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
            info.addAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
        }
    }

    @TargetApi(16)
    @Override
    public boolean performAccessibilityAction(int action, Bundle args) {
        if (super.performAccessibilityAction(action, args)) {
            return true;
        }
        if (duration <= 0) {
            return false;
        }
        if (action == AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD) {
            if (scrubIncrementally(-getPositionIncrement())) {
                stopScrubbing(false);
            }
        } else if (action == AccessibilityNodeInfo.ACTION_SCROLL_FORWARD) {
            if (scrubIncrementally(getPositionIncrement())) {
                stopScrubbing(false);
            }
        } else {
            return false;
        }
        sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED);
        return true;
    }

    // Internal methods.

    @TargetApi(16)
    private void maybeSetImportantForAccessibilityV16() {
        if (getImportantForAccessibility() == View.IMPORTANT_FOR_ACCESSIBILITY_AUTO) {
            setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
        }
    }

    private void startScrubbing() {
        scrubbing = true;
        setPressed(true);
        ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true);
        }
        for (OnScrubListener listener : listeners) {
            listener.onScrubStart(this, getScrubberPosition());
        }
    }

    private void stopScrubbing(boolean canceled) {
        scrubbing = false;
        setPressed(false);
        ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(false);
        }
        invalidate();
        for (OnScrubListener listener : listeners) {
            listener.onScrubStop(this, getScrubberPosition(), canceled);
        }
    }

    private void setScrubPosition() {
        for (OnScrubListener listener : listeners) {
            listener.onScrubStop(this, getScrubberPosition(), false);
        }
    }

    private void update() {
        bufferedBar.set(progressBar);
        scrubberBar.set(progressBar);
        long newScrubberTime = scrubbing ? scrubPosition : position;
        if (duration > 0) {
            int bufferedPixelWidth = (int) ((progressBar.width() * bufferedPosition) / duration);
            bufferedBar.right = Math.min(progressBar.left + bufferedPixelWidth, progressBar.right);
            int scrubberPixelPosition = (int) ((progressBar.width() * newScrubberTime) / duration);
            scrubberBar.right = Math.min(progressBar.left + scrubberPixelPosition, progressBar.right);
            //scrubberBar.right = scrubberBar.right-100;
        } else {
            bufferedBar.right = progressBar.left;
            scrubberBar.right = progressBar.left;
        }
        invalidate(seekBounds);
    }

    private void positionScrubber(float xPosition) {
        scrubberBar.right = Util.constrainValue((int) xPosition, progressBar.left, progressBar.right);
    }

    private Point resolveRelativeTouchPosition(MotionEvent motionEvent) {
        if (locationOnScreen == null) {
            locationOnScreen = new int[2];
            touchPosition = new Point();
        }
        getLocationOnScreen(locationOnScreen);
        touchPosition.set(
                ((int) motionEvent.getRawX()) - locationOnScreen[0],
                ((int) motionEvent.getRawY()) - locationOnScreen[1]);
        return touchPosition;
    }

    private long getScrubberPosition() {
        if (progressBar.width() <= 0 || duration == C.TIME_UNSET) {
            return 0;
        }
        return (scrubberBar.width() * duration) / progressBar.width();
    }

    private boolean isInSeekBar(float x, float y) {
        return seekBounds.contains((int) x, (int) y);
    }

    private void drawTimeBar(Canvas canvas) {
        //TODO draw bitmaps here
        int bitmapWidth = 0;
        for(Bitmap b : samepleBitmaps){
            canvas.drawBitmap(b,bitmapWidth,190,scrubberPaint);
            bitmapWidth+=imagesOffset;
        }

        int progressBarHeight = progressBar.height();
        int barTop = progressBar.centerY() - progressBarHeight / 2;
        int barBottom = barTop + progressBarHeight;
//        if (duration <= 0) {
//            canvas.drawRect(progressBar.left, barTop, progressBar.right, barBottom, unplayedPaint);
//            return;
//        }
//        int bufferedLeft = bufferedBar.left;
//        int bufferedRight = bufferedBar.right;
//        int progressLeft = Math.max(Math.max(progressBar.left, bufferedRight), scrubberBar.right);
//        if (progressLeft < progressBar.right) {
//            canvas.drawRect(progressLeft, barTop, progressBar.right, barBottom, unplayedPaint);
//        }
//        bufferedLeft = Math.max(bufferedLeft, scrubberBar.right);
//        if (bufferedRight > bufferedLeft) {
//            canvas.drawRect(bufferedLeft, barTop, bufferedRight, barBottom, bufferedPaint);
//        }
//        if (scrubberBar.width() > 0) {
//            canvas.drawRect(scrubberBar.left, barTop, scrubberBar.right, barBottom, playedPaint);
//        }
//        int adMarkerOffset = adMarkerWidth / 2;
//        //ads
//        for (int i = 0; i < adGroupCount; i++) {
//            long adGroupTimeMs = Util.constrainValue(adGroupTimesMs[i], 0, duration);
//            int markerPositionOffset =
//                    (int) (progressBar.width() * adGroupTimeMs / duration) - adMarkerOffset;
//            int markerLeft = progressBar.left + Math.min(progressBar.width() - adMarkerWidth,
//                    Math.max(0, markerPositionOffset));
//            Paint paint = playedAdGroups[i] ? playedAdMarkerPaint : adMarkerPaint;
//            canvas.drawRect(markerLeft, barTop, markerLeft + adMarkerWidth, barBottom, paint);
//        }
    }

    private void drawPlayhead(Canvas canvas) {
        if (duration <= 0) {
            return;
        }
        Float fPos = (float)position/(float)1000;
        Double intPos =(double) Math.round(fPos * 100) / 100;

        //Log.d("TIMEBAR","TIMEBAR="+position);
        String durationText = Double.toString(intPos);

        int playheadX = Util.constrainValue(scrubberBar.right, scrubberBar.left, progressBar.right);
        Log.d(TAG,"==============");
        Log.d(TAG,"RIGHT="+scrubberBar.right);
        Log.d(TAG,"LEFT="+scrubberBar.left);
        Log.d(TAG,"PROG="+progressBar.right);
        Log.d(TAG,"playheadX="+playheadX);
//        Log.d(TAG,"DURATION="+duration);
//        Log.d(TAG,"==============");

        int playheadY = scrubberBar.centerY();
        if (scrubberDrawable == null) {
            //int scrubberSize = (scrubbing || isFocused()) ? scrubberDraggedSize : (isEnabled() ? scrubberEnabledSize : scrubberDisabledSize);
            //int playheadRadius = scrubberSize / 2;
            scrubberPaint.setStrokeWidth(8);
            drawTsBubble(durationText,playheadX,playheadY,canvas);
            drawMarker(canvas,playheadX,playheadY);

        } else {
            int scrubberDrawableWidth = scrubberDrawable.getIntrinsicWidth();
            int scrubberDrawableHeight = scrubberDrawable.getIntrinsicHeight();
            scrubberDrawable.setBounds(
                    playheadX - scrubberDrawableWidth / 2,
                    playheadY - scrubberDrawableHeight / 2,
                    playheadX + scrubberDrawableWidth / 2,
                    playheadY + scrubberDrawableHeight / 2);
            scrubberDrawable.draw(canvas);
        }


    }

    public void drawMarker(Canvas canvas, Integer playheadX,Integer playheadY) {
        Display dis = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Float perPx = (float)dis.getWidth()/(float)duration;
        //Log.d("PERPX","perPx="+perPx);
        // .46302748*3620
        //DrawingController drawer = new DrawingController(dis);
        //drawer.drawKf2AtoK(new Point(1667, 300), new Point(1800, 300));
//        final Integer kf3Time = 3710;
//        final Integer kf4Time = 3790;

//        final Integer kf2Time = 3620;
//        final Integer kf3Time = 3695;
//        final Integer kf4Time = 3765;

        final Integer kf2Time = 1570;
        final Integer kf3Time = 1730;
        final Integer kf4Time = 1850;


        final Integer kf2Start =  Util.constrainValue(kf2Time-28, 168, progressBar.right)+2;
        final Integer kf3Start =  (int)(perPx*kf3Time)+28;
        final Integer kf4Start =  (int)(perPx*kf4Time)+28;

//        Log.d("kf2Start","perPx="+perPx);
//        Log.d("kf2Start","kf2Start="+kf2Start);
//        Log.d("kf2Start","setStrokeWidth="+playheadX);
//        Log.d("kf2Start","scrubberPadding="+scrubberPadding);


        Integer kf3Bubble = kf2Start+95;
        Integer kf4Bubble = kf2Start+190;

        if (kf3Start > kf3Bubble)  {
            kf3Bubble = kf3Start;
        }

        if (kf4Start > kf4Bubble)  {
            kf4Bubble = kf4Start;
        }

        //scrubber line
        scrubberPaint.setStrokeWidth(19);
        canvas.drawLine(playheadX, playheadY+20, playheadX, playheadY+180, scrubberPaint);


        Paint circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.GRAY  );
        circlePaint.setStrokeWidth(3);
        drawKfBubble(kf2Start,circlePaint,canvas,playheadY,kf2Time);
        drawKfBubble(kf3Bubble,circlePaint,canvas,playheadY,kf3Time);
        drawKfBubble(kf4Bubble,circlePaint,canvas,playheadY,kf4Time);

        //bubble text
        Paint textPaint= new Paint();
        textPaint.setColor(Color.RED);
        textPaint.setTextSize(50);
        textPaint.setTextAlign(Paint.Align.CENTER);
        drawKfText(kf2Start,textPaint,canvas,playheadY,kf2Time,"1");
        drawKfText(kf3Bubble,textPaint,canvas,playheadY,kf3Time,"2");
        drawKfText(kf4Bubble,textPaint,canvas,playheadY,kf4Time,"3");

        // connecting lines
        Paint linePaint= new Paint();
        linePaint.setStrokeWidth(19);
        linePaint.setColor(Color.GRAY);

        if (position == kf3Time && kf3Start != kf3Bubble) {
            canvas.drawLine(kf3Start, playheadY+86, kf3Start, playheadY+200, linePaint);
            canvas.drawLine(kf3Start, playheadY+95, kf3Bubble, playheadY+95, linePaint);
            canvas.drawLine(kf3Bubble, playheadY+105, kf3Bubble, playheadY+45, linePaint);
        }

        else if (position == kf4Time && kf4Start != kf4Bubble) {
            canvas.drawLine(kf4Start, playheadY+86, kf4Start, playheadY+200, linePaint);
            canvas.drawLine(kf4Start, playheadY+95, kf4Bubble, playheadY+95, linePaint);
            canvas.drawLine(kf4Bubble, playheadY+105, kf4Bubble, playheadY+45, linePaint);
        }


        Paint circleMarker = new Paint();
        circleMarker.setAntiAlias(true);
        drawKfdot(kf2Start,circleMarker,canvas,playheadY,kf2Time);
        drawKfdot(kf3Start,circleMarker,canvas,playheadY,kf3Time);
        drawKfdot(kf4Start,circleMarker,canvas,playheadY,kf4Time);
    }

    public void drawConnectingLines(Integer kf, Paint paint, Canvas canvas, Integer playheadY) {
        if (kf == position) {
            paint.setColor(Color.GREEN);
        } else {
            paint.setColor(Color.GRAY);
        }
        canvas.drawLine(kf, playheadY+86, kf, playheadY+200, paint);
    }

    public void drawKfBubble(Integer kf, Paint paint, Canvas canvas, Integer playheadY,Integer kfTime) {
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(kf,playheadY,45,paint);
        if (kfTime == position) {
            paint.setColor(Color.RED);
            paint.setStrokeWidth(3);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(kf,playheadY,45,paint);
        }
    }

    public void drawKfText(Integer kf, Paint paint, Canvas canvas, Integer playheadY,Integer kfTime, String text) {
        if (kfTime == position) {
            paint.setColor(Color.RED);
        } else {
            paint.setColor(Color.RED);
        }
        canvas.drawText(text, kf, playheadY+15 ,paint);
    }

    public void drawKfdot(Integer kf, Paint paint, Canvas canvas, Integer playheadY, Integer kfTime) {
        if (kfTime == position) {
            paint.setColor(Color.GREEN);
        } else {
            paint.setColor(Color.RED);
        }
        canvas.drawCircle(kf,playheadY+100,10,paint);
    }

    public void drawTsBubble(String durationText,Integer playheadX,Integer playheadY, Canvas canvas)  {
        Path mPointer = new Path();
        mPointer.setFillType(Path.FillType.EVEN_ODD);
        Integer mPointerWidth = 40;
        Integer mPointerHeight = 40;

        Paint textPaint= new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(80);  //set text size
        textPaint.setTextAlign(Paint.Align.CENTER);
        Integer rectStart = playheadX-100;
        RectF mBoxRect = new RectF(rectStart,10,playheadX+100,playheadY-40);
        canvas.drawRoundRect(mBoxRect,5,5,scrubberPaint);
        mPointer.moveTo(playheadX-20, 123);
        mPointer.rLineTo(mPointerWidth, 0);
        mPointer.rLineTo(-(mPointerWidth / 2), mPointerHeight);
        mPointer.rLineTo(-(mPointerWidth / 2), -mPointerHeight);
        mPointer.close();
        canvas.drawText(durationText, playheadX+5, 100 ,textPaint);
        canvas.drawPath(mPointer, scrubberPaint);
    }

    private void drawKeyMaRker(String durationText,Integer playheadX,Integer playheadY, Canvas canvas) {
        Paint textPaint= new Paint();
        textPaint.setColor(Color.RED);
        textPaint.setTextSize(50);  //set text size
        textPaint.setTextAlign(Paint.Align.CENTER);
        Integer rectStart = playheadX-100;
        RectF mBoxRect = new RectF(rectStart,10,playheadX+100,playheadY-40);
        canvas.drawRoundRect(mBoxRect,5,5,scrubberPaint);
        canvas.drawText(durationText, playheadX+5, 100 ,textPaint);
    }

    private void updateDrawableState() {
        if (scrubberDrawable != null && scrubberDrawable.isStateful()
                && scrubberDrawable.setState(getDrawableState())) {
            invalidate();
        }
    }

    private String getProgressText() {
        return Util.getStringForTime(formatBuilder, formatter, position);
    }

    private long getPositionIncrement() {
//        Long t = keyTimeIncrement == C.TIME_UNSET
//                ? (duration == C.TIME_UNSET ? 0 : (duration / keyCountIncrement)) : keyTimeIncrement;
//        Log.d(TAG,"pos:"+Double.toString(t));
        return keyTimeIncrement == C.TIME_UNSET
                ? (duration == C.TIME_UNSET ? 0 : (duration / keyCountIncrement)) : keyTimeIncrement;
    }

    /**
     * Incrementally scrubs the position by {@code positionChange}.
     *
     * @param positionChange The change in the scrubber position, in milliseconds. May be negative.
     * @return Returns whether the scrubber position changed.
     */
    private boolean scrubIncrementally(long positionChange) {
        if (duration <= 0) {
            return false;
        }
        long scrubberPosition = getScrubberPosition();
        scrubPosition = Util.constrainValue(scrubberPosition + positionChange, 0, duration);
        if (scrubPosition == scrubberPosition) {
            return false;
        }
        if (!scrubbing) {
            startScrubbing();
        }
        for (OnScrubListener listener : listeners) {
            listener.onScrubMove(this, scrubPosition);
        }
        update();
        return true;
    }

    private static int dpToPx(DisplayMetrics displayMetrics, int dps) {
        return (int) (dps * displayMetrics.density + 0.5f);
    }

    private static boolean setDrawableLayoutDirection(Drawable drawable, int layoutDirection) {
        return Util.SDK_INT >= 23 && drawable.setLayoutDirection(layoutDirection);
    }

    public static int getDefaultScrubberColor(int playedColor) {
        return 0xFF000000 | playedColor;
    }

    public static int getDefaultUnplayedColor(int playedColor) {
        return 0x33000000 | (playedColor & 0x00FFFFFF);
    }

    public static int getDefaultBufferedColor(int playedColor) {
        return 0xCC000000 | (playedColor & 0x00FFFFFF);
    }

    public static int getDefaultPlayedAdMarkerColor(int adMarkerColor) {
        return 0x33000000 | (adMarkerColor & 0x00FFFFFF);
    }

    private CustomTimeBar.DisplayDrawingListener displayDrawingListener;

    public void setDisplayDrawingListener(CustomTimeBar.DisplayDrawingListener displayDrawingListener) {
        this.displayDrawingListener = displayDrawingListener;
    }

    public interface DisplayDrawingListener{
        void onDisplayDrawing(int pos);
    }
}
