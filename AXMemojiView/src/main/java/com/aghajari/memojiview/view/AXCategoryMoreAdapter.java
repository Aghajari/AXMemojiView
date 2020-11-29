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
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.aghajari.emojiview.utils.Utils;
import com.aghajari.emojiview.view.AXEmojiLayout;
import com.aghajari.memojiview.AXMemojiManager;
import com.aghajari.memojiview.memoji.MemojiCategory;
import com.aghajari.memojiview.memoji.MemojiCharacter;

import java.util.ArrayList;
import java.util.List;


class AXCategoryMoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    AXEmojiLayout pager;
    List<MemojiCharacter> characterList;
    int selected = 0;

    public MemojiCharacter getSelectedCharacter() {
        return characterList.get(selected);
    }

    public void refresh() {
        selected = 0;
        characterList.clear();
        for (MemojiCategory category : AXMemojiManager.getMemojiProvider().getCategories()) {
            characterList.addAll(category.getMemojiCharacters());
        }
        notifyDataSetChanged();
    }

    public AXCategoryMoreAdapter(AXEmojiLayout pager) {
        characterList = new ArrayList<>();
        this.pager = pager;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

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

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        boolean isSelected = this.selected == i;
        AXEmojiLayout layout = (AXEmojiLayout) viewHolder.itemView;
        View icon = layout.getChildAt(1);
        View selection = layout.getChildAt(0);

        if (isSelected) selection.setVisibility(View.VISIBLE);
        else selection.setVisibility(View.GONE);

        AXMemojiManager.getMemojiProvider().getLoader().onLoadMemojiCategory(((AppCompatImageView) icon),
                characterList.get(i).getCategoryMemoji()
                , isSelected);

        //Utils.setClickEffect(icon, true);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selected == i) return;
                notifyItemChanged(selected);
                selected = i;
                notifyItemChanged(selected);
                ((AXMemojiView) pager).setMoreCategoryIndex(i);
            }
        };
        layout.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return characterList.size();
    }

    public GradientDrawable getSelectionDrawable(Context context) {
        GradientDrawable cd = new GradientDrawable();
        cd.setColor(AXMemojiManager.getMemojiViewTheme().getSelectionColor());
        cd.setCornerRadius(Utils.dpToPx(context, 8));
        return cd;
    }
}
