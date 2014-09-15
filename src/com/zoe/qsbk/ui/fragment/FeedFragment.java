package com.zoe.qsbk.ui.fragment;

import java.util.ArrayList;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
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
import com.zoe.qsbk.ui.MainActivity;
import com.zoe.qsbk.ui.adapter.ItemsAdapter;
import com.zoe.qsbk.util.CommonUtils;
import com.zoe.qsbk.view.PulltoRefreshListView;
import com.zoe.qsbk.view.PulltoRefreshListView.OnLoadNextListener;
import com.zoe.qsbk.view.PulltoRefreshListView.State;

public class FeedFragment extends BaseFragment implements
		LoaderManager.LoaderCallbacks<Cursor>, OnRefreshListener,
		OnLoadNextListener {
	private static final String TAG = "ShotsFragment";
	public static final String EXTRA_CATEGORY = "EXTRA_CATEGORY";
	private MainActivity mActivity;
	private Category mCategory;
	private PulltoRefreshListView mListView;
	private SwipeRefreshLayout swipeLayout;
	private ItemDataHelper mDataHelper;
	private ItemsAdapter mAdapter;

	private int mPage = 1;

	public static FeedFragment newInstance(Category category) {
		FeedFragment fragment = new FeedFragment();
		Bundle bundle = new Bundle();
		bundle.putString(EXTRA_CATEGORY, category.name());
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mActivity = (MainActivity) getActivity();
		View contentView = inflater.inflate(R.layout.fragment_feed, null);
		swipeLayout = (SwipeRefreshLayout) contentView
				.findViewById(R.id.swipe_refresh);
		swipeLayout.setOnRefreshListener(this);

		swipeLayout.setColorScheme(android.R.color.holo_red_light,
				android.R.color.holo_green_light,
				android.R.color.holo_blue_bright,
				android.R.color.holo_orange_light);
		mListView = (PulltoRefreshListView) contentView
				.findViewById(R.id.listView);
		Bundle bundle = getArguments();
		mCategory = Category.valueOf(bundle.getString(EXTRA_CATEGORY));
		mDataHelper = new ItemDataHelper(MyApp.getContext(), mCategory);
		mAdapter = new ItemsAdapter(getActivity(), mListView);
		mListView.setAdapter(mAdapter);
		// LoaderManager实例化一个loader，第一个参数为唯一标示，可选参数提供给Loader进行构造，第三个参数为LoaderCallbacks接口
		getLoaderManager().initLoader(0, null, this);
		mListView.setLoadNextListener(this);
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
										if (mPage == 1) {
											mListView.setSelection(0);
										}
										mActivity.hideRefreshAnimation();
										swipeLayout.setRefreshing(false);
										mListView.setState(State.stop);
									}
								});
					}

				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						swipeLayout.setRefreshing(false);
						mActivity.hideRefreshAnimation();
						mListView.setState(State.stop);
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
		// 清空数据
		mAdapter.changeCursor(null);
	}

	private void loadNextPage() {
		loadData(mPage + 1);
	}

	@Override
	public void onRefresh() {
		if (!swipeLayout.isRefreshing()) {
			swipeLayout.setRefreshing(true);
		}
		loadData(1);
	}

	@Override
	public void onLoadNext() {
		loadNextPage();
	}

}
