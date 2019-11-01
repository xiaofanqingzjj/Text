package com.example.test.card;

import android.content.res.TypedArray;
import android.graphics.Outline;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.Dimension;

import com.example.test.R;

class MaterialCardViewHelper {

    private static final int DEFAULT_STROKE_VALUE = -1;
    private final MaterialCardView materialCardView;

    private int strokeColor;
    private int strokeWidth;

    private float cornerTopLeft = 0;
    private float cornerTopRight = 0;
    private float cornerBottomLeft = 0;
    private float cornerBottomRight = 0;

    public MaterialCardViewHelper(MaterialCardView card) {
        this.materialCardView = card;
    }

    public void loadFromAttributes(TypedArray attributes) {
        this.strokeColor = attributes.getColor(R.styleable.MaterialCardView_mcv_border_color, -1);
        this.strokeWidth = attributes.getDimensionPixelSize(R.styleable.MaterialCardView_mcv_border_width, 0);

        cornerTopLeft = attributes.getDimensionPixelOffset(R.styleable.MaterialCardView_mcv_corner_top_left, 0);
        cornerTopRight = attributes.getDimensionPixelOffset(R.styleable.MaterialCardView_mcv_corner_top_right, 0);
        cornerBottomLeft = attributes.getDimensionPixelOffset(R.styleable.MaterialCardView_mcv_corner_bottom_left, 0);
        cornerBottomRight = attributes.getDimensionPixelOffset(R.styleable.MaterialCardView_mcv_corner_bottom_right, 0);


        this.updateForeground();
        this.adjustContentPadding();
    }

    void setStrokeColor(@ColorInt int strokeColor) {
        this.strokeColor = strokeColor;
        this.updateForeground();
    }

    @ColorInt
    int getStrokeColor() {
        return this.strokeColor;
    }

    void setStrokeWidth(@Dimension int strokeWidth) {
        this.strokeWidth = strokeWidth;
        this.updateForeground();
        this.adjustContentPadding();
    }

    @Dimension
    int getStrokeWidth() {
        return this.strokeWidth;
    }

    void updateForeground() {
        Drawable drawable = this.createForegroundDrawable();
        this.materialCardView.setBackground(drawable);
        this.materialCardView.setForeground(drawable);

        View view = materialCardView;
        view.setClipToOutline(true);

    }

    static class BackgroundDrawable extends GradientDrawable {

        float cornerTopLeft = 0;
        float cornerTopRight = 0;
        float cornerBottomLeft = 0;
        float cornerBottomRight = 0;

        float radius = 0;

        @Override
        public void getOutline(Outline outline) {
            super.getOutline(outline);

            Path path = new Path();

            Rect b = getBounds();
            if (cornerTopLeft != 0
                    || cornerTopRight != 0
                    || cornerBottomLeft != 0
                    || cornerBottomRight != 0) {

                RectF rectF = new RectF();
                rectF.left = b.left;
                rectF.top = b.top;
                rectF.right = b.right;
                rectF.bottom = b.bottom;
                path.addRoundRect(rectF, new float[] {cornerTopLeft, cornerTopLeft, cornerTopRight, cornerTopRight, cornerBottomRight, cornerBottomRight, cornerBottomLeft, cornerBottomLeft}, Path.Direction.CW);


                path.close();

                boolean isConvex = path.isConvex();
                Log.d(TAG, "isConvex:" + isConvex);

                outline.setConvexPath(path);
            } else {

                outline.setRoundRect(b, radius);

//                fgDrawable.setCornerRadius(this.materialCardView.getRadius());
            }

//            path.addRoundRect(getBounds(), new float[] {cornerTopLeft, cornerTopLeft, cornerTopRight, cornerTopRight, cornerBottomRight, cornerBottomRight, cornerBottomLeft, cornerBottomLeft});
        }

        static final String TAG = "BackgroundDrawable";
    }



    private Drawable createForegroundDrawable() {

        BackgroundDrawable fgDrawable = new BackgroundDrawable();

        float radius = this.materialCardView.getRadius();

        if (cornerTopLeft != 0
                || cornerTopRight != 0
                || cornerBottomLeft != 0
                || cornerBottomRight != 0) {
            fgDrawable.setCornerRadii(new float[] {cornerTopLeft, cornerTopLeft, cornerTopRight, cornerTopRight, cornerBottomRight, cornerBottomRight, cornerBottomLeft, cornerBottomLeft});
        } else {
            fgDrawable.setCornerRadius(this.materialCardView.getRadius());
        }

        fgDrawable.cornerTopLeft = cornerTopLeft;
        fgDrawable.cornerTopRight = cornerTopRight;
        fgDrawable.cornerBottomLeft = cornerBottomLeft;
        fgDrawable.cornerBottomRight = cornerBottomRight;
        fgDrawable.radius = radius;



        if (this.strokeColor != -1) {
            fgDrawable.setStroke(this.strokeWidth, this.strokeColor);
        }

        return fgDrawable;
    }

    private void adjustContentPadding() {
        int contentPaddingLeft = this.materialCardView.getContentPaddingLeft() + this.strokeWidth;
        int contentPaddingTop = this.materialCardView.getContentPaddingTop() + this.strokeWidth;
        int contentPaddingRight = this.materialCardView.getContentPaddingRight() + this.strokeWidth;
        int contentPaddingBottom = this.materialCardView.getContentPaddingBottom() + this.strokeWidth;
        this.materialCardView.setContentPadding(contentPaddingLeft, contentPaddingTop, contentPaddingRight, contentPaddingBottom);
    }
}
