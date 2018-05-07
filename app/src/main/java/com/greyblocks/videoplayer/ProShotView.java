package com.greyblocks.videoplayer;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.View;

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
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.exo_controls_previous, 0, 0, 0);
            proShotAction.onCollapse();

        } else {
            setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.exo_controls_next, 0);
            shown = true;
            proShotAction.onExpand();
        }
    }
}
