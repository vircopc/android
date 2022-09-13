package com.bbg.textalbum;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by administrator on 2017/10/19.
 */

public class SquareImg extends ImageView {
    public SquareImg(Context context) {
        super(context);
    }

    public SquareImg(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImg(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SquareImg(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0,widthMeasureSpec),getDefaultSize(0,heightMeasureSpec));
        int childwid=getMeasuredWidth();
        heightMeasureSpec=widthMeasureSpec=MeasureSpec.makeMeasureSpec(childwid,MeasureSpec.EXACTLY);


        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
