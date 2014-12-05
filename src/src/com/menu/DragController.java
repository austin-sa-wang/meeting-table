/**
 * @author Austin S.A. Wang
 */
package com.menu;

import android.content.Context;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.view.KeyEvent;

/**
 * 
 * This class implements different OnTouch and OnDrag methods inside the drag layer.
 */
public class DragController {
    private static final String TAG = "Drag_listener";
    protected static boolean annotating_image = false;
    protected static View annotated_view = null;
    protected static boolean false_annotation = false;
    protected static boolean new_text_mode = false;
    protected static EditText current_text_view = null;
    public final InputMethodManager imm;

    static DragLayerListener drag_layer_listener;
    static AnnotationListener annotation_listener;
    static ItemListener item_listener;
    static ViewRemovalListener view_removal_listener;

    public DragController() {
        drag_layer_listener = new DragLayerListener();
        annotation_listener = new AnnotationListener();
        item_listener = new ItemListener();
        view_removal_listener = new ViewRemovalListener();
        imm = (InputMethodManager) MainActivity.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    public class DragLayerListener extends GestureDetector.SimpleOnGestureListener implements View.OnDragListener, View.OnTouchListener {
        private GestureDetector gesture_detector;
        private View tmp_view;

        public DragLayerListener() {
            gesture_detector = new GestureDetector(MainActivity.getContext(), this);
        }

        /**
         * Move dragged item to the drop coordinates
         */
        @Override
        public boolean onDrag(View v, DragEvent e) {
            final int action = e.getAction();
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED: {
                    Log.i(TAG, "Drag layer started.");
                    return true;
                }
                case DragEvent.ACTION_DROP: {
                    Log.i(TAG, "Drag layer dropped.");
                    View view = (View) e.getLocalState();

                    double x_drop_coord = (double) e.getX();
                    double y_drop_coord = (double) e.getY();

                    int w = view.getWidth();
                    int h = view.getHeight();

                    int x_pos = (int) (x_drop_coord - (0.5 * w));
                    int y_pos = (int) (y_drop_coord - (0.5 * h));

                    // Log.v(DebugUtilities.TAG, "xy double coord - X: " +
                    // x_drop_coord + "Y:" + y_drop_coord);
                    // Log.v(DebugUtilities.TAG, "View dimention - width: " + w +
                    // "height:" + h);
                    // Log.v(DebugUtilities.TAG, "xy drop coord - X: " + x_pos +
                    // "Y:" + y_pos);

                    if (view instanceof EditText) {
                        w = MyAbsoluteLayout.LayoutParams.WRAP_CONTENT;
                    }
                    if (view.findViewById(R.id.listview) != null) { // Keep the WRAP_CONTENT
                        h = MyAbsoluteLayout.LayoutParams.WRAP_CONTENT;
                    } else if (view.findViewById(R.id.tableLayout) != null) {
                        h = MyAbsoluteLayout.LayoutParams.WRAP_CONTENT;
                    }
                    MyAbsoluteLayout.LayoutParams lp = new MyAbsoluteLayout.LayoutParams(w, h, x_pos, y_pos);
                    MyAbsoluteLayout canvas = (MyAbsoluteLayout) v;
                    canvas.removeView(view);
                    canvas.addView(view, lp);

                    view.setVisibility(View.VISIBLE);
                    return true;
                }
                case DragEvent.ACTION_DRAG_ENDED: {
                    Log.i(TAG, "Drag layer ended.");
                    View view = (View) e.getLocalState();
                    view.setVisibility(View.VISIBLE);
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
            MainActivity.main_canvas.requestFocus();
            tmp_view = v;
            boolean eventConsumed = gesture_detector.onTouchEvent(e);
            if (eventConsumed) {
                return true;
            } else
                return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent ev) {
            Toast.makeText(MainActivity.getContext(), "Create list", Toast.LENGTH_SHORT).show();
            new CanvasList((int) ev.getX(), (int) ev.getY());
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent ev) {
            if (annotating_image) {
                annotating_image = false;
                if (annotated_view instanceof DrawableImageView) {
                    ((DrawableImageView) annotated_view).in_edit_mode = false;
                    annotated_view.setOnTouchListener(DragController.item_listener);
                    Toast.makeText(MainActivity.getContext(), "Exit drawing mode", Toast.LENGTH_SHORT).show();
                }
                annotated_view = null;
            } else if (current_text_view == null) {
                current_text_view = (EditText) MainActivity.inflater.inflate(R.layout.edit_view, null);
                current_text_view.setOnTouchListener(DragController.item_listener);
                current_text_view.setOnEditorActionListener(new KeyboardHandlingListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int keyCode, KeyEvent event) {
                        Toast.makeText(MainActivity.getContext(), "EditorAction triggered", Toast.LENGTH_SHORT).show();
                        boolean result = super.onEditorAction(v, keyCode, event);
                        if (result) {
                            finalizeEditText();
                        }
                        return false;
                    }
                });
                MyAbsoluteLayout.LayoutParams lp =
                                new MyAbsoluteLayout.LayoutParams(MyAbsoluteLayout.LayoutParams.WRAP_CONTENT, MyAbsoluteLayout.LayoutParams.WRAP_CONTENT, (int) ev.getX(),
                                                (int) ev.getY());
                MainActivity.main_canvas.addView(current_text_view, lp);
                current_text_view.requestFocus();

                // imm.showSoftInput(current_text_view, InputMethodManager.SHOW_IMPLICIT);
                //imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
            } else {
                finalizeEditText();
            }
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
            Toast.makeText(MainActivity.getContext(), "Create Table", Toast.LENGTH_SHORT).show();
            new CanvasTable(MainActivity.inflater, MainActivity.main_canvas, (int) ev.getX(), (int) ev.getY());
        }

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
            return true;
        }

        public void finalizeEditText() {
            String text = current_text_view.getText().toString().trim();
            if (text.length() < 1) {
                MainActivity.main_canvas.removeView(current_text_view);
                current_text_view = null;
                return;
            }
            TextView new_text_view = (TextView) MainActivity.inflater.inflate(R.layout.floating_text_view, null);
            new_text_view.setText(text);
            new_text_view.setOnTouchListener(DragController.item_listener);
            MyAbsoluteLayout.LayoutParams lp = (MyAbsoluteLayout.LayoutParams) current_text_view.getLayoutParams();
            MainActivity.main_canvas.removeView(current_text_view);
            MainActivity.main_canvas.addView(new_text_view, lp);
            current_text_view = null;
        }
    }

    public class AnnotationListener extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener {
        private GestureDetector gesture_detector;
        private View tmp_view;

        public AnnotationListener() {
            gesture_detector = new GestureDetector(MainActivity.getContext(), this);
        }

        /**
         * Start drag
         */
        @Override
        public boolean onTouch(View v, MotionEvent e) {
            tmp_view = v;

            if (false_annotation) {
                if (e.getAction() == MotionEvent.ACTION_UP) {
                    false_annotation = false;
                }
                return true;
            }

            boolean eventConsumed = gesture_detector.onTouchEvent(e);

            if (eventConsumed) {
                return true;
            } else
                return false;
        }
    }

    public class ItemListener extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener {
        private GestureDetector gesture_detector;
        private View tmp_view;

        public ItemListener() {
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
            MainActivity.main_canvas.removeView(tmp_view);
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent ev) {
            // MyAbsoluteLayout.LayoutParams mylp = (MyAbsoluteLayout.LayoutParams)
            // tmp_view.getLayoutParams();
            if (tmp_view instanceof EditText) {
                tmp_view.requestFocus();
            } else {
                tmp_view.setRotation(tmp_view.getRotation() + 90);
            }
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
            Toast.makeText(MainActivity.getContext(), "Enable drawing on picture", Toast.LENGTH_SHORT).show();
            if (tmp_view instanceof DrawableImageView) {
                annotating_image = true;
                annotated_view = tmp_view;
                ((DrawableImageView) tmp_view).in_edit_mode = true;
                tmp_view.setOnTouchListener(DragController.annotation_listener);
                false_annotation = true;
                // Toast.makeText(MainActivity.getContext(), "triggerred edit mode.",
                // Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (tmp_view == annotated_view) {
                // Toast.makeText(MainActivity.getContext(), "teehee", Toast.LENGTH_SHORT).show();
                return true;
            }
            DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(tmp_view) {

            };
            tmp_view.startDrag(null, shadowBuilder, tmp_view, 0);
            tmp_view.setVisibility(View.INVISIBLE);
            return true;
        }

        @Override
        public boolean onDown(MotionEvent ev) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }

    public class ViewRemovalListener implements View.OnDragListener, View.OnTouchListener {
        /**
         * Move dragged item to the drop coordinates
         */
        @Override
        public boolean onDrag(View v, DragEvent e) {
            final int action = e.getAction();
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED: {
                    Log.i(TAG, "Drag layer started.");
                    return true;
                }
                case DragEvent.ACTION_DROP: {
                    Log.i(TAG, "Drag layer dropped.");
                    View view = (View) e.getLocalState();
                    MainActivity.main_canvas.removeView(view);
                    return true;
                }
                case DragEvent.ACTION_DRAG_ENDED: {
                    Log.i(TAG, "Drag layer ended.");
                    View view = (View) e.getLocalState();
                    view.setVisibility(View.VISIBLE);
                    return true;
                }
                default: {
                    // Ignore not handled events
                    return true;
                }
            }
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Toast.makeText(MainActivity.getContext(), "Erase canvas", Toast.LENGTH_SHORT).show();
            MainActivity.main_canvas.removeAllViews();
            return true;
        }
    }
}
