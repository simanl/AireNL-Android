package com.icalialabs.airenl.CustomViews;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * ImageView to display top-crop scale of an image view.
 *
 * @author Chris Arriola
 */
public class TopCropImageView extends ImageView {

    WeakReference viewContext;

    public TopCropImageView(Context context) {
        super(context);
        viewContext = new WeakReference<Context>(context);
        setScaleType(ScaleType.MATRIX);
    }

    public TopCropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        viewContext = new WeakReference<Context>(context);
        setScaleType(ScaleType.MATRIX);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        recomputeImgMatrix();
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        recomputeImgMatrix();
        return super.setFrame(l, t, r, b);
    }

    private void recomputeImgMatrix() {
        final Matrix matrix = getImageMatrix();

        float scale;
        final int viewWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        final int viewHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        final int drawableWidth = getDrawable().getIntrinsicWidth();
        final int drawableHeight = getDrawable().getIntrinsicHeight();

        if (drawableWidth * viewHeight > drawableHeight * viewWidth) {
            scale = (float) viewHeight / (float) drawableHeight;
        } else {
            scale = (float) viewWidth / (float) drawableWidth;
        }

        matrix.setScale(scale, scale);
        setImageMatrix(matrix);
    }
}
