package com.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.MeasureSpec;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class CanvasTable {
    private final MyAbsoluteLayout canvas;
    private final ArrayList<TableRow> table_rows;
    private final ArrayList<ArrayList<TextView>> table_values;
    private final View linear_layout;
    private final TableLayout table_layout;
    private final LayoutInflater inflater;
    public int table_layout_width = 0;
    private int header_column_width = 0;
    private final String init_string;
    private boolean is_col_empty; // Horrible. Use to handle initial state of the table. Use for now
                                  // cuz of time contraint - Austin 5:03pm 3/31/2014
    private boolean is_row_empty; // Horrible. Use to handle initial state of the table. Use for now
                                  // cuz of time contraint - Austin 5:03pm 3/31/2014
    private final int init_string_width;

    public CanvasTable(LayoutInflater inflater, MyAbsoluteLayout canvas, int x, int y) {
        init_string = MainActivity.getContext().getString(R.string.drop_here);
        this.canvas = canvas;
        this.table_rows = new ArrayList<TableRow>();
        this.table_values = new ArrayList<ArrayList<TextView>>();
        this.inflater = inflater;
        linear_layout = inflater.inflate(R.layout.canvas_table, null);
        this.table_layout = (TableLayout) linear_layout.findViewById(R.id.tableLayout);

        TableRow row0 = (TableRow) table_layout.findViewById(R.id.tableRow0);
        TextView header = (TextView) table_layout.findViewById(R.id.header);

        header.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        header_column_width = table_layout_width;
        table_layout_width = header_column_width;
        

        // Toast.makeText(MainActivity.getContext(), "table_layout_width= " + table_layout_width,
        // Toast.LENGTH_SHORT).show();

        table_rows.add(row0);
        table_values.add(new ArrayList<TextView>());

        addCell(header, 0, 0);

        header.setOnTouchListener(new HeaderListener());
        header.setOnDragListener(new HeaderListener());
        table_layout.findViewById(R.id.header).setOnTouchListener(new HeaderListener());
        MyAbsoluteLayout.LayoutParams llp = new MyAbsoluteLayout.LayoutParams(table_layout_width, MyAbsoluteLayout.LayoutParams.WRAP_CONTENT, x, y);
        canvas.addView(linear_layout, llp);

        /*
         * Below implementation is horrible. Use to handle initial state of the table. Use for now
         * cuz of time contraint - Austin 5:03pm 3/31/2014
         */
        is_col_empty = false;
        is_row_empty = false;
        addRow(init_string);
        addColumn(init_string);
        is_col_empty = true;
        is_row_empty = true;

        init_string_width = getCell(0, 1).getMeasuredWidth();
        table_layout.requestFocus();

    }

    private void removeRow(int row_index) {
        if (table_rows.size() <= 2) {
            int old_row_width = getCell(row_index, 0).getWidth();
            setCell(MainActivity.getContext().getString(R.string.drop_here), 1, 0);
            int column_size = table_values.get(0).size();
            int i;
            for (i = 1; i < column_size; i++) {
                setCell("", row_index, i);
            }
            is_row_empty = true;
            table_layout_width = table_layout_width - old_row_width + init_string_width;
            MainActivity.main_canvas.updateLayout(linear_layout, (int) (table_layout_width), -1, -1, -1);
        } else {
            table_values.remove(row_index);
            table_layout.removeView(table_rows.get(row_index));
            table_rows.remove(row_index);
        }
    }

    private void removeColumn(int column_index) {
        int new_col_width = 0;
        int old_col_width = getCell(0, column_index).getWidth();
        int row_size = table_values.size();
        int i;
        if (table_values.get(0).size() <= 2) {
            setCell(MainActivity.getContext().getString(R.string.drop_here), 0, 1);
            Log.wtf("?", "row_size= " + row_size);
            for (i = 1; i < row_size; i++) {
                setCell("", i, column_index);
            }
            new_col_width = init_string_width;

            is_col_empty = true;
        } else {
            TextView cell;
            for (i = 0; i < row_size; i++) {
                cell = getCell(i, column_index);
                table_rows.get(i).removeView(cell);
                table_values.get(i).remove(column_index);
            }
        }
        table_layout_width = table_layout_width - old_col_width + new_col_width;
        MainActivity.main_canvas.updateLayout(linear_layout, (int) (table_layout_width), -1, -1, -1);



    }

    private void addCell(String new_text, int row_index, int col_index) {
        TextView new_text_view = (TextView) inflater.inflate(R.layout.canvas_table_cell, null);
        new_text_view.setText(new_text);
        addCell(new_text_view, row_index, col_index);
    }

    private void addCell(TextView new_text_view, int row_index, int col_index) {
        table_values.get(row_index).add(col_index, new_text_view);
    }

    private void setCell(String new_text, int row_index, int col_index) {
        table_values.get(row_index).get(col_index).setText(new_text);
    }

    private TextView getCell(int row_index, int col_index) {
        return table_values.get(row_index).get(col_index);
    }

    private void debug_print_table() {
        int i, j;
        int row_size = table_rows.size();
        int col_size = table_values.get(0).size();

        for (i = 0; i < row_size; i++) {

            for (j = 0; j < col_size; j++) {
                Log.wtf("table", "index (" + i + "," + j + ") = " + table_values.get(i).get(j).getText().toString());
            }
        }
    }

    public int findRowIndex(View view) {
        int i;
        int row_index_range = table_rows.size();
        TextView current_view;
        for (i = 1; i < row_index_range; i++) {
            current_view = getCell(i, 0);
            String current_text = current_view.getText().toString();
            if (current_view.equals(view)) {
                Log.wtf("[Log]", "Selected row index= " + i);
                break;
            }
        }
        return i;
    }

    public int findColumnIndex(View view) {
        int i = table_values.get(0).indexOf(view);
        Log.wtf("[Log]", "Selected column index= " + i);
        return i;
    }

    public void sortByColumnValues(int selected_index) {
        // Make a snapshot of current table values
        ArrayList<ArrayList<String>> table_values_snapshot = makeTableValueSnapShot();

        // Build comparable arraylist
        int i;
        int row_size = table_rows.size();
        String tmp_string;
        CellComparable list[] = new CellComparable[row_size - 1];
        // ArrayList<CellComparable> cell_list = new ArrayList<CellComparable>();
        for (i = 1; i < row_size; i++) {
            tmp_string = table_values_snapshot.get(i).get(selected_index);
            Log.wtf("[Log]", "Add cell (" + i + "," + selected_index + ")= " + tmp_string);
            // cell_list.add(new CellComparable(getCell(i, selected_index), i));
            list[i - 1] = new CellComparable(getCell(i, selected_index), i);
        }

        Arrays.sort(list);

        // Set sorted cells
        int table_index;
        int column_index;
        int column_size = table_values.get(0).size();
        for (i = 0; i < row_size - 1; i++) {
            table_index = i + 1;
            Log.wtf("[Log]", "Sorted: (" + table_index + "," + selected_index + ")= " + list[i].value);
            if (list[i].index != table_index) {
                // Set the whole row
                for (column_index = 0; column_index < column_size; column_index++) {
                    setCell(table_values_snapshot.get(list[i].index).get(column_index), table_index, column_index);
                }
            }
        }
    }

    public ArrayList<ArrayList<String>> makeTableValueSnapShot() {
        ArrayList<ArrayList<String>> table_values_snapshot = new ArrayList<ArrayList<String>>();
        int row_size = table_rows.size();
        int column_size = table_values.get(0).size();
        int i, j;
        for (i = 0; i < row_size; i++) {
            table_values_snapshot.add(new ArrayList<String>());
            for (j = 0; j < column_size; j++) {
                table_values_snapshot.get(i).add(getCell(i, j).getText().toString());
            }
        }
        return table_values_snapshot;
    }

    private class CellComparable implements Comparable<CellComparable> {
        public final String value;
        public final int index;
        public final TextView view;

        public CellComparable(TextView view, int index) {
            this.value = view.getText().toString();
            this.index = index;
            this.view = view;
        }

        @Override
        public int compareTo(CellComparable arg0) {
            String this_value = this.value;
            String compare_to = arg0.value;
            return compare_to.compareToIgnoreCase(this_value);
        }
    }

    public void addRows(List<String> values) {
        for (String temp : values) {
            addRow(temp);
        }
    }

    public void addColumns(List<String> values) {
        for (String temp : values) {
            addColumn(temp);
        }
    }

    public void addRow(String header_text) {
        // Handle initial state - Horrible implementation, refer to declaration of is_col_empty
        if (is_row_empty) {
            TextView first_row = getCell(1, 0);
            first_row.setText(header_text);
            is_row_empty = false;
            getCell(0, 0).setText("");
            return;
        }

        TableRow new_row = (TableRow) inflater.inflate(R.layout.canvas_table_row, null);
        int row_index = table_rows.size();
        int column_size = table_values.get(0).size();
        table_layout.addView(new_row);
        table_rows.add(new_row);
        table_values.add(new ArrayList<TextView>());

        TextView new_text_view;
        // Create header cell for the row
        new_text_view = (TextView) inflater.inflate(R.layout.canvas_table_cell, null);
        new_text_view.setText(header_text);

        new_text_view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        int new_width = new_text_view.getMeasuredWidth();
        if (new_width > header_column_width) {
            // Toast.makeText(MainActivity.getContext(), "new column width= " + new_width,
            // Toast.LENGTH_SHORT).show();

            table_layout_width = table_layout_width - header_column_width + new_width;
            header_column_width = new_width;
            MainActivity.main_canvas.updateLayout(linear_layout, (int) (table_layout_width), -1, -1, -1);
            // Toast.makeText(MainActivity.getContext(), "table_layout_width= " +
            // table_layout_width, Toast.LENGTH_SHORT).show();
        }
        new_row.addView(new_text_view);
        addCell(new_text_view, row_index, 0);
        new_text_view.setOnDragListener(new RowListener());
        new_text_view.setOnTouchListener(new RowListener());

        // Fill the rest with blank editable cells
        int i;
        for (i = 1; i < column_size; i++) {
            new_text_view = (EditText) inflater.inflate(R.layout.canvas_table_editable_cell, null);
            new_text_view.setText("");
            new_row.addView(new_text_view);
            addCell(new_text_view, row_index, i);
            new_text_view.setOnDragListener(new EditableCellListener());
            new_text_view.setOnEditorActionListener(KeyboardHandlingListener.listener);
        }
    }

    public void addColumn(String header_text) {
        // Handle initial state - Horrible implementation, refer to declaration of is_col_empty
        if (is_col_empty) {
            TextView first_column = getCell(0, 1);
            TextView new_text_view;
            new_text_view = (TextView) inflater.inflate(R.layout.canvas_table_cell, null);
            new_text_view.setText(header_text);
            new_text_view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            int new_width = new_text_view.getMeasuredWidth();
            int original_width = first_column.getWidth();
            //Toast.makeText(MainActivity.getContext(), "new_width= " + new_width, Toast.LENGTH_SHORT).show();
            //Toast.makeText(MainActivity.getContext(), "original_width= " + original_width, Toast.LENGTH_SHORT).show();
            table_layout_width = table_layout_width - original_width + new_width;
            MainActivity.main_canvas.updateLayout(linear_layout, (int) (table_layout_width), -1, -1, -1);
            first_column.setText(header_text);
            is_col_empty = false;
            getCell(0, 0).setText("");
            return;
        }

        int column_index = table_values.get(0).size();
        int row_size = table_values.size();
        TextView new_text_view;
        // Create header cell for the row
        new_text_view = (TextView) inflater.inflate(R.layout.canvas_table_cell, null);
        new_text_view.setText(header_text);

        new_text_view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        table_layout_width += new_text_view.getMeasuredWidth();
        MainActivity.main_canvas.updateLayout(linear_layout, (int) (table_layout_width), -1, -1, -1);
        // Toast.makeText(MainActivity.getContext(), "table_layout_width= " + table_layout_width,
        // Toast.LENGTH_SHORT).show();

        table_rows.get(0).addView(new_text_view);
        addCell(new_text_view, 0, column_index);
        new_text_view.setOnDragListener(new ColumnListener());
        new_text_view.setOnTouchListener(new ColumnListener());

        // Fill the rest with blank editable cells
        int i;
        for (i = 1; i < row_size; i++) {
            new_text_view = (EditText) inflater.inflate(R.layout.canvas_table_editable_cell, null);
            new_text_view.setText("");
            addCell(new_text_view, i, column_index);
            table_rows.get(i).addView(new_text_view);
            new_text_view.setOnDragListener(new EditableCellListener());
            new_text_view.setOnEditorActionListener(KeyboardHandlingListener.listener);
        }
    }

    public class ColumnListener extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener, View.OnDragListener {
        private GestureDetector gesture_detector;
        private View tmp_view;

        public ColumnListener() {
            gesture_detector = new GestureDetector(MainActivity.getContext(), this);
        }

        @Override
        public boolean onDrag(View v, DragEvent e) {
            final int action = e.getAction();
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED: {
                    // ((ImageView) v).setColorFilter(Color.BLUE);
                    // v.invalidate();
                    return true;
                }
                case DragEvent.ACTION_DROP: {
                    View view = (View) e.getLocalState();
                    if (view instanceof TextView) {
                        String new_text = ((TextView) view).getText().toString();
                        addColumn(new_text);
                    } else if (view.findViewById(R.id.listview) != null) {
                        Toast.makeText(MainActivity.getContext(), "Populate table row with list", Toast.LENGTH_SHORT).show();
                        List<String> values = ((StringAdapter) ((ListView) view.findViewById(R.id.listview)).getAdapter()).values;
                        addColumns(values);
                    } else {
                        Toast.makeText(MainActivity.getContext(), "Table only supports text", Toast.LENGTH_SHORT).show();
                    }
                    view.setVisibility(View.VISIBLE);
                    return true;
                }
                case DragEvent.ACTION_DRAG_ENDED: {
                    return false;
                }
                case DragEvent.ACTION_DRAG_ENTERED: {
                    return true;
                }
                case DragEvent.ACTION_DRAG_EXITED: {
                    return true;
                }
                case DragEvent.ACTION_DRAG_LOCATION: {
                    return true;
                }
                default: {
                    // Ignore not handled events
                    return true;
                }
            }
        }

        /**
         * Start drag
         */
        @Override
        public boolean onTouch(View v, MotionEvent e) {
            tmp_view = v;
            boolean eventConsumed = gesture_detector.onTouchEvent(e);
            if (eventConsumed) {
                return true;
            } else
                return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent ev) {
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent ev) {
            Toast.makeText(MainActivity.getContext(), "Sort by column data", Toast.LENGTH_SHORT).show();
            int index = findColumnIndex(tmp_view);
            sortByColumnValues(index);
            return true;
        }

        @Override
        public void onShowPress(MotionEvent ev) {

        }

        @Override
        public void onLongPress(MotionEvent ev) {}

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return true;
        }

        @Override
        public boolean onDown(MotionEvent ev) {

            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            int index = findColumnIndex(tmp_view);
            removeColumn(index);
            return true;
        }
    }

    public class RowListener extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener, View.OnDragListener {
        private GestureDetector gesture_detector;
        private View tmp_view;

        public RowListener() {
            gesture_detector = new GestureDetector(MainActivity.getContext(), this);
        }

        @Override
        public boolean onDrag(View v, DragEvent e) {
            final int action = e.getAction();
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED: {
                    // ((ImageView) v).setColorFilter(Color.BLUE);
                    // v.invalidate();
                    return true;
                }
                case DragEvent.ACTION_DROP: {
                    View view = (View) e.getLocalState();
                    if (view instanceof TextView) {
                        String new_text = ((TextView) view).getText().toString();
                        addRow(new_text);
                    } else if (view.findViewById(R.id.listview) != null) {
                        Toast.makeText(MainActivity.getContext(), "Populate table row with list", Toast.LENGTH_SHORT).show();
                        List<String> values = ((StringAdapter) ((ListView) view.findViewById(R.id.listview)).getAdapter()).values;
                        addRows(values);
                    } else {
                        Toast.makeText(MainActivity.getContext(), "Table only supports text", Toast.LENGTH_SHORT).show();
                    }
                    view.setVisibility(View.VISIBLE);
                    return true;
                }
                case DragEvent.ACTION_DRAG_ENDED: {
                    return true;
                }
                case DragEvent.ACTION_DRAG_ENTERED: {
                    return true;
                }
                case DragEvent.ACTION_DRAG_EXITED: {
                    return true;
                }
                case DragEvent.ACTION_DRAG_LOCATION: {
                    return true;
                }
                default: {
                    // Ignore not handled events
                    return true;
                }
            }
        }

        /**
         * Start drag
         */
        @Override
        public boolean onTouch(View v, MotionEvent e) {
            tmp_view = v;
            boolean eventConsumed = gesture_detector.onTouchEvent(e);
            if (eventConsumed) {
                return true;
            } else
                return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent ev) {
            Toast.makeText(MainActivity.getContext(), "onDoubleTap", Toast.LENGTH_SHORT).show();
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent ev) {
            // findRowIndex(tmp_view);
            return true;
        }

        @Override
        public void onShowPress(MotionEvent ev) {
            Toast.makeText(MainActivity.getContext(), "Sort by row data is not supported.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onLongPress(MotionEvent ev) {
            Toast.makeText(MainActivity.getContext(), "onLongPress", Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public boolean onDown(MotionEvent ev) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            int index = findRowIndex(tmp_view);
            removeRow(index);
            return true;
        }
    }

    public class EditableCellListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent e) {
            final int action = e.getAction();
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED: {
                    // ((ImageView) v).setColorFilter(Color.BLUE);
                    // v.invalidate();
                    return true;
                }
                case DragEvent.ACTION_DROP: {
                    View view = (View) e.getLocalState();
                    if (!(view instanceof TextView)) {
                        Toast.makeText(MainActivity.getContext(), "Table only supports text", Toast.LENGTH_SHORT).show();
                        view.setVisibility(View.VISIBLE);
                        return true;
                    }
                    String new_text = ((TextView) view).getText().toString();
                    ((TextView) v).setText(new_text);
                    view.setVisibility(View.VISIBLE);
                    return true;
                }
                case DragEvent.ACTION_DRAG_ENDED: {
                    return false;
                }
                case DragEvent.ACTION_DRAG_ENTERED: {
                    return true;
                }
                case DragEvent.ACTION_DRAG_EXITED: {
                    return true;
                }
                case DragEvent.ACTION_DRAG_LOCATION: {
                    return true;
                }
                default: {
                    // Ignore not handled events
                    return true;
                }
            }
        }
    }

    public class HeaderListener extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener, View.OnDragListener {
        private GestureDetector gesture_detector;
        private View tmp_view;

        public HeaderListener() {
            gesture_detector = new GestureDetector(MainActivity.getContext(), this);
        }

        /**
         * Start drag
         */
        @Override
        public boolean onTouch(View v, MotionEvent e) {
            tmp_view = v;
            boolean eventConsumed = gesture_detector.onTouchEvent(e);
            if (eventConsumed) {
                return true;
            } else
                return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent ev) {
        	Toast.makeText(MainActivity.getContext(), "onDoubleTap", Toast.LENGTH_SHORT).show();
        	MainActivity.main_canvas.removeView((View) tmp_view.getParent().getParent().getParent());
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent ev) {
        	//Toast.makeText(MainActivity.getContext(), "onSingleTapUp", Toast.LENGTH_SHORT).show();
            return true;
        }
        
        @Override
        public boolean onSingleTapUp(MotionEvent ev) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent ev) {

        }

        @Override
        public void onLongPress(MotionEvent ev) {

        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            LinearLayout parent = (LinearLayout) table_layout.getParent();
            DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(parent);
            parent.startDrag(null, shadowBuilder, parent, 0);
            parent.setVisibility(View.INVISIBLE);
            return true;
        }

        @Override
        public boolean onDown(MotionEvent ev) {

            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return true;
        }

        @Override
        public boolean onDrag(View v, DragEvent e) {
            final int action = e.getAction();
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED: {
                    return true;
                }
                case DragEvent.ACTION_DROP: {
                    Toast.makeText(MainActivity.getContext(), "Drop item into any row or column", Toast.LENGTH_SHORT).show();
                    return true;
                }
                case DragEvent.ACTION_DRAG_ENDED: {
                    return false;
                }
                case DragEvent.ACTION_DRAG_ENTERED: {
                    return true;
                }
                case DragEvent.ACTION_DRAG_EXITED: {
                    return true;
                }
                case DragEvent.ACTION_DRAG_LOCATION: {
                    return true;
                }
                default: {
                    // Ignore not handled events
                    return true;
                }
            }
        }
    }
}
