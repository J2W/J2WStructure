package j2w.team.common.utils;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Debug;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.util.List;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import j2w.team.J2WHelper;

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
	public static final boolean isOpenGps() {
		LocationManager locationManager = (LocationManager) J2WHelper.screenHelper().currentActivity().getSystemService(Context.LOCATION_SERVICE);
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
	public static boolean isNetworkConnected() {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) J2WHelper.screenHelper().currentActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		return mNetworkInfo == null ? false : mNetworkInfo.isAvailable();
	}

	/**
	 * 判断当前联网状态是否为Wifi
	 * 
	 * @return
	 */
	public static boolean isWifiConnected() {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) J2WHelper.screenHelper().currentActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return mWiFiNetworkInfo == null ? false : mWiFiNetworkInfo.isAvailable();
	}

	/**
	 * 判断当前手机运营商网络是否可用
	 * 
	 * @return
	 */
	public static boolean isMobileConnected() {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) J2WHelper.screenHelper().currentActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mMobileNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		return mMobileNetworkInfo == null ? false : mMobileNetworkInfo.isAvailable();

	}

	/**
	 * 获取手机屏幕宽高
	 * 
	 * @return 显示器信息实体类
	 */
	public static final DisplayMetrics getWindowsSize() {
		DisplayMetrics dm = new DisplayMetrics();
		J2WHelper.screenHelper().currentActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
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
}
