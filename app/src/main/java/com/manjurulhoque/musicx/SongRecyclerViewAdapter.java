package com.manjurulhoque.musicx;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SongRecyclerViewAdapter extends RecyclerView.Adapter<SongRecyclerViewAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(File file, int position);
    }

    private List<String> mySongs;
    List<File> allSongs;
    private Context mContext;
    private final OnItemClickListener listener;

    public SongRecyclerViewAdapter(Context context, List<File> songs, OnItemClickListener listener) {
        this.mContext = context;
        this.allSongs = songs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_song_single, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(allSongs.get(position), position, listener);
    }

    @Override
    public int getItemCount() {
        return allSongs.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.textViewSongName)
        public TextView textViewSongName;
        @BindView(R.id.materialRippleLayout)
        public MaterialRippleLayout materialRippleLayout;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(final File song, final int position, final OnItemClickListener listener) {
            textViewSongName.setText(Utils.getSongTitle(song));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(song, position);
                }
            });
        }
    }
}
