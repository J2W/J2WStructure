package j2w.team.common.view;

import java.util.HashMap;
import java.util.LinkedHashMap;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.nineoldandroids.view.ViewHelper;

/**
 * Created by sky on 15/3/8. 自定义viewpager
 */
public class J2WViewPager extends ViewPager {

	public static final String			TAG			= "J2WViewPager";

	private boolean						isCanScroll	= true;

	private HashMap<Integer, Object>	mObjs		= new LinkedHashMap<Integer, Object>();

	public J2WViewPager(Context context) {
		this(context, null);
	}

	public J2WViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	private View	mLeft;

	private View	mRight;

	protected void animateFade(View left, View right, float positionOffset) {
		if (left != null) {
			ViewHelper.setAlpha(left, 1 - positionOffset);
		}
		if (right != null) {
			ViewHelper.setAlpha(right, positionOffset);
		}
	}

	public void setScanScroll(boolean isCanScroll) {
		this.isCanScroll = isCanScroll;
	}

	@Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

		mLeft = findViewFromObject(position);
		mRight = findViewFromObject(position + 1);

		animateFade(mLeft, mRight, positionOffset);

		super.onPageScrolled(position, positionOffset, positionOffsetPixels);

	}

	public void setObjectForPosition(Object obj, int position) {
		mObjs.put(Integer.valueOf(position), obj);
	}

	public View findViewFromObject(int position) {
		Object o = mObjs.get(Integer.valueOf(position));
		if (o == null) {
			return null;
		}
		PagerAdapter a = getAdapter();
		View v;
		for (int i = 0; i < getChildCount(); i++) {
			v = getChildAt(i);
			if (a.isViewFromObject(v, o)) return v;
		}
		return null;
	}

	@Override public boolean onTouchEvent(MotionEvent arg0) {
		// TODO Auto-generated method stub
		if (isCanScroll) {
			return super.onTouchEvent(arg0);
		} else {
			return false;
		}

	}

	@Override public void setCurrentItem(int item, boolean smoothScroll) {
		// TODO Auto-generated method stub
		super.setCurrentItem(item, smoothScroll);
	}

	@Override public void setCurrentItem(int item) {
		// TODO Auto-generated method stub
		super.setCurrentItem(item);
	}

	@Override public boolean onInterceptTouchEvent(MotionEvent arg0) {
		// TODO Auto-generated method stub
		if (isCanScroll) {
			return super.onInterceptTouchEvent(arg0);
		} else {
			return false;
		}

	}
}
