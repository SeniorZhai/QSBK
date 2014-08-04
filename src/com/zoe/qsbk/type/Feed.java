package com.zoe.qsbk.type;

import java.util.List;

public class Feed {
	private int count;
	private int total;
    private int page;
    private List<Item> items;
    
	public int getCount() {
		return count;
	}
	public List<Item> getItems() {
		return items;
	}
	public void setItems(List<Item> items) {
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
