package com.zoe.qsbk.type;

import java.util.HashMap;

import android.database.Cursor;

import com.google.gson.Gson;
import com.zoe.qsbk.dao.ItemDataHelper.ItemDBInfo;

public class Item {
	private static final HashMap<Long, Item> CACHE = new HashMap<Long, Item>();
	// 可能为空
	private User user;
	private String image;
	private ImageSize image_size;
	// 发布时间
	private long published_at;
	private String tag;

	private long id;
	private Votes votes;
	private long created_at;
	private String content;
	private String state;
	private int comments_count;
	private boolean allow_comment;

	public String toJson() {
		return new Gson().toJson(this);
	}

	private static void addToCache(Item item) {
		CACHE.put(item.getId(), item);
	}

	private static Item getFromCache(long id) {
		return CACHE.get(id);
	}

	public static Item fromCursor(Cursor cursor) {
		long id = cursor.getLong(cursor.getColumnIndex(ItemDBInfo._ID));
		Item item = getFromCache(id);
		if (item != null) {
			return item;
		}
		item = new Gson().fromJson(
				cursor.getString(cursor.getColumnIndex(ItemDBInfo.JSON)),
				Item.class);
		addToCache(item);
		return item;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public long getPublished_at() {
		return published_at;
	}

	public void setPublished_at(long published_at) {
		this.published_at = published_at;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public ImageSize getImage_size() {
		return image_size;
	}

	public void setImage_size(ImageSize image_size) {
		this.image_size = image_size;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Votes getVotes() {
		return votes;
	}

	public void setVotes(Votes votes) {
		this.votes = votes;
	}

	public long getCreated_at() {
		return created_at;
	}

	public void setCreated_at(long created_at) {
		this.created_at = created_at;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public int getComments_count() {
		return comments_count;
	}

	public void setComments_count(int comments_count) {
		this.comments_count = comments_count;
	}

	public boolean isAllow_comment() {
		return allow_comment;
	}

	public void setAllow_comment(boolean allow_comment) {
		this.allow_comment = allow_comment;
	}

	public class Votes {
		private int down;
		private int up;

		public int getDown() {
			return down;
		}

		public void setDown(int down) {
			this.down = down;
		}

		public int getUp() {
			return up;
		}

		public void setUp(int up) {
			this.up = up;
		}
	}

	public class ImageSize {
		private int[] s;
		private int[] m;

		public int[] getS() {
			return s;
		}

		public void setS(int[] s) {
			this.s = s;
		}

		public int[] getM() {
			return m;
		}

		public void setM(int[] m) {
			this.m = m;
		}
	}
}
