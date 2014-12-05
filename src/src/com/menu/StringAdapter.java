package com.menu;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class StringAdapter extends ArrayAdapter<String> {
    private final Context context;
    public final List<String> values;
    public View bound_list_view;
    public int max_width = 0;
    
    public StringAdapter(Context context, List<String> values) {
        super(context, R.layout.canvas_list_list_item, values);
        this.context = context;
        this.values = values;
    }
    
    public void remove(int index) {
        if (values.size() <= 1) { // -@ Doesn't work within this class, but does work in MainActivity. Ignore for now: 4:39pm March.31.2014 by Austin
            Toast.makeText(MainActivity.getContext(), "Remove list from canvas", Toast.LENGTH_SHORT).show();
            MainActivity.main_canvas.removeView(bound_list_view);
            return;
        }
        String removed_string = values.remove(index);
        this.notifyDataSetChanged();
        Toast.makeText(MainActivity.getContext(), "Remove \"" + removed_string + "\" from list", Toast.LENGTH_SHORT).show();
        resizeToFit();
    }
    
    public void sortByAlphabeticalOrder() {
        Collections.sort(values);
        this.notifyDataSetChanged();
    }
    
    public void resizeToFit() {
    	MainActivity.main_canvas.updateLayout(bound_list_view, measureMax(), -1, -1, -1);
    }

    public void setHeaderText(String header_text) {
        TextView header = (TextView) bound_list_view.findViewById(R.id.header);
        header.setText(header_text);
    }
    
    public int measureMax() {
        // Create a TextView for measurements
        max_width = 0;
        TextView tmp_view = (TextView) MainActivity.inflater.inflate(R.layout.canvas_list_list_item, null);
        int i;
        int tmp_width;
        int length = values.size();
        String value;
        for (i = 0; i < length; i++) {
            value = values.get(i);
            tmp_view = (TextView) MainActivity.inflater.inflate(R.layout.canvas_list_list_item, null);
            tmp_view.setText(value);
            tmp_view.measure(MeasureSpec.UNSPECIFIED,MeasureSpec.UNSPECIFIED);
            tmp_width = tmp_view.getMeasuredWidth();
            if (tmp_width > max_width) {
                max_width = tmp_width;
            }
        }
        
        return max_width;
    }
}
