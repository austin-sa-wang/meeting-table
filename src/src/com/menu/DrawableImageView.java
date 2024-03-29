package com.menu;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

@SuppressLint("ViewConstructor")
public class DrawableImageView extends View {
	private Bitmap mBitmap;
	private Bitmap pic;
	private Canvas mCanvas;
	private final Paint mPaint;
	private int a = 255;
	private int r = 255;
	private int g = 0;
	private int b = 0;
	//private int g = 255;
	//private int b = 255;
	private float width = 4;
	public boolean in_edit_mode = false;

	public DrawableImageView(Context c, Bitmap img) {
		super(c);
		pic = img;
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setARGB(a,r,g,b);

		Bitmap newBitmap = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.RGB_565);
		Canvas newCanvas = new Canvas();
		newCanvas.setBitmap(newBitmap);
		if (img != null) {
			newCanvas.drawBitmap(img, 0, 0, null);
		}
		mBitmap = newBitmap;
		mCanvas = newCanvas;

		mCanvas.setBitmap(mBitmap);
	}

	public DrawableImageView(Context c, Bitmap img, int alpha, int red, int green, int blue) {
		this(c, img);
		setColor(alpha, red, green, blue);
	}    
	public DrawableImageView(Context c, Bitmap img, int alpha, int red, int green, int blue, float w) {
		this(c, img, alpha, red, green, blue);
		width = w;
	}

	public Bitmap getBitmap() {return mBitmap;}
	public void setWidth(float w) {width = w;}
	public void setColor(int alpha, int red, int green, int blue) {
		a = alpha;
		r = red;
		g = green;
		b = blue;
		mPaint.setARGB(a,r,g,b);
	}
	public void Undo() {
		mCanvas.drawBitmap(pic, 0, 0, null);
		invalidate();
	}

	float scaleX;
	float scaleY;
	float scale;
	@Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {        
		scaleX = (float) w/mBitmap.getWidth();
		scaleY = (float) h/mBitmap.getHeight();
		scale = scaleX > scaleY ? scaleY : scaleX;
	}

	@SuppressLint("DrawAllocation")
	@Override protected void onDraw(Canvas canvas) {
		if (mBitmap != null) {
			Matrix matrix = new Matrix();
			matrix.postScale(scale, scale);
			canvas.drawBitmap(mBitmap, matrix, null);
			//canvas.drawBitmap(mBitmap, 0,0, null);
		}
	}

	float lastX;
	float lastY;
	@Override public boolean onTouchEvent(MotionEvent event) {
		if (!in_edit_mode) return true;
		mPaint.setStrokeWidth(width/scale);

		float curX =  event.getX()/scale;
		float curY =  event.getY()/scale;
		switch (event.getAction()){ 
		case MotionEvent.ACTION_DOWN:{
			mCanvas.drawCircle(curX, curY,width/2/scale, mPaint);
			break;
		}
		case MotionEvent.ACTION_MOVE:{
			mCanvas.drawLine(lastX, lastY, curX, curY, mPaint);
			mCanvas.drawCircle(curX, curY,width/2/scale, mPaint);  //fix for weird jaggies occur between line start and line stop
			break;
		} 
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:{
			mCanvas.drawLine(lastX, lastY, curX, curY, mPaint);
			mCanvas.drawCircle(curX, curY,width/2/scale, mPaint);
			break;
		}

		}
		lastX = curX;
		lastY = curY;
		invalidate();  //invalidate only modified rect...

		return true;
	}
}