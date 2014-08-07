package com.zoe.qsbk.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

public abstract class BaseDataHelper {
	private Context mContext;

	public BaseDataHelper(Context context) {
		mContext = context;
	}

	public Context getContext() {
		return mContext;
	}
	// 获取ContentUri
	protected abstract Uri getContentUri();
	// 更新内容
	public void notifyChange() {
		mContext.getContentResolver().notifyChange(getContentUri(), null);
	}
	// 查询
	protected final Cursor query(Uri uri, String[] projection,
			String selection, String[] selectionArgs, String sortOrder) {
		return mContext.getContentResolver().query(uri, projection, selection,
				selectionArgs, sortOrder);
	}

	protected final Cursor query(String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		return mContext.getContentResolver().query(getContentUri(), projection,
				selection, selectionArgs, sortOrder);
	}
	// 插入
	protected final Uri insert(ContentValues values) {
		return mContext.getContentResolver().insert(getContentUri(), values);
	}
	// 批量插入
	protected final int bulkInsert(ContentValues[] values) {
		return mContext.getContentResolver()
				.bulkInsert(getContentUri(), values);
	}
	// 更新
	protected final int update(ContentValues values, String where,
			String[] whereArgs) {
		return mContext.getContentResolver().update(getContentUri(), values,
				where, whereArgs);
	}
	// 删除
	protected final int delete(Uri uri, String selection, String[] selectionArgs) {
		return mContext.getContentResolver().delete(getContentUri(), selection,
				selectionArgs);
	}
	// 根据查询语获取数据
	protected final Cursor getList(String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		return mContext.getContentResolver().query(getContentUri(), projection,
				selection, selectionArgs, sortOrder);
	}
	// 获取CursorLoader
	public CursorLoader getCursorLoader(Context context) {
		return getCursorLoader(context, null, null, null, null);
	}
	// 获取查询后的CursorLoader
	protected final CursorLoader getCursorLoader(Context context,
			String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		return new CursorLoader(context, getContentUri(), projection,
				selection, selectionArgs, sortOrder);
	}
}
