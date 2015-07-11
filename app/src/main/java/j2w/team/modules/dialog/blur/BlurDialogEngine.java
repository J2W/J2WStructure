package j2w.team.modules.dialog.blur;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * jc
 */
public class BlurDialogEngine {

	private static final String TAG = BlurDialogEngine.class.getSimpleName();

	private static final float BLUR_DOWN_SCALE_FACTOR = 4.0f;

	private static final int BLUR_RADIUS = 8;

	private ImageView mBlurredBackgroundView;

	private FrameLayout.LayoutParams mBlurredBackgroundLayoutParams;

	private BlurAsyncTask mBluringTask;

	private boolean mDebudEnable = false;

	private float mDownScaleFactor = BLUR_DOWN_SCALE_FACTOR;

	private int mBlurRadius = BLUR_RADIUS;

	private Activity mHoldingActivity;

	public BlurDialogEngine(Activity holdingActivity) {
		mHoldingActivity = holdingActivity;
	}

	public void onResume(boolean retainedInstance) {
		if (mBlurredBackgroundView == null || retainedInstance) {
			mBluringTask = new BlurAsyncTask();
			mBluringTask.execute();
		}
	}

	public void onDismiss() {
		if (mBlurredBackgroundView != null) {
			mBlurredBackgroundView.setVisibility(View.GONE);
			mBlurredBackgroundView = null;
		}

        if(mBluringTask != null){
            mBluringTask.cancel(true);
            mBluringTask = null;
        }

	}

	public void onDestroy() {
		mHoldingActivity = null;
	}

	public void debug(boolean enable) {
		mDebudEnable = enable;
	}
	public void setDownScaleFactor(float factor) {
		if (factor >= 1.0f) {
			mDownScaleFactor = factor;
		} else {
			mDownScaleFactor = 1.0f;
		}
	}

	public void setBlurRadius(int radius) {
		if (radius >= 0) {
			mBlurRadius = radius;
		} else {
			mBlurRadius = 0;
		}
	}
	private void blur(Bitmap bkg, View view) throws Exception {
		long startMs = System.currentTimeMillis();
		mBlurredBackgroundLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

		Bitmap overlay = null;

		int actionBarHeight = 0;
		try {
			if (mHoldingActivity instanceof ActionBarActivity) {
				ActionBar supportActionBar = ((ActionBarActivity) mHoldingActivity).getSupportActionBar();
				if (supportActionBar != null) {
						actionBarHeight = supportActionBar.getHeight();
				}
			} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				android.app.ActionBar actionBar = mHoldingActivity.getActionBar();
				if (actionBar != null) {
					actionBarHeight = actionBar.getHeight();
				}
			}
		} catch (NoClassDefFoundError e) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				android.app.ActionBar actionBar = mHoldingActivity.getActionBar();
				if (actionBar != null) {
					actionBarHeight = actionBar.getHeight();
				}
			}
		}
		int statusBarHeight = 0;
		if ((mHoldingActivity.getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) == 0) {
			statusBarHeight = getStatusBarHeight();
		}

		final int topOffset = actionBarHeight + statusBarHeight;
		final int bottomOffset = getNavigationBarOffset();

		Rect srcRect = new Rect(0, actionBarHeight + statusBarHeight, bkg.getWidth(), bkg.getHeight() - bottomOffset);

		overlay = Bitmap.createBitmap((int) ((view.getWidth()) / mDownScaleFactor), (int) ((view.getMeasuredHeight() - topOffset - bottomOffset) / mDownScaleFactor), Bitmap.Config.RGB_565);

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB || mHoldingActivity instanceof ActionBarActivity) {
			mBlurredBackgroundLayoutParams.setMargins(0, 0, 0, 0);
			mBlurredBackgroundLayoutParams.gravity = Gravity.TOP;
		}

		Canvas canvas = new Canvas(overlay);
		Paint paint = new Paint();
		paint.setFlags(Paint.FILTER_BITMAP_FLAG);

		final RectF destRect = new RectF(0, 0, overlay.getWidth(), overlay.getHeight());

		canvas.drawBitmap(bkg, srcRect, destRect, paint);

		overlay = FastBlurHelper.doBlur(overlay, mBlurRadius, false);

		if (mDebudEnable) {
			String blurTime = (System.currentTimeMillis() - startMs) + " ms";

			Log.d(TAG, "Radius : " + mBlurRadius);
			Log.d(TAG, "Down Scale Factor : " + mDownScaleFactor);
			Log.d(TAG, "Blurred achieved in : " + blurTime);
			Log.d(TAG, "Allocation : " + bkg.getRowBytes() + "ko (screen capture) + " + overlay.getRowBytes() + "ko (FastBlur)");
			// display blurring time directly on screen
			Rect bounds = new Rect();
			Canvas canvas1 = new Canvas(overlay);
			paint.setColor(Color.BLACK);
			paint.setAntiAlias(true);
			paint.setTextSize(20.0f);
			paint.getTextBounds(blurTime, 0, blurTime.length(), bounds);
			canvas1.drawText(blurTime, 2, bounds.height(), paint);
		}

		mBlurredBackgroundView = new ImageView(mHoldingActivity);
		mBlurredBackgroundView.setImageDrawable(new BitmapDrawable(mHoldingActivity.getResources(), overlay));
	}

	private int getStatusBarHeight() {
		int result = 0;
		int resourceId = mHoldingActivity.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = mHoldingActivity.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	private int getNavigationBarOffset() {
		int result = 0;
		Resources resources = mHoldingActivity.getResources();
		return result;
	}

	public class BlurAsyncTask extends AsyncTask<Void, Void, Void> {

		private Bitmap mBackground;

		private View mBackgroundView;

		@Override protected void onPreExecute() {
			super.onPreExecute();

			mBackgroundView = mHoldingActivity.getWindow().getDecorView();

			Rect rect = new Rect();
			mBackgroundView.getWindowVisibleDisplayFrame(rect);
			mBackgroundView.destroyDrawingCache();
			mBackgroundView.setDrawingCacheEnabled(true);
			mBackgroundView.buildDrawingCache(true);
			mBackground = mBackgroundView.getDrawingCache(true);
			if (mBackground == null) {
				mBackgroundView.measure(View.MeasureSpec.makeMeasureSpec(rect.width(), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(rect.height(), View.MeasureSpec.EXACTLY));
				mBackgroundView.layout(0, 0, mBackgroundView.getMeasuredWidth(), mBackgroundView.getMeasuredHeight());
				mBackgroundView.destroyDrawingCache();
				mBackgroundView.setDrawingCacheEnabled(true);
				mBackgroundView.buildDrawingCache(true);
				mBackground = mBackgroundView.getDrawingCache(true);
			}
		}

		@Override protected Void doInBackground(Void... params) {

			try {
				blur(mBackground, mBackgroundView);
			} catch (Exception e) {
                recycle();
			}
            recycle();
			return null;
		}

        private void recycle(){
            if (mBackground != null) {
                mBackground.recycle();
            }
            if (mBackgroundView != null) {
                mBackgroundView.destroyDrawingCache();
                mBackgroundView.setDrawingCacheEnabled(false);
            }
        }
		@Override protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);


			if (mHoldingActivity != null) {
                Window  window = mHoldingActivity.getWindow();
                if (window != null && mBlurredBackgroundView != null) {
                    window.addContentView(mBlurredBackgroundView, mBlurredBackgroundLayoutParams);
                }
			}

			mBackgroundView = null;
			mBackground = null;
		}
	}
}
