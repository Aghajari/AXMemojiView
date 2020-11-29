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

import com.aghajari.memojiview.memoji.OnMemojiActions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

class AXMemojiMoreViewPagerAdapter extends PagerAdapter {
    OnMemojiActions events;
    RecyclerView.OnScrollListener scrollListener;
    public List<AXMemojiRecyclerView> recyclerViews;

    private Queue<View> destroyedItems = new LinkedList<>();
    AXMemojiView memojiView;

    public AXMemojiMoreViewPagerAdapter(OnMemojiActions events, RecyclerView.OnScrollListener scrollListener, AXMemojiView memojiView) {
        this.events = events;
        this.memojiView = memojiView;
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
            recycler = new AXMemojiRecyclerView(collection.getContext(), memojiView);
        collection.addView(recycler);

        try {
            recycler.setAdapter(new AXMemojiMoreRecyclerAdapter(events, ((AXCategoryMoreAdapter) memojiView.categoryViews.addIcons.getAdapter()).characterList.get(position)));
        } catch (Exception e) {
            e.printStackTrace();
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
        if (memojiView.categoryViews != null &&
                memojiView.categoryViews.addIcons != null &&
                memojiView.categoryViews.addIcons.getAdapter() != null)
            return memojiView.categoryViews.addIcons.getAdapter().getItemCount();
        return 0;
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
