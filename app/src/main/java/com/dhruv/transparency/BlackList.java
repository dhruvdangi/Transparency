package com.dhruv.transparency;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialogCompat;
import com.alertdialogpro.AlertDialogPro;
import com.shamanland.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class BlackList extends Fragment {
    List<String> blacklistApps;
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        View myInflatedView = inflater.inflate(R.layout.activity_blacklist, container,false);
        TextView cvHeading = (TextView) myInflatedView.findViewById(R.id.CardViewHeading);
        TextView cvText = (TextView) myInflatedView.findViewById(R.id.CardViewText);
        cvHeading.setText("Black list apps are monitored closely");
        cvText.setText("Add apps to your black list to specifically schedule notifications to remind yourself to reduce the app usage.");
        FloatingActionButton fab = (FloatingActionButton) myInflatedView.findViewById(R.id.fab);
        fab.setSize(FloatingActionButton.SIZE_NORMAL);
        fab.setImageResource(R.drawable.ic_add_white_48dp);
        fab.initBackground();
        final ListView lv = (ListView) myInflatedView.findViewById(R.id.list);
        SharedPreferences sharedPref = getActivity().getApplicationContext().getSharedPreferences("AppList", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();
        String s = sharedPref.getString("blacklist","  ");
        final List<String> blacklistApps = new LinkedList<String>(Arrays.asList(s.substring(1, s.length() - 1).split(", ")));
        if(blacklistApps.contains(""))
            blacklistApps.remove(0);
        adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,android.R.id.text1,blacklistApps);
        lv.setAdapter(adapter);
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            final MaterialDialogCompat.Builder builderdel = new MaterialDialogCompat.Builder(getActivity());

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                builderdel.setTitle("Delete").setMessage("Are you sure?").setPositiveButton("Delete",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //adapter.remove(blacklistApps.get(position));
                        blacklistApps.remove(position);
                        editor.putString("blacklist",blacklistApps.toString());
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
    static ArrayAdapter<String> adapter;
}




