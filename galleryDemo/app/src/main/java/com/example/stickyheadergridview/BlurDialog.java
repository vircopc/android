package com.example.stickyheadergridview;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;



public class BlurDialog extends Dialog {

    protected BlurView mBlurView;
    private boolean mIsBlur;

    public BlurDialog(@NonNull Context context,boolean isBlur) {
        super(context, R.style.MyDialog);
        init(context);
        mIsBlur = isBlur;
    }

    public BlurDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    protected BlurDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    public void setMatchParent(){
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    private void init(Context context){
        Activity activity = getActivityFromContext(context);
        if(activity == null){
            Log.e("BlurDialog", "context is not a Activity Context......");
            return;
        }
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        mBlurView = decorView.findViewById(R.id.blur_dialog_bg);
        if(mBlurView == null){
            mBlurView = new BlurView(activity);
            mBlurView.setId(R.id.blur_dialog_bg);
            mBlurView.setForeground(new ColorDrawable(Color.parseColor("#59000000")));
            decorView.addView(mBlurView, new ViewGroup.LayoutParams(-1,-1));
        }
    }

    private Activity getActivityFromContext(Context context){
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity)context;
            }
            context = ((ContextWrapper)context).getBaseContext();
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(mBlurView != null && mIsBlur) {
            mBlurView.blur();
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(mBlurView != null) {
            mBlurView.show();
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(mBlurView != null) {
            mBlurView.hide();
        }
    }
}
