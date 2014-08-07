package com.zoe.qsbk.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.zoe.qsbk.MyApp;
import com.zoe.qsbk.R;
import com.zoe.qsbk.api.Api;
import com.zoe.qsbk.data.RequestManager;
import com.zoe.qsbk.type.Item;

// Cursor和ListView之间的Adapter
public class ShotsAdapter extends CursorAdapter {
	
	private LayoutInflater mLayoutInflater;

	private ListView mListView;

	private BitmapDrawable mDefaultAvatarBitmap = (BitmapDrawable) MyApp
			.getContext().getResources().getDrawable(R.drawable.default_avatar);
	private Drawable mDefaultImageDrawable = new ColorDrawable(Color.argb(255,
			201, 201, 201));

	public ShotsAdapter(Context context, ListView listView) {
		super(context, null, false);
		mLayoutInflater = ((Activity) context).getLayoutInflater();
		mListView = listView;
	}

	@Override
	public Item getItem(int position) {
		mCursor.moveToPosition(position);
		return Item.fromCursor(mCursor);
	}
	// 数据增加后调用
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
		return mLayoutInflater.inflate(R.layout.listitem_item, null);
	}
	// 重绘时调用
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		Holder holder = getHolder(view);
		
		if (holder.imageRequest != null) {
			holder.imageRequest.cancelRequest();
		}

		if (holder.avartarRequest != null) {
			holder.avartarRequest.cancelRequest();
		}

		view.setEnabled(!mListView.isItemChecked(cursor.getPosition()
				+ mListView.getHeaderViewsCount()));

		Item item = Item.fromCursor(cursor);
		if (item.getImage() != null && item.getImage() != "") {
			holder.image.setVisibility(View.VISIBLE);
			holder.imageRequest = RequestManager.loadImage(Api.ImageURL(item
					.getImage()), RequestManager.getImageListener(holder.image,
					mDefaultImageDrawable, mDefaultImageDrawable));
		}else{
			holder.image.setVisibility(View.GONE);
		}

		if (item.getUser()!=null) {
			holder.avatar.setVisibility(View.VISIBLE);
			holder.userName.setText(item.getUser().getLogin());
			holder.avartarRequest = RequestManager.loadImage(Api.Avtnew(String.valueOf(item
					.getUser()
					.getId()), item.getUser().getIcon()), RequestManager
					.getImageListener(holder.avatar, mDefaultAvatarBitmap,
							mDefaultAvatarBitmap));
		}else {
			holder.avatar.setVisibility(View.GONE);
		}
		holder.content.setText(item.getContent());
	}

	private Holder getHolder(final View view) {
		Holder holder = (Holder) view.getTag();
		if (holder == null) {
			holder = new Holder(view);
			view.setTag(holder);
		}
		return holder;
	}

	private class Holder {
		public ImageView image;

		public ImageView avatar;

		public TextView userName;

		public TextView content;

		public ImageLoader.ImageContainer imageRequest;

		public ImageLoader.ImageContainer avartarRequest;

		public Holder(View view) {
			avatar = (ImageView) view.findViewById(R.id.avatar);
			userName = (TextView) view.findViewById(R.id.userName);
			content = (TextView) view.findViewById(R.id.content);
			image = (ImageView) view.findViewById(R.id.image);
		}
	}
}
