package com.zoe.qsbk.ui.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.zoe.qsbk.R;
import com.zoe.qsbk.data.GsonRequest;
import com.zoe.qsbk.type.Api;
import com.zoe.qsbk.type.Category;
import com.zoe.qsbk.type.Feed;
import com.zoe.qsbk.type.Item;

public class ShotsFragment extends BaseFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {
 public static final String EXTRA_CATEGORY = "EXTRA_CATEGORY";
	private Category mCategory;
	private ListView mListView;
//	private ShotsDataHelper mDataHelper;
    private int mPage = 1;
	public static ShotsFragment newInstance(Category category){
		ShotsFragment fragment = new ShotsFragment();
		Bundle bundle = new Bundle();
        bundle.putString(EXTRA_CATEGORY, category.name());
        fragment.setArguments(bundle);
        return fragment;
	}
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_shot, null);
        mListView = (ListView) contentView.findViewById(R.id.listView);
        Bundle bundle = getArguments();
        mCategory = Category.valueOf(bundle.getString(EXTRA_CATEGORY));
//        mDataHelper = new ShotsDataHelper(MyApp.getContext(), mCategory);
        loadData(1);
        return contentView;
	}
	private void loadData(final int page) {
		executeRequest(new GsonRequest<Feed>(String.format(
				Api.ITEM_LIST,
				mCategory.name(), page), Feed.class, null,
				new Response.Listener<Feed>() {

					@Override
					public void onResponse(Feed requestData) {	
						for(Item item :requestData.getItems()){
							Log.d("---",item.getImage()!=null?item.getImage():"null");
						}
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {

					}
				}));
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {

	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {

	}

}
