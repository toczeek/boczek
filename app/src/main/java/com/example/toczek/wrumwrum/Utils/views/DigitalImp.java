package com.example.toczek.wrumwrum.Utils.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;

import com.github.glomadrian.velocimeterlibrary.painter.digital.Digital;
import com.github.glomadrian.velocimeterlibrary.utils.DimensionUtils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.text.TextPaint;
import com.github.glomadrian.velocimeterlibrary.painter.digital.Digital;
import com.github.glomadrian.velocimeterlibrary.utils.DimensionUtils;

public class DigitalImp implements Digital {
    private float value;
    private Typeface typeface;
    protected Paint digitPaint;
    protected Paint textPaint;
    private Context context;
    private float textSize;
    private int marginTop;
    private int color;
    private float centerX;
    private float centerY;
    private float correction;
    private String units;

    public DigitalImp(int color, Context context, int marginTop, int textSize, String units) {
        this.context = context;
        this.color = color;
        this.marginTop = marginTop;
        this.textSize = (float)textSize;
        this.units = units;
        this.initTypeFace();
        this.initPainter();
        this.initValues();
    }

    private void initPainter() {
        this.digitPaint = new Paint();
        this.digitPaint.setAntiAlias(true);
        this.digitPaint.setTextSize(this.textSize);
        this.digitPaint.setColor(this.color);
        this.digitPaint.setTypeface(this.typeface);
        this.digitPaint.setTextAlign(Paint.Align.CENTER);
        this.textPaint = new TextPaint();
        this.textPaint.setAntiAlias(true);
        this.textPaint.setTextSize(this.textSize / 3.0F);
        this.textPaint.setColor(this.color);
        this.textPaint.setTypeface(this.typeface);
        this.textPaint.setTextAlign(Paint.Align.CENTER);
    }

    private void initValues() {
        this.correction = (float) DimensionUtils.getSizeInPixels(10.0F, this.context);
    }

    private void initTypeFace() {
        this.typeface = Typeface.createFromAsset(this.context.getAssets(), "fonts/digit.TTF");
    }

    public void setValue(float value) {
        this.value = value;
    }

    public void draw(Canvas canvas) {
        canvas.drawText(String.format("%.0f", new Object[]{Float.valueOf(this.value)}), this.centerX - this.correction, this.centerY + (float)this.marginTop, this.digitPaint);
        canvas.drawText(this.units, this.centerX + this.textSize * 1.2F - this.correction, this.centerY + (float)this.marginTop, this.textPaint);
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return this.color;
    }

    public void onSizeChanged(int height, int width) {
        this.centerX = (float)(width / 2);
        this.centerY = (float)(height / 2);
    }
    public void setUnits(String units) {
        this.units = units;
    }
}
