package com.manjurulhoque.musicx;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.editTextSearch)
    EditText editTextSearch;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    //List<String> allSongs = new ArrayList<String>();
    //List<File> mySongs;
    SongRecyclerViewAdapter songRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        checkPermission();

        Constants.mySongs = findSongs(Environment.getExternalStorageDirectory());
        Collections.sort(Constants.mySongs, new Comparator<File>() {
            @Override
            public int compare(File file1, File file2) {
                return file1.getName().compareTo(file2.getName());
            }
        });
//        Collections.sort(allSongs, new Comparator<String>() {
//            @Override
//            public int compare(String item, String t1) {
//                return item.compareTo(t1);
//            }
//        });

        initRecyclerView();
    }

    private void initRecyclerView() {
        songRecyclerViewAdapter = new SongRecyclerViewAdapter(getApplicationContext(), Constants.mySongs, new SongRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(File songFile, int position) {
                Toast.makeText(getApplicationContext(), Utils.getSongTitle(songFile), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
                intent.putExtra("song", songFile);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(songRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    private void checkPermission() {
        String[] PERMISSIONS = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 101);
        }
    }

    public List<File> findSongs(File root) {
        List<File> al = new ArrayList<File>();

        File[] files = root.listFiles();

        for (File singleFile : files) {
            if (singleFile.isDirectory() && !singleFile.isHidden()) {
                al.addAll(findSongs(singleFile));
            } else {
                if (singleFile.getName().endsWith(".mp3")) {
                    al.add(singleFile);
                    //allSongs.add(getSongTitle(singleFile));
                }
            }
        }
        return al;
    }
}
