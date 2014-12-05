package com.menu;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ObjectListAdapter extends ArrayAdapter<objectItemInList> {
	Context context;
	
	public ObjectListAdapter(Context context, int resourceId, List<objectItemInList> items) {
		super(context, resourceId, items);
		this.context = context;
	}
	
	private class ViewHolder {
		ImageView thumbnailImageView;
		TextView nameTextView;
		TextView valueTextView;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		objectItemInList rowItem = getItem(position);
 
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.object_row, null);
            holder = new ViewHolder();
            holder.nameTextView = (TextView) convertView.findViewById(R.id.object_name);
            holder.valueTextView = (TextView) convertView.findViewById(R.id.object_value);
            holder.thumbnailImageView = (ImageView) convertView.findViewById(R.id.object_image);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
 
        holder.nameTextView.setText(rowItem.getObjectName());
        holder.valueTextView.setText(rowItem.getObjectValue());
        //////// should be replace by getting external picture on storage
        int imgResId = context.getResources().getIdentifier(rowItem.getImagePath(),"drawable",context.getPackageName());
        holder.thumbnailImageView.setImageResource(imgResId);
        ////////
 
        return convertView;
	}
}
