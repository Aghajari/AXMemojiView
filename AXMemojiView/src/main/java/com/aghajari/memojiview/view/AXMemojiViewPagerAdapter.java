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

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.aghajari.memojiview.AXMemojiManager;
import com.aghajari.memojiview.memoji.OnMemojiActions;
import com.aghajari.memojiview.shared.MemojiSaver;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

class AXMemojiViewPagerAdapter extends PagerAdapter {
    OnMemojiActions events;
    RecyclerView.OnScrollListener scrollListener;
    public List<AXMemojiRecyclerView> recyclerViews;
    public int add = 0;

    private Queue<View> destroyedItems = new LinkedList<>();
    FindVariantListener findVariantListener;

    public AXMemojiViewPagerAdapter(OnMemojiActions events, RecyclerView.OnScrollListener scrollListener, FindVariantListener listener) {
        this.events = events;
        this.findVariantListener = listener;
        this.scrollListener = scrollListener;
        recyclerViews = new ArrayList<AXMemojiRecyclerView>();
    }

    public RecyclerView.ItemDecoration itemDecoration = null;

    public Object instantiateItem(ViewGroup collection, int position) {
        AXMemojiRecyclerView recycler = null;
        try {
            recycler = (AXMemojiRecyclerView) destroyedItems.poll();
        } catch (Exception e) {
            recycler = null;
        }

        if (recycler == null)
            recycler = new AXMemojiRecyclerView(collection.getContext(), findVariantListener);
        collection.addView(recycler);

        if (position == 0 && add == 1) {
            recycler.setAdapter(new AXRecentMemojiRecyclerAdapter(events));
        } else {
            recycler.setAdapter(new AXMemojiRecyclerAdapter(events, MemojiSaver.savedCharacters.get(position - add)));
        }

        recyclerViews.add(recycler);
        if (itemDecoration != null) {
            recycler.removeItemDecoration(itemDecoration);
            recycler.addItemDecoration(itemDecoration);
        }
        if (scrollListener != null) {
            recycler.removeOnScrollListener(scrollListener);
            recycler.addOnScrollListener(scrollListener);
        }
        return recycler;
    }

    @Override
    public int getCount() {
        if (!AXMemojiManager.getRecentMemoji().isEmpty()) {
            add = 1;
        } else {
            add = 0;
        }
        return MemojiSaver.savedCharacters.size() + add;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        recyclerViews.remove(object);
        destroyedItems.add((View) object);
    }
}
