package com.bedrock.module_base.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatImageView;


import com.bedrock.module_base.R;

import java.util.HashMap;
import java.util.Map;


/**
 *
 * 一个功能扩展功能比较多的ImageView
 *
 * 1.系统的ImageView只支持CenterCrop, 这个ImageView支持各个方向的Crop
 * 2.支持各个方向的fit
 * 3.支持圆角，包括各个方法的圆角
 * 4.支持边框
 *
 * @author fortunexiao
 */
public class SuperImageView extends AppCompatImageView {

    static final String TAG = "CropImageView";

    private ScaleType cropType = ScaleType.NONE;

    public SuperImageView(Context context) {
        super(context);

        initImageView(null);
    }

    public SuperImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SuperImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        parseAttributes(attrs);
        initImageView(attrs);
    }


    /**
     * Set crop type for the {@link ImageView}
     *
     * @param cropType A {@link ScaleType} desired scaling mode.
     */
    public void setCropType(ScaleType cropType) {
        if (cropType == null) {
            throw new NullPointerException();
        }

        if (this.cropType != cropType) {
            this.cropType = cropType;

            setWillNotCacheDrawing(false);

            requestLayout();
            invalidate();
        }
    }

    private int  cornerRadius;

    public void setCornerRadius(int cornerRadius) {
        this.cornerRadius = cornerRadius;
        applyCorner();

    }


    private void applyCorner() {
        if (cornerRadius != 0) {
            setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, getWidth(), getHeight(), cornerRadius);
                }
            });
        }
    }

    /**
     * Return the current crop type in use by this ImageView.
     *
     * @return a {@link ScaleType} in use by this ImageView
     */
    public ScaleType getCropType() {
        return cropType;
    }

    RoundedImageHelper roundedImageHelper;

    private void initImageView( AttributeSet attrs) {
//        imageMaths = new ImageMathsFactory().getImageMaths(this);

//        setClipToOutline(true);

        roundedImageHelper = new RoundedImageHelper(this,  attrs);
    }

    private void parseAttributes(AttributeSet attrs) {
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SuperImageView);

        final int crop = a.getInt(R.styleable.SuperImageView_crop, ScaleType.NONE.getCrop());
        if (crop >= 0) {

            // scaleType为matrix
            setScaleType(ImageView.ScaleType.MATRIX);

            cropType = ScaleType.get(crop);
        }
        int cornerRadius = a.getDimensionPixelSize(R.styleable.SuperImageView_civ_corner_radius, 0);
        setCornerRadius(cornerRadius);
        a.recycle();
    }

    @Override
    public void draw(Canvas canvas) {
//        super.draw(canvas);
        if (roundedImageHelper != null) {
            roundedImageHelper.draw(canvas);
        } else {
            super.draw(canvas);
        }
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        final boolean changed = super.setFrame(l, t, r, b);
        if (!isInEditMode() && getDrawable() != null) {
            computeImageMatrix();
        }
        return changed;
    }



    private void computeImageMatrix() {

        // view的大小
        final int viewWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        final int viewHeight = getHeight() - getPaddingTop() - getPaddingBottom();

        if (cropType != ScaleType.NONE && viewHeight > 0 && viewWidth > 0) {
            final Matrix matrix = getImageMatrix(); //imageMaths.getMatrix();

            // 图片大小
            final int drawableWidth = getDrawable().getIntrinsicWidth();
            final int drawableHeight = getDrawable().getIntrinsicHeight();

            Log.d(TAG, "computeImageMatrix:" + drawableWidth + "x" + drawableHeight);

            if (drawableHeight == -1 || drawableWidth == -1) {
                postInvalidate();
                return;
            }

            final float scaleY = (float) viewHeight / (float) drawableHeight;
            final float scaleX = (float) viewWidth / (float) drawableWidth;

            // 设置缩放，匹配短的边，长的边超过的裁剪
            float scale = -1;

            if (cropType.getCrop() <= 7) { // crop
                scale = scaleX > scaleY ? scaleX : scaleY;
            } else { // fit
                scale = scaleX < scaleY ? scaleX : scaleY;
            }


//                    scaleX > scaleY ? scaleX : scaleY;
            matrix.setScale(scale, scale); // Same as doing matrix.reset() and matrix.preScale(...)

            // 竖图
            final boolean verticalImageMode = scaleX > scaleY;

            // 计算缩放之后的目标宽度
            final float postDrawableWidth = drawableWidth * scale;

            // 设置X方向的平移
            final float xTranslation = getXTranslation(cropType, viewWidth, postDrawableWidth, verticalImageMode);

            // 缩放之后的高度
            final float postDrawableHeight = drawableHeight * scale;

            // Y方向的平移
            final float yTranslation = getYTranslation(cropType, viewHeight, postDrawableHeight, verticalImageMode);

//            matrix.setTranslate();
            matrix.postTranslate(xTranslation, yTranslation);
            setImageMatrix(matrix);
        }
    }

    private static float getYTranslation(ScaleType cropType, int viewHeight, float postDrawabeHeigth, boolean verticalImageMode) {
        if (verticalImageMode) {
            switch (cropType) {
                case CROP_CENTER_BOTTOM: //底部对齐
                case CROP_LEFT_BOTTOM:
                case CROP_RIGHT_BOTTOM:

                case FIT_BOTTOM_LEFT:
                case FIT_BOTTOM_CENTER:
                case FIT_BOTTOM_RIGHT:

                    return viewHeight - postDrawabeHeigth;
                case CROP_LEFT_CENTER:
                case CROP_RIGHT_CENTER:
                    // View in the middle of the screen
                    return (viewHeight - postDrawabeHeigth) / 2f;

                    //

            }
        }

        // All other cases we don't need to translate
        return 0;
    }

    private static float getXTranslation(ScaleType cropType, int viewWidth, float postDrawableWidth, boolean verticalImageMode) {
        if (!verticalImageMode) {
            switch (cropType) {
                case CROP_RIGHT_TOP:
                case CROP_RIGHT_CENTER:
                case CROP_RIGHT_BOTTOM:

                case FIT_TOP_RIGHT:
                case FIT_BOTTOM_RIGHT:

                    return viewWidth - postDrawableWidth;
                case CROP_CENTER_TOP:
                case CROP_CENTER_BOTTOM:

                case FIT_TOP_CENTER:
                case FIT_BOTTOM_CENTER:
                    // View in the middle of the screen
                    return (viewWidth - postDrawableWidth) / 2f;

            }
        }
        // All other cases we don't need to translate
        return 0;
    }

    /**
     * Options for cropping the bounds of an image to the bounds of this view.
     */
    public static enum ScaleType {
        NONE(-1),

        // crop
        CROP_LEFT_TOP(0),
        CROP_LEFT_CENTER(1),
        CROP_LEFT_BOTTOM(2),

        CROP_RIGHT_TOP(3),
        CROP_RIGHT_CENTER(4),
        CROP_RIGHT_BOTTOM(5),

        CROP_CENTER_TOP(6),
        CROP_CENTER_BOTTOM(7),

        // fit

        FIT_TOP_LEFT(8),
        FIT_TOP_CENTER(9),
        FIT_TOP_RIGHT(10),

        FIT_BOTTOM_LEFT(11),
        FIT_BOTTOM_CENTER(12),
        FIT_BOTTOM_RIGHT(13);


        final int cropType;

        // Reverse-lookup map for getting a day from an abbreviation
        private static final Map<Integer, ScaleType> codeLookup = new HashMap<>();

        static {
            for (ScaleType ft : ScaleType.values()) {
                codeLookup.put(ft.getCrop(), ft);
            }
        }

        ScaleType(int ct) {
            cropType = ct;
        }

        public int getCrop() {
            return cropType;
        }

        public static ScaleType get(int number) {
            return codeLookup.get(number);
        }
    }
}