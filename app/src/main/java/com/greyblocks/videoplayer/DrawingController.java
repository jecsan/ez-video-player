package com.greyblocks.videoplayer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
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

    private Integer videoWidth = 720;
    private Integer videoHeight = 1280;
    private Integer deviceWidth = 0;
    private Integer deviceHeight = 0;
    private Double screenRatio = 0.0;
    private Canvas canvas;
    private Bitmap bg;

    public DrawingController(Display dis) {
        this.deviceWidth = dis.getWidth();
        this.deviceHeight = dis.getHeight();
        bg = Bitmap.createBitmap(videoWidth, videoHeight, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bg);
    }

    public Bitmap getBitmap() {
        return this.bg;
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
        canvas.drawLine(newP1.x,newP1.y,newP2.x,newP2.y,paint);
    }

    public Point normalisePoint(Point point) {
        Point newPoint = new Point(0,0);
        Float newX = (((float)point.x/(float)videoHeight)*(float)deviceHeight)+80;
        Float newY = ((float)((float)point.y/(float)videoWidth)*(float)deviceWidth)+190;
        newPoint.set(newX.intValue(),newY.intValue());
        return newPoint;
    }


    public void drawCurvedArrow(int x1, int y1, int x2, int y2, int curveRadius, Point p3) {

        Paint paint  = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
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

        path.moveTo(x1, y1);
        path.cubicTo(x1,y1-5,p3.x+30, p3.y+300, x2, y2-5);
        canvas.drawPath(path, paint);
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


    public void drawKf2AtoK(Point p1, Point p2) {
        drawLine("#00ffff", new Point(p1.x-10,p1.y-5), p2);
    }


    public void drawKf2KtoH(Point p1, Point p2) {
        drawLine("#00ff03", p1,new Point(p2.x+10,p2.y-5));
    }

    public void drawKf2Line(Point p1) {
        Point newP1 = normalisePoint(p1);
        drawDashedLines(newP1.x+10,newP1.y);
    }

    public void drawKf2AtoKAngle(Point p1, Point p2, Point p3) {
        Point newP1 = normalisePoint(p1);
        Point newP2 = normalisePoint(p2);
        Point newP3 = normalisePoint(p3);
        Point midPoint = getMidPoint(newP1,newP2);
        Point midPoint2 = getMidPoint(newP2,newP3);
        drawCurvedArrow(midPoint.x,midPoint.y,midPoint2.x,midPoint2.y,30,p2);
    }

}
