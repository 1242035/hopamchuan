package com.hac.android.helper.widget.alpha;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * A custom layout for dialogs that need to be stretched to the full height of the screen.
 * It overrides the height measure specification to ignore "wrap_content" and
 * do "match_parent" instead.  The "wrap_content" part is hard-coded in the framework
 * implementation of the dialog theme.
 */
public final class FullHeightLinearLayout extends LinearLayout {

    public FullHeightLinearLayout(Context context) {
        super(context);
    }

    public FullHeightLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public FullHeightLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                    MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
