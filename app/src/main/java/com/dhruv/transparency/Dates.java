package com.dhruv.transparency;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;

public class Dates extends Fragment {
    long counter=0;
    String[] menu;
    static RecyclerAdapter recAdapter;
    static DBAdapter db;
    static SharedPreferences sharedPref;
    static ArrayAdapter<String> adapter;
    int selected=0;
    DrawerLayout dLayout;
    ListView dList;
    RecyclerView recList;
    ArrayList<String> values=new ArrayList<String>();
    ActionBarDrawerToggle drawerListener;
    ArrayAdapter<String> adapter1;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        View myInflatedView = inflater.inflate(R.layout.activity_dates, container,false);
        db = new DBAdapter(getActivity().getApplicationContext());
        db.open();
        final Cursor c = db.getAllRecords();
        counter=c.getCount();
        int count = c.getCount();
        final RecyclerView lv = (RecyclerView) myInflatedView.findViewById(R.id.list);
        lv.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).build());

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
/*
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()

                                  {
                                      public void onItemClick (AdapterView < ? > arg0, View arg1,int arg2, long arg3){
                                          if (values.get(arg2).equals("No Data To Display")) {
                                              Toast.makeText(getActivity().getApplicationContext(), "Please Lock/Unlock your phone to save data", Toast.LENGTH_LONG).show();

                                          } else {
                                              Intent i = new Intent(getActivity().getApplicationContext(), Time.class);
                                              i.putExtra("date", values.get(arg2));
                                              startActivity(i);
                                          }
                                      }
                                  }

        );*/

        adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,android.R.id.text1,values);
        recList = (RecyclerView) myInflatedView.findViewById(R.id.list);
        recList.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).build());
        recList.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        if (values.get(position).equals("No Data To Display")) {
                            Toast.makeText(getActivity().getApplicationContext(), "Please Lock/Unlock your phone to save data", Toast.LENGTH_LONG).show();

                        } else {
                            Intent i = new Intent(getActivity().getApplicationContext(), Time.class);
                            i.putExtra("date", values.get(position));
                            startActivity(i);
                        }                    }
                })
        );
        return myInflatedView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if(drawerListener.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recAdapter = new RecyclerAdapter(values,true);
        recList.setAdapter(recAdapter);
        recList.setLayoutManager(new LinearLayoutManager(getActivity()));

    }
}
