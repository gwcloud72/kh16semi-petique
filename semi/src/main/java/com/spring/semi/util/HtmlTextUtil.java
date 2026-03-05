package com.spring.semi.util;


/**
 * HtmlTextUtil - 공통 유틸.
 */
public class HtmlTextUtil {

	private HtmlTextUtil() {
	}

	private static String decodeBasic(String s) {
		if (s == null) return null;
		return s.replace("&nbsp;", " ")
				.replace("&lt;", "<")
				.replace("&gt;", ">")
				.replace("&amp;", "&")
				.replace("&quot;", "\"")
				.replace("&#39;", "'");
	}

	public static String toPlainText(String input) {
		if (input == null) return null;
		String s = decodeBasic(input);
		s = s.replaceAll("(?is)<(script|style)[^>]*>.*?</\\1>", " ");
		s = s.replaceAll("(?s)<[^>]+>", " ");
		s = decodeBasic(s);
		s = s.replaceAll("\\s+", " ").trim();
		return s;
	}

	public static String ellipsis(String input, int maxLen) {
		if (input == null) return null;
		String s = input.trim();
		if (maxLen <= 0) return "";
		if (s.length() <= maxLen) return s;
		return s.substring(0, maxLen) + "…";
	}
}
