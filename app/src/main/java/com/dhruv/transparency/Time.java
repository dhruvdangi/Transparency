package com.dhruv.transparency;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Time extends ActionBarActivity {

    long counter = 0;
    String date=null;
    int time;
    static RecyclerAdapter recAdapter;
    ArrayAdapter<String> adapter;
    int start = 0, entries = 0;
    ArrayList<String> values = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setTintColor(getResources().getColor(R.color.primaryColorDark));
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        DBAdapter db = new DBAdapter(this);
        db.open();
        final Cursor c = db.getAllRecords();
        Bundle extras = getIntent().getExtras();
        date = extras.getString("date");
        getSupportActionBar().setTitle(date);
        counter = c.getCount();
//        final ListView lv = (ListView) findViewById(R.id.list);
        if (c.getCount() == 0) {
            values.add("No Data To Display");
        } else {
            if (c.moveToLast()) {
                for (int i = 0; i < c.getCount(); i++) {
                    if (c.getString(2).contains(date)) {
                        if (entries == 0) {
                            start = c.getPosition();
                            entries++;
                        }
                        values.add(c.getString(1));
                    }
                    if (c.moveToPrevious() == false)
                        break;
                }
            }
        }
        class Item {
            public final String text;
            public final int icon;

            public Item(String text, Integer icon) {
                this.text = text;
                this.icon = icon;
            }

            @Override
            public String toString() {
                return text;
            }
        }

        RecyclerView recList = (RecyclerView) findViewById(R.id.list);
        recList.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).build());
        recAdapter = new RecyclerAdapter(values,false);
        recList.setAdapter(recAdapter);
        recList.setLayoutManager(new LinearLayoutManager(this));
        recList.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        time = position;
                        c.moveToPosition(start - position);
                        List<String> myList = new ArrayList<String>(Arrays.asList(c.getString(4).substring(1, c.getString(4).length() - 1).replaceAll("\"", "").split(",")));
                        final ArrayList<Item> items = new ArrayList<Item>();
                        items.add(new Item("Picture", R.drawable.ic_account_circle_black_36dp));
                        items.add(new Item("Time Used", R.drawable.ic_restore_black_36dp));
                        items.add(new Item(c.getString(3), R.drawable.transparent));
                        items.add(new Item("Apps Used", R.drawable.appsused));
                        for (int i = 0; i < myList.size(); i++) {
                            items.add(new Item(myList.get(i), R.drawable.transparent));
                        }

                        final Toast t = new Toast(getApplicationContext());
                        //t.makeText(getApplicationContext(), "No Image Found, please check settings", Toast.LENGTH_SHORT);
                        ListAdapter adapter = new ArrayAdapter<Item>(Time.this, android.R.layout.select_dialog_item, android.R.id.text1, items) {
                            public View getView(int position, View convertView, ViewGroup parent) {
                                //User super class to create the View
                                View v = super.getView(position, convertView, parent);
                                TextView tv = (TextView) v.findViewById(android.R.id.text1);

                                //Put the image on the TextView
                                tv.setCompoundDrawablesWithIntrinsicBounds(items.get(position).icon, 0, 0, 0);

                                //Add margin between image and text (support various screen densities)
                                int dp5 = (int) (5 * getResources().getDisplayMetrics().density + 0.5f);
                                tv.setCompoundDrawablePadding(dp5);

                                return v;
                            }

                        };
                        MaterialDialog materialDialog = new MaterialDialog.Builder(Time.this)
                                .customView(R.layout.apps_used, true)
                                .build();
                        File imgFile = new  File(getExternalFilesDir(null).getAbsolutePath() + "/" + date.replace(":","_") + " " + values.get(position).replace(":","_") + ".jpg");
                        Log.d("variables",""+date+values);
                        if(imgFile.exists()){
                            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                            Bitmap thumbnail = Bitmap.createScaledBitmap(myBitmap,64,64,false);
                            ImageView myImage = (ImageView) materialDialog.getCustomView().findViewById(R.id.AppsUsedPicture);
                            myImage.setRotation(-90);
                            myImage.setImageBitmap(thumbnail);
                        }
                        TextView title = (TextView) materialDialog.getCustomView().findViewById(R.id.title);
                        title.setText(c.getString(1));
                        TextView timeUsed = (TextView) materialDialog.getCustomView().findViewById(R.id.timeUsed);
                        timeUsed.setText("Time used: "+c.getString(3));
                        LinearLayout dialogLayout = (LinearLayout) materialDialog.getCustomView().findViewById(R.id.dialogLayout);
                        for (int i = 0; i < myList.size(); i++) {
                            TextView textView = new TextView(Time.this);
                            textView.setTextSize(18);
                            if(i==0)
                                textView.setText("Apps used: "+myList.get(i));
                            else
                                textView.setText("                    " + myList.get(i));
                            dialogLayout.addView(textView);
                        }
                        materialDialog.show();
                    }
                })
        );
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }

    public void Picture(View v) {

        File imgFile = new File(getExternalFilesDir(null).getAbsolutePath() + "/" + date.replace(":","_") + " " + values.get(time).replace(":","_") + ".jpg");
        if (imgFile.exists()) {
            Intent picIntent = new Intent(getApplicationContext(), Picture.class);
            picIntent.putExtra("path", getExternalFilesDir(null).getAbsolutePath() + "/" + date.replace(":","_") + " " + values.get(time).replace(":","_") + ".jpg");
            startActivity(picIntent);
        } else {
            Toast.makeText(getApplicationContext(), "No Image Found, please check settings ", Toast.LENGTH_SHORT).show();
        }

    }
}




