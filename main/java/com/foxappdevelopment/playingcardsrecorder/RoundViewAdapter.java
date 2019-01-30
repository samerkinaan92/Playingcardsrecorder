package com.foxappdevelopment.playingcardsrecorder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class RoundViewAdapter extends BaseAdapter {

    private ArrayList<Round> list;
    private static LayoutInflater inflater = null;
    private Context mContext;

    public RoundViewAdapter(Context mContext) {
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
    }

    public RoundViewAdapter(Context mContext, ArrayList<Round> list){
        new RoundViewAdapter(mContext);
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Round getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View newView = view;
        ViewHolder holder;
        Round curr = list.get(i);

        if(newView == null){
            holder = new ViewHolder();
            newView = inflater.inflate(R.layout.round_badge, viewGroup, false);

            holder.p[0] = (TextView)newView.findViewById(R.id.first_player_tv);
            holder.p[1] = (TextView)newView.findViewById(R.id.second_player_tv);
            holder.p[2] = (TextView)newView.findViewById(R.id.third_player_tv);
            holder.p[3] = (TextView)newView.findViewById(R.id.fourth_player_tv);
            holder.details = (TextView)newView.findViewById(R.id.round_tv);

            newView.setTag(holder);
        }else {
            holder = (ViewHolder) newView.getTag();
        }

        holder.details.setText(curr.getNum() + ".");
        int[] points = curr.getPoints();
        boolean[] passed = curr.getPassed();
        for (int j = 0; j < 4; j++){
            if(passed[j]){
                holder.p[j].setText(Integer.toString(points[j]));
            }else {
                holder.p[j].setText("-" + Integer.toString(points[j]));
            }
        }

        return newView;
    }

    public void addItem(Round round){
        list.add(round);
        notifyDataSetChanged();
    }

    public void setList(ArrayList<Round> list){
        this.list = list;
    }

    private class ViewHolder{
        TextView[] p = new TextView[4];
        TextView details;
    }
}
