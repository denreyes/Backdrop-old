package io.denreyes.backdrop;

/**
 * Created by Dj on 9/27/2015.
 */
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Region;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class QuadMaskView extends ImageView{

    private Path hexagonPath;
    private Path hexagonBorderPath;
    private float radius;
    private Bitmap image;
    private int viewWidth;
    private int viewHeight;
    private Paint paint;
    private BitmapShader shader;
    private Paint paintBorder;
    private int borderWidth = 0;

    public QuadMaskView(Context context) {
        super(context);
        setup();
    }

    public QuadMaskView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public QuadMaskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    private void setup() {
        paint = new Paint();
        paint.setAntiAlias(true);

        paintBorder = new Paint();
        setBorderColor(Color.WHITE);
        paintBorder.setAntiAlias(true);
        this.setLayerType(LAYER_TYPE_SOFTWARE, paintBorder);
        paintBorder.setShadowLayer(0f, 0f, 0f, Color.BLACK);

        hexagonPath = new Path();
        hexagonBorderPath = new Path();
    }

    public void setBorderColor(int borderColor)  {
        if (paintBorder != null)
            paintBorder.setColor(borderColor);

        this.invalidate();
    }

    private void calculatePath() {

        float triangleHeight = (float) (Math.sqrt(3) * radius / 2);
        float centerX = viewWidth/2;
        float centerY = viewHeight/2;

        hexagonPath.moveTo(0, 0);
        hexagonPath.lineTo(viewWidth, 0);
        hexagonPath.lineTo(viewWidth-(viewWidth/3),viewHeight);
        hexagonPath.lineTo(0,viewHeight);
        hexagonPath.lineTo(0,0);

//        hexagonBorderPath.lineTo(centerX + triangleHeight, centerY + radius/2);
//        hexagonBorderPath.moveTo(centerX, centerY + radius);
//
//        float radiusBorder = radius - borderWidth;
//        float triangleBorderHeight = (float) (Math.sqrt(3) * radiusBorder / 2);
//
//        hexagonPath.moveTo(centerX, centerY + radiusBorder);
//        hexagonPath.lineTo(centerX - triangleBorderHeight, centerY + radiusBorder/2);
//        hexagonPath.lineTo(centerX - triangleBorderHeight, centerY - radiusBorder/2);
//        hexagonPath.lineTo(centerX, centerY - radiusBorder);
//        hexagonPath.lineTo(centerX + triangleBorderHeight, centerY - radiusBorder/2);
//        hexagonPath.lineTo(centerX + triangleBorderHeight, centerY + radiusBorder/2);
//        hexagonPath.moveTo(centerX, centerY + radiusBorder);

        invalidate();
    }

    public Bitmap drawableToBitmap(Drawable drawable)  {
        if (drawable == null) {
            return null;
        } else if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    @SuppressLint("DrawAllocation")
    @Override
    public void onDraw(Canvas canvas){
        image = drawableToBitmap(getDrawable());

        // init shader
        if (image != null) {

            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

            shader = new BitmapShader(Bitmap.createScaledBitmap(image, canvas.getWidth(), canvas.getHeight(), false), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            paint.setShader(shader);

            canvas.drawPath(hexagonBorderPath, paintBorder);
            canvas.drawPath(hexagonPath, paint);
        }

    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec, widthMeasureSpec);

        viewWidth = width - (borderWidth * 2);
        viewHeight = height - (borderWidth * 2);

        radius = height / 2 - borderWidth;

        calculatePath();

        setMeasuredDimension(width, height);
    }

    private int measureWidth(int measureSpec)   {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY)  {
            result = specSize;
        }
        else {
            result = viewWidth;
        }

        return result;
    }

    private int measureHeight(int measureSpecHeight, int measureSpecWidth)  {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpecHeight);
        int specSize = MeasureSpec.getSize(measureSpecHeight);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        }
        else {
            result = viewHeight;
        }

        return (result + 2);
    }

}