package com.zoe.qsbk.type;

import java.util.ArrayList;

public class Feed {
	// 当前页内容数
	private int count;
	// 总数
	private int total;
	// 当前页
    private int page;
    private ArrayList<Item> items;
    
	public int getCount() {
		return count;
	}
	public ArrayList<Item> getItems() {
		return items;
	}
	public void setItems(ArrayList<Item> items) {
		this.items = items;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
}
