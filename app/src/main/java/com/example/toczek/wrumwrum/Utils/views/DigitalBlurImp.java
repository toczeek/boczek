package com.example.toczek.wrumwrum.Utils.views;

import android.content.Context;
import android.graphics.BlurMaskFilter;


public class DigitalBlurImp extends DigitalImp {
    public DigitalBlurImp(int color, Context context, int marginTop, int textSize, String units) {
        super(color, context, marginTop, textSize, units);
        this.digitPaint.setMaskFilter(new BlurMaskFilter(6.0F, BlurMaskFilter.Blur.NORMAL));
    }
}