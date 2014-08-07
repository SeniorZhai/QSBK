package com.zoe.qsbk.api;

public class Api {
	private static final String host = "http://m2.qiushibaike.com/article/list";
	public static final String ITEM_LIST = host + "/%s?count=30&page=%d";

	public static String ImageURL(String str) {
		String s1 = str.substring(3, 7);
		String s2 = str.substring(3,11);
		return String.format(
				"http://pic.qiushibaike.com/system/pictures/%s/%s/medium/%s",
				s1, s2, str);
	}
	public static String Avtnew(String id,String icon){
		String s1 = id.substring(0,4);
		return String.format("http://pic.qiushibaike.com/system/avtnew/%s/%s/medium/%s",s1,id,icon);
	}
}
