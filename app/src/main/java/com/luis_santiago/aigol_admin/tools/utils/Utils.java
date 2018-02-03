package com.luis_santiago.aigol_admin.tools.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.ImageView;

import com.luis_santiago.aigol_admin.UI.tabs_actions.MatchesFragment;
import com.luis_santiago.aigol_admin.UI.tabs_actions.TablesFragment;
import com.luis_santiago.aigol_admin.tools.adapters.PageAdapter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Luis Fernando Santiago Ruiz on 9/8/17.
 */

public class Utils {


    public static PageAdapter setUpFragment(Context c, Bundle bundle){
        TablesFragment tablesFragment = new TablesFragment();
        MatchesFragment matchesFragment = new MatchesFragment();
        tablesFragment.setArguments(bundle);
        matchesFragment.setArguments(bundle);
        FragmentManager fragmentManager = ((FragmentActivity) c).getSupportFragmentManager();
        PageAdapter pageAdapter = new PageAdapter(fragmentManager, tablesFragment, matchesFragment);
        return pageAdapter;
    }

    /** This is for downloading the image with piccasso*/
    public static void DownloadImage(ImageView imageView, String url){
        Picasso.with(imageView.getContext())
                .load(url)
                .into(imageView, new Callback.EmptyCallback(){
                    @Override
                    public void onSuccess() {
                        // do nothing for now
                    }
                });
        Picasso.with(imageView.getContext())
                .load(url)
                .into(target);
    }

    private static Target target = new Target() {
        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int numberToChangeNameOfFile= 0;
                    File file = new File(Environment.getExternalStorageDirectory().getPath() +
                            "/team_logo.jpg"+String.valueOf(numberToChangeNameOfFile));
                    try {
                        file.createNewFile();
                        FileOutputStream outputStream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream);
                        //In order to save an image correctly we change the number to create a
                        // diffent image file on local storage
                        numberToChangeNameOfFile++;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };
}
