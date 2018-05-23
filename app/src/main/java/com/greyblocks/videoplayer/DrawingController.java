package com.greyblocks.videoplayer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.LinearLayout;

/**
 * Created by rav on 09/05/2018.
 */

public class DrawingController {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Integer videoWidth = 1280;
    private Integer videoHeight = 720;
    private Integer deviceWidth = 0;
    private Integer deviceHeight = 0;
    private Double screenRatio = 0.0;
    private Canvas canvas;
    private Bitmap bg;

    public DrawingController(Display dis) {
        this.deviceWidth = dis.getWidth();
        this.deviceHeight = dis.getHeight();
//        Log.d(TAG,"WIDTH"+deviceWidth);
//        Log.d(TAG,"WIDTH"+deviceHeight);
        bg = Bitmap.createBitmap(videoWidth, videoHeight, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bg);
    }

    public Bitmap getBitmap() {
        return this.bg;
    }

    public void drawRaw(String color, Point p1, Point p2) {
        Paint paint = new Paint();
        paint.setColor(Color.parseColor(color));
        paint.setStrokeWidth(6);
        paint.setStrokeCap(Paint.Cap.SQUARE);
        paint.setAntiAlias(true);
        canvas.drawLine(p1.x,p1.y,p2.x,p2.y,paint);
    }

    public void drawLine(String color, Point p1, Point p2) {
        Paint paint = new Paint();
        paint.setColor(Color.parseColor(color));
        paint.setStrokeWidth(6);
        paint.setStrokeCap(Paint.Cap.SQUARE);
        paint.setAntiAlias(true);
        //paint.setDither(false);
        Point newP1 = normalisePoint(p1);
        Point newP2 = normalisePoint(p2);
//
//        drawer.drawKf2AtoK(new Point(169,141), new Point(212,157));
//        drawer.drawKf2KtoH(new Point(212,157), new Point(238,122));
//        drawer.drawKf2Line(new Point(238,122));
//        drawer.drawKf2AtoKAngle(new Point(169,141), new Point(212,157),new Point(238,122));
//

        canvas.drawLine(newP1.x,newP1.y,newP2.x,newP2.y,paint);
    }

    public Point normalisePoint(Point point) {
        Point newPoint = new Point(0,0);
//        Float newX = (((float)point.x/(float)videoWidth)*(float)deviceWidth);
//        Float newY = ((float)((float)point.y/(float)videoHeight)*(float)deviceHeight);

        Float newX = (float)(point.x*.8)+point.x;
        Float newY = (float)(point.y*.8)+point.y;

        newPoint.set(newX.intValue(),newY.intValue());
        return newPoint;
    }


    public void drawCurvedArrow(int x1, int y1, int x2, int y2, int curveRadius, Point p3, String color) {

        Paint paint  = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor(color));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.SQUARE);
        final Path path = new Path();
        int midX            = x1 + ((x2 - x1) / 2);
        int midY            = y1 + ((y2 - y1) / 2);
        float xDiff         = midX - x1;
        float yDiff         = midY - y1;
        double angle        = (Math.atan2(yDiff, xDiff) * (180 / Math.PI)) - 90;
        double angleRadians = Math.toRadians(angle);
        float pointX        = (float) (midX + curveRadius * Math.cos(angleRadians));
        float pointY        = (float) (midY + curveRadius * Math.sin(angleRadians));
        Point m3 = getMidPoint(new Point(x1,y1),new Point(x2,y2));



        p3.x = p3.x;
        p3.y = p3.y;
        angle= Math.abs(angle);
        // Draw the circle at (x,y) with radius 250


        int radius = 50;
        Log.d("RERRR","RRR="+angle);
        Log.d("RERRR","RRR="+angleRadians);
        RectF oval = new RectF(p3.x - radius, p3.y - radius, p3.x + radius, p3.y + radius);
        //canvas.drawArc(oval, -90, 90, false, paint);
        paint.setColor(Color.parseColor(color));
        canvas.drawArc(oval, (int)(90-angle), 151, false, paint);

    }

    private void drawK4Curve(int x1, int y1, int x2, int y2,String color) {
        Paint paint  = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor(color));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setAntiAlias(true);
        int radius = 20;
        paint.setStrokeCap(Paint.Cap.SQUARE);
        RectF oval = new RectF(x1 - radius, y1 - radius, x2 + radius, y2 + radius);
        paint.setColor(Color.parseColor(color));
        canvas.drawArc(oval, -(int)(90-angle), 151, false, paint);
    }

    private void drawDashedLines(Integer x, Integer y) {
        Paint mPaint = new Paint();
        mPaint.setARGB(255, 255, 255, 255);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setPathEffect(new DashPathEffect(new float[]{5, 5, 5, 5}, 0));
        canvas.drawLine(x,y-250,x,y+200,mPaint);
    }

    public Point getMidPoint(Point p1, Point p2) {
        return new Point((p1.x+p2.x)/2, (p1.y+p2.y)/2);
    }



    public void drawKf2KtoH(Point p1, Point p2) {
        drawLine("#00ff03", p1,new Point(p2.x,p2.y));
    }

    public void drawKf2AtoK(Point p1, Point p2) {
        drawLine("#00ff03", new Point(p1.x,p1.y), p2);
    }

    public void drawKf4HtoN(Point p1, Point p2) {
        drawLine("#00ff03", p1,new Point(p2.x,p2.y));
    }


    public void drawKf4HtoH(Point p1, Point p2) {
        drawLine("#00ff03", p1,new Point(p2.x,p2.y));
    }


    public void drawKf2Line(Point p1) {
        Point newP1 = normalisePoint(p1);
        drawDashedLines(newP1.x,newP1.y);
    }

    public void drawKf2AtoKAngle(Point p1, Point p2, Point p3) {
        Point newP1 = normalisePoint(p1);
        Point newP2 = normalisePoint(p2);
        Point newP3 = normalisePoint(p3);
        Point midPoint = getMidPoint(newP1,newP2);
        Point midPoint2 = getMidPoint(newP2,newP3);
        drawCurvedArrow(newP2.x,newP2.y,newP1.x,newP1.y,0,newP2,"#00ff03");
    }

    public void drawKf4AtoKAngle(Point p1, Point p2) {
        Point newP1 = normalisePoint(p1);
        Point newP2 = normalisePoint(p2);
        drawK4Curve(newP2.x,newP2.y,newP1.x,newP1.y,0,newP2,"#00ff03");
    }

    public static double angleBetweenPoints(Point a, Point b) {
        double angleA = angleFromOriginCounterClockwise(a);
        double angleB = angleFromOriginCounterClockwise(b);
        return Math.abs(angleA-angleB);
    }

    public static double angleFromOriginCounterClockwise(Point a) {
        double degrees = Math.toDegrees(Math.atan(a.y/a.x));
        if(a.x < 0.0) return degrees+180.0;
        else if(a.y < 0.0) return degrees+360.0;
        else return degrees;
    }

    public void drawBall(Point ap) {
        Point newP1 = normalisePoint(ap);
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        RectF oval = new RectF(newP1.x-50, newP1.y-10, newP1.x+50, newP1.y+40);
        canvas.drawOval(oval, paint);
        //canvas.drawCircle(newP1.x,newP1.y,30,paint);
    }

}
