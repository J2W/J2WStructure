package j2w.team.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.google.common.base.Preconditions;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import butterknife.ButterKnife;
import j2w.team.biz.J2WIDisplay;
import j2w.team.common.log.L;
import j2w.team.common.utils.KeyboardUtils;
import j2w.team.J2WHelper;
import j2w.team.biz.J2WIBiz;
import j2w.team.biz.J2WBizUtils;

/**
 * @创建人 sky
 * @创建时间 15/7/8 上午12:15
 * @类描述 activity
 */
public abstract class J2WActivity<D extends J2WIDisplay> extends ActionBarActivity {

	/**
	 * 定制对话框
	 * 
	 * @param initialBuilder
	 * @return
	 **/
	protected abstract Builder build(Builder initialBuilder);

	/**
	 * 初始化数据
	 *
	 * @param savedInstanceState
	 *            数据
	 */
	protected abstract void initData(Bundle savedInstanceState);

	/** View层编辑器 **/
	private Builder				builder;

	/** 业务逻辑对象 **/
	private Map<String, Object>	stackBiz	= null;

	/** 显示调度对象 **/
	private D					display		= null;

	/**
	 * 初始化
	 * 
	 * @param savedInstanceState
	 */
	@Override protected final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/** 初始化视图 **/
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		builder = new Builder(this, inflater);
		setContentView(build(builder).create());
		/** 初始化所有组建 **/
		ButterKnife.bind(this);
		/** 添加到堆栈 **/
		J2WHelper.screenHelper().pushActivity(this);
		/** 初始化视图 **/
		J2WHelper.getInstance().onCreate(this, savedInstanceState);
		/** 初始化业务 **/
		attachBiz();
		/** 初始化视图组建 **/
		initData(savedInstanceState);
	}

	@Override protected void onStart() {
		super.onStart();
		J2WHelper.getInstance().onStart(this);
	}

	@Override protected void onResume() {
		super.onResume();
		attachBiz();
		if (builder.isOpenEventBus()) {
			J2WHelper.getEventBus().register(this);
		}
		J2WHelper.getInstance().onResume(this);
	}

	@Override protected void onPause() {
		super.onPause();
		detachBiz();
		J2WHelper.getInstance().onPause(this);
	}

	@Override protected void onRestart() {
		super.onRestart();
		J2WHelper.getInstance().onRestart(this);
	}

	@Override protected void onStop() {
		super.onStop();
		J2WHelper.getInstance().onStop(this);
	}

	@Override protected void onDestroy() {
		super.onDestroy();
		/** 判断EventBus 然后销毁 **/
		if (builder.isOpenEventBus()) {
			J2WHelper.getEventBus().unregister(this);
		}
		/** 从堆栈里移除 **/
		J2WHelper.screenHelper().popActivity(this);
		J2WHelper.getInstance().onDestroy(this);
	}

	/**
	 * 获取显示调度
	 * 
	 * @param objects
	 *            参数
	 * @return
	 */
	public D display(Object... objects) {
		if (objects.length > 0) {
			display.setActivity(this, objects);
		}
		return display;
	}

	/**
	 * 获取业务
	 * 
	 * @param biz
	 *            泛型
	 * @param <B>
	 * @return
	 */
	public <B extends J2WIBiz> B biz(Class<B> biz) {
		Preconditions.checkNotNull(biz, "请指定业务接口～");
		if (stackBiz.get(biz.getSimpleName()) == null) {// 如果没有索索到
			stackBiz.put(biz.getSimpleName(), J2WBizUtils.createBiz(biz, this, display));
		}
		return (B) stackBiz.get(biz.getSimpleName());
	}


	/**
	 * 业务初始化
	 */
	synchronized final void attachBiz() {
		if (stackBiz == null) {
			stackBiz = new HashMap<>();
		}
		/** 创建业务类 **/
		if (display == null) {
			display = J2WBizUtils.createDisplay(this);
		}
	}

	/**
	 * 业务分离
	 */
	synchronized final void detachBiz() {
		for (Object b : stackBiz.values()) {
			((J2WIBiz) b).detach();
		}
		stackBiz.clear();
		stackBiz = null;
		display = null;
	}

	/**
	 * 屏幕点击事件 - 关闭键盘
	 *
	 * @param ev
	 * @return
	 */
	@Override public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			// 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
			View v = getCurrentFocus();
			if (KeyboardUtils.isShouldHideInput(v, ev)) {
				KeyboardUtils.hideSoftInput(J2WHelper.screenHelper().currentActivity());
			}
		}
		return super.dispatchTouchEvent(ev);
	}

	/**
	 * 自定义对话框生成器
	 */
	protected static class Builder {

		/** 上下文 **/
		private final Context			mContext;

		/** 布局加载器 **/
		private final LayoutInflater	mInflater;

		/**
		 * EventBus开关
		 */
		private boolean					isOpenEventBus;

		boolean isOpenEventBus() {
			return isOpenEventBus;
		}

		public void setIsOpenEventBus(boolean isOpenEventBus) {
			this.isOpenEventBus = isOpenEventBus;
		}

		/**
		 * 布局ID
		 */
		private int	layoutId;

		int getLayoutId() {
			return layoutId;
		}

		public void setLayoutId(int layoutId) {
			this.layoutId = layoutId;
		}

		/**
		 * 构造器
		 *
		 * @param context
		 * @param inflater
		 */
		public Builder(Context context, LayoutInflater inflater) {
			this.mContext = context;
			this.mInflater = inflater;
		}

		/**
		 * 创建
		 *
		 * @return
		 */
		View create() {
			L.i("Builder.create()");
			Preconditions.checkArgument(getLayoutId() > 0, "请给出布局文件ID");
			View view = mInflater.inflate(getLayoutId(), null, false);
			Preconditions.checkNotNull(view, "无法根据布局文件ID,获取View");

			return view;
		}

	}
}