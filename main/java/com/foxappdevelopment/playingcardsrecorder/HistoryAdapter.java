package com.foxappdevelopment.playingcardsrecorder;

import android.icu.text.DateFormat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private ArrayList<GameListDemo> gameLists;
    private OnItemClickListener onItemClickListener;
    private static int AD_TYPE = 1;
    private static int CONTENT_TYPE = 2;


    public HistoryAdapter(ArrayList<GameListDemo> gameLists){
        this.gameLists = gameLists;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_card_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        if(gameLists.get(position)==null)
            return AD_TYPE;
        return CONTENT_TYPE;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final GameListDemo gameList = gameLists.get(position);
        String[] names_text = gameList.getNames();
        int[] score_text = gameList.getFinal_score();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        holder.date.setText(dateFormat.format(gameList.getDate()));
        for (int i = 0; i < 4; i++){
            holder.names[i].setText(names_text[i]);
            holder.finalScore[i].setText(Integer.toString(score_text[i]));
        }

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(gameList);
            }
        };

        View.OnClickListener listener1 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onDeleteItemClick(gameList);
            }
        };
        holder.cardView.setOnClickListener(listener);
        holder.delete.setOnClickListener(listener1);
    }

    @Override
    public int getItemCount() {
        return (null != gameLists ? gameLists.size() : 0);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public CardView cardView;
        public TextView[] names = new TextView[4];
        public TextView[] finalScore = new TextView[4];
        public TextView date;
        public TextView delete;

        public ViewHolder(View view) {
            super(view);

            //finds the card view
            cardView = (CardView) view.findViewById(R.id.card_view);

            //finds the date and delete view
            date = (TextView) view.findViewById(R.id.date);
            delete = (TextView) view.findViewById(R.id.delete);

            //finds names text views
            names[0] = (TextView) view.findViewById(R.id.player1_tv_h);
            names[1] = (TextView) view.findViewById(R.id.player2_tv_h);
            names[2] = (TextView) view.findViewById(R.id.player3_tv_h);
            names[3] = (TextView) view.findViewById(R.id.player4_tv_h);

            //finds finalScore text views
            finalScore[0] = (TextView) view.findViewById(R.id.player1_tv_f);
            finalScore[1] = (TextView) view.findViewById(R.id.player2_tv_f);
            finalScore[2] = (TextView) view.findViewById(R.id.player3_tv_f);
            finalScore[3] = (TextView) view.findViewById(R.id.player4_tv_f);
        }
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
