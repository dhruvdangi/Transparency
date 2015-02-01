package com.dhruv.transparency;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialogCompat;
import com.alertdialogpro.AlertDialogPro;
import com.shamanland.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WhiteList extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        View myInflatedView = inflater.inflate(R.layout.activity_whitelist, container,false);
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setTintColor(getResources().getColor(R.color.primaryColorDark));
        }
        initialiseNavigationDrawer();
        //getSupportActionBar().setTitle("Ignored Apps");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>Ignored Apps </font>"));
        */
        TextView cvHeading = (TextView) myInflatedView.findViewById(R.id.CardViewHeading);
        TextView cvText = (TextView) myInflatedView.findViewById(R.id.CardViewText);
        cvHeading.setText("Ignored Apps are not tracked");
        cvText.setText("Apps listed here won't show up in your recently visited apps section and can be safely chosen to be ignored.");
        FloatingActionButton fab = (FloatingActionButton) myInflatedView.findViewById(R.id.fab);
        fab.setSize(FloatingActionButton.SIZE_NORMAL);
        fab.setImageResource(R.drawable.ic_add_white_48dp);
        fab.initBackground();
        final ListView lv = (ListView) myInflatedView.findViewById(R.id.list);
        final SharedPreferences sharedPref = getActivity().getApplicationContext().getSharedPreferences("AppList", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();
        String s = sharedPref.getString("whitelist","  ");
        final List<String> whitelistApps = new ArrayList<String>(Arrays.asList(s.substring(1, s.length() - 1).split(", ")));
        if(whitelistApps.contains(""))
            whitelistApps.remove(0);
        adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,android.R.id.text1,whitelistApps);
        lv.setAdapter(adapter);
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            final MaterialDialogCompat.Builder builderdel = new MaterialDialogCompat.Builder(getActivity());

                @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                builderdel.setTitle("Delete").setMessage("Are you sure?").setPositiveButton("Delete",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                         whitelistApps.remove(position);
                         editor.putString("whitelist",whitelistApps.toString());
                         editor.commit();
                         adapter.notifyDataSetChanged();

                    }
                }).setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                }).show();
                return false;
            }
        });
        return myInflatedView;
    }
    long counter=0;
    String[] menu;
    int selected=0;
    DrawerLayout dLayout;
    ListView dList;
    ArrayList<String> values=new ArrayList<String>();
    ActionBarDrawerToggle drawerListener;
    ArrayAdapter<String> adapter1;
    static ArrayAdapter<String> adapter;


  /*

    public void initialiseNavigationDrawer(){
        menu = new String[]{"Home","No. Of Entries","Ignored Apps","Blacklist Apps","Change Password","Delete All","Rate/Review App","More Apps","About"};
        dLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        dList = (ListView) findViewById(R.id.left_drawer);
        adapter1 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,menu);
        dList.setAdapter(adapter1);
        dList.setDrawSelectorOnTop(true);
        drawerListener = new ActionBarDrawerToggle(this, dLayout,0, 0);
        dLayout.setDrawerListener(drawerListener);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        //getSupportActionBar().setTitle("Ignored Apps");
        final AlertDialog.Builder builderdel = new AlertDialog.Builder(this);
        builderdel.setTitle("Delete").setMessage("Are you sure?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        DBAdapter.deleteAll();
                        Log.e("Delete", "Deleted");
                        SharedPreferences sharedPref;
                        sharedPref = WhiteList.this.getSharedPreferences("variables", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        DBAdapter.deleteAll();
                        editor.putInt("First", 0);
                        editor.putInt("Last", 0);
                        editor.commit();
                        adapter.clear();
                        adapter.add("No Data To Display");
                        adapter.notifyDataSetChanged();
                    }
                }

        ).

                setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }.
                        }

                );
        final AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("About").setMessage("Version " + getString(R.string.version));
        final EditText passwordEt = new EditText(this);
        final Toast passwordChanged = Toast.makeText(this, "Password changed", Toast.LENGTH_SHORT);
        final Toast passwordRemoved = Toast.makeText(this, "Password removed", Toast.LENGTH_SHORT);
        final Toast noPassword = Toast.makeText(this, "Password field cannot be empty", Toast.LENGTH_SHORT);

        final AlertDialog.Builder passBuilder = new AlertDialog.Builder(this).setTitle("Change Password").setMessage("Enter new password: ").setView(passwordEt).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                SharedPreferences password = WhiteList.this.getSharedPreferences("Password", Context.MODE_PRIVATE);
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
                SharedPreferences password = WhiteList.this.getSharedPreferences("Password", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = password.edit();
                editor.putBoolean("pass", false);
                editor.commit();
                passwordRemoved.show();
            }
        });
        final AlertDialog passDialog = passBuilder.create();

        dList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                                 @Override
                                                 public void onItemClick (AdapterView < ? > arg0, View v,int position, long id){
                                                     dLayout.setSelected(true);
                                                     dLayout.closeDrawers();
                                                     Bundle args = new Bundle();
                                                     args.putString("Menu", "");//menu[position]
                                                     Fragment detail = new DetailFragment();
                                                     detail.setArguments(args);
                                                     FragmentManager fragmentManager = getFragmentManager();
                                                     fragmentManager.beginTransaction().replace(R.id.content_frame, detail).commit();
                                                     switch (position) {
                                                         case 0:
                                                             finish();
                                                             break;
                                                         case 5:
                                                             builderdel.show();

                                                             break;
                                                         case 4:
                                                             passDialog.show();
                                                             break;
                                                         case 1:

                                                             AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                                                                     WhiteList.this);
                                                             builderSingle.setTitle("Max number of entries");
                                                             final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(WhiteList.this, android.R.layout.select_dialog_singlechoice);
                                                             arrayAdapter.addAll("100 Entries", "500 Entries", "1000 Entries", "5000 Entries", "Unlimited");
                                                             SharedPreferences sharedPref = WhiteList.this.getSharedPreferences("variables", Context.MODE_PRIVATE);
                                                             switch (sharedPref.getInt("MaxEnteries", 0)) {
                                                                 case 100:
                                                                     selected = 0;
                                                                     break;
                                                                 case 500:
                                                                     selected = 1;
                                                                     break;
                                                                 case 1000:
                                                                     selected = 2;
                                                                     break;
                                                                 case 5000:
                                                                     selected = 3;
                                                                     break;
                                                                 default:
                                                                     selected = 4;
                                                                     break;
                                                             }
                                                             // selected=sharedPref.getInt("MaxEnteries", 0);
                                                             builderSingle.setSingleChoiceItems(arrayAdapter, selected, new DialogInterface.OnClickListener() {

                                                                 @Override
                                                                 public void onClick(DialogInterface dialog, int which) {
                                                                     SharedPreferences sharedPref = WhiteList.this.getSharedPreferences("variables", Context.MODE_PRIVATE);
                                                                     SharedPreferences.Editor editor = sharedPref.edit();
                                                                     switch (which) {
                                                                         case 0:
                                                                             selected = 0;
                                                                             editor.putInt("MaxEnteries", 100);
                                                                             break;
                                                                         case 1:
                                                                             selected = 1;
                                                                             editor.putInt("MaxEnteries", 500);
                                                                             break;
                                                                         case 2:
                                                                             selected = 2;
                                                                             editor.putInt("MaxEnteries", 1000);
                                                                             break;

                                                                         case 3:
                                                                             selected = 3;
                                                                             editor.putInt("MaxEnteries", 5000);
                                                                             break;
                                                                         case 4:
                                                                             selected = 4;
                                                                             editor.putInt("MaxEnteries", -1);
                                                                             break;
                                                                     }
                                                                     editor.commit();
                                                                 }
                                                             });
                                                             builderSingle.setNegativeButton("OK",
                                                                     new DialogInterface.OnClickListener() {

                                                                         @Override
                                                                         public void onClick(DialogInterface dialog, int which) {
                                                                             dialog.dismiss();
                                                                         }
                                                                     });
                                                             builderSingle.show();
                                                             break;
                                                         case 8:
                                                             AlertDialog alertDialog = builder.create();
                                                             alertDialog.show();
                                                             break;
                                                         case 6:
                                                             Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                                                             i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.dhruv.unlockhistory"));
                                                             startActivity(i);
                                                             break;
                                                         case 7:
                                                             Intent i1 = new Intent(android.content.Intent.ACTION_VIEW);
                                                             i1.setData(Uri.parse("https://play.google.com/store/apps/developer?id=Dhruv+Dangi"));
                                                             startActivity(i1);
                                                             break;
                                                         case 2:
                                                             startActivity(new Intent(WhiteList.this,WhiteList.class));
                                                             break;
                                                         case 3:
                                                             startActivity(new Intent(WhiteList.this,BlackList.class));
                                                             break;


                                                     }
                                                 }
                                             });
    }

*/
}




