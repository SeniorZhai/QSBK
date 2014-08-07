package com.zoe.qsbk.dao;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.zoe.qsbk.MyApp;

public class DataProvider extends ContentProvider {
	private static final String TAG = "DataProvider";
	// 同步锁
	static final Object DBLock = new Object();

	public static final String AUTHORITY = "com.zoe.qsbk.provider";

	public static final String SCHEME = "content://";

	// messages
	public static final String PATH_SHOTS = "/items";

	public static final Uri ITEM_CONTENT_URI = Uri.parse(SCHEME + AUTHORITY
			+ PATH_SHOTS);

	private static final int ITEMS = 0;

	/*
	 * MIME type definitions
	 */
	public static final String ITEM_CONTENT_TYPE = "vnd.android.cursor.dir/vnd.zoe.item";

	private static final UriMatcher sUriMatcher;

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(AUTHORITY, "items", ITEMS);
	}
	private static DBHelper mDBHelper;

	public static DBHelper getDBHelper() {
		if (mDBHelper == null) {
			mDBHelper = new DBHelper(MyApp.getContext());
		}
		return mDBHelper;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case ITEMS:
			return ITEM_CONTENT_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	private String matchTable(Uri uri) {
		String table = null;
		switch (sUriMatcher.match(uri)) {
		case ITEMS:
			table = ItemDataHelper.ItemDBInfo.TABLE_NAME;
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		return table;
	}

	@Override
	public boolean onCreate() {
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		synchronized (DBLock) {
			SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
			String table = matchTable(uri);
			queryBuilder.setTables(table);
			SQLiteDatabase db = getDBHelper().getReadableDatabase();
			Cursor cursor = queryBuilder.query(db, projection, selection,
					selectionArgs, null, null, sortOrder);
			cursor.setNotificationUri(getContext().getContentResolver(), uri);
			return cursor;
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) throws SQLException {
		synchronized (DBLock) {
			String table = matchTable(uri);
			SQLiteDatabase db = getDBHelper().getWritableDatabase();
			long rowId = 0;
			db.beginTransaction();
			try {
				rowId = db.insert(table, null, values);
				db.setTransactionSuccessful();
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			} finally {
				db.endTransaction();
			}
			if (rowId > 0) {
				Uri returnUri = ContentUris.withAppendedId(uri, rowId);
				getContext().getContentResolver().notifyChange(uri, null);
				return returnUri;
			}
			throw new SQLException("Failed to insert row into " + uri);
		}
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		synchronized (DBLock) {
			String table = matchTable(uri);
			SQLiteDatabase db = getDBHelper().getWritableDatabase();
			db.beginTransaction();
			try {
				for (ContentValues contentValues : values) {
					db.insertWithOnConflict(table, BaseColumns._ID,
							contentValues, SQLiteDatabase.CONFLICT_IGNORE);
				}
				db.setTransactionSuccessful();
				getContext().getContentResolver().notifyChange(uri, null);
				return values.length;
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			} finally {
				db.endTransaction();
			}
			throw new SQLException("Failed to insert row into " + uri);
		}
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		synchronized (DBLock) {
			SQLiteDatabase db = getDBHelper().getWritableDatabase();

			int count = 0;
			String table = matchTable(uri);
			db.beginTransaction();
			try {
				count = db.delete(table, selection, selectionArgs);
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}
			getContext().getContentResolver().notifyChange(uri, null);
			return count;
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		synchronized (DBLock) {
			SQLiteDatabase db = getDBHelper().getWritableDatabase();
			int count;
			String table = matchTable(uri);
			db.beginTransaction();
			try {
				count = db.update(table, values, selection, selectionArgs);
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}
			getContext().getContentResolver().notifyChange(uri, null);

			return count;
		}
	}

	public static class DBHelper extends SQLiteOpenHelper {
		//
		private static final String DB_NAME = "qsbk.db";
		//
		private static final int VERSION = 1;

		private DBHelper(Context context) {
			super(context, DB_NAME, null, VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			ItemDataHelper.ItemDBInfo.TABLE.create(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}
}
