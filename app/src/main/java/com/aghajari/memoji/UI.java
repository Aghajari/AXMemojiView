package com.aghajari.memoji;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageView;

import com.aghajari.emojiview.AXEmojiManager;
import com.aghajari.emojiview.view.AXEmojiPager;
import com.aghajari.emojiview.view.AXEmojiView;
import com.aghajari.emojiview.view.AXSingleEmojiView;
import com.aghajari.memojiview.AXMemojiManager;
import com.aghajari.memojiview.memoji.Memoji;
import com.aghajari.memojiview.memoji.MemojiCharacter;
import com.aghajari.memojiview.memoji.OnMemojiActions;
import com.aghajari.memojiview.view.AXMemojiView;

public class UI {

    public static boolean mEmojiView = true;
    public static boolean mSingleEmojiView = true;
    public static boolean mFooterView = true;
    public static boolean mWhiteCategory = false;


    public static void loadTheme(){
        AXEmojiManager.resetTheme();

        // set EmojiView Theme
        AXEmojiManager.getEmojiViewTheme().setFooterEnabled(mFooterView);
        AXEmojiManager.getEmojiViewTheme().setSelectionColor(AXMemojiManager.getMemojiViewTheme().getSelectedColor());
        AXEmojiManager.getEmojiViewTheme().setFooterSelectedItemColor(AXMemojiManager.getMemojiViewTheme().getSelectedColor());

        if (mWhiteCategory) {
            AXEmojiManager.getEmojiViewTheme().setSelectionColor(Color.TRANSPARENT);
            AXEmojiManager.getEmojiViewTheme().setSelectedColor(AXMemojiManager.getMemojiViewTheme().getSelectedColor());
            AXEmojiManager.getEmojiViewTheme().setCategoryColor(Color.WHITE);
            AXEmojiManager.getEmojiViewTheme().setFooterBackgroundColor(Color.WHITE);
            AXEmojiManager.getEmojiViewTheme().setAlwaysShowDivider(true);

            AXMemojiManager.getMemojiViewTheme().setCategoryColor(Color.WHITE);
            AXMemojiManager.getMemojiViewTheme().setAlwaysShowDivider(true);
        }
    }

    public static AXEmojiPager loadView(final Context context, EditText editText , final AppCompatImageView imageView){
        AXEmojiPager emojiPager = new AXEmojiPager(context);

        // Memoji Page
        AXMemojiView memojiView = new AXMemojiView(context);
        emojiPager.addPage(memojiView, new AXEmojiPager.OnFooterItemBinder() {
            @Override
            public void onBindFooterItem(AppCompatImageView view, int index, boolean selected) {
                MemojiCharacter character = AXMemojiManager.findPageMemojiCharacter();
                if (character!=null){
                    AXMemojiManager.getMemojiProvider().getLoader().onLoadMemoji(view,character.getCategoryMemoji());
                }
            }
        });
        memojiView.setOnMemojiActionsListener(new OnMemojiActions() {
            @Override
            public void onClick(AppCompatImageView view, Memoji memoji, boolean fromRecent, boolean fromVariant) {
                if (!fromVariant) {
                    Toast.makeText(context,memoji.getImage() + " Clicked!",Toast.LENGTH_SHORT).show();
                    AXMemojiManager.getMemojiProvider().getLoader().onLoadMemoji(imageView,memoji);
                }
            }

            @Override
            public boolean onLongClick(AppCompatImageView view, Memoji memoji, boolean fromRecent, boolean fromVariant) {
                return false;
            }
        });


        if (mSingleEmojiView) {
            /**
             * add single emoji view
             */
            AXSingleEmojiView singleEmojiView = new AXSingleEmojiView(context);
            emojiPager.addPage(singleEmojiView, R.drawable.ic_msg_panel_smiles);
        }

        if (mEmojiView) {
            /**
             * add emoji view (with viewpager)
             */
            AXEmojiView emojiView = new AXEmojiView(context);
            emojiPager.addPage(emojiView, R.drawable.ic_msg_panel_stickers);
        }

        // set target emoji edit text to emojiViewPager
        emojiPager.setEditText(editText);

        emojiPager.setSwipeWithFingerEnabled(true);

        emojiPager.setLeftIcon(R.drawable.ic_ab_search);
        emojiPager.setOnFooterItemClicked(new AXEmojiPager.OnFooterItemClicked() {
                @Override
                public void onClick(View view,boolean leftIcon) {
                    if (leftIcon) Toast.makeText(context,"Search Clicked", Toast.LENGTH_SHORT).show();
                }
        });

        return emojiPager;
    }
}
