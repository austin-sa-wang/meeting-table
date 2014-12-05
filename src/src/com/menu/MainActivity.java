package com.menu;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.menu.XmlParser.Entry;

public class MainActivity extends Activity {
    private static MainActivity instance;
    public final static String EXTRA_MESSAGE = "com.saw.gettingstarted.MESSAGE";
    public final static String XML_SRC = "xml_template";
    public static LayoutInflater inflater;
    public String parsed_xml;
    public List<objectItemInList> objectList = new ArrayList<objectItemInList>();
    ObjectListAdapter adapter;

    public static MyAbsoluteLayout main_canvas;

    public int temporary_counter = 0;

    public MainActivity() {
        instance = this;
    }

    public static Context getContext() {
        return instance.getApplicationContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Parse XML and fill data into
        // "ArrayList<objectItemInList> objectList":
        // try {
        // parsed_xml = loadXmlFromFile(XML_SRC);
        // } catch (XmlPullParserException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        //
        // //just trying to display the parsed file.
        // TextView canvas_txt = (TextView)findViewById(R.id.canvas_reserved);
        // canvas_txt.setText(parsed_xml); //shows nothing right now... parsing
        // issue.

        // ////// use this temporary code because parser not working yet
        // Create ArrayList for local object in list:
        int i;
        for (i = 1; i <= 4; i++) {
            objectList.add(new objectItemInList("htc_one_x_" + Integer.toString(i), "HTC OneX Concept", "sketch #" + Integer.toString(i)));
        }
        for (i = 1; i <= 5; i++) {
            objectList.add(new objectItemInList("rsz_ducati_panigale_concept_" + Integer.toString(i), "Ducati Panigale", "concept #" + Integer.toString(i)));
        }
        for (i = 1; i <= 4; i++) {
            objectList.add(new objectItemInList("rsz_ducati_panigale_schematic_" + Integer.toString(i), "Ducati Panigale", "schematic #" + Integer.toString(i)));
        }
        //for (i = 1; i <= 3; i++) {
        //    objectList.add(new objectItemInList("ducati_panigale_finalized_" + Integer.toString(i), "Ducati Panigale", "finalized #" + Integer.toString(i)));
        //}

        ListView list_view = (ListView) findViewById(R.id.objectList);
        adapter = new ObjectListAdapter(this, R.layout.object_row, objectList);
        ObjectListAdapter adapter1 = new ObjectListAdapter(this, R.layout.object_row, objectList);
        list_view.setAdapter(adapter);

        list_view.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                objectItemInList item = objectList.get(position);
                String pathToImage = item.getImagePath();
                int imgResId = getResources().getIdentifier(item.getImagePath(), "drawable", getPackageName());
                addNewImageItem(imgResId);
            }
        });

        // Canvas
        main_canvas = (MyAbsoluteLayout) findViewById(R.id.drag_layer);

        // Instantiate listeners
        new DragController();
        // Bind listeners
        main_canvas.setOnDragListener(DragController.drag_layer_listener);
        main_canvas.setOnTouchListener(DragController.drag_layer_listener);
        
        ImageView trash_bin = (ImageView) findViewById(R.id.trash_bin);
        trash_bin.setOnDragListener(DragController.view_removal_listener);
        trash_bin.setOnTouchListener(DragController.view_removal_listener);
        
        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /*
     * PIECE OF SHIT (non-Javadoc)
     * 
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    public void addNewImageItem(int image_resource_id) {
        BitmapFactory.Options dimensions = new BitmapFactory.Options();
        Bitmap ImageBitmap = BitmapFactory.decodeResource(getResources(), image_resource_id, dimensions);
        
        DrawableImageView newView = new DrawableImageView(this, ImageBitmap);
        
        int w = dimensions.outWidth;
        int h = dimensions.outHeight;
        int left = 80;
        int top = 100;
        MyAbsoluteLayout.LayoutParams lp = new MyAbsoluteLayout.LayoutParams(w, h, left, top);
        main_canvas.addView(newView, lp);
        newView.setOnTouchListener(DragController.item_listener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.add(0, Menu.FIRST, 0, "Add View Item");
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void addEmptyObjectToList(View view) {
        temporary_counter++;
        // Add object to list:
        objectList.add(new objectItemInList("sample_7", "<new_name>", "<new_value>"));
        adapter.notifyDataSetChanged();
        return;
    }

    public void button_addTextItem(View view) {
        // Do something in response to button
        EditText editText = (EditText) findViewById(R.id.editText);
        TextView newView = new TextView(this);

        String text = editText.getText().toString();
        if (text.length() < 1) {
            return;
        }
        newView.setText(text.trim());
        newView.setBackgroundColor(0x10110100);
        newView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.floating_text_size));
        newView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        int w = newView.getMeasuredWidth();
        int h = newView.getMeasuredHeight();
        int left = 120;
        int top = 120;
        MyAbsoluteLayout.LayoutParams lp = new MyAbsoluteLayout.LayoutParams(w, h, left, top);
        main_canvas.addView(newView, lp);
        newView.setOnTouchListener(DragController.item_listener);
    }

    public void button_addList(View view) {
        new CanvasList(0, 0);
    }

    public void button_addTable(View view) {
        new CanvasTable(inflater, main_canvas, 0, 0);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // mPaint.setXfermode(null);
        // mPaint.setAlpha(0xFF);

        switch (item.getItemId()) {
            case Menu.FIRST:
                // Add a new object to the DragLayer and see if it can be dragged around.
                ImageView newView = new ImageView(this);
                newView.setImageResource(R.drawable.red_ball);
                int w = 60;
                int h = 60;
                int left = 80;
                int top = 100;
                MyAbsoluteLayout.LayoutParams lp = new MyAbsoluteLayout.LayoutParams(w, h, left, top);
                main_canvas.addView(newView, lp);
                newView.setOnTouchListener(DragController.item_listener);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // parses it
    private String loadXmlFromFile(String file_name) throws XmlPullParserException, IOException {
        InputStream stream = null;
        // Instantiate the parser
        // File sdcard = Environment.getExternalStorageDirectory();
        // File sdcard = Environment.getDataDirectory();
        // File file = new File(sdcard, file_name);
        File file = new File("sdcard/TestApp1", "xml_template.xml");
        if (!file.exists())
            Log.d("[failed]", "\"file.exists()\" returns false");
        else
            Log.d("[suspect]", "\"file.exists()\" returns true");
        // //////
        // File ghost_file = new File("sdcard/should_not_exist.xml");
        // if (!ghost_file.exists())
        // Log.d("[correct]", "ghost file does not exist");
        // else
        // Log.d("[incorrect]", "ghost file exists!?");
        // //////
        XmlParser SrcXmlParser = new XmlParser();
        List<Entry> entries = null;
        StringBuilder tmp_builder = new StringBuilder();
        // //////
        // String line;
        // InputStreamReader stream_tmp = new InputStreamReader(new
        // FileInputStream(file));
        // BufferedReader reader = new BufferedReader(stream_tmp);
        // while ((line = reader.readLine()) != null) {
        // Log.d("[XML_LINE]", line);
        // }
        // reader.close();
        // //////

        Log.d("{STEP}", "try");
        try {
            stream = new BufferedInputStream(new FileInputStream(file));
            entries = SrcXmlParser.parse(stream);
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        // StackOverflowXmlParser returns a List (called "entries") of Entry
        // objects.
        // Each Entry object represents a single post in the XML feed.
        // This section processes the entries list to combine each entry with
        // HTML markup.
        // Each entry is displayed in the UI as a link that optionally includes
        // a text summary.
        Log.d("{STEP}", "for");
        for (Entry entry : entries) {
            tmp_builder.append(entry.id);
            tmp_builder.append(" ");
            tmp_builder.append(entry.name);
            tmp_builder.append(" ");
            tmp_builder.append(entry.attr);
            tmp_builder.append("\n");
            Log.d("[id]", entry.id);
            Log.d("[name]", entry.name);
            Log.d("[attr]", entry.name);
        }
        return tmp_builder.toString();
    }
}
