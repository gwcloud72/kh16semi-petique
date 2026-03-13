package com.spring.semi.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * HtmlMediaUtil - 공통 유틸.
 */
public class HtmlMediaUtil {

	public static Set<Integer> extractMediaNos(String html) {
		Document document = Jsoup.parse(html == null ? "" : html);
		Elements elements = document.select(".custom-image[data-pk]");
		if (elements.isEmpty()) return Collections.emptySet();

		Set<Integer> result = new HashSet<>();
		for (Element element : elements) {
			String pk = element.attr("data-pk");
			if (pk == null || pk.isBlank()) continue;
			if (!pk.matches("\\d+")) continue;
			result.add(Integer.valueOf(pk));
		}
		return result;
	}
}
