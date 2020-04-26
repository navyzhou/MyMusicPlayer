package com.yc.mysicplayer.util;

public class StringUtil {
	/**
	 * 为空判断
	 * @param strs
	 * @return
	 */
	public static boolean checkNull(String ... strs) {
		if (strs == null || strs.length <= 0) {
			return true;
		}
		
		for (String str : strs) {
			if (str == null || "".equals(str)) {
				return true;
			}
		}
		return false;
	}
}
