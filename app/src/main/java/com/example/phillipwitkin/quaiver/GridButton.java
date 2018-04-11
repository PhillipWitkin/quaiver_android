package com.example.phillipwitkin.quaiver;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by phillipwitkin on 10/10/17.
 */

@TargetApi(21)
public class GridButton extends android.support.v7.widget.AppCompatButton  {

    private int blockNumber;
    private boolean containsNote;
//    private int colPos;

    public GridButton(Context context, AttributeSet attrs){
        super(context, attrs);
        setContainsNote(attrs.getAttributeBooleanValue("http://schemas.android.com/apk/res/com.example.customviews", "containsNote", false));
        setBlockNumber(attrs.getAttributeIntValue("http://schemas.android.com/apk/res/com.example.customviews", "blockNumber", 1));
//        TypedArray a = context.getTheme().obtainStyledAttributes(
//                attrs,
//                R.styleable.GridButton,
//                0, 0);
//
//        try {
//            mShowText = a.getBoolean(R.styleable.GridButton_containsNote, false);
//            mTextPos = a.getInteger(R.styleable.GridButton_blockNumber, 0);
//        } finally {
//            a.recycle();
//        }


//        ShapeDrawable shapedrawable = new ShapeDrawable();
//        shapedrawable.setShape(new RectShape());
//        shapedrawable.getPaint().setColor(Color.BLUE);
//        shapedrawable.getPaint().setStrokeWidth(10f);
//        shapedrawable.getPaint().setStyle(Paint.Style.STROKE);

//        LayerDrawable normal = (LayerDrawable)context.getResources().getDrawable(R.drawable.shape, null);


//        normal.getDrawable(1).setAlpha(0);
//        this.setBackground(normal);

    }

    public boolean isContainsNote() {
        return containsNote;
    }

    public void setContainsNote(boolean containsNote) {
        this.containsNote = containsNote;
    }

    public int getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(int blockumber) {
        this.blockNumber = blockumber;
    }

    public ValueAnimator turnBgGreenAnimator(int start, int finish){
        final LayerDrawable bgLayerNormal = (LayerDrawable)this.getBackground();
        ValueAnimator colorAnimator = ValueAnimator.ofInt(start, finish);

        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {


            final Drawable bgNormalRect = (Drawable)bgLayerNormal.getDrawable(1);

            @Override
            public void onAnimationUpdate(final ValueAnimator animator) {

                bgNormalRect.setAlpha((Integer) animator.getAnimatedValue());
            }
        });

        return colorAnimator;
    }
}
