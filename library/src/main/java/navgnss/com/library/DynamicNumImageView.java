package navgnss.com.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by duian on 16/7/25.
 */
public class DynamicNumImageView extends RelativeLayout {

    private Context mContext;

    private AttributeSet mAttrs;

    private ImageView mImageView;

    private int mCustomViewBackgroudColor;

    private CustomView mCustomView;

    private int mWidthSize;

    private int mHeightSize;

    private boolean isNumberViewAdded = false;

    private float mNumberViewScale;

    private int mNumberVertexPosition;

    private String mTextContent;

    private float mTextContentScale;

    private int mTextContentColor;

    private Paint mCirclePaint;

    private Paint mTextPaint;

    private final static int NUMBER_VERTEX_POSITION_TOP_RIGHT = 0;
    private final static int NUMBER_VERTEX_POSITION_TOP_LEFT = 1;
    private final static int NUMBER_VERTEX_POSITION_BOTTOM_RIGHT = 2;
    private final static int NUMBER_VERTEX_POSITION_BOTTOM_LEFT = 3;

    public DynamicNumImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mAttrs = attrs;
        initView();
        Log.d("test1", "DynamicNumImageView: 2");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        mHeightSize = MeasureSpec.getSize(heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        Log.d("test", "onLayout: ");
        Log.d("test", "ChildCount: " + getChildCount());
        if (!isNumberViewAdded) {
            addCustomView();
            isNumberViewAdded = true;
            Log.d("test", "inChildCount: " + getChildCount());
        }


    }

    private void addCustomView() {
        mCustomView = new CustomView(mContext);
//        testView.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
        LayoutParams params = new LayoutParams((int) (mWidthSize * mNumberViewScale), (int) (mHeightSize * mNumberViewScale));
        switch (mNumberVertexPosition) {
            case NUMBER_VERTEX_POSITION_TOP_RIGHT:
                params.addRule(ALIGN_PARENT_RIGHT);
                params.addRule(ALIGN_PARENT_TOP);
                break;
            case NUMBER_VERTEX_POSITION_TOP_LEFT:
                params.addRule(ALIGN_PARENT_LEFT);
                params.addRule(ALIGN_PARENT_TOP);
                break;
            case NUMBER_VERTEX_POSITION_BOTTOM_RIGHT:
                params.addRule(ALIGN_PARENT_RIGHT);
                params.addRule(ALIGN_PARENT_BOTTOM);
                break;
            case NUMBER_VERTEX_POSITION_BOTTOM_LEFT:
                params.addRule(ALIGN_PARENT_LEFT);
                params.addRule(ALIGN_PARENT_BOTTOM);
                break;
        }

        addView(mCustomView, params);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("test", "onDraw: ");
    }

    private void initView() {
        Log.d("test", "initPaint: ");
        TypedArray typedArray = mContext.obtainStyledAttributes(mAttrs, R.styleable.DynamicNumImageView);

        Drawable thumbDrawable = typedArray.getDrawable(R.styleable.DynamicNumImageView_android_thumb);
        mNumberViewScale = typedArray.getFloat(R.styleable.DynamicNumImageView_number_view_scale, 0.25f);
        mNumberVertexPosition = typedArray.getInteger(R.styleable.DynamicNumImageView_vertex_position, 0);
        mCustomViewBackgroudColor = typedArray.getColor(R.styleable.DynamicNumImageView_number_view_backgroud_color, getResources().getColor(android.R.color.holo_red_light));
        mTextContent = typedArray.getString(R.styleable.DynamicNumImageView_text_content);
        mTextContentScale = typedArray.getFloat(R.styleable.DynamicNumImageView_number_text_content_scale, 0.7f);
        mTextContentColor = typedArray.getColor(R.styleable.DynamicNumImageView_number_text_content_color, getResources().getColor(android.R.color.white));

        mImageView = new ImageView(mContext, mAttrs);
        mImageView.setImageDrawable(thumbDrawable);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(mImageView, params);

    }

    private class CustomView extends View {


        private int mCustomViewWidthSize;
        private int mCustomViewHeightSize;
        private int mBaseline;
        private Paint.FontMetricsInt mFontMetrics;

        public CustomView(Context context) {
            super(context);
        }

        public void initPaint() {
            mCirclePaint = new Paint();
            mCirclePaint.setColor(mCustomViewBackgroudColor);
            mCirclePaint.setAntiAlias(true);
            mCirclePaint.setStyle(Paint.Style.FILL);
            mCirclePaint.setStrokeWidth(1.0f);

            mTextPaint = new Paint();
            mTextPaint.setAntiAlias(true);
            mTextPaint.setTextAlign(Paint.Align.CENTER);
            // Must manually scale the desired text size to match screen density
            mTextPaint.setTextSize(mCustomViewHeightSize * 0.3f * mTextContentScale * getResources().getDisplayMetrics().density);
            mTextPaint.setColor(mTextContentColor);
            mFontMetrics = mTextPaint.getFontMetricsInt();

            Log.d("test3", "mCustomViewWidthSize: " + mCustomViewWidthSize);
            Log.d("test3", "result: " + String.valueOf((10 / (float) mCustomViewWidthSize)));
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            mCustomViewWidthSize = MeasureSpec.getSize(widthMeasureSpec);
            mCustomViewHeightSize = MeasureSpec.getSize(heightMeasureSpec);
            initPaint();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            canvas.drawCircle(mCustomViewWidthSize / 2, mCustomViewHeightSize / 2, mCustomViewWidthSize / 2, mCirclePaint);

            if (mTextContent != null) {
                mBaseline = (getMeasuredHeight() - (mFontMetrics.descent - mFontMetrics.ascent)) / 2 - mFontMetrics.ascent;
                canvas.drawText(mTextContent, mCustomViewWidthSize / 2, mBaseline, mTextPaint);
            }
            Log.d("test", "CustomView onDraw: ");
        }

    }

    public String getTextContent() {
        return mTextContent;
    }

    public void setTextContent(String mTextContent) {
        if (mCustomView != null) {
            this.mTextContent = mTextContent;
            mCustomView.invalidate();
        }
        Log.d("test", "setTextContent: ");
    }

    public int getNumberVertexPosition() {
        return mNumberVertexPosition;
    }

    public void setNumberVertexPosition(int mNumberVertexPosition) {
        this.mNumberVertexPosition = mNumberVertexPosition;
    }

    public float getNumberViewScale() {
        return mNumberViewScale;
    }

    public void setNumberViewScale(float mNumberViewScale) {
        this.mNumberViewScale = mNumberViewScale;
    }

    public int getNumberViewBackgroudColor() {
        return mCustomViewBackgroudColor;
    }

    public void setNumberViewBackgroudColor(int mCustomViewBackgroudColor) {
        if (mCustomView != null) {
            mCirclePaint.setColor(mCustomViewBackgroudColor);
            mCustomView.invalidate();
        }
    }

    public ImageView getImageView() {
        if (mImageView == null) {
            throw new RuntimeException("ImageView is null");
        }
        return mImageView;
    }
}
