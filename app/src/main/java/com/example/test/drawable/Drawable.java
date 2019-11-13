
package com.example.test.drawable;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.NinePatch;
import android.graphics.Outline;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Trace;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.StateSet;
import android.util.TypedValue;
import android.util.Xml;
import android.view.View;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Arrays;

public abstract class Drawable {

    private static final Rect ZERO_BOUNDS_RECT = new Rect();

    static final PorterDuff.Mode DEFAULT_TINT_MODE = PorterDuff.Mode.SRC_IN;

    private int[] mStateSet = StateSet.WILD_CARD;
    private int mLevel = 0;
    private int mChangingConfigurations = 0;


    /**
     * 最重要的drawable的大小
     */
    private Rect mBounds = ZERO_BOUNDS_RECT;  // lazily becomes a new Rect()

    /**
     * callback
     */
    private WeakReference<Callback> mCallback = null;

    private boolean mVisible = true;

    private int mLayoutDirection;

    protected int mSrcDensityOverride = 0;


    /**
     * 核心方法，绘制当前的Drawable
     * @param canvas
     */
    public abstract void draw(@NonNull Canvas canvas);



    public void setBounds(int left, int top, int right, int bottom) {
        Rect oldBounds = mBounds;

        if (oldBounds == ZERO_BOUNDS_RECT) {
            oldBounds = mBounds = new Rect();
        }

        if (oldBounds.left != left || oldBounds.top != top ||
                oldBounds.right != right || oldBounds.bottom != bottom) {
            if (!oldBounds.isEmpty()) {
                // first invalidate the previous bounds
                invalidateSelf();
            }
            mBounds.set(left, top, right, bottom);
            onBoundsChange(mBounds);
        }
    }

    public void setBounds(@NonNull Rect bounds) {
        setBounds(bounds.left, bounds.top, bounds.right, bounds.bottom);
    }

    public final void copyBounds(@NonNull Rect bounds) {
        bounds.set(mBounds);
    }

    public final Rect copyBounds() {
        return new Rect(mBounds);
    }

    public final Rect getBounds() {
        if (mBounds == ZERO_BOUNDS_RECT) {
            mBounds = new Rect();
        }

        return mBounds;
    }

    public Rect getDirtyBounds() {
        return getBounds();
    }

    /**
     * Set a mask of the configuration parameters for which this drawable
     * may change, requiring that it be re-created.
     *
     * @param configs A mask of the changing configuration parameters, as
     * defined by {@link android.content.pm.ActivityInfo}.
     *
     * @see android.content.pm.ActivityInfo
     */
    public void setChangingConfigurations( int configs) {
        mChangingConfigurations = configs;
    }

    /**
     * Return a mask of the configuration parameters for which this drawable
     * may change, requiring that it be re-created.  The default implementation
     * returns whatever was provided through
     * {@link #setChangingConfigurations(int)} or 0 by default.  Subclasses
     * may extend this to or in the changing configurations of any other
     * drawables they hold.
     *
     * @return Returns a mask of the changing configuration parameters, as
     * defined by {@link android.content.pm.ActivityInfo}.
     *
     * @see android.content.pm.ActivityInfo
     */
    public int getChangingConfigurations() {
        return mChangingConfigurations;
    }

    public void setDither(boolean dither) {}

    public void setFilterBitmap(boolean filter) {}

    public boolean isFilterBitmap() {
        return false;
    }

    /**
     * Implement this interface if you want to create an animated drawable that
     * extends {@link android.graphics.drawable.Drawable Drawable}.
     * Upon retrieving a drawable, use
     * {@link Drawable#setCallback(android.graphics.drawable.Drawable.Callback)}
     * to supply your implementation of the interface to the drawable; it uses
     * this interface to schedule and execute animation changes.
     */
    public interface Callback {
        /**
         * Called when the drawable needs to be redrawn.  A view at this point
         * should invalidate itself (or at least the part of itself where the
         * drawable appears).
         *
         * @param who The drawable that is requesting the update.
         */
        void invalidateDrawable(@NonNull Drawable who);

        /**
         * A Drawable can call this to schedule the next frame of its
         * animation.  An implementation can generally simply call
         * {@link android.os.Handler#postAtTime(Runnable, Object, long)} with
         * the parameters <var>(what, who, when)</var> to perform the
         * scheduling.
         *
         * @param who The drawable being scheduled.
         * @param what The action to execute.
         * @param when The time (in milliseconds) to run.  The timebase is
         *             {@link android.os.SystemClock#uptimeMillis}
         */
        void scheduleDrawable(@NonNull Drawable who, @NonNull Runnable what, long when);

        /**
         * A Drawable can call this to unschedule an action previously
         * scheduled with {@link #scheduleDrawable}.  An implementation can
         * generally simply call
         * {@link android.os.Handler#removeCallbacks(Runnable, Object)} with
         * the parameters <var>(what, who)</var> to unschedule the drawable.
         *
         * @param who The drawable being unscheduled.
         * @param what The action being unscheduled.
         */
        void unscheduleDrawable(@NonNull Drawable who, @NonNull Runnable what);
    }

    public final void setCallback(@Nullable Callback cb) {
        mCallback = cb != null ? new WeakReference<>(cb) : null;
    }

    public Callback getCallback() {
        return mCallback != null ? mCallback.get() : null;
    }

    public void invalidateSelf() {
        final Callback callback = getCallback();
        if (callback != null) {
            callback.invalidateDrawable(this);
        }
    }

    public void scheduleSelf(@NonNull Runnable what, long when) {
        final Callback callback = getCallback();
        if (callback != null) {
            callback.scheduleDrawable(this, what, when);
        }
    }

    public void unscheduleSelf(@NonNull Runnable what) {
        final Callback callback = getCallback();
        if (callback != null) {
            callback.unscheduleDrawable(this, what);
        }
    }

    public  int getLayoutDirection() {
        return mLayoutDirection;
    }

    public final boolean setLayoutDirection(int layoutDirection) {
        if (mLayoutDirection != layoutDirection) {
            mLayoutDirection = layoutDirection;
            return onLayoutDirectionChanged(layoutDirection);
        }
        return false;
    }

    public boolean onLayoutDirectionChanged(int layoutDirection) {
        return false;
    }

    public abstract void setAlpha(int alpha);

    public int getAlpha() {
        return 0xFF;
    }

    public void setXfermode(@Nullable Xfermode mode) {
        // Base implementation drops it on the floor for compatibility. Whee!
    }

    public abstract void setColorFilter(@Nullable ColorFilter colorFilter);

    public void setColorFilter(@ColorInt int color, @NonNull PorterDuff.Mode mode) {
        if (getColorFilter() instanceof PorterDuffColorFilter) {
            PorterDuffColorFilter existing = (PorterDuffColorFilter) getColorFilter();
//            if (existing.getColor() == color && existing.getMode() == mode) {
//                return;
//            }
        }
        setColorFilter(new PorterDuffColorFilter(color, mode));
    }

    public void setTint(@ColorInt int tintColor) {
        setTintList(ColorStateList.valueOf(tintColor));
    }

    public void setTintList(@Nullable ColorStateList tint) {}

    public void setTintMode(@NonNull PorterDuff.Mode tintMode) {}

    public @Nullable ColorFilter getColorFilter() {
        return null;
    }

    public void clearColorFilter() {
        setColorFilter(null);
    }

    public void setHotspot(float x, float y) {}

    public void setHotspotBounds(int left, int top, int right, int bottom) {}

    public void getHotspotBounds(@NonNull Rect outRect) {
        outRect.set(getBounds());
    }

    public boolean isProjected() {
        return false;
    }

    /**
     * Indicates whether this drawable will change its appearance based on
     * state. Clients can use this to determine whether it is necessary to
     * calculate their state and call setState.
     *
     * @return True if this drawable changes its appearance based on state,
     *         false otherwise.
     * @see #setState(int[])
     */
    public boolean isStateful() {
        return false;
    }

    public boolean hasFocusStateSpecified() {
        return false;
    }

    /**
     * Specify a set of states for the drawable. These are use-case specific,
     * so see the relevant documentation. As an example, the background for
     * widgets like Button understand the following states:
     * [{@link android.R.attr#state_focused},
     *  {@link android.R.attr#state_pressed}].
     *
     * <p>If the new state you are supplying causes the appearance of the
     * Drawable to change, then it is responsible for calling
     * {@link #invalidateSelf} in order to have itself redrawn, <em>and</em>
     * true will be returned from this function.
     *
     * <p>Note: The Drawable holds a reference on to <var>stateSet</var>
     * until a new state array is given to it, so you must not modify this
     * array during that time.</p>
     *
     * @param stateSet The new set of states to be displayed.
     *
     * @return Returns true if this change in state has caused the appearance
     * of the Drawable to change (hence requiring an invalidate), otherwise
     * returns false.
     */
    public boolean setState(@NonNull final int[] stateSet) {
        if (!Arrays.equals(mStateSet, stateSet)) {
            mStateSet = stateSet;
            return onStateChange(stateSet);
        }
        return false;
    }

    /**
     * Describes the current state, as a union of primitve states, such as
     * {@link android.R.attr#state_focused},
     * {@link android.R.attr#state_selected}, etc.
     * Some drawables may modify their imagery based on the selected state.
     * @return An array of resource Ids describing the current state.
     */
    public @NonNull int[] getState() {
        return mStateSet;
    }

    /**
     * If this Drawable does transition animations between states, ask that
     * it immediately jump to the current state and skip any active animations.
     */
    public void jumpToCurrentState() {
    }

    public @NonNull Drawable getCurrent() {
        return this;
    }

    /**
     * Specify the level for the drawable.  This allows a drawable to vary its
     * imagery based on a continuous controller, for example to show progress
     * or volume level.
     *
     * <p>If the new level you are supplying causes the appearance of the
     * Drawable to change, then it is responsible for calling
     * {@link #invalidateSelf} in order to have itself redrawn, <em>and</em>
     * true will be returned from this function.
     *
     * @param level The new level, from 0 (minimum) to 10000 (maximum).
     *
     * @return Returns true if this change in level has caused the appearance
     * of the Drawable to change (hence requiring an invalidate), otherwise
     * returns false.
     */
    public final boolean setLevel(int level) {
        if (mLevel != level) {
            mLevel = level;
            return onLevelChange(level);
        }
        return false;
    }

    /**
     * Retrieve the current level.
     *
     * @return int Current level, from 0 (minimum) to 10000 (maximum).
     */
    public final int getLevel() {
        return mLevel;
    }

    /**
     * Set whether this Drawable is visible.  This generally does not impact
     * the Drawable's behavior, but is a hint that can be used by some
     * Drawables, for example, to decide whether run animations.
     *
     * @param visible Set to true if visible, false if not.
     * @param restart You can supply true here to force the drawable to behave
     *                as if it has just become visible, even if it had last
     *                been set visible.  Used for example to force animations
     *                to restart.
     *
     * @return boolean Returns true if the new visibility is different than
     *         its previous state.
     */
    public boolean setVisible(boolean visible, boolean restart) {
        boolean changed = mVisible != visible;
        if (changed) {
            mVisible = visible;
            invalidateSelf();
        }
        return changed;
    }

    public final boolean isVisible() {
        return mVisible;
    }

    /**
     * Set whether this Drawable is automatically mirrored when its layout direction is RTL
     * (right-to left). See {@link android.util.LayoutDirection}.
     *
     * @param mirrored Set to true if the Drawable should be mirrored, false if not.
     */
    public void setAutoMirrored(boolean mirrored) {
    }

    /**
     * Tells if this Drawable will be automatically mirrored  when its layout direction is RTL
     * right-to-left. See {@link android.util.LayoutDirection}.
     *
     * @return boolean Returns true if this Drawable will be automatically mirrored.
     */
    public boolean isAutoMirrored() {
        return false;
    }

    /**
     * Applies the specified theme to this Drawable and its children.
     *
     * @param t the theme to apply
     */
    public void applyTheme(@NonNull @SuppressWarnings("unused") Theme t) {
    }

    public boolean canApplyTheme() {
        return false;
    }

    /**
     * Return the opacity/transparency of this Drawable.  The returned value is
     * one of the abstract format constants in
     * {@link android.graphics.PixelFormat}:
     * {@link android.graphics.PixelFormat#UNKNOWN},
     * {@link android.graphics.PixelFormat#TRANSLUCENT},
     * {@link android.graphics.PixelFormat#TRANSPARENT}, or
     * {@link android.graphics.PixelFormat#OPAQUE}.
     *
     * <p>An OPAQUE drawable is one that draws all all content within its bounds, completely
     * covering anything behind the drawable. A TRANSPARENT drawable is one that draws nothing
     * within its bounds, allowing everything behind it to show through. A TRANSLUCENT drawable
     * is a drawable in any other state, where the drawable will draw some, but not all,
     * of the content within its bounds and at least some content behind the drawable will
     * be visible. If the visibility of the drawable's contents cannot be determined, the
     * safest/best return value is TRANSLUCENT.
     *
     * <p>Generally a Drawable should be as conservative as possible with the
     * value it returns.  For example, if it contains multiple child drawables
     * and only shows one of them at a time, if only one of the children is
     * TRANSLUCENT and the others are OPAQUE then TRANSLUCENT should be
     * returned.  You can use the method {@link #resolveOpacity} to perform a
     * standard reduction of two opacities to the appropriate single output.
     *
     * <p>Note that the returned value does not necessarily take into account a
     * custom alpha or color filter that has been applied by the client through
     * the {@link #setAlpha} or {@link #setColorFilter} methods. Some subclasses,
     * such as {@link BitmapDrawable}, {@link ColorDrawable}, and {@link GradientDrawable},
     * do account for the value of {@link #setAlpha}, but the general behavior is dependent
     * upon the implementation of the subclass.
     *
     * @return int The opacity class of the Drawable.
     *
     * @see android.graphics.PixelFormat
     */
    public abstract int getOpacity();

    /**
     * Return the appropriate opacity value for two source opacities.  If
     * either is UNKNOWN, that is returned; else, if either is TRANSLUCENT,
     * that is returned; else, if either is TRANSPARENT, that is returned;
     * else, OPAQUE is returned.
     *
     * <p>This is to help in implementing {@link #getOpacity}.
     *
     * @param op1 One opacity value.
     * @param op2 Another opacity value.
     *
     * @return int The combined opacity value.
     *
     * @see #getOpacity
     */
    public static int resolveOpacity(int op1,
           int op2) {
        if (op1 == op2) {
            return op1;
        }
        if (op1 == PixelFormat.UNKNOWN || op2 == PixelFormat.UNKNOWN) {
            return PixelFormat.UNKNOWN;
        }
        if (op1 == PixelFormat.TRANSLUCENT || op2 == PixelFormat.TRANSLUCENT) {
            return PixelFormat.TRANSLUCENT;
        }
        if (op1 == PixelFormat.TRANSPARENT || op2 == PixelFormat.TRANSPARENT) {
            return PixelFormat.TRANSPARENT;
        }
        return PixelFormat.OPAQUE;
    }

    /**
     * Returns a Region representing the part of the Drawable that is completely
     * transparent.  This can be used to perform drawing operations, identifying
     * which parts of the target will not change when rendering the Drawable.
     * The default implementation returns null, indicating no transparent
     * region; subclasses can optionally override this to return an actual
     * Region if they want to supply this optimization information, but it is
     * not required that they do so.
     *
     * @return Returns null if the Drawables has no transparent region to
     * report, else a Region holding the parts of the Drawable's bounds that
     * are transparent.
     */
    public @Nullable Region getTransparentRegion() {
        return null;
    }

    /**
     * Override this in your subclass to change appearance if you recognize the
     * specified state.
     *
     * @return Returns true if the state change has caused the appearance of
     * the Drawable to change (that is, it needs to be drawn), else false
     * if it looks the same and there is no need to redraw it since its
     * last state.
     */
    protected boolean onStateChange(int[] state) {
        return false;
    }

    /** Override this in your subclass to change appearance if you vary based
     *  on level.
     * @return Returns true if the level change has caused the appearance of
     * the Drawable to change (that is, it needs to be drawn), else false
     * if it looks the same and there is no need to redraw it since its
     * last level.
     */
    protected boolean onLevelChange(int level) {
        return false;
    }

    /**
     * Override this in your subclass to change appearance if you vary based on
     * the bounds.
     */
    protected void onBoundsChange(Rect bounds) {
        // Stub method.
    }

    /**
     * Returns the drawable's intrinsic width.
     * <p>
     * Intrinsic width is the width at which the drawable would like to be laid
     * out, including any inherent padding. If the drawable has no intrinsic
     * width, such as a solid color, this method returns -1.
     *
     * @return the intrinsic width, or -1 if no intrinsic width
     */
    public int getIntrinsicWidth() {
        return -1;
    }

    /**
     * Returns the drawable's intrinsic height.
     * <p>
     * Intrinsic height is the height at which the drawable would like to be
     * laid out, including any inherent padding. If the drawable has no
     * intrinsic height, such as a solid color, this method returns -1.
     *
     * @return the intrinsic height, or -1 if no intrinsic height
     */
    public int getIntrinsicHeight() {
        return -1;
    }

    /**
     * Returns the minimum width suggested by this Drawable. If a View uses this
     * Drawable as a background, it is suggested that the View use at least this
     * value for its width. (There will be some scenarios where this will not be
     * possible.) This value should INCLUDE any padding.
     *
     * @return The minimum width suggested by this Drawable. If this Drawable
     *         doesn't have a suggested minimum width, 0 is returned.
     */
    public int getMinimumWidth() {
        final int intrinsicWidth = getIntrinsicWidth();
        return intrinsicWidth > 0 ? intrinsicWidth : 0;
    }

    /**
     * Returns the minimum height suggested by this Drawable. If a View uses this
     * Drawable as a background, it is suggested that the View use at least this
     * value for its height. (There will be some scenarios where this will not be
     * possible.) This value should INCLUDE any padding.
     *
     * @return The minimum height suggested by this Drawable. If this Drawable
     *         doesn't have a suggested minimum height, 0 is returned.
     */
    public int getMinimumHeight() {
        final int intrinsicHeight = getIntrinsicHeight();
        return intrinsicHeight > 0 ? intrinsicHeight : 0;
    }

    /**
     * Return in padding the insets suggested by this Drawable for placing
     * content inside the drawable's bounds. Positive values move toward the
     * center of the Drawable (set Rect.inset).
     *
     * @return true if this drawable actually has a padding, else false. When false is returned,
     * the padding is always set to 0.
     */
    public boolean getPadding(@NonNull Rect padding) {
        padding.set(0, 0, 0, 0);
        return false;
    }

//    /**
//     * Return in insets the layout insets suggested by this Drawable for use with alignment
//     * operations during layout.
//     *
//     * @hide
//     */
//    public @NonNull Insets getOpticalInsets() {
//        return Insets.NONE;
//    }

    /**
     * Called to get the drawable to populate the Outline that defines its drawing area.
     * <p>
     * This method is called by the default {@link android.view.ViewOutlineProvider} to define
     * the outline of the View.
     * <p>
     * The default behavior defines the outline to be the bounding rectangle of 0 alpha.
     * Subclasses that wish to convey a different shape or alpha value must override this method.
     *
     * @see android.view.View#setOutlineProvider(android.view.ViewOutlineProvider)
     */
    public void getOutline(@NonNull Outline outline) {
        outline.setRect(getBounds());
        outline.setAlpha(0);
    }

    /**
     * Make this drawable mutable. This operation cannot be reversed. A mutable
     * drawable is guaranteed to not share its state with any other drawable.
     * This is especially useful when you need to modify properties of drawables
     * loaded from resources. By default, all drawables instances loaded from
     * the same resource share a common state; if you modify the state of one
     * instance, all the other instances will receive the same modification.
     *
     * Calling this method on a mutable Drawable will have no effect.
     *
     * @return This drawable.
     * @see ConstantState
     * @see #getConstantState()
     */
    public @NonNull Drawable mutate() {
        return this;
    }

    /**
     * Clears the mutated state, allowing this drawable to be cached and
     * mutated again.
     * <p>
     * This is hidden because only framework drawables can be cached, so
     * custom drawables don't need to support constant state, mutate(), or
     * clearMutated().
     *
     * @hide
     */
    public void clearMutated() {
        // Default implementation is no-op.
    }

    /**
     * Create a drawable from an inputstream
     */
    public static Drawable createFromStream(InputStream is, String srcName) {
//        Trace.traceBegin(Trace.TRACE_TAG_RESOURCES, srcName != null ? srcName : "Unknown drawable");
        try {
            return createFromResourceStream(null, null, is, srcName);
        } finally {
//            Trace.traceEnd(Trace.TRACE_TAG_RESOURCES);
        }
    }

    /**
     * Create a drawable from an inputstream, using the given resources and
     * value to determine density information.
     */
    public static Drawable createFromResourceStream(Resources res, TypedValue value,
            InputStream is, String srcName) {
//        Trace.traceBegin(Trace.TRACE_TAG_RESOURCES, srcName != null ? srcName : "Unknown drawable");
        try {
            return createFromResourceStream(res, value, is, srcName, null);
        } finally {
//            Trace.traceEnd(Trace.TRACE_TAG_RESOURCES);
        }
    }

    /**
     * Create a drawable from an inputstream, using the given resources and
     * value to determine density information.
     */
    public static Drawable createFromResourceStream(Resources res, TypedValue value,
            InputStream is, String srcName, BitmapFactory.Options opts) {
        if (is == null) {
            return null;
        }

        /*  ugh. The decodeStream contract is that we have already allocated
            the pad rect, but if the bitmap does not had a ninepatch chunk,
            then the pad will be ignored. If we could change this to lazily
            alloc/assign the rect, we could avoid the GC churn of making new
            Rects only to drop them on the floor.
        */
        Rect pad = new Rect();

        // Special stuff for compatibility mode: if the target density is not
        // the same as the display density, but the resource -is- the same as
        // the display density, then don't scale it down to the target density.
        // This allows us to load the system's density-correct resources into
        // an application in compatibility mode, without scaling those down
        // to the compatibility density only to have them scaled back up when
        // drawn to the screen.
        if (opts == null) opts = new BitmapFactory.Options();
        opts.inScreenDensity = Drawable.resolveDensity(res, 0);
        Bitmap  bm = BitmapFactory.decodeResourceStream(res, value, is, pad, opts);
        if (bm != null) {
            byte[] np = bm.getNinePatchChunk();
            if (np == null || !NinePatch.isNinePatchChunk(np)) {
                np = null;
                pad = null;
            }

            final Rect opticalInsets = new Rect();
//            bm.getOpticalInsets(opticalInsets);
            return drawableFromBitmap(res, bm, np, pad, opticalInsets, srcName);
        }
        return null;
    }

    /**
     * Create a drawable from an XML document. For more information on how to
     * create resources in XML, see
     * <a href="{@docRoot}guide/topics/resources/drawable-resource.html">Drawable Resources</a>.
     */
    @NonNull
    public static Drawable createFromXml(@NonNull Resources r, @NonNull XmlPullParser parser)
            throws XmlPullParserException, IOException {
        return createFromXml(r, parser, null);
    }

    /**
     * Create a drawable from an XML document using an optional {@link Theme}.
     * For more information on how to create resources in XML, see
     * <a href="{@docRoot}guide/topics/resources/drawable-resource.html">Drawable Resources</a>.
     */
    @NonNull
    public static Drawable createFromXml(@NonNull Resources r, @NonNull XmlPullParser parser,
            @Nullable Theme theme) throws XmlPullParserException, IOException {
        return createFromXmlForDensity(r, parser, 0, theme);
    }

    /**
     * Version of {@link #createFromXml(Resources, XmlPullParser, Theme)} that accepts a density
     * override.
     * @hide
     */
    @NonNull
    public static Drawable createFromXmlForDensity(@NonNull Resources r,
            @NonNull XmlPullParser parser, int density, @Nullable Theme theme)
            throws XmlPullParserException, IOException {
        AttributeSet attrs = Xml.asAttributeSet(parser);

        int type;
        //noinspection StatementWithEmptyBody
        while ((type=parser.next()) != XmlPullParser.START_TAG
                && type != XmlPullParser.END_DOCUMENT) {
            // Empty loop.
        }

        if (type != XmlPullParser.START_TAG) {
            throw new XmlPullParserException("No start tag found");
        }

        Drawable drawable = createFromXmlInnerForDensity(r, parser, attrs, density, theme);

        if (drawable == null) {
            throw new RuntimeException("Unknown initial tag: " + parser.getName());
        }

        return drawable;
    }

    /**
     * Create from inside an XML document.  Called on a parser positioned at
     * a tag in an XML document, tries to create a Drawable from that tag.
     * Returns null if the tag is not a valid drawable.
     */
    @NonNull
    public static Drawable createFromXmlInner(@NonNull Resources r, @NonNull XmlPullParser parser,
            @NonNull AttributeSet attrs) throws XmlPullParserException, IOException {
        return createFromXmlInner(r, parser, attrs, null);
    }

    /**
     * Create a drawable from inside an XML document using an optional
     * {@link Theme}. Called on a parser positioned at a tag in an XML
     * document, tries to create a Drawable from that tag. Returns {@code null}
     * if the tag is not a valid drawable.
     */
    @NonNull
    public static Drawable createFromXmlInner(@NonNull Resources r, @NonNull XmlPullParser parser,
            @NonNull AttributeSet attrs, @Nullable Theme theme)
            throws XmlPullParserException, IOException {
        return createFromXmlInnerForDensity(r, parser, attrs, 0, theme);
    }

    /**
     * Version of {@link #createFromXmlInner(Resources, XmlPullParser, AttributeSet, Theme)} that
     * accepts an override density.
     */
    @NonNull
    static Drawable createFromXmlInnerForDensity(@NonNull Resources r,
            @NonNull XmlPullParser parser, @NonNull AttributeSet attrs, int density,
            @Nullable Theme theme) throws XmlPullParserException, IOException {
//        return r.getDrawableInflater().inflateFromXmlForDensity(parser.getName(), parser, attrs,
//                density, theme);
        return null;
    }

    /**
     * Create a drawable from file path name.
     */
    @Nullable
    public static Drawable createFromPath(String pathName) {
        if (pathName == null) {
            return null;
        }

//        Trace.traceBegin(Trace.TRACE_TAG_RESOURCES, pathName);
        try {
            Bitmap bm = BitmapFactory.decodeFile(pathName);
            if (bm != null) {
                return drawableFromBitmap(null, bm, null, null, null, pathName);
            }
        } finally {
//            Trace.traceEnd(Trace.TRACE_TAG_RESOURCES);
        }

        return null;
    }

    /**
     * Inflate this Drawable from an XML resource. Does not apply a theme.
     *
     * @see #inflate(Resources, XmlPullParser, AttributeSet, Theme)
     */
    public void inflate(@NonNull Resources r, @NonNull XmlPullParser parser,
            @NonNull AttributeSet attrs) throws XmlPullParserException, IOException {
        inflate(r, parser, attrs, null);
    }

    /**
     * Inflate this Drawable from an XML resource optionally styled by a theme.
     * This can't be called more than once for each Drawable. Note that framework may have called
     * this once to create the Drawable instance from XML resource.
     *
     * @param r Resources used to resolve attribute values
     * @param parser XML parser from which to inflate this Drawable
     * @param attrs Base set of attribute values
     * @param theme Theme to apply, may be null
     * @throws XmlPullParserException
     * @throws IOException
     */
    public void inflate(@NonNull Resources r, @NonNull XmlPullParser parser,
            @NonNull AttributeSet attrs, @Nullable Theme theme)
            throws XmlPullParserException, IOException {
//        final TypedArray a = obtainAttributes(r, theme, attrs, R.styleable.Drawable);
//        mVisible = a.getBoolean(R.styleable.Drawable_visible, mVisible);
//        a.recycle();
    }

    /**
     * Inflate a Drawable from an XML resource.
     *
     * @throws XmlPullParserException
     * @throws IOException
     */
    void inflateWithAttributes(@NonNull @SuppressWarnings("unused") Resources r,
            @NonNull @SuppressWarnings("unused") XmlPullParser parser, @NonNull TypedArray attrs,
            @AttrRes int visibleAttr) throws XmlPullParserException, IOException {
//        mVisible = attrs.getBoolean(visibleAttr, mVisible);
    }

    /**
     * Sets the source override density for this Drawable. If non-zero, this density is to be used
     * for any calls to {@link Resources#getDrawableForDensity(int, int, Theme)} or
     * {@link Resources#getValueForDensity(int, int, TypedValue, boolean)}.
     * @hide
     */
    final void setSrcDensityOverride(int density) {
        mSrcDensityOverride = density;
    }

    /**
     * This abstract class is used by {@link Drawable}s to store shared constant state and data
     * between Drawables. {@link BitmapDrawable}s created from the same resource will for instance
     * share a unique bitmap stored in their ConstantState.
     *
     * <p>
     * {@link #newDrawable(Resources)} can be used as a factory to create new Drawable instances
     * from this ConstantState.
     * </p>
     *
     * Use {@link Drawable#getConstantState()} to retrieve the ConstantState of a Drawable. Calling
     * {@link Drawable#mutate()} on a Drawable should typically create a new ConstantState for that
     * Drawable.
     */
    public static abstract class ConstantState {
        /**
         * Creates a new Drawable instance from its constant state.
         * <p>
         * <strong>Note:</strong> Using this method means density-dependent
         * properties, such as pixel dimensions or bitmap images, will not be
         * updated to match the density of the target display. To ensure
         * correct scaling, use {@link #newDrawable(Resources)} instead to
         * provide an appropriate Resources object.
         *
         * @return a new drawable object based on this constant state
         * @see #newDrawable(Resources)
         */
        public abstract @NonNull Drawable newDrawable();

        /**
         * Creates a new Drawable instance from its constant state using the
         * specified resources. This method should be implemented for drawables
         * that have density-dependent properties.
         * <p>
         * The default implementation for this method calls through to
         * {@link #newDrawable()}.
         *
         * @param res the resources of the context in which the drawable will
         *            be displayed
         * @return a new drawable object based on this constant state
         */
        public @NonNull Drawable newDrawable(@Nullable Resources res) {
            return newDrawable();
        }

        /**
         * Creates a new Drawable instance from its constant state using the
         * specified resources and theme. This method should be implemented for
         * drawables that have theme-dependent properties.
         * <p>
         * The default implementation for this method calls through to
         * {@link #newDrawable(Resources)}.
         *
         * @param res the resources of the context in which the drawable will
         *            be displayed
         * @param theme the theme of the context in which the drawable will be
         *              displayed
         * @return a new drawable object based on this constant state
         */
        public @NonNull Drawable newDrawable(@Nullable Resources res,
                @Nullable @SuppressWarnings("unused") Theme theme) {
            return newDrawable(res);
        }

        /**
         * Return a bit mask of configuration changes that will impact
         * this drawable (and thus require completely reloading it).
         */
        public abstract int getChangingConfigurations();

        /**
         * Return whether this constant state can have a theme applied.
         */
        public boolean canApplyTheme() {
            return false;
        }
    }

    /**
     * Return a {@link ConstantState} instance that holds the shared state of this Drawable.
     *
     * @return The ConstantState associated to that Drawable.
     * @see ConstantState
     * @see Drawable#mutate()
     */
    public @Nullable ConstantState getConstantState() {
        return null;
    }

    private static Drawable drawableFromBitmap(Resources res, Bitmap bm, byte[] np,
            Rect pad, Rect layoutBounds, String srcName) {

//        if (np != null) {
//            return new NinePatchDrawable(res, bm, np, pad, layoutBounds, srcName);
//        }
//
//        return new BitmapDrawable(res, bm);
        return null;
    }

    /**
     * Ensures the tint filter is consistent with the current tint color and
     * mode.
     */
    @Nullable PorterDuffColorFilter updateTintFilter(@Nullable PorterDuffColorFilter tintFilter,
            @Nullable ColorStateList tint, @Nullable PorterDuff.Mode tintMode) {
        if (tint == null || tintMode == null) {
            return null;
        }

        final int color = tint.getColorForState(getState(), Color.TRANSPARENT);
        if (tintFilter == null) {
            return new PorterDuffColorFilter(color, tintMode);
        }

//        tintFilter.setColor(color);
//        tintFilter.setMode(tintMode);
        return tintFilter;
    }

    /**
     * Obtains styled attributes from the theme, if available, or unstyled
     * resources if the theme is null.
     * @hide
     */
    protected static @NonNull TypedArray obtainAttributes(@NonNull Resources res,
            @Nullable Theme theme, @NonNull AttributeSet set, @NonNull int[] attrs) {
        if (theme == null) {
            return res.obtainAttributes(set, attrs);
        }
        return theme.obtainStyledAttributes(set, attrs, 0, 0);
    }

    /**
     * Scales a floating-point pixel value from the source density to the
     * target density.
     *
     * @param pixels the pixel value for use in source density
     * @param sourceDensity the source density
     * @param targetDensity the target density
     * @return the scaled pixel value for use in target density
     */
    static float scaleFromDensity(float pixels, int sourceDensity, int targetDensity) {
        return pixels * targetDensity / sourceDensity;
    }

    /**
     * Scales a pixel value from the source density to the target density,
     * optionally handling the resulting pixel value as a size rather than an
     * offset.
     * <p>
     * A size conversion involves rounding the base value and ensuring that
     * a non-zero base value is at least one pixel in size.
     * <p>
     * An offset conversion involves simply truncating the base value to an
     * integer.
     *
     * @param pixels the pixel value for use in source density
     * @param sourceDensity the source density
     * @param targetDensity the target density
     * @param isSize {@code true} to handle the resulting scaled value as a
     *               size, or {@code false} to handle it as an offset
     * @return the scaled pixel value for use in target density
     */
    static int scaleFromDensity(
            int pixels, int sourceDensity, int targetDensity, boolean isSize) {
        if (pixels == 0 || sourceDensity == targetDensity) {
            return pixels;
        }

        final float result = pixels * targetDensity / (float) sourceDensity;
        if (!isSize) {
            return (int) result;
        }

        final int rounded = Math.round(result);
        if (rounded != 0) {
            return rounded;
        } else if (pixels > 0) {
            return 1;
        } else {
            return -1;
        }
    }

    static int resolveDensity(@Nullable Resources r, int parentDensity) {
        final int densityDpi = r == null ? parentDensity : r.getDisplayMetrics().densityDpi;
        return densityDpi == 0 ? DisplayMetrics.DENSITY_DEFAULT : densityDpi;
    }

    /**
     * Re-throws an exception as a {@link RuntimeException} with an empty stack
     * trace to avoid cluttering the log. The original exception's stack trace
     * will still be included.
     *
     * @param cause the exception to re-throw
     * @throws RuntimeException
     */
    static void rethrowAsRuntimeException(@NonNull Exception cause) throws RuntimeException {
        final RuntimeException e = new RuntimeException(cause);
        e.setStackTrace(new StackTraceElement[0]);
        throw e;
    }

    /**
     * Parses a {@link android.graphics.PorterDuff.Mode} from a tintMode
     * attribute's enum value.
     *
     * @hide
     */
    public static PorterDuff.Mode parseTintMode(int value, Mode defaultMode) {
        switch (value) {
            case 3: return Mode.SRC_OVER;
            case 5: return Mode.SRC_IN;
            case 9: return Mode.SRC_ATOP;
            case 14: return Mode.MULTIPLY;
            case 15: return Mode.SCREEN;
            case 16: return Mode.ADD;
            default: return defaultMode;
        }
    }
}

