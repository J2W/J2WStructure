package j2w.team.common.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

public class J2WApnUtil {
  /** APN_URI */
  static Uri APN_URI = Uri.parse("content://telephony/carriers");
  /** PREFERRED_APN_URI */
  static final Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");
  /** ID */
  private static final String ID = "_id";
  /** APN */
  private static final String APN = "apn";
  /** TYPE */
  private static final String TYPE = "type";
  /** NAME */
  private static final String NAME = "name";
  /** CURRENT */
  private static final String CURRENT = "current";
  /** PROXY */
  private static final String PROXY = "proxy";
  /** PROXY */
  private static String projection[] = { "name,_id,apn,type,mcc,mnc,current,proxy,port" };

  /** TODO */
  public static final String WAP = "wap";

  /** TODO */
  public static final String CM_NET = "cmnet";

  /** TODO */
  public static final String CM_WAP = "cmwap";

  /** TODO */
  public static final String CT_NET = "ctnet";

  /** TODO */
  public static final String CT_WAP = "ctwap";

  /** TODO */
  public static final String UN_3G_NET = "3gnet";

  /** TODO */
  public static final String UN_3G_WAP = "3gwap";

  /** TODO */
  public static final String UN_NET = "uninet";

  /** TODO */
  public static final String UN_WAP = "uniwap";

  /** TODO */
  public static final String CMWAP_PROXY = "10.0.0.172";

  /** TODO */
  public static final String CTWAP_PROXY = "10.0.0.200";

  /** TODO */
  public static final String UNWAP_PROXY = "xxx";

  /** TODO */
  public static final int DEFAULT_PORT = 80;

  public static class APN {
    String id;
    String name;
    String apn;
    String type;
    String mcc;
    String mnc;
    String current;
    String proxy;
    String port;
  }

  /**
   * 判断APN是否连接
   * 
   * @param context
   * @return boolean
   */
  public static boolean isActivityApnConnected(Context context) {
    boolean isConnected = false;
    ConnectivityManager conMgr = (ConnectivityManager) (context.getSystemService(Context.CONNECTIVITY_SERVICE));
    NetworkInfo info = conMgr.getActiveNetworkInfo();

    if (null != info) {
      isConnected = info.isConnected();
    }

    return isConnected;
  }

  /**
   * 获取Proxy
   * 
   * @param context
   * @return boolean
   */
  public static HttpHost getProxyOld(Context context) {
    long id = getCurrentApnInUse(context);
    List<APN> list = getAPNList(context);
    for (APN apn : list) {
      if (String.valueOf(id).equals(apn.id)) {
        if (apn.proxy != null && apn.proxy.trim().length() > 0)
          if (apn.port != null && apn.port.trim().length() > 0) {
            return new HttpHost(apn.proxy, Integer.valueOf(apn.port));
          }
        return null;
      }
    }
    return null;
  }

  public static HttpHost getProxy(Context context) {
    HttpHost proxy = null;
    String apnName = getActivityApnType(context);

    if (apnName != null) {
      if (apnName.equalsIgnoreCase(CM_WAP)) {
        proxy = new HttpHost(J2WApnUtil.CMWAP_PROXY, J2WApnUtil.DEFAULT_PORT);
      } else if (apnName.equalsIgnoreCase(CT_WAP)) {
        // proxy = new HttpHost(J2WApnUtil.CTWAP_PROXY,
        // J2WApnUtil.DEFAULT_PORT);
        // 2011-12-22 @fuqiangyang 电信wap与net网络融合无需代理，否则下载大文件时会受到wap网关限制
        proxy = null;
      } else if (apnName.equalsIgnoreCase(UN_WAP)) {
        proxy = new HttpHost(J2WApnUtil.CMWAP_PROXY, J2WApnUtil.DEFAULT_PORT);
      } else if (apnName.equalsIgnoreCase(UN_3G_WAP)) {
        proxy = new HttpHost(J2WApnUtil.CMWAP_PROXY, J2WApnUtil.DEFAULT_PORT);
      }
    }

    return proxy;
  }

  public static String getActivityApnType(Context context) {
    String apnType = null;
    ConnectivityManager conMgr = (ConnectivityManager) (context.getSystemService(Context.CONNECTIVITY_SERVICE));
    NetworkInfo info = conMgr.getActiveNetworkInfo();

    if (null == info) {
      apnType = null;
    } else {
      apnType = info.getExtraInfo();
    }

    return apnType;
  }

  /**
   * 获取正在使用的APN
   * 
   * @param context
   * @return boolean
   */
  private static long getCurrentApnInUse(Context context) {
    long ret = -1;
    Cursor cursor = context.getContentResolver().query(PREFERRED_APN_URI,
        new String[] { ID, NAME, APN, TYPE, CURRENT, PROXY }, null, null, null);
    cursor.moveToFirst();
    if (cursor.isAfterLast()) {
      return ret;
    }
    ret = cursor.getLong(0);

    String apnStr = "id=" + cursor.getLong(0) + ",name=" + cursor.getShort(1) + ",apn=" + cursor.getString(2)
        + ",type=" + cursor.getString(3) + ",current=" + cursor.getString(4) + ",proxy=" + cursor.getString(5);
    // Log.i("test", apnStr);
    return ret;
  }

  /**
   * 获取APN列表
   * 
   * @param context
   * @return boolean
   */
  private static List<APN> getAPNList(Context context) {
    String tag = "ApnSelect.getAPNList()";
    // current��Ϊ�ձ�ʾ����ʹ�õ�APN
    Cursor cr = context.getContentResolver().query(APN_URI, projection, null, null, null);

    List<APN> list = new ArrayList<APN>();

    while (cr != null && cr.moveToNext()) {
      APN a = new APN();
      a.name = cr.getString(cr.getColumnIndex("name"));
      a.id = cr.getString(cr.getColumnIndex("_id"));
      a.apn = cr.getString(cr.getColumnIndex("apn"));
      a.type = cr.getString(cr.getColumnIndex("type"));
      a.mcc = cr.getString(cr.getColumnIndex("mcc"));
      a.mnc = cr.getString(cr.getColumnIndex("mnc"));
      a.current = cr.getString(cr.getColumnIndex("current"));
      a.proxy = cr.getString(cr.getColumnIndex("proxy"));
      a.port = cr.getString(cr.getColumnIndex("port"));
      if (a.current != null && a.current.equals("1"))
        list.add(a);
    }
    if (cr != null)
      cr.close();
    return list;
  }

}
