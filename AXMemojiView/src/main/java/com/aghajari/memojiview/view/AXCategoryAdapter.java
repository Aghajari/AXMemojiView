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

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.aghajari.emojiview.utils.Utils;
import com.aghajari.emojiview.view.AXEmojiLayout;
import com.aghajari.memojiview.AXMemojiManager;
import com.aghajari.memojiview.shared.MemojiSaver;


class AXCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    AXEmojiLayout pager;
    boolean recent;

    public AXCategoryAdapter(AXEmojiLayout pager) {
        recent = !AXMemojiManager.getRecentMemoji().isEmpty();
        this.pager = pager;
    }

    public void update() {
        recent = !AXMemojiManager.getRecentMemoji().isEmpty();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if (i == 10) {
            int iconSize = Utils.dpToPx(viewGroup.getContext(), 24);
            AXEmojiLayout layout = new AXEmojiLayout(viewGroup.getContext());

            View selection = new View(viewGroup.getContext());
            layout.addView(selection, new AXEmojiLayout.LayoutParams(0, 0, 0, 0));
            //selection.setBackgroundColor(AXMemojiManager.getMemojiViewTheme().getSelectionColor());
            selection.setVisibility(View.GONE);

            AppCompatImageView icon = new AppCompatImageView(viewGroup.getContext());
            layout.addView(icon, new AXEmojiLayout.LayoutParams(Utils.dpToPx(viewGroup.getContext(), 9), Utils.dpToPx(viewGroup.getContext(), 6), iconSize, iconSize));
            layout.setLayoutParams(new ViewGroup.LayoutParams(Utils.dpToPx(viewGroup.getContext(), 42), Utils.dpToPx(viewGroup.getContext(), 36)));

            return new RecyclerView.ViewHolder(layout) {
            };
        } else {
            int iconSize = Utils.dpToPx(viewGroup.getContext(), 36);
            AXEmojiLayout layout = new AXEmojiLayout(viewGroup.getContext());

            View selection = new View(viewGroup.getContext());
            layout.addView(selection, new AXEmojiLayout.LayoutParams(0, 0, iconSize, iconSize));
            selection.setBackground(getSelectionDrawable(viewGroup.getContext()));
            selection.setVisibility(View.GONE);

            iconSize = Utils.dpToPx(viewGroup.getContext(), 30);
            AppCompatImageView icon = new AppCompatImageView(viewGroup.getContext());
            layout.addView(icon, new AXEmojiLayout.LayoutParams(Utils.dpToPx(viewGroup.getContext(), 3), Utils.dpToPx(viewGroup.getContext(), 3), iconSize, iconSize));
            layout.setLayoutParams(new ViewGroup.LayoutParams(Utils.dpToPx(viewGroup.getContext(), 42), Utils.dpToPx(viewGroup.getContext(), 36)));

            return new RecyclerView.ViewHolder(layout) {
            };
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        boolean selected = pager.getPageIndex() == i;
        AXEmojiLayout layout = (AXEmojiLayout) viewHolder.itemView;
        View icon = layout.getChildAt(1);
        View selection = layout.getChildAt(0);

        if (selected) selection.setVisibility(View.VISIBLE);
        else selection.setVisibility(View.GONE);

        if (recent && i == 0) {
            Drawable dr = AppCompatResources.getDrawable(layout.getContext(), com.aghajari.emojiview.R.drawable.emoji_recent);
            if (selected) {
                DrawableCompat.setTint(DrawableCompat.wrap(dr), AXMemojiManager.getMemojiViewTheme().getSelectedColor());
            } else {
                DrawableCompat.setTint(DrawableCompat.wrap(dr), AXMemojiManager.getMemojiViewTheme().getDefaultColor());
            }
            ((AppCompatImageView) icon).setImageDrawable(dr);
        } else {
            int i2 = i;
            if (recent) i2--;
            AXMemojiManager.getMemojiProvider().getLoader().onLoadMemojiCategory(((AppCompatImageView) icon),
                    MemojiSaver.savedCharacters.get(i2).getCategoryMemoji()
                    , selected);
        }

        //Utils.setClickEffect(icon, true);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pager.setPageIndex(i);
            }
        };
        icon.setOnClickListener(listener);
        layout.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        if (recent) return MemojiSaver.savedCharacters.size() + 1;
        return MemojiSaver.savedCharacters.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (recent && position == 0) return 10;
        return -1;
    }

    public GradientDrawable getSelectionDrawable(Context context) {
        GradientDrawable cd = new GradientDrawable();
        cd.setColor(AXMemojiManager.getMemojiViewTheme().getSelectionColor());
        cd.setCornerRadius(Utils.dpToPx(context, 8));
        return cd;
    }
}
