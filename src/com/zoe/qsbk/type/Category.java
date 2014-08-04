package com.zoe.qsbk.type;

public enum Category {
	suggest("干货"),latest("嫩草"),day("日"),week("周"),month("月"),imgrank("硬菜"),images("时令");
//	popular("Popular"), everyone("Everyone"), debuts("Debuts");
	private String mDisplayName;
	private Category(String displayName) {
		mDisplayName = displayName;
	}
	
	public String getDisplayName() {
        return mDisplayName;
    }
}
