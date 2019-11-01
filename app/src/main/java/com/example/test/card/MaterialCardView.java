package com.example.test.card;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.ColorInt;
import androidx.annotation.Dimension;
import androidx.cardview.widget.CardView;

import com.example.test.R;


/**
 * 一个自定义的CardView，在系统的基础上增加了
 *
 * @author fortunexiao
 */
public class MaterialCardView extends CardView {
    private final MaterialCardViewHelper cardViewHelper;

    public MaterialCardView(Context context) {
        this(context, (AttributeSet) null);
    }

    public MaterialCardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.MaterialCardView);
        this.cardViewHelper = new MaterialCardViewHelper(this);
        this.cardViewHelper.loadFromAttributes(attributes);
        attributes.recycle();
    }

    public void setStrokeColor(@ColorInt int strokeColor) {
        this.cardViewHelper.setStrokeColor(strokeColor);
    }

    @ColorInt
    public int getStrokeColor() {
        return this.cardViewHelper.getStrokeColor();
    }

    public void setStrokeWidth(@Dimension int strokeWidth) {
        this.cardViewHelper.setStrokeWidth(strokeWidth);
    }

    @Dimension
    public int getStrokeWidth() {
        return this.cardViewHelper.getStrokeWidth();
    }

    public void setRadius(float radius) {
        super.setRadius(radius);
        this.cardViewHelper.updateForeground();
    }
}
