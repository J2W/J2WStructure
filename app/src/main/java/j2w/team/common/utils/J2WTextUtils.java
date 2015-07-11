package j2w.team.common.utils;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import j2w.team.J2WHelper;

/**
 * @创建人 sky
 * @创建时间 15/4/24 上午10:33
 * @类描述 文本工具类
 */
public final class J2WTextUtils {

	/**
	 * 修改文本颜色
	 * 
	 * @param value
	 *            文本
	 * @param color
	 *            颜色
	 * @param startIndex
	 *            开始
	 * @param endIndex
	 *            结束
	 * @return
	 */
	public static final SpannableStringBuilder changeTextColor(String value, int color, int startIndex, int endIndex) {
		SpannableStringBuilder spannable = new SpannableStringBuilder(value);
		spannable.setSpan(new ForegroundColorSpan(J2WHelper.getInstance().getResources().getColor(color)), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		return spannable;
	}

	public static boolean isEmpty(CharSequence text) {
		return null == text || text.length() == 0;
	}

}
