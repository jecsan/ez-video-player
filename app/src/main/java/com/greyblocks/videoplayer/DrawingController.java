package com.greyblocks.videoplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import api.models.Assessments;
import api.models.Texttips;

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
    private Context context;
    List<Assessments> assessments;
    List<Texttips> texttips;

    String success = "#00ff03";
    String danger = "#ff0000";

    public DrawingController(Display dis, List<Assessments> assessments, List<Texttips> texttips, Context context) {
        this.deviceWidth = dis.getWidth();
        this.deviceHeight = dis.getHeight();
        this.assessments = assessments;
        this.texttips = texttips;
        this.context = context;
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
        Point newP1 = normalisePoint(p1);
        Point newP2 = normalisePoint(p2);
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
        int midX            = x1 + ((x2 - x1) / 2);
        int midY            = y1 + ((y2 - y1) / 2);
        float xDiff         = midX - x1;
        float yDiff         = midY - y1;
        double angle        = (Math.atan2(yDiff, xDiff) * (180 / Math.PI)) - 90;
        angle= Math.abs(angle);
        int radius = 50;
        RectF oval = new RectF(p3.x - radius, p3.y - radius, p3.x + radius, p3.y + radius);
        paint.setColor(Color.parseColor(color));
        canvas.drawArc(oval, (int)(90-angle), curveRadius, false, paint);
        Paint textPaint= new Paint();
        textPaint.setColor(Color.parseColor(color));
        textPaint.setTextSize(22);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setAntiAlias( true );
        canvas.drawText(Integer.toString(curveRadius)+"°\n", x1-30,midY-50,textPaint(color));
    }

    public void drawReversedCurvedArrow(int x1, int y1, int x2, int y2, int curveRadius, Point p3, String color) {
        Paint paint  = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor(color));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.SQUARE);
        int midX            = x1 + ((x2 - x1) / 2);
        int midY            = y1 + ((y2 - y1) / 2);
        float xDiff         = midX - x1;
        float yDiff         = midY - y1;
        double angle        = (Math.atan2(yDiff, xDiff) * (180 / Math.PI)) - 90;
        angle= Math.abs(angle);
        int radius = 50;
        RectF oval = new RectF(p3.x - radius, p3.y - radius, p3.x + radius, p3.y + radius);
        paint.setColor(Color.parseColor(color));
        canvas.drawArc(oval, 90, curveRadius, false, paint);
        canvas.drawText(Integer.toString(curveRadius)+"°\n", p3.x-30,p3.y+90,textPaint(color));
    }

    private void drawK4Curve( int x2, int y2,String color, Integer angle) {
        Paint paint  = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor(color));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setAntiAlias(true);
        int radius = 20;
        paint.setStrokeCap(Paint.Cap.SQUARE);
        RectF oval = new RectF(x2 - radius, y2 - radius, x2 + radius, y2 + radius);
        paint.setColor(Color.parseColor(color));
        canvas.drawArc (oval, -90, angle, false, paint);
        canvas.drawText(Integer.toString(angle)+"°\n", x2-10,y2-30,textPaint(color));
    }

    private void drawK4HCurve(int x1, int y1,String color, Integer angle) {
        Paint paint  = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor(color));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setAntiAlias(true);
        int radius = 20;
        paint.setStrokeCap(Paint.Cap.SQUARE);
        RectF oval = new RectF(x1 - radius, y1 - radius, x1 + radius, y1 + radius);
        paint.setColor(Color.parseColor(color));
        canvas.drawArc (oval, 90, angle, false, paint);
        canvas.drawText(Integer.toString(angle)+"°\n", x1-40,y1+20,textPaint(color));
    }

    public Paint textPaint(String color) {
        Paint textPaint= new Paint();
        textPaint.setColor(Color.parseColor(color));
        textPaint.setTextSize(22);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setAntiAlias( true );
        return textPaint;
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



    public void drawKf2KtoH(Point p1, Point p2, Integer angle) {

        String assessment = "Good";
        String color = "Good";
        String tip = "";
        String textlabel = "";
        Rect rect;
        color = getColor(assessment);
        Point newP1 = normalisePoint(p1);
        Point newP2 = normalisePoint(p2);

        for (Assessments obj : assessments) {
            if (new String(obj.getKey()).equals("keyframe2/kickleg/angles/u2v-i")) {
                assessment = obj.getLabel();
            }
        }


        if (new String(assessment).equals("Good")) {
            textlabel = "keyframe2a_good";
            rect =  new Rect(newP1.x-220, newP1.y+135, newP1.x, newP1.y+30);
        } else {
            textlabel = "keyframe2a";
            rect =  new Rect(newP1.x-220, newP1.y+110, newP1.x, newP1.y+30);
        }

        for (Texttips obj : texttips) {
            if (new String(obj.getKey()).equals(textlabel)) {
                tip = obj.getValue();
            }
        }
        color = getColor(assessment);
        tip = tip.replaceAll("\\-", "");
        drawReversedCurvedArrow(newP2.x,newP2.y,newP1.x,newP1.y,angle,newP2,color);
        drawLine(color, new Point(p1.x,p1.y), p2);
        Point tPoint = new Point(newP1.x-160, newP1.y+40 );
        drawTooltip(color,tPoint,tip,rect,150);
    }

    public String getColor(String assessment) {
        if (new String(assessment).equals("Good")) {
            return success;
        } else {
            return danger;
        }
    }

    public void drawKf2AtoK(Point p1, Point p2, Integer angle) {
        String assessment = "Good";
        String color = "Good";
        String tip = "";
        String textlabel = "keyframe2b_good";

        for (Assessments obj : assessments) {
            if (new String(obj.getKey()).equals("keyframe2/kickleg/angles/u2l-e")) {
                assessment = obj.getLabel();
            }
        }

        if (new String(assessment).equals("Good")) {
            textlabel = "keyframe2b_good";
        } else {
            textlabel = "keyframe2b";
        }

        for (Texttips obj : texttips) {
            if (new String(obj.getKey()).equals(textlabel)) {
                tip = obj.getValue();
            }
        }

        tip = tip.replaceAll("\\-", "");
        color = getColor(assessment);
        Point newP1 = normalisePoint(p1);
        Point newP2 = normalisePoint(p2);
        drawCurvedArrow(newP2.x,newP2.y,newP1.x,newP1.y,angle,newP2,color);
        drawLine(color, new Point(p1.x,p1.y), p2);
        Rect rect =  new Rect(newP1.x-220, newP1.y-130, newP1.x, newP1.y-50);
        Point tPoint = new Point(newP1.x-160, newP1.y-120 );
        drawTooltip(color,tPoint,tip,rect,150);
    }

    public void drawTooltip(String color, Point p1, String tip, Rect rect, Integer maxWidth) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.GRAY);
        paint.setColor(Color.parseColor(color));
        paint.setAlpha(190);
        canvas.drawRect(rect, paint);
        Paint textPaint= new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(18);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setAntiAlias( true );
        TextRect textRect = new TextRect( textPaint );
        final int h = textRect.prepare(
                "TIP: "+tip,
                maxWidth ,
                100);
        textRect.draw( canvas, p1.x,p1.y);
        drawImage(color,p1);
    }

    public void drawKf4HtoN(Point p1, Point p2, Integer angle) {
        String assessment = "Good";
        String color = "Good";
        String tip = "";
        String textlabel = "keyframe2b_good";

        for (Assessments obj : assessments) {
            if (new String(obj.getKey()).equals("keyframe4/body/angles/h2v")) {
                assessment = obj.getLabel();
            }
        }

        if (new String(assessment).equals("Good")) {
            textlabel = "keyframe4a_good";
        } else {
            textlabel = "keyframe4a";
        }

        for (Texttips obj : texttips) {
            if (new String(obj.getKey()).equals(textlabel)) {
                tip = obj.getValue();
            }
        }

        tip = tip.replaceAll("\\-", "");
        color = getColor(assessment);
        Point newP1 = normalisePoint(p1);
        Point newP2 = normalisePoint(p2);
        drawLine(color, p1,new Point(p2.x,p2.y));
        Rect rect =  new Rect(newP1.x+250, newP1.y+40, newP1.x+40, newP1.y-40);
        Point tPoint = new Point(newP1.x+100, newP1.y-30 );
        drawTooltip(color,tPoint,tip,rect,150);
        drawK4Curve(newP1.x,newP1.y,color,angle);
    }


    public void drawKf4HtoH(Point p1, Point p2, Integer angle) {

        String assessment = "Good";
        String color = "Good";
        String tip = "";
        String textlabel = "keyframe2b_good";

        for (Assessments obj : assessments) {
            if (new String(obj.getKey()).equals("keyframe4/body/angles/b2v")) {
                assessment = obj.getLabel();
            }
        }

        if (new String(assessment).equals("Good")) {
            textlabel = "keyframe4b_good";
        } else {
            textlabel = "keyframe4b";
        }

        for (Texttips obj : texttips) {
            if (new String(obj.getKey()).equals(textlabel)) {
                tip = obj.getValue();
            }
        }

        tip = tip.replaceAll("\\-", "");
        color = getColor(assessment);
        Point newP1 = normalisePoint(p2);
        Point anglePoint = new Point(p1.x,p1.y+20);
        Point newP2 = normalisePoint(anglePoint);
        drawLine(color, p1,new Point(p2.x,p2.y));
        Rect rect =  new Rect(newP1.x-255, newP1.y+80, newP1.x-40, newP1.y);
        Point tPoint = new Point(newP1.x-190, newP1.y+10 );
        drawTooltip(color,tPoint,tip,rect,150);
        drawK4HCurve(newP2.x,newP2.y,color,angle);
        //drawTooltip(color,tPoint,tip,rect,150);
        //drawKf4HtoHAngle(newP2.x,newP2.y,newP1.x,newP1.y,color,angle);

        //drawLine("#00ff03", p1,new Point(p2.x,p2.y));
    }


    public void drawKf2Line(Point p1) {
        Point newP1 = normalisePoint(p1);
        drawDashedLines(newP1.x,newP1.y);
    }

    public void drawKf2AtoKAngle(Point p1, Point p2, Point p3, Integer angle) {
        Point newP1 = normalisePoint(p1);
        Point newP2 = normalisePoint(p2);
        Point newP3 = normalisePoint(p3);
        drawCurvedArrow(newP2.x,newP2.y,newP1.x,newP1.y,angle,newP2,"#00ff03");
    }


    public void drawKf4HtoHAngle(Point p1, Integer angle) {
        Point newP1 = normalisePoint(p1);
        drawK4HCurve(newP1.x,newP1.y,"#00ff03",angle);
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

        String assessment = "Good";
        String color = "";
        String tip = "";
        String textlabel = "keyframe3_good";

        for (Assessments obj : assessments) {
            if (new String(obj.getKey()).equals("keyframe3/ball")) {
                assessment = obj.getLabel();
            }
        }

        if (new String(assessment).equals("Good")) {
            textlabel = "keyframe3_good";
        } else {
            textlabel = "keyframe3";
        }

        for (Texttips obj : texttips) {
            if (new String(obj.getKey()).equals(textlabel)) {
                tip = obj.getValue();
            }
        }

        tip = tip.replaceAll("\\-", "");

        Point newP1 = normalisePoint(ap);
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        RectF oval = new RectF(newP1.x-60, newP1.y-20, newP1.x+60, newP1.y+40);
        canvas.drawOval(oval, paint);

        Rect rect =  new Rect(newP1.x-260, newP1.y-130, newP1.x-20, newP1.y-50);
        Point tPoint = new Point(newP1.x-200, newP1.y-120 );
        drawTooltip(getColor(assessment),tPoint,tip,rect,180);

        //canvas.drawCircle(newP1.x,newP1.y,30,paint);
    }

    public void drawImage(String assessment, Point p) {
        String img = "tip.png";
        Paint paint=new Paint();
        if (new String(assessment).equals(danger)) {
            img = "poor.png";
        }
        try {
            InputStream istr = context.getAssets().open(img);
            Bitmap bitmap = BitmapFactory.decodeStream(istr);
            Bitmap newBtmp = bitmap.createScaledBitmap(bitmap, 40,40, false);
            canvas.drawBitmap(newBtmp,p.x-50,p.y+10,paint);
        } catch (IOException e) {
            // handle exception
        }
    }

    public void drawProshot() {
        String img = "ProShotKF2@2x.png";
        Paint paint=new Paint();
        try {
            InputStream istr = context.getAssets().open(img);
            Bitmap bitmap = BitmapFactory.decodeStream(istr);
            Bitmap newBtmp = bitmap.createScaledBitmap(bitmap, 600,638, false);
            canvas.drawBitmap(newBtmp,1200,0,paint);
        } catch (IOException e) {
            // handle exception
        }
    }

}
