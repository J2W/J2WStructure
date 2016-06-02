package j2w.team.common.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.List;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import j2w.team.J2WHelper;
import j2w.team.modules.log.L;

/**
 * Created by sky on 15/1/29 程序工具包
 */
public final class J2WAppUtil {

	/**
	 * 获取当前App内存使用量
	 * 
	 * @param context
	 *            上下文
	 * @return 使用量 (MB)
	 */

	public static final String getAppMemory(Context context) {
		try {
			// 获取当前PID
			int pid = android.os.Process.myPid();
			// 初始化返回值
			StringBuilder stringBuilderRmm = new StringBuilder();
			// 获取activity管理器
			ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			// 获取系统中所有正在运行的进程
			List<ActivityManager.RunningAppProcessInfo> appProcessInfos = mActivityManager.getRunningAppProcesses();
			// 循环检查-找到自己的进程
			for (ActivityManager.RunningAppProcessInfo appProcess : appProcessInfos) {
				// 检查是否是自己的进程
				if (appProcess.pid == pid) {
					// 从activity管理器中获取当前进程的内存数组
					Debug.MemoryInfo[] memoryInfo = mActivityManager.getProcessMemoryInfo(new int[] { appProcess.pid });
					// 换算
					long releaseMM = memoryInfo[0].getTotalPrivateDirty() * 1000;
					// 格式化显示
					stringBuilderRmm.append(Formatter.formatFileSize(context, releaseMM));
					break;
				}
			}
			return stringBuilderRmm.toString();
		} catch (Exception e) {
			return "无法检测";
		}
	}

	/**
	 * 获取泛型类型
	 * 
	 * @param clazz
	 * @return
	 */
	public static Class getClassGenricType(final Class clazz, final int index) {
		Type type = clazz.getGenericSuperclass();

		if (!(type instanceof ParameterizedType)) {
			return null;
		}
		// 强制类型转换
		ParameterizedType pType = (ParameterizedType) type;

		Type[] tArgs = pType.getActualTypeArguments();

		if (tArgs.length < 1) {
			return null;
		}

		return (Class) tArgs[index];
	}

	/**
	 * 通过反射, 获得定义Class时声明的父类的泛型参数的类型. 如无法找到, 返回Object.class. 1.因为获取泛型类型-所以增加逻辑判定
	 */
	public static Class<Object> getSuperClassGenricType(final Class clazz, final int index) {

		// 返回表示此 Class 所表示的实体（类、接口、基本类型或 void）的直接超类的 Type。
		Type[] genType = clazz.getGenericInterfaces();
		Type[] params = null;
		Type baseType = clazz.getGenericSuperclass();
		// 父类
		if (baseType != null && (baseType instanceof ParameterizedType)) {
			params = ((ParameterizedType) baseType).getActualTypeArguments();
			if (index >= params.length || index < 0) {
				return Object.class;
			}
			if (!(params[index] instanceof Class)) {
				return Object.class;
			}

			return (Class<Object>) params[index];
		}
		// 接口
		if (genType == null || genType.length < 1) {
			Type testType = clazz.getGenericSuperclass();
			if (!(testType instanceof ParameterizedType)) {
				return Object.class;
			}
			// 返回表示此类型实际类型参数的 Type 对象的数组。
			params = ((ParameterizedType) testType).getActualTypeArguments();
		} else {
			if (!(genType[index] instanceof ParameterizedType)) {
				return Object.class;
			}
			// 返回表示此类型实际类型参数的 Type 对象的数组。
			params = ((ParameterizedType) genType[index]).getActualTypeArguments();
		}

		if (index >= params.length || index < 0) {
			return Object.class;
		}
		if (!(params[index] instanceof Class)) {
			return Object.class;
		}

		return (Class<Object>) params[index];
	}

	/**
	 * 判断SDCard状态是否可以读写
	 */
	public static boolean isSDCardState() {
		final String state = Environment.getExternalStorageState();
		return state.equals(Environment.MEDIA_MOUNTED);
	}

	/**
	 * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
	 * 
	 * @return true 表示开启
	 */
	public static final boolean isOpenGps(Activity activity) {
		LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
		// 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
		boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		// 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
		// boolean network =
		// locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if (gps) {
			return true;
		}
		return false;
	}

	/**
	 * 判断当前手机是否联网
	 * 
	 * @return
	 */
	public static boolean isNetworkConnected(Activity activity) {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		return mNetworkInfo == null ? false : mNetworkInfo.isAvailable();
	}

	/**
	 * 判断当前联网状态是否为Wifi
	 * 
	 * @return
	 */
	public static boolean isWifiConnected(Activity activity) {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return mWiFiNetworkInfo == null ? false : mWiFiNetworkInfo.isAvailable();
	}

	/**
	 * 判断当前手机运营商网络是否可用
	 * 
	 * @return
	 */
	public static boolean isMobileConnected(Activity activity) {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mMobileNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		return mMobileNetworkInfo == null ? false : mMobileNetworkInfo.isAvailable();

	}

	/**
	 * 获取手机屏幕宽高
	 * 
	 * @return 显示器信息实体类
	 */
	public static final DisplayMetrics getWindowsSize(FragmentActivity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm;
	}

	/**
	 * 获取手机宽高
	 * 
	 * @return
	 */
	public static final DisplayMetrics getWindowsSize() {
		DisplayMetrics dm = J2WHelper.getInstance().getResources().getDisplayMetrics();
		return dm;
	}

	/**
	 * 需要权限 <uses-permission android:name="android.permission.WRITE_SETTINGS"/>
	 * 设置手机飞行模式
	 * 
	 * @param context
	 * @param enabling
	 *            true:设置为飞行模式 false:取消飞行模式
	 */
	public static void setAirplaneModeOn(Context context, boolean enabling) {
		Settings.System.putInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, enabling ? 1 : 0);
		Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		intent.putExtra("state", enabling);
		context.sendBroadcast(intent);
	}

	/**
	 * 判断手机是否是飞行模式
	 * 
	 * @param context
	 * @return true 飞行模式 false 不是飞行模式
	 */
	public static boolean isAirplaneMode(Context context) {
		int isAirplaneMode = Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0);
		return (isAirplaneMode == 1) ? true : false;
	}

	/**
	 * 检查Sim卡
	 * 
	 * @param context
	 * @return true 无卡 false 有卡
	 */
	public static boolean isSimMode(Context context) {
		TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
		int state = mTelephonyManager.getSimState();
		switch (state) {
			case TelephonyManager.SIM_STATE_UNKNOWN:
			case TelephonyManager.SIM_STATE_ABSENT:
				return true;
		}
		return false;
	}

	/**
	 * PX 转换DP
	 * 
	 * @param context
	 * @param px
	 * @return
	 */
	public static int getDIP(Context context, int px) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, dm);
	}

	/**
	 * PX 转换 SP
	 * 
	 * @param context
	 * @param px
	 * @return
	 */
	public static int getSP(Context context, int px) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, px, dm);
	}

	/**
	 * 获取状态栏高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getStatusBarHeight(Context context) {
		int result = 0;
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	/**
	 * 意图响应检查
	 * 
	 * @param context
	 * @param intent
	 * @return
	 */
	public static boolean checkResponseIntent(Context context, Intent intent) {
		if (context == null || intent == null) return false;
		List<ResolveInfo> activitys = context.getPackageManager().queryIntentActivities(intent, 10);
		return activitys.size() > 0;
	}

	/**
	 * 键盘自动关闭
	 *
	 * @param ev
	 * @return
	 */
	public static void keyBoardAutoHidden(MotionEvent ev, Activity activiy) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			// 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
			View v = activiy.getCurrentFocus();
			if (J2WKeyboardUtils.isShouldHideInput(v, ev)) {
				J2WKeyboardUtils.hideSoftInput(activiy);
			}
		}
	}

	/**
	 * 设置全屏
	 */
	public static void openFullScreen(Activity activity) {

		WindowManager.LayoutParams lp = activity.getWindow().getAttributes();

		lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;

		activity.getWindow().setAttributes(lp);

		activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

	}

	/**
	 * 关闭全屏
	 * 
	 * @param activity
	 */
	public static void closeFullScreen(Activity activity) {
		WindowManager.LayoutParams attr = activity.getWindow().getAttributes();

		attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);

		activity.getWindow().setAttributes(attr);

		activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
	}

	/**
	 * 判断是否运行
	 * 
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static boolean isAppAlive(Context context, String packageName) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> processInfos = activityManager.getRunningAppProcesses();
		for (int i = 0; i < processInfos.size(); i++) {
			if (processInfos.get(i).processName.equals(packageName)) {
				L.i("J2WNotificationLaunch", String.format("the %s is running, isAppAlive return true", packageName));
				return true;
			}
		}
		L.i("J2WNotificationLaunch", String.format("the %s is not running, isAppAlive return false", packageName));
		return false;
	}

	/**
	 * 后台唤醒到前台
	 * 
	 * @param context
	 * @param clazz
	 */
	public static void awaken(Context context, Class clazz, Bundle bundle) {
		Intent mainIntent = new Intent(context, clazz);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		mainIntent.setAction(Intent.ACTION_MAIN);
		mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		if (bundle != null) {
			mainIntent.putExtras(bundle);
		}
		context.startActivity(mainIntent);
	}
}
