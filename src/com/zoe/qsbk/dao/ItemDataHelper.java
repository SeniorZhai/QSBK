package com.zoe.qsbk.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.v4.content.CursorLoader;

import com.zoe.qsbk.database.Column;
import com.zoe.qsbk.database.SQLiteTable;
import com.zoe.qsbk.type.Category;
import com.zoe.qsbk.type.Item;

public class ItemDataHelper extends BaseDataHelper {
	private Category mCategory; // 根据类别新建数据库表

	public ItemDataHelper(Context context, Category category) {
		super(context);
		mCategory = category;
	}

	@Override
	protected Uri getContentUri() {
		return DataProvider.ITEM_CONTENT_URI;
	}

	// item -> contentVlaues
	private ContentValues getContentValues(Item item) {
		ContentValues values = new ContentValues();
		values.put(ItemDBInfo.ID, item.getId());
		values.put(ItemDBInfo.CATEGORY, mCategory.ordinal());
		values.put(ItemDBInfo.JSON, item.toJson());
		return values;
	}

	// 查询
	public Item query(long id) {
		Item item = null;
		Cursor cursor = query(
				null,
				ItemDBInfo.CATEGORY + "=?" + " AND " + ItemDBInfo.ID + "= ?",
				new String[] { String.valueOf(mCategory.ordinal()),
						String.valueOf(id) }, null);
		if (cursor.moveToFirst()) {
			item = Item.fromCursor(cursor);
		}
		cursor.close();
		return item;
	}

	// 批量插入
	public void bulkInsert(List<Item> items) {
		ArrayList<ContentValues> contentValues = new ArrayList<ContentValues>();
		for (Item item : items) {
			ContentValues values = getContentValues(item);
			contentValues.add(values);
		}
		ContentValues[] valueArray = new ContentValues[contentValues.size()];
		// 数组->list内容相同的类型
		bulkInsert(contentValues.toArray(valueArray));
	}

	// 删除所有内容
	public int deleteAll() {
		synchronized (DataProvider.DBLock) {
			DataProvider.DBHelper mDBHelper = DataProvider.getDBHelper();
			SQLiteDatabase db = mDBHelper.getWritableDatabase();
			int row = db.delete(ItemDBInfo.TABLE_NAME, ItemDBInfo.CATEGORY
					+ "=?",
					new String[] { String.valueOf(mCategory.ordinal()) });
			return row;
		}
	}

	public CursorLoader getCursorLoader() {
		// 获取CursorLoader 根据category查询，按_id排序
		return new CursorLoader(getContext(), getContentUri(), null,
				ItemDBInfo.CATEGORY + "=?",
				new String[] { String.valueOf(mCategory.ordinal()) },
				ItemDBInfo._ID + " ASC");
		// ASC 为排序法则
	}

	public static final class ItemDBInfo implements BaseColumns {
		private ItemDBInfo() {
		}
		public static final String TABLE_NAME = "item";
		public static final String ID = "id";
		public static final String CATEGORY = "category";
		public static final String JSON = "json";

		public static final SQLiteTable TABLE = new SQLiteTable(TABLE_NAME)
				.addColumn(ID, Column.DataType.INTEGER)
				.addColumn(CATEGORY, Column.DataType.INTEGER)
				.addColumn(JSON, Column.DataType.TEXT);
	}
}
