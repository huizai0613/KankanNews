package com.kankan.kankanews.utils;

import java.util.HashSet;
import java.util.Set;
 

public class NewsBrowseUtils {
	private static Set<String> newsHasBrowsedList = new HashSet<String>();
	
	public static void hasBrowedNews(String newsId){
		newsHasBrowsedList.add(newsId);
	}
	
	public static boolean isBrowed(String newsId){
		return newsHasBrowsedList.contains(newsId);
	}
}
