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

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aghajari.emojiview.AXEmojiUtils;
import com.aghajari.emojiview.view.AXEmojiTextView;
import com.aghajari.memojiview.AXMemojiManager;
import com.aghajari.memojiview.memoji.Memoji;
import com.aghajari.memojiview.memoji.OnMemojiActions;


class AXRecentMemojiRecyclerAdapter extends RecyclerView.Adapter<AXRecentMemojiRecyclerAdapter.ViewHolder> {

    OnMemojiActions events;

    public AXRecentMemojiRecyclerAdapter(OnMemojiActions events) {
        this.events = events;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        FrameLayout frameLayout = new FrameLayout(viewGroup.getContext());
        AXMemojiImageView memojiView = new AXMemojiImageView(viewGroup.getContext());
        int cw = Utils.getColumnWidth(viewGroup.getContext());
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(cw, cw));
        frameLayout.addView(memojiView);

        AXEmojiTextView emojiTextView = new AXEmojiTextView(viewGroup.getContext());
        int emojiSize = com.aghajari.emojiview.utils.Utils.dpToPx(viewGroup.getContext(), 20);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(emojiSize, emojiSize);
        lp.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        lp.bottomMargin = com.aghajari.emojiview.utils.Utils.dp(viewGroup.getContext(), 6);
        lp.rightMargin = lp.bottomMargin;
        frameLayout.addView(emojiTextView, lp);
        emojiTextView.setTextSize(com.aghajari.emojiview.utils.Utils.dpToPx(viewGroup.getContext(), 18));
        emojiTextView.setEmojiSize(emojiSize);

        return new ViewHolder(frameLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        FrameLayout frameLayout = (FrameLayout) viewHolder.itemView;
        final AXMemojiImageView memojiView = (AXMemojiImageView) frameLayout.getChildAt(0);
        final AXEmojiTextView emojiView = (AXEmojiTextView) frameLayout.getChildAt(1);

        final Memoji memoji = AXMemojiManager.getVariantMemoji().getVariant((Memoji) AXMemojiManager.getRecentMemoji().getRecentMemojis().toArray()[i]);

        if (AXMemojiManager.getMemojiViewTheme().isEmojiEnabled()) {
            emojiView.setText(AXEmojiUtils.replaceWithEmojis(emojiView.getContext(), memoji.getEmoji(), emojiView.getEmojiSize()));
            emojiView.setVisibility(View.VISIBLE);
        } else {
            emojiView.setVisibility(View.GONE);
        }

        memojiView.setMemoji(memoji);
        memojiView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                events.onClick(memojiView, memojiView.getMemoji(), false, false);
            }
        });

        memojiView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return events.onLongClick(memojiView, memojiView.getMemoji(), false, false);
            }
        });

        AXMemojiManager.getMemojiProvider().getLoader().onLoadMemoji(memojiView, memoji);
        if (AXMemojiManager.isRippleEnabled())
            com.aghajari.emojiview.utils.Utils.setClickEffect(memojiView, false);
    }

    @Override
    public int getItemCount() {
        return AXMemojiManager.getRecentMemoji().getRecentMemojis().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
