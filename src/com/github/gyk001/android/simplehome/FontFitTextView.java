package com.github.gyk001.android.simplehome;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class FontFitTextView extends TextView {

    public FontFitTextView(Context context) {
        super(context);
        initialise();
    }

    public FontFitTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialise();
    }

    private void initialise() {
        testPaint = new Paint();
        testPaint.set(this.getPaint());
        //max size defaults to the intially specified text size unless it is too small
        maxTextSize = this.getTextSize();
        if (maxTextSize < 11) {
            maxTextSize = 20;
        }
        minTextSize = 10;
    }

    /* Re size the font so the specified text fits in the text box
     * assuming the text box is the specified width.
     */
	private void refitText(String text, int textWidth) {
		if (textWidth > 0) {
			int availableWidth = textWidth - this.getPaddingLeft()
					- this.getPaddingRight();
			int trySize = (int) maxTextSize;
			int increment = ~(trySize - (int) minTextSize) / 2;

			testPaint.setTextSize(trySize);
			while ((trySize > minTextSize)
					&& (testPaint.measureText(text) > availableWidth)) {
				trySize += increment;
				increment = (increment == 0) ? -1 : ~increment / 2;
				if (trySize <= minTextSize) {
					trySize = (int) minTextSize;
					break;
				}
				testPaint.setTextSize(trySize);
			}

			this.setTextSize(TypedValue.COMPLEX_UNIT_PX, trySize);

			//FontMetrics fm = testPaint.getFontMetrics();
			//this.setHeight(Double.valueOf(Math.ceil(fm.descent - fm.ascent))
			//		.intValue());
			/*
			LayoutParams lp = getLayoutParams();
			lp.height = Double.valueOf(Math.ceil(fm.descent - fm.ascent))
							.intValue();
			this.setLayoutParams( lp );
			this.requestLayout();*/
		}
	}


    @Override
    protected void onTextChanged(final CharSequence text, final int start, final int before, final int after) {
        refitText(text.toString(), this.getWidth());
    }

    @Override
    protected void onSizeChanged (int w, int h, int oldw, int oldh) {
        if (w != oldw) {
            refitText(this.getText().toString(), w);
        }
    }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
        {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
            int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
            refitText(this.getText().toString(), parentWidth);
            this.setMeasuredDimension(parentWidth, parentHeight);
        }

    //Getters and Setters
    public float getMinTextSize() {
        return minTextSize;
    }

    public void setMinTextSize(int minTextSize) {
        this.minTextSize = minTextSize;
    }

    public float getMaxTextSize() {
        return maxTextSize;
    }

    public void setMaxTextSize(int minTextSize) {
        this.maxTextSize = minTextSize;
    }


    //Attributes
    private Paint testPaint;
    private float minTextSize;
    private float maxTextSize;

}