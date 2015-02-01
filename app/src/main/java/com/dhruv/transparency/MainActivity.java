package com.dhruv.transparency;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialogCompat;
import com.alertdialogpro.AlertDialogPro;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends ActionBarActivity {
	long counter=0;
	String[] menu;
    ArrayList<NavDrawerItem> navDrawerItemList;
    static SharedPreferences sharedPref;
	ArrayAdapter<String> adapter;
	int selected=0;
    List<String> blacklistApps;
    List<String> whitelistApps;
    DrawerLayout dLayout;
    ListView dList;
    ArrayList<String> values=new ArrayList<String>();
    ActionBarDrawerToggle drawerListener;
    ArrayAdapter<String> adapter1;

    static Fragment fr;
    static FragmentManager fm;
    static FragmentTransaction fragmentTransaction;
    public class NavDrawerItem {
        private String mTitle;
        private int mIcon;

        public NavDrawerItem(){}

        public NavDrawerItem(String title, int icon){
            this.mTitle = title;
            this.mIcon = icon;
        }

        public String getTitle(){
            return this.mTitle;
        }

        public int getIcon(){
            return this.mIcon;
        }

        public void setTitle(String title){
            this.mTitle = title;
        }

        public void setIcon(int icon){
            this.mIcon = icon;
        }
    }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        fr = new Dates();
        fm = getFragmentManager();
        fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fr);
        fragmentTransaction.commit();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setTintColor(getResources().getColor(R.color.primaryColorDark));
        }
        startService();
        menu = new String[]{"Home","Ignored Apps","Blacklist Apps","Settings","Delete All","Rate/Review App","More Apps","About"};
        navDrawerItemList = new ArrayList<NavDrawerItem>();
         class NavDrawerListAdapter extends BaseAdapter {

            private Context context;
            private ArrayList<NavDrawerItem> navDrawerItems;

            public NavDrawerListAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItems){
                this.context = context;
                this.navDrawerItems = navDrawerItems;
            }

            @Override
            public int getCount() {
                return navDrawerItems.size();
            }

            @Override
            public Object getItem(int position) {
                return navDrawerItems.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    LayoutInflater mInflater = LayoutInflater.from(getApplicationContext());
                    convertView = mInflater.inflate(R.layout.navigation_li, null);
                }

                ImageView imgIcon = (ImageView) convertView.findViewById(R.id.ivIcon);
                TextView txtTitle = (TextView) convertView.findViewById(R.id.tvTitle);

                imgIcon.setImageResource(navDrawerItems.get(position).getIcon());
                txtTitle.setText(navDrawerItems.get(position).getTitle());

                return convertView;
            }
        }
        navDrawerItemList.add(new NavDrawerItem("Home",R.drawable.ic_home_grey600_24dp));
        navDrawerItemList.add(new NavDrawerItem("Ignored Apps",R.drawable.ic_visibility_off_grey600_24dp));
        navDrawerItemList.add(new NavDrawerItem("Blacklist Apps",R.drawable.ic_dnd_on_grey600_24dp));
        navDrawerItemList.add(new NavDrawerItem("Settings",R.drawable.ic_settings_grey600_24dp));
        navDrawerItemList.add(new NavDrawerItem("Delete",R.drawable.ic_delete_grey600_24dp));
        navDrawerItemList.add(new NavDrawerItem("Rate/Review App",R.drawable.ic_rate_review_grey600_24dp));
        navDrawerItemList.add(new NavDrawerItem("More Apps",R.drawable.ic_shop_grey600_24dp));
        navDrawerItemList.add(new NavDrawerItem("Info",R.drawable.ic_info_grey600_24dp));

        dLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        dList = (ListView) findViewById(R.id.left_drawer);
        adapter1 = new ArrayAdapter<String>(this,R.layout.navigation_li,menu);
        NavDrawerListAdapter navDrawerListAdapter = new NavDrawerListAdapter(getApplicationContext(),navDrawerItemList);
        //navDrawerItemArrayAdapter = new ArrayAdapter<NavDrawerItem>(this,R.layout.navigation_li,navDrawerItemList);
        dList.setAdapter(navDrawerListAdapter);
        dList.setDrawSelectorOnTop(true);
        drawerListener = new ActionBarDrawerToggle(this,dLayout,0,0);
        dLayout.setDrawerListener(drawerListener);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>Transparency</font>"));
		final MaterialDialogCompat.Builder builderdel = new MaterialDialogCompat.Builder(this);

		builderdel.setMessage("Are you sure?").setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        DBAdapter.deleteAll();
                        Log.d("Delete", "Deleted");
                        sharedPref = MainActivity.this.getSharedPreferences("variables", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        DBAdapter.deleteAll();
                        editor.putInt("First", 0);
                        editor.putInt("Last", 0);
                        editor.commit();
                        File dir = new File(getExternalFilesDir(null),"/");
                        if (dir.isDirectory()) {
                            String[] children = dir.list();
                            for (int i = 0; i < children.length; i++) {
                                new File(dir, children[i]).delete();
                            }
                        }
                        //Dates.values.set(0,"no data");
                        Dates.recAdapter.notifyDataSetChanged();
                    }
                }

        ).
            setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    }

            );
            final MaterialDialogCompat.Builder builder = new MaterialDialogCompat.Builder(this).setTitle("Info").setMessage("Version " + getString(R.string.version));
            final EditText passwordEt = new EditText(this);
            final Toast passwordChanged = Toast.makeText(this, "Password changed", Toast.LENGTH_SHORT);
            final Toast passwordRemoved = Toast.makeText(this, "Password removed", Toast.LENGTH_SHORT);
            final Toast noPassword = Toast.makeText(this, "Password field cannot be empty", Toast.LENGTH_SHORT);

            final AlertDialog.Builder passBuilder = new AlertDialog.Builder(this).setTitle("Change Password").setMessage("Enter new password: ").setView(passwordEt).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    SharedPreferences password = MainActivity.this.getSharedPreferences("Password", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = password.edit();
                    if (!passwordEt.getText().toString().equals("")) {
                        editor.putBoolean("pass", true);
                        editor.putString("password", passwordEt.getText().toString());
                        editor.commit();
                        passwordChanged.show();
                    } else {
                        noPassword.show();
                    }
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            }).setNeutralButton("Remove password", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    SharedPreferences password = MainActivity.this.getSharedPreferences("Password", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = password.edit();
                    editor.putBoolean("pass", false);
                    editor.commit();
                    passwordRemoved.show();
                }
            });
            final AlertDialog passDialog = passBuilder.create();
            dList.setOnItemClickListener(new

            OnItemClickListener() {

                @Override
                public void onItemClick (AdapterView < ? > arg0, View v,int position, long id){
                    dLayout.setSelected(true);
                    dLayout.closeDrawers();
                    Bundle args = new Bundle();
                    args.putString("Menu", "");
                    Fragment detail = new DetailFragment();
                    detail.setArguments(args);
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, detail).commit();
                    switch (getSupportActionBar().getTitle().toString()){
                        case "Transparency":
                            fr = new Dates();
                            break;
                        case "Ignored Apps":
                            fr = new WhiteList();
                            break;
                        case "Blacklist Apps":
                            fr = new BlackList();
                            break;
                        case "Settings":
                            fr = new Settings();
                            break;
                    }
                    switch (position) {
                        case 4:
                            fm = getFragmentManager();
                            fragmentTransaction = fm.beginTransaction();
                            fragmentTransaction.replace(R.id.content_frame, fr);
                            fragmentTransaction.commit();
                            builderdel.show();
                            break;
                        case 7:
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                            fm = getFragmentManager();
                            fragmentTransaction = fm.beginTransaction();
                            fragmentTransaction.replace(R.id.content_frame, fr);
                            fragmentTransaction.commit();
                            break;
                        case 5:

                            fm = getFragmentManager();
                            fragmentTransaction = fm.beginTransaction();
                            fragmentTransaction.replace(R.id.content_frame, fr);
                            fragmentTransaction.commit();
                            Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                            i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.dhruv.transparency"));
                            startActivity(i);
                            break;
                        case 6:
                            fm = getFragmentManager();
                            fragmentTransaction = fm.beginTransaction();
                            fragmentTransaction.replace(R.id.content_frame, fr);
                            fragmentTransaction.commit();
                            Intent i1 = new Intent(android.content.Intent.ACTION_VIEW);
                            i1.setData(Uri.parse("https://play.google.com/store/apps/developer?id=Dhruv+Dangi"));
                            startActivity(i1);
                            break;
                        case 1:
                            fr = new WhiteList();
                            fm = getFragmentManager();
                            fragmentTransaction = fm.beginTransaction();
                            fragmentTransaction.replace(R.id.content_frame, fr);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                            getSupportActionBar().setTitle("Ignored Apps");
                            break;
                        case 2:
                            fr = new BlackList();
                            fm = getFragmentManager();
                            fragmentTransaction = fm.beginTransaction();
                            fragmentTransaction.replace(R.id.content_frame, fr);
                            fragmentTransaction.commit();
                            getSupportActionBar().setTitle("Blacklist Apps");
                            break;
                        case 3:
                            fr = new Settings();
                            fm = getFragmentManager();
                            fragmentTransaction = fm.beginTransaction();
                            fragmentTransaction.replace(R.id.content_frame, fr);
                            fragmentTransaction.commit();
                            getSupportActionBar().setTitle("Settings");
                            break;
                        case 0:
                            fr = new Dates();
                            fm = getFragmentManager();
                            fragmentTransaction = fm.beginTransaction();
                            fragmentTransaction.replace(R.id.content_frame, fr);
                            fragmentTransaction.commit();
                            getSupportActionBar().setTitle("Transparency");
                            break;
                    }
                }
            }

            );
            /*
            DBAdapter db = new DBAdapter(this);
            db.open();
            final Cursor c = db.getAllRecords();
            counter=c.getCount();
            int count = c.getCount();
            final ListView lv = (ListView) findViewById(R.id.list);
            if(count==0)
            {
                values.add("No Data To Display");
            }
            else
            {
                if (c.moveToLast()) {
                    for (int i = 0; i < c.getCount(); i++) {
                        if (!values.contains(c.getString(2)))
                            values.add(c.getString(2));
                        if (c.moveToPrevious() == false)
                            break;
                    }
                }

            }
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener()

            {
                public void onItemClick (AdapterView < ? > arg0, View arg1,int arg2, long arg3){
                if (values.get(arg2).equals("No Data To Display")) {
                    Toast.makeText(getBaseContext(), "Please Lock/Unlock your phone to save data", Toast.LENGTH_LONG).show();

                } else {
                    Intent i = new Intent(getBaseContext(), Time.class);
                    i.putExtra("date", values.get(arg2));
                    startActivity(i);
                }
            }
            }

            );

            adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,android.R.id.text1,values);
            lv.setAdapter(adapter);
            */
        }
    @Override
    public void onBackPressed() {switch (getSupportActionBar().getTitle().toString()){

        case "Ignored Apps":
        case "Blacklist Apps":
        case "Settings":
            fr = new Dates();
            fm = getFragmentManager();
            fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fr);
            fragmentTransaction.commit();
            getSupportActionBar().setTitle("Transparency");
            break;
        case "Transparency":
            finish();
            break;
    }
    }
    public void fab_click_whitelist(View v){

        class Item{
            public final String text;
            public final Drawable icon;
            public Item(String text, Drawable icon) {
                this.text = text;
                this.icon = icon;
            }
            @Override
            public String toString() {
                return text;
            }
        }
        final PackageManager pm = this.getApplicationContext().getPackageManager();
        final SharedPreferences sharedPref = this.getSharedPreferences("AppList", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();
        String s = sharedPref.getString("whitelist","  ");
        whitelistApps = new ArrayList<String>(Arrays.asList(s.substring(1, s.length() - 1).split(", ")));
//get a list of installed apps.
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> apps = pm.queryIntentActivities(intent, PackageManager.GET_META_DATA);
        final ArrayList<Item> items= new ArrayList<Item>();
        for (ResolveInfo packageInfo : apps) {
            if(!whitelistApps.contains(packageInfo.loadLabel(this.getPackageManager()).toString()))
                items.add(new Item(packageInfo.loadLabel(this.getApplicationContext().getPackageManager()).toString(),null));

        }
        Collections.sort(items,new Comparator<Item>() {
            @Override
            public int compare(Item lhs, Item rhs) {
                return lhs.text.compareTo(rhs.text);
            }
        });
        ListAdapter adapter2 = new ArrayAdapter<Item>(this,android.R.layout.select_dialog_item,android.R.id.text1,items)
        {
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                TextView tv = (TextView) v.findViewById(android.R.id.text1);
                tv.setTextColor(Color.rgb(75,75,75));
                tv.setPadding(50,0,0,0);
                tv.setTextSize(18);
                //int dp5 = (int) (5 * getResources().getDisplayMetrics().density + 0.5f);
                //tv.setCompoundDrawablePadding(dp5);
                return v;
            }
        };
        AlertDialogPro.Builder appsOptions= new AlertDialogPro.Builder(this);
        appsOptions.setTitle("Select App to Whitelist")
                .setAdapter(adapter2, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        //List<String> whitelistApps = new ArrayList<String>(Arrays.asList(s.substring(1, s.length() - 1).split(", ")));
                        if(whitelistApps.contains(""))
                            whitelistApps.remove(0);

                        whitelistApps.add(items.get(item).toString());
                        editor.putString("whitelist",whitelistApps.toString());
                        editor.commit();
                        WhiteList.adapter.add(items.get(item).toString());
                        WhiteList.adapter.notifyDataSetChanged();
                    }
                }).show();
    }
    public void fab_click_blacklist(View v){

        class Item{
            public final String text;
            public final Drawable icon;
            public Item(String text, Drawable icon) {
                this.text = text;
                this.icon = icon;
            }
            @Override
            public String toString() {
                return text;
            }
        }
        final PackageManager pm = this.getPackageManager();
        final SharedPreferences sharedPref = this.getSharedPreferences("AppList", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();
        String s = sharedPref.getString("blacklist","  ");
        blacklistApps = new LinkedList<String>(Arrays.asList(s.substring(1, s.length() - 1).split(", ")));
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> apps = pm.queryIntentActivities(intent, PackageManager.GET_META_DATA);
        final ArrayList<Item> items= new ArrayList<Item>();
        for (ResolveInfo packageInfo : apps) {
            if(!blacklistApps.contains(packageInfo.loadLabel(this.getPackageManager()).toString()))
                items.add(new Item(packageInfo.loadLabel(this.getPackageManager()).toString(), null));

        }
        Collections.sort(items,new Comparator<Item>() {
            @Override
            public int compare(Item lhs, Item rhs) {
                return lhs.text.compareTo(rhs.text);
            }
        });
        ListAdapter adapter2 = new ArrayAdapter<Item>(this,android.R.layout.select_dialog_item,android.R.id.text1,items)
        {
            public View getView(int position, View convertView, ViewGroup parent) {
                //User super class to create the View
                View v = super.getView(position, convertView, parent);
                TextView tv = (TextView)v.findViewById(android.R.id.text1);
                tv.setTextColor(Color.rgb(75,75,75));
                tv.setPadding(50,0,0,0);
                tv.setTextSize(18);

                //Put the image on the TextView
                //tv.setCompoundDrawablesWithIntrinsicBounds(items.get(position).icon, 0, 0, 0);
                return v;
            }
        };
        //final SharedPreferences sharedPref = this.getSharedPreferences("AppList", Context.MODE_PRIVATE);
        //final SharedPreferences.Editor editor = sharedPref.edit();
        AlertDialogPro.Builder appsOptions= new AlertDialogPro.Builder(this);
        appsOptions.setTitle("Select App to Blacklist")
                .setAdapter(adapter2, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        String s = sharedPref.getString("blacklist","  ");
                        List<String> blacklistApps = new LinkedList<String>(Arrays.asList(s.substring(1, s.length() - 1).split(", ")));
                        if(blacklistApps.contains(""))
                            blacklistApps.remove(0);

                        blacklistApps.add(items.get(item).toString());
                        editor.putString("blacklist",blacklistApps.toString());
                        editor.commit();
                        BlackList.adapter.add(items.get(item).toString());
                        BlackList.adapter.notifyDataSetChanged();
                    }
                }).show();
    }
        @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(drawerListener.onOptionsItemSelected(item))
			return true;
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onPostCreate(savedInstanceState);
		drawerListener.syncState();
	}

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Dates.recAdapter.notifyDataSetChanged();
    }

    public void startService()
	{
		Intent i= new Intent(getBaseContext(), ScreenService.class);
		if(getBaseContext().startService(i) != null){
			Log.e("Service", "Started");
		} ;
	}

}
