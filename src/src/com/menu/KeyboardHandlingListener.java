package com.menu;

import android.content.Context;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

public class KeyboardHandlingListener implements TextView.OnEditorActionListener {
    public final InputMethodManager imm = (InputMethodManager) MainActivity.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    public static final KeyboardHandlingListener listener = new KeyboardHandlingListener();

    @Override
    public boolean onEditorAction(TextView v, int keyCode, KeyEvent event) {
        //Toast.makeText(MainActivity.getContext(), "KeyCode [Actual:Expecting] =" + keyCode + ":" + KeyEvent.KEYCODE_ENTER, Toast.LENGTH_SHORT).show();
        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == 0)) {
            // hide virtual keyboard
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
            MainActivity.main_canvas.requestFocus();
            return true;
        }
        return false;
    }

}
