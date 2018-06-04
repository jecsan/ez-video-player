package com.greyblocks.videoplayer;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ProShotView extends RelativeLayout implements View.OnClickListener {

    private boolean shown = false;
    private ProShotAction proShotAction;
    private ImageView expandBtn;
    private ImageView collapseBtn;
    private int halfScreenWidth;


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
        halfScreenWidth = getContext().getResources().getDisplayMetrics().widthPixels/2;
        inflate(getContext(), R.layout.proshot_view, this);
        expandBtn = findViewById(R.id.expand_ib);
        collapseBtn = findViewById(R.id.collapse_ib);
//        expandBtn.setOnClickListener(this);
//        collapseBtn.setOnClickListener(this);
        collapseBtn.setVisibility(View.GONE);
        super.setOnClickListener(this);
    }

    private void onCollapsed() {
        expandBtn.setVisibility(View.VISIBLE);
        collapseBtn.setVisibility(View.GONE);
        proShotAction.onExpand();
    }

    private void onExpanded() {
        expandBtn.setVisibility(View.GONE);
        collapseBtn.setVisibility(View.VISIBLE);
        proShotAction.onCollapse();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.expand_ib:
                onExpanded();
                shown = true;
                break;
            case R.id.collapse_ib:
                onCollapsed();
                shown = false;
                break;
            default:
                if (shown) {
                    shown = false;
                    onCollapsed();
                    proShotAction.onCollapse();

                } else {
                    shown = true;
                    onExpanded();
                    proShotAction.onExpand();
                }
                break;
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int chosenDimension = resolveSize(halfScreenWidth, heightMeasureSpec);

        widthMeasureSpec = MeasureSpec.makeMeasureSpec(chosenDimension, MeasureSpec.AT_MOST);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(chosenDimension, MeasureSpec.AT_MOST);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private int reconcileSize(int contentSize, int measureSpec) {
        final int mode = MeasureSpec.getMode(measureSpec);
        final int specSize = MeasureSpec.getSize(measureSpec);
        switch(mode) {
            case MeasureSpec.EXACTLY:
                return specSize;
            case MeasureSpec.AT_MOST:
                if (contentSize < specSize) {
                    return contentSize;
                } else {
                    return specSize;
                }
            case MeasureSpec.UNSPECIFIED:
            default:
                return contentSize;
        }
    }

}