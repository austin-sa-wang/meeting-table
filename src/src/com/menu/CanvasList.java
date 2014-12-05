package com.menu;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CanvasList {
    private boolean is_empty; // Horrible. Use to handle initial state of the table. Use for now cuz
                              // of time contraint - Austin 5:03pm 3/31/2014
    private final ListView list_veiw; 
    private final StringAdapter adapter;

    public CanvasList(int x, int y) {
        // new list
        LinearLayout new_list = (LinearLayout) MainActivity.inflater.inflate(R.layout.canvas_list, null);
        TextView header = (TextView) new_list.findViewById(R.id.header);
        header.setText("Empty List");
        header.setOnTouchListener(new HeaderListener());
        header.setOnDragListener(new HeaderListener());

        MyAbsoluteLayout.LayoutParams llp = new MyAbsoluteLayout.LayoutParams(250, MyAbsoluteLayout.LayoutParams.WRAP_CONTENT, x, y);

        List<String> list_1 = new ArrayList<String>();
        adapter = new StringAdapter(MainActivity.getContext(), list_1);
        list_veiw = (ListView) new_list.findViewById(R.id.listview);
        list_veiw.setAdapter(adapter);
        adapter.bound_list_view = new_list;
        ListListener ll = new ListListener();
        list_veiw.setOnDragListener(ll);
        list_veiw.setOnTouchListener(ll);

        MainActivity.main_canvas.addView(new_list, llp);

        is_empty = false;
        adapter.add(MainActivity.getContext().getString(R.string.drop_here));
        is_empty = true;

        llp.width = adapter.measureMax();
        MainActivity.main_canvas.updateViewLayout(new_list, llp);
    }

    public class ListListener extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener, View.OnDragListener {
        private GestureDetector gesture_detector;
        private View tmp_view;

        public ListListener() {
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
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent ev) {
            return true;
        }

        @Override
        public void onShowPress(MotionEvent ev) {

        }

        @Override
        public void onLongPress(MotionEvent ev) {}

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
            int id = list_veiw.pointToPosition((int) e1.getX(), (int) e1.getY());
            adapter.remove(id);
            return true;
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
                    if (!(view instanceof TextView)) {
                        Toast.makeText(MainActivity.getContext(), "List only supports text", Toast.LENGTH_SHORT).show();
                        view.setVisibility(View.VISIBLE);
                        return true;
                    }

                    StringAdapter target_list_adapter = (StringAdapter) ((ListView) v).getAdapter();
                    if (is_empty) {
                        target_list_adapter.clear();
                        is_empty = false;
                    }
                    target_list_adapter.add(((TextView) view).getText().toString());
                    target_list_adapter.setHeaderText("");
                    target_list_adapter.resizeToFit();
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
        	MainActivity.main_canvas.removeView((View) tmp_view.getParent());
            return true;
        }
        
        @Override
        public boolean onSingleTapUp(MotionEvent ev) {
            return false;
        }
        
        @Override
        public boolean onSingleTapConfirmed(MotionEvent ev) {
            Toast.makeText(MainActivity.getContext(), "Sort list by alphabetical order", Toast.LENGTH_SHORT).show();
            ((StringAdapter) ((ListView) ((View) tmp_view.getParent()).findViewById(R.id.listview)).getAdapter()).sortByAlphabeticalOrder();
            return true;
        }

        @Override
        public void onShowPress(MotionEvent ev) {

        }

        @Override
        public void onLongPress(MotionEvent ev) {
            
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            LinearLayout parent = (LinearLayout) tmp_view.getParent();
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
                    View view = (View) e.getLocalState();
                    if (!(view instanceof TextView)) {
                        Toast.makeText(MainActivity.getContext(), "List only supports text", Toast.LENGTH_SHORT).show();
                        view.setVisibility(View.VISIBLE);
                        return true;
                    }

                    if (is_empty) {
                        adapter.clear();
                        is_empty = false;
                    }
                    adapter.add(((TextView) view).getText().toString());
                    adapter.setHeaderText("");
                    adapter.resizeToFit();
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
}
