package com.zoe.qsbk.ui.fragment;

import java.util.ArrayList;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.zoe.qsbk.MyApp;
import com.zoe.qsbk.R;
import com.zoe.qsbk.api.Api;
import com.zoe.qsbk.dao.ItemDataHelper;
import com.zoe.qsbk.data.GsonRequest;
import com.zoe.qsbk.type.Category;
import com.zoe.qsbk.type.Feed;
import com.zoe.qsbk.type.Item;
import com.zoe.qsbk.ui.adapter.ShotsAdapter;
import com.zoe.qsbk.util.CommonUtils;

public class ShotsFragment extends BaseFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {
	private static final String TAG = "ShotsFragment";
	public static final String EXTRA_CATEGORY = "EXTRA_CATEGORY";
	private Category mCategory;
	private ListView mListView;
	private ItemDataHelper mDataHelper;
	private ShotsAdapter mAdapter;
	private int mPage = 1;

	public static ShotsFragment newInstance(Category category) {
		ShotsFragment fragment = new ShotsFragment();
		Bundle bundle = new Bundle();
		bundle.putString(EXTRA_CATEGORY, category.name());
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View contentView = inflater.inflate(R.layout.fragment_shot, null);
		mListView = (ListView) contentView.findViewById(R.id.listView);
		Bundle bundle = getArguments();
		mCategory = Category.valueOf(bundle.getString(EXTRA_CATEGORY));
		mDataHelper = new ItemDataHelper(MyApp.getContext(), mCategory);
		mAdapter = new ShotsAdapter(getActivity(), mListView);
		mListView.setAdapter(mAdapter);
		// LoaderManager实例化一个loader，第一个参数为唯一标示，可选参数提供给Loader进行构造，第三个参数为LoaderCallbacks接口
		getLoaderManager().initLoader(0, null, this);
		//
		mDataHelper.deleteAll();
		mListView.setOnScrollListener(new AbsListView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem + visibleItemCount >= totalItemCount
						&& totalItemCount != 0
						&& totalItemCount != mListView.getHeaderViewsCount()
								+ mListView.getFooterViewsCount()
						&& mAdapter.getCount() > 0) {
					loadNextPage();
				}
			}
		});

		return contentView;
	}

	private void loadData(final int page) {
		executeRequest(new GsonRequest<Feed>(String.format(Api.ITEM_LIST,
				mCategory.name(), page), Feed.class, null,
				new Response.Listener<Feed>() {
					@Override
					public void onResponse(final Feed requestData) {
						CommonUtils
								.executeAsyncTask(new AsyncTask<Object, Object, Object>() {
									@Override
									protected Object doInBackground(
											Object... params) {
										mPage = requestData.getPage();
										if (mPage == 1) {
											mDataHelper.deleteAll();
										}
										ArrayList<Item> shots = requestData
												.getItems();
									
										mDataHelper.bulkInsert(shots);
										return null;
									}

									@Override
									protected void onPostExecute(Object o) {
										super.onPostExecute(o);
									}
								});
					}

				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {

					}
				}));
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return mDataHelper.getCursorLoader();
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		
		if (data != null && data.getCount() == 0) {
			loadData(1);
		}
		mAdapter.changeCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.changeCursor(null);
	}

	private void loadNextPage() {
		loadData(mPage + 1);
	}
}
