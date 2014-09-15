package com.zoe.qsbk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.zoe.qsbk.R;

public class PulltoRefreshListView extends ListView implements OnScrollListener {
	public enum State {
		load, stop;
	}

	private OnLoadNextListener mLoadNextListener;
	private State state = State.stop;
	private View footerView;

	public PulltoRefreshListView(Context context) {
		super(context);
		init();
	}

	public PulltoRefreshListView(Context context, AttributeSet attr) {
		super(context, attr);
		init();
	}

	public PulltoRefreshListView(Context context, AttributeSet attr,
			int defStyle) {
		super(context, attr, defStyle);
		init();
	}

	private void init() {
		footerView = LayoutInflater.from(getContext()).inflate(
				R.layout.footer_view, null);
		addFooterView(footerView);
		setOnScrollListener(this);
	}

	public void setLoadNextListener(OnLoadNextListener listener) {
		mLoadNextListener = listener;
	}

	public void setState(State state) {
		if (this.state == state) {
			return;
		}

		this.state = state;
		if (this.state == State.load) {
			footerView.setVisibility(View.VISIBLE);
		} else {
			footerView.setVisibility(View.GONE);
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (state == State.load) {
			return;
		}
		if (firstVisibleItem + visibleItemCount >= totalItemCount
				&& totalItemCount != 0
				&& totalItemCount != getHeaderViewsCount()
						+ getFooterViewsCount() && mLoadNextListener != null) {
			setState(State.load);
			mLoadNextListener.onLoadNext();
		}
	}

	public interface OnLoadNextListener {
		public void onLoadNext();
	}
}
