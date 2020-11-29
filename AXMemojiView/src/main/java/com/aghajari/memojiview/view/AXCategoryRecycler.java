/*
 * Copyright (C) 2020 - Amir Hossein Aghajari
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */


package com.aghajari.memojiview.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aghajari.emojiview.utils.Utils;
import com.aghajari.emojiview.view.AXEmojiLayout;
import com.aghajari.memojiview.AXMemojiManager;
import com.aghajari.memojiview.R;
import com.aghajari.memojiview.shared.MemojiSaver;

class AXCategoryRecycler extends AXEmojiLayout {

    public AXCategoryRecycler(Context context, AXMemojiView pager) {
        super(context);
        this.pager = pager;
        init();
    }

    AXMemojiView pager;
    RecyclerView icons, addIcons;
    View Divider;

    void clear() {
        addIcons.setAdapter(null);
        addLayout.removeAllViews();
        valueAnimator = null;
    }

    void init() {
        View background = new View(getContext());
        background.setBackgroundColor(AXMemojiManager.getMemojiViewTheme().getCategoryColor());
        this.addView(background, new LayoutParams(0, 0, -1, -1));
        // int iconSize = Utils.dpToPx(getContext(),24);
        int w = getContext().getResources().getDisplayMetrics().widthPixels;
        icons = new RecyclerView(getContext());
        this.addView(icons, new LayoutParams(0, Utils.dpToPx(getContext(), 6), w - Utils.dpToPx(getContext(), 43), -1));
        initAdd();

        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        lm.setOrientation(LinearLayoutManager.HORIZONTAL);
        icons.setLayoutManager(lm);
        icons.setItemAnimator(null);
        icons.setAdapter(new AXCategoryAdapter(pager));
        icons.setOverScrollMode(View.OVER_SCROLL_NEVER);
        icons.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                if (parent.findContainingViewHolder(view).getAbsoluteAdapterPosition() == 0) {
                    outRect.left = Utils.dpToPx(getContext(), 2);
                }
            }
        });

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        Divider = new View(getContext());
        this.addView(Divider, new LayoutParams(
                0, Utils.dpToPx(getContext(), 48), w, Utils.dpToPx(getContext(), 1)));
        if (!AXMemojiManager.getMemojiViewTheme().shouldShowAlwaysDivider())
            Divider.setVisibility(GONE);
        Divider.setBackgroundColor(AXMemojiManager.getMemojiViewTheme().getDividerColor());

        if (isShowingAddPage) {
            isShowingAddPage = false;
            showAddPage(0);
        } else {
            isShowingAddPage = true;
            hideAddPage(0);
        }
    }

    public void setPageIndex(int index) {
        ((AXCategoryAdapter) icons.getAdapter()).update();
    }

    public void setAddPageIndex(int index) {
        ((AXCategoryMoreAdapter) addIcons.getAdapter()).selected = index;
        addIcons.getAdapter().notifyDataSetChanged();
        addIcons.smoothScrollToPosition(index);
    }


    AppCompatImageView add;
    AXEmojiLayout addLayout = new AXEmojiLayout(getContext());
    View saveSelection;
    AppCompatImageView save;

    private void initAdd() {
        addLayout.setBackgroundColor(AXMemojiManager.getMemojiViewTheme().getCategoryColor());
        this.setBackgroundColor(AXMemojiManager.getMemojiViewTheme().getCategoryColor());

        final int w = getContext().getResources().getDisplayMetrics().widthPixels;
        //w = Utils.dpToPx(getContext(),43)
        this.addView(addLayout, new LayoutParams(icons.getLayoutParams().width, Utils.dpToPx(getContext(), 6), w, -1));

        View divider = new View(getContext());
        addLayout.addView(divider, new LayoutParams(
                0, Utils.dpToPx(getContext(), 6), Utils.dpToPx(getContext(), 1), Utils.dpToPx(getContext(), 24)));
        divider.setBackgroundColor(AXMemojiManager.getMemojiViewTheme().getDividerColor());

        View divider2 = new View(getContext());
        addLayout.addView(divider2, new LayoutParams(
                Utils.dpToPx(getContext(), 75), Utils.dpToPx(getContext(), 6), Utils.dpToPx(getContext(), 1), Utils.dpToPx(getContext(), 24)));
        divider2.setBackgroundColor(AXMemojiManager.getMemojiViewTheme().getDividerColor());

        View addSelection = new View(getContext());
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(AXMemojiManager.getMemojiViewTheme().getAddBackgroundColor());
        gd.setCornerRadius(Utils.dpToPx(getContext(), 18));
        addSelection.setBackground(gd);
        addLayout.addView(addSelection, new LayoutParams(Utils.dpToPx(getContext(), 10), Utils.dpToPx(getContext(), 6)
                , Utils.dpToPx(getContext(), 24), Utils.dpToPx(getContext(), 24)));
        //addSelection.setVisibility(GONE);

        add = new AppCompatImageView(getContext());
        Drawable dr = AppCompatResources.getDrawable(addLayout.getContext(), R.drawable.add);
        DrawableCompat.setTint(DrawableCompat.wrap(dr), AXMemojiManager.getMemojiViewTheme().getAddColor());
        add.setImageDrawable(dr);
        addLayout.addView(add, new LayoutParams(Utils.dpToPx(getContext(), 12), Utils.dpToPx(getContext(), 8)
                , Utils.dpToPx(getContext(), 20), Utils.dpToPx(getContext(), 20)));

        saveSelection = new View(getContext());
        addLayout.addView(saveSelection, new LayoutParams(Utils.dpToPx(getContext(), 43), Utils.dpToPx(getContext(), 6)
                , Utils.dpToPx(getContext(), 24), Utils.dpToPx(getContext(), 24)));
        //saveSelection.setVisibility(GONE);

        save = new AppCompatImageView(getContext());
        addLayout.addView(save, new LayoutParams(Utils.dpToPx(getContext(), 45), Utils.dpToPx(getContext(), 8)
                , Utils.dpToPx(getContext(), 20), Utils.dpToPx(getContext(), 20)));

        addIcons = new RecyclerView(getContext());
        addLayout.addView(addIcons, new LayoutParams(Utils.dpToPx(getContext(), 76), 0, w - Utils.dpToPx(getContext(), 76), -1));
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        lm.setOrientation(LinearLayoutManager.HORIZONTAL);
        addIcons.setLayoutManager(lm);
        addIcons.setItemAnimator(null);
        addIcons.setOverScrollMode(View.OVER_SCROLL_NEVER);
        addIcons.setAdapter(new AXCategoryMoreAdapter(pager));
        //addIcons.setOverScrollMode(View.OVER_SCROLL_NEVER);
        addIcons.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                if (parent.findContainingViewHolder(view).getAbsoluteAdapterPosition() == 0) {
                    outRect.left = Utils.dpToPx(getContext(), 6);
                }
            }
        });

        addLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        View.OnClickListener openClick = new OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Clicked");
                if (isShowingAddPage) {
                    hideAddPage(250);
                } else {
                    showAddPage(250);
                }
            }
        };
        add.setOnClickListener(openClick);
        addSelection.setOnClickListener(openClick);

        View.OnClickListener saver = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (saveSelection.getTag() != null && saveSelection.getTag().equals(true)) {
                    AXMemojiManager.getMemojiSaver().deleteCharacter(((AXCategoryMoreAdapter) addIcons.getAdapter()).getSelectedCharacter());
                } else {
                    AXMemojiManager.getMemojiSaver().saveCharacter(((AXCategoryMoreAdapter) addIcons.getAdapter()).getSelectedCharacter());
                }
                updateSaveSelection(((AXCategoryMoreAdapter) addIcons.getAdapter()).selected);
            }
        };
        saveSelection.setOnClickListener(saver);
        save.setOnClickListener(saver);

        addLayout.requestLayout();
    }

    boolean isShowingAddPage = false;
    ValueAnimator valueAnimator = null;

    public void showAddPage(int duration) {
        if (!isShowingAddPage && (valueAnimator == null || !valueAnimator.isRunning())) {
            isShowingAddPage = true;
            ((AXCategoryMoreAdapter) addIcons.getAdapter()).refresh();
            final int w = getContext().getResources().getDisplayMetrics().widthPixels;
            valueAnimator = com.aghajari.memojiview.view.Utils.animator(addLayout, duration, w - Utils.dpToPx(getContext(), 41), -Utils.dpToPx(getContext(), 1));
            valueAnimator.start();

            addLayout.requestLayout();
            addIcons.scrollToPosition(0);
            updateSaveSelection(0);

            ObjectAnimator rotation = ObjectAnimator.ofFloat(add, "rotation", 0f, 45f);
            rotation.setDuration(duration);
            rotation.start();
            pager.showAddPageNow(duration);
        }
    }

    public void hideAddPage(int duration) {
        if (isShowingAddPage && (valueAnimator == null || !valueAnimator.isRunning())) {
            isShowingAddPage = false;
            final int w = getContext().getResources().getDisplayMetrics().widthPixels;
            valueAnimator = com.aghajari.memojiview.view.Utils.animator(addLayout, duration, -Utils.dpToPx(getContext(), 1), w - Utils.dpToPx(getContext(), 41));
            valueAnimator.start();

            ObjectAnimator rotation = ObjectAnimator.ofFloat(add, "rotation", 45f, 0f);
            rotation.setDuration(duration);
            rotation.start();
            pager.hideAddPageNow(duration);
        }
    }

    public void updateSaveSelection(int index) {
        boolean hasSaved = MemojiSaver.savedCharacters.contains(((AXCategoryMoreAdapter) addIcons.getAdapter()).characterList.get(index));

        if (saveSelection.getTag() == null || !saveSelection.getTag().equals(hasSaved)) {
            if (hasSaved) {
                GradientDrawable gd2 = new GradientDrawable();
                gd2.setColor(AXMemojiManager.getMemojiViewTheme().getDeleteBackgroundColor());
                gd2.setCornerRadius(Utils.dpToPx(getContext(), 18));
                saveSelection.setBackground(gd2);

                Drawable dr2 = AppCompatResources.getDrawable(addLayout.getContext(), AXMemojiManager.getMemojiViewTheme().getDeleteIcon());
                DrawableCompat.setTint(DrawableCompat.wrap(dr2), AXMemojiManager.getMemojiViewTheme().getDeleteColor());
                save.setImageDrawable(dr2);
            } else {
                GradientDrawable gd2 = new GradientDrawable();
                gd2.setColor(AXMemojiManager.getMemojiViewTheme().getSaveBackgroundColor());
                gd2.setCornerRadius(Utils.dpToPx(getContext(), 18));
                saveSelection.setBackground(gd2);

                Drawable dr2 = AppCompatResources.getDrawable(addLayout.getContext(), AXMemojiManager.getMemojiViewTheme().getSaveIcon());
                DrawableCompat.setTint(DrawableCompat.wrap(dr2), AXMemojiManager.getMemojiViewTheme().getSaveColor());
                save.setImageDrawable(dr2);
            }
            saveSelection.setTag(hasSaved);
        }
    }
}
