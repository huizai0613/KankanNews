package com.kankan.kankanews.ui.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class MultiImageView extends ImageView {

    public MultiImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MultiImageView(Context context) {
        super(context);
    }

    public MultiImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        start();
    }

    private void start() {
        post(new Runnable() {
            
            @Override
            public void run() {
                Drawable drawable = getDrawable();
                if(drawable instanceof AnimationDrawable){
                    ((AnimationDrawable) drawable).start();
                }
            }
        });
    }
    

}
