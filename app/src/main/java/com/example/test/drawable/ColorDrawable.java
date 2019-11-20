
package com.example.test.drawable;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.view.ViewDebug;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.example.test.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class ColorDrawable extends Drawable {
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);


    @ViewDebug.ExportedProperty(deepExport = true, prefix = "state_")
    private ColorState mColorState;

    private PorterDuffColorFilter mTintFilter;

    private boolean mMutated;

    /**
     * Creates a new black ColorDrawable.
     */
    public ColorDrawable() {
        mColorState = new ColorState();
    }

    /**
     * Creates a new ColorDrawable with the specified color.
     *
     * @param color The color to draw.
     */
    public ColorDrawable(@ColorInt int color) {
        mColorState = new ColorState();

        setColor(color);
    }

    @Override
    public int getChangingConfigurations() {
        return super.getChangingConfigurations() | mColorState.getChangingConfigurations();
    }

    /**
     * A mutable BitmapDrawable still shares its Bitmap with any other Drawable
     * that comes from the same resource.
     *
     * @return This drawable.
     */
    @Override
    public Drawable mutate() {
        if (!mMutated && super.mutate() == this) {
            mColorState = new ColorState(mColorState);
            mMutated = true;
        }
        return this;
    }

    /**
     * @hide
     */
    public void clearMutated() {
        super.clearMutated();
        mMutated = false;
    }

    @Override
    public void draw(Canvas canvas) {

        final ColorFilter colorFilter = mPaint.getColorFilter();

        if ((mColorState.mUseColor >>> 24) != 0 || colorFilter != null || mTintFilter != null) {
            if (colorFilter == null) {
                mPaint.setColorFilter(mTintFilter);
            }

            mPaint.setColor(mColorState.mUseColor);

            // 直接在
            canvas.drawRect(getBounds(), mPaint);

            // Restore original color filter.
            mPaint.setColorFilter(colorFilter);
        }
    }

    public int getColor() {
        return mColorState.mUseColor;
    }

    public void setColor(int color) {
        if (mColorState.mBaseColor != color || mColorState.mUseColor != color) {
            mColorState.mBaseColor = mColorState.mUseColor = color;
            invalidateSelf();
        }
    }

    @Override
    public int getAlpha() {
        return mColorState.mUseColor >>> 24;
    }

    @Override
    public void setAlpha(int alpha) {
        alpha += alpha >> 7;   // make it 0..256
        final int baseAlpha = mColorState.mBaseColor >>> 24;
        final int useAlpha = baseAlpha * alpha >> 8;
        final int useColor = (mColorState.mBaseColor << 8 >>> 8) | (useAlpha << 24);
        if (mColorState.mUseColor != useColor) {
            mColorState.mUseColor = useColor;
            invalidateSelf();
        }
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public void setTintList(ColorStateList tint) {
        mColorState.mTint = tint;
        mTintFilter = updateTintFilter(mTintFilter, tint, mColorState.mTintMode);
        invalidateSelf();
    }

    @Override
    public void setTintMode(Mode tintMode) {
        mColorState.mTintMode = tintMode;
        mTintFilter = updateTintFilter(mTintFilter, mColorState.mTint, tintMode);
        invalidateSelf();
    }

    @Override
    protected boolean onStateChange(int[] stateSet) {
        final ColorState state = mColorState;
        if (state.mTint != null && state.mTintMode != null) {
            mTintFilter = updateTintFilter(mTintFilter, state.mTint, state.mTintMode);
            return true;
        }
        return false;
    }

    @Override
    public boolean isStateful() {
        return mColorState.mTint != null && mColorState.mTint.isStateful();
    }

    /** @hide */
    @Override
    public boolean hasFocusStateSpecified() {
//        return mColorState.mTint != null && mColorState.mTint.hasFocusStateSpecified();
        return  false;
    }

    /**
     * @hide
     * @param mode new transfer mode
     */
    @Override
    public void setXfermode(@Nullable Xfermode mode) {
        mPaint.setXfermode(mode);
        invalidateSelf();
    }

    /**
     * @hide
     * @return current transfer mode
     */
    public Xfermode getXfermode() {
        return mPaint.getXfermode();
    }

    @Override
    public int getOpacity() {
        if (mTintFilter != null || mPaint.getColorFilter() != null) {
            return PixelFormat.TRANSLUCENT;
        }

        switch (mColorState.mUseColor >>> 24) {
            case 255:
                return PixelFormat.OPAQUE;
            case 0:
                return PixelFormat.TRANSPARENT;
        }
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void getOutline(@NonNull Outline outline) {
        outline.setRect(getBounds());
        outline.setAlpha(getAlpha() / 255.0f);
    }

    @Override
    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs, Theme theme)
            throws XmlPullParserException, IOException {
        super.inflate(r, parser, attrs, theme);

//        final TypedArray a = obtainAttributes(r, theme, attrs, R.styleable.ColorDrawable);
//        updateStateFromTypedArray(a);
//        a.recycle();

        updateLocalState(r);
    }

    /**
     * Updates the constant state from the values in the typed array.
     */
    private void updateStateFromTypedArray(TypedArray a) {
        final ColorState state = mColorState;

        // Account for any configuration changes.
        state.mChangingConfigurations |= a.getChangingConfigurations();

//        // Extract the theme attributes, if any.
//        state.mThemeAttrs = a.extractThemeAttrs();
//
//        state.mBaseColor = a.getColor(R.styleable.ColorDrawable_color, state.mBaseColor);
        state.mUseColor = state.mBaseColor;
    }

    @Override
    public boolean canApplyTheme() {
        return mColorState.canApplyTheme() || super.canApplyTheme();
    }

    @Override
    public void applyTheme(Theme t) {
        super.applyTheme(t);

        final ColorState state = mColorState;
        if (state == null) {
            return;
        }

//        if (state.mThemeAttrs != null) {
//            final TypedArray a = t.resolveAttributes(state.mThemeAttrs, R.styleable.ColorDrawable);
//            updateStateFromTypedArray(a);
//            a.recycle();
//        }
//
//        if (state.mTint != null && state.mTint.canApplyTheme()) {
//            state.mTint = state.mTint.obtainForTheme(t);
//        }

        updateLocalState(t.getResources());
    }

    @Override
    public ConstantState getConstantState() {
        return mColorState;
    }

    final static class ColorState extends ConstantState {
        int[] mThemeAttrs;
        int mBaseColor; // base color, independent of setAlpha()
        @ViewDebug.ExportedProperty
        int mUseColor;  // basecolor modulated by setAlpha()

        int mChangingConfigurations;
        ColorStateList mTint = null;
        Mode mTintMode = DEFAULT_TINT_MODE;

        ColorState() {
            // Empty constructor.
        }

        ColorState(ColorState state) {
            mThemeAttrs = state.mThemeAttrs;
            mBaseColor = state.mBaseColor;
            mUseColor = state.mUseColor;
            mChangingConfigurations = state.mChangingConfigurations;
            mTint = state.mTint;
            mTintMode = state.mTintMode;
        }

        @Override
        public boolean canApplyTheme() {
//            return mThemeAttrs != null
//                    || (mTint != null && mTint.canApplyTheme());
            return false;

        }

        @Override
        public Drawable newDrawable() {
            return new ColorDrawable(this, null);
        }

        @Override
        public Drawable newDrawable(Resources res) {
            return new ColorDrawable(this, res);
        }

        @Override
        public int getChangingConfigurations() {
            return mChangingConfigurations
                    | (mTint != null ? mTint.getChangingConfigurations() : 0);
        }
    }

    private ColorDrawable(ColorState state, Resources res) {
        mColorState = state;

        updateLocalState(res);
    }

    /**
     * Initializes local dynamic properties from state. This should be called
     * after significant state changes, e.g. from the One True Constructor and
     * after inflating or applying a theme.
     */
    private void updateLocalState(Resources r) {
        mTintFilter = updateTintFilter(mTintFilter, mColorState.mTint, mColorState.mTintMode);
    }
}
