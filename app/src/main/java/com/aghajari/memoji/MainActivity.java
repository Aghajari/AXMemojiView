package com.aghajari.memoji;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.aghajari.emojiview.AXEmojiManager;
import com.aghajari.emojiview.iosprovider.AXIOSEmojiProvider;
import com.aghajari.memojiview.AXMemojiManager;
import com.aghajari.memojiview.memoji.Memoji;
import com.aghajari.memojiview.memoji.MemojiCharacter;
import com.aghajari.memojiview.memoji.MemojiLoader;
import com.aghajari.memojiview.shared.OnSavedMemojiCharactersChanged;
import com.bumptech.glide.Glide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AXEmojiManager.install(this,new AXIOSEmojiProvider(this));

        AXMemojiManager.install(this, readMemojiDataAsJson(),createLoader());

        UI.loadTheme();

        AXMemojiManager.setOnSavedMemojiCharactersChanged(new OnSavedMemojiCharactersChanged() {
            @Override
            public void onChanged(MemojiCharacter character, boolean hasSaved) {
                String text = character.getName() + " " + (hasSaved ? "Saved!" : "Removed");
                Toast.makeText(MainActivity.this,text,Toast.LENGTH_SHORT).show();
            }
        });

        startActivity(new Intent(this,EmojiPopupViewActivity.class));
    }

    private MemojiLoader createLoader(){
        return  new MemojiLoader() {
            @Override
            public void onLoadMemoji(AppCompatImageView view, Memoji memoji) {

                // Load memojis from server
                /*String serverURL = "https://..../Memoji/";
                String url = serverURL + memoji.getCategory()+"/"+memoji.getCharacter()+"/"+memoji.getImage();
                Glide.with(view)
                 .load(url)
                 .into(view);*/


                // Load memojis from assets
                Glide.with(view)
                        .load(Uri.parse("file:///android_asset/Memoji/"+memoji.getCategory()+"/"+memoji.getCharacter()+"/"+memoji.getImage()))
                        .into(view);
            }

            @Override
            public void onLoadMemojiCategory(AppCompatImageView view, Memoji memoji, boolean selected) {
                onLoadMemoji(view,memoji);
            }
        };
    }

    private String readMemojiDataAsJson(){
        StringBuilder sb = new StringBuilder();
        String str = "";
        try {
            InputStream is = null;
            is = getAssets().open("memoji_data.json");
            BufferedReader br = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8 ));
            }
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}