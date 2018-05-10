package com.greyblocks.videoplayer;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class ProShotView extends RelativeLayout implements View.OnClickListener {

    private boolean shown = false;
    private ProShotAction proShotAction;
    private ImageButton expandBtn;
    private ImageButton collapseBtn;


    public void setProShotAction(ProShotAction proShotAction) {
        this.proShotAction = proShotAction;
    }

    public interface ProShotAction {
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
        //Do nothing
    }

    public void setProShotDrawable(int res) {
        setBackgroundResource(res);
    }

    private void init() {
        inflate(getContext(), R.layout.proshot_view, this);
        expandBtn = findViewById(R.id.expand_ib);
        collapseBtn = findViewById(R.id.collapse_ib);
        expandBtn.setOnClickListener(this);
        collapseBtn.setOnClickListener(this);
        collapseBtn.setVisibility(View.GONE);
        super.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.expand_ib:
                proShotAction.onExpand();
                expandBtn.setVisibility(View.GONE);
                collapseBtn.setVisibility(View.VISIBLE);
                shown = true;
                break;
            case R.id.collapse_ib:
                proShotAction.onCollapse();
                expandBtn.setVisibility(View.VISIBLE);
                collapseBtn.setVisibility(View.GONE);
                shown = false;
                break;
            default:
                if (shown) {
                    shown = false;
                    proShotAction.onCollapse();

                } else {
                    shown = true;
                    proShotAction.onExpand();
                }
                break;
        }

    }
}
