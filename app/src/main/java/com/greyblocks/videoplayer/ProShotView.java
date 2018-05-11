package com.greyblocks.videoplayer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.View;

import java.io.IOException;

public class ProShotView extends AppCompatButton implements View.OnClickListener {

    private boolean shown = false;
    private ProShotAction proShotAction;

    public void setProShotAction(ProShotAction proShotAction) {
        this.proShotAction = proShotAction;
    }

    public interface ProShotAction{
        void onExpand();
        void onCollapse();
    }

    public ProShotView(Context context) {
        super(context);
        init();
    }

    public ProShotView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProShotView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {

    }

    private void init() {
        setCompoundDrawablesWithIntrinsicBounds(R.drawable.exo_controls_previous, 0, 0, 0);
        super.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (shown) {
            shown = false;
            //setBackgroundColor(getResources().getColor(android.R.color.transparent));

        } else {
//            setCompoundDrawablesWithIntrinsicBounds(R.drawable.exo_controls_previous, 0, 0, 0);
//            Context context = getContext();
//            Drawable d = null;
//            try {
//                d = Drawable.createFromStream(context.getAssets().open("proshot/kf3.png"), null);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            setBackground(d);
            proShotAction.onCollapse();

            setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.exo_controls_next, 0);
            shown = true;
            proShotAction.onExpand();
        }
    }
}
