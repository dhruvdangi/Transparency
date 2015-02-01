package com.dhruv.transparency;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

class ViewHolder extends RecyclerView.ViewHolder {
    protected TextView recyclerTV,recyclerTVUnlocks;
    protected LinearLayout recyclerLayout;

    public ViewHolder(View v) {
        super(v);
        recyclerTV =  (TextView) v.findViewById(R.id.recycler_tv);
        recyclerTVUnlocks =  (TextView) v.findViewById(R.id.recycler_tv_unlocks);
        recyclerLayout = (LinearLayout) v.findViewById(R.id.recycler_layout);
    }
}

class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    GestureDetector mGestureDetector;

    public RecyclerItemClickListener(Context context, OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildPosition(childView));
            return true;
        }
        return false;
    }

    @Override public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) { }
}
public class RecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {


    public List<String> List;
    private boolean dates = false;

    public RecyclerAdapter(ArrayList<String> List,boolean d) {
        this.dates = d;
        this.List = List;
    }

    @Override
    public int getItemCount() {
        return List.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder ViewHolder, int i) {
        String ci = List.get(i);
        if(dates){
            Dates.db.open();
            final Cursor c = Dates.db.getAllRecords();
            int noOfUnlocks=0;
            if (c.moveToLast()) {
                for (int x = 0; x < c.getCount(); x++) {
                    if (c.getString(2).contains(ci)) {
                        noOfUnlocks++;
                    }
                    if (c.moveToPrevious() == false)
                        break;            }
            }
            if(noOfUnlocks==0){
                ViewHolder.recyclerTVUnlocks.setText("");
                ViewHolder.recyclerTV.setPadding(0,30,0,25);
                ci = "No Data To Display";
            }
            else
            ViewHolder.recyclerTVUnlocks.setText("Unlocks: "+noOfUnlocks);
            ViewHolder.recyclerTV.setText(ci);
        }
        else {
            ViewHolder.recyclerLayout.removeView(ViewHolder.recyclerTVUnlocks);
            ViewHolder.recyclerTV.setTextSize(20);
            ViewHolder.recyclerTV.setPadding(0,25,0,25);
            ViewHolder.recyclerTV.setText(ci);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.recycler_text_view, viewGroup, false);

        return new ViewHolder(itemView);
    }

}