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

import androidx.appcompat.widget.AppCompatImageView;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.EditText;
import android.widget.FrameLayout;

import com.aghajari.emojiview.utils.Utils;
import com.aghajari.emojiview.view.AXEmojiLayout;
import com.aghajari.memojiview.AXMemojiManager;
import com.aghajari.memojiview.memoji.Memoji;
import com.aghajari.memojiview.memoji.OnMemojiActions;

public class AXMemojiView extends AXEmojiLayout implements FindVariantListener {
    public AXMemojiView(Context context) {
        super(context);
        init();
    }

    public AXMemojiView(Context context, OnMemojiActions ev) {
        super(context);
        this.events = ev;
        init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (vp!=null) vp.getLayoutParams().width = w;
        if (add_vp!=null) add_vp.getLayoutParams().width = w;
        if (add_vp!=null) ((FrameLayout.LayoutParams)add_vp.getLayoutParams()).leftMargin = w;
    }

    AXCategoryRecycler categoryViews;
    ViewPager vp, add_vp;
    AXMemojiVariantPopup variantPopup;

    OnMemojiActions events = new OnMemojiActions() {
        @Override
        public void onClick(AppCompatImageView view, Memoji memoji, boolean fromRecent, boolean fromVariant) {
            if (!fromVariant && variantPopup != null && variantPopup.isShowing()) return;
            if (!fromVariant) AXMemojiManager.getRecentMemoji().addMemoji(memoji);

            if (fromVariant) AXMemojiManager.getVariantMemoji().addVariant(memoji);
            if (variantPopup != null) variantPopup.dismiss();

            if (!fromRecent && ((AXMemojiViewPagerAdapter) vp.getAdapter()).add == 1)
                ((AXMemojiViewPagerAdapter) vp.getAdapter()).recyclerViews.get(0).getAdapter().notifyDataSetChanged();

            if (emojiActions != null) emojiActions.onClick(view, memoji, fromRecent, fromVariant);
        }

        @Override
        public boolean onLongClick(AppCompatImageView view, Memoji memoji, boolean fromRecent, boolean fromVariant) {

            if (variantPopup != null && (!fromRecent || AXMemojiManager.isRecentVariantEnabled())) {
                if (memoji.getBase().hasVariants()) variantPopup.show(view, memoji, fromRecent);
            }

            if (emojiActions != null)
                return emojiActions.onLongClick(view, memoji, fromRecent, fromVariant);
            return false;
        }
    };

    OnMemojiActions emojiActions = null;

    /**
     * add memoji click and longClick listener
     *
     * @param listener
     */
    public void setOnMemojiActionsListener(OnMemojiActions listener) {
        emojiActions = listener;
    }


    RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {

        private boolean isShowing = true;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (recyclerView == null) {
                if (!AXMemojiManager.getMemojiViewTheme().isAlwaysShowDividerEnabled()) {
                    if (!isShowing) {
                        isShowing = true;
                        if (categoryViews != null) categoryViews.Divider.setVisibility(GONE);
                    }
                }
                return;
            }
            if (dy == 0) return;
            if (dy == 1) dy = 0;
            super.onScrolled(recyclerView, dx, dy);
            if (recyclerView != null && scrollListener2 != null)
                scrollListener2.onScrolled(recyclerView, dx, dy);

            if (!AXMemojiManager.getMemojiViewTheme().isAlwaysShowDividerEnabled()) {
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int firstVisibleItemPosition = ((GridLayoutManager) layoutManager).findFirstCompletelyVisibleItemPosition();
                int visibleItemCount = layoutManager.getChildCount();
                if ((visibleItemCount > 0 && (firstVisibleItemPosition) == 0)) {
                    if (!isShowing) {
                        isShowing = true;
                        if (categoryViews != null) categoryViews.Divider.setVisibility(GONE);
                    }
                } else {
                    if (isShowing) {
                        isShowing = false;
                        if (categoryViews != null) categoryViews.Divider.setVisibility(VISIBLE);
                    }
                }
            }
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (scrollListener2 != null)
                scrollListener2.onScrollStateChanged(recyclerView, newState);
        }
    };

    RecyclerView.OnScrollListener scrollListener2 = null;

    private void init() {
        int top = 0;
        if (AXMemojiManager.getMemojiViewTheme().isCategoryEnabled())
            top = Utils.dpToPx(getContext(), 49);

        final int w = getContext().getResources().getDisplayMetrics().widthPixels;

        vp = new ViewPager(getContext());
        this.addView(vp, new AXEmojiLayout.LayoutParams(0, top, w, -1));
        vp.setAdapter(new AXMemojiViewPagerAdapter(events, scrollListener, this));
        //vp.setPadding(0, 0, 0, top);

        add_vp = new ViewPager(getContext());
        this.addView(add_vp, new AXEmojiLayout.LayoutParams(w, top, w, -1));
        add_vp.setAdapter(new AXMemojiMoreViewPagerAdapter(events, scrollListener, this));
        //add_vp.setPadding(0, 0, 0, top);

        if (AXMemojiManager.getMemojiViewTheme().isCategoryEnabled()) {
            categoryViews = new AXCategoryRecycler(getContext(), this);
            this.addView(categoryViews, new AXEmojiLayout.LayoutParams(0, 0, -1, top));
        } else {
            categoryViews = null;
        }

        this.setBackgroundColor(AXMemojiManager.getMemojiViewTheme().getBackgroundColor());
        add_vp.setBackgroundColor(AXMemojiManager.getMemojiViewTheme().getBackgroundColor());
        if (categoryViews != null)
            categoryViews.setBackgroundColor(AXMemojiManager.getMemojiViewTheme().getBackgroundColor());

        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                if (pagerListener2 != null) pagerListener2.onPageScrolled(i, v, i1);
            }

            @Override
            public void onPageSelected(int i) {
                vp.setCurrentItem(i, true);
                if (((AXMemojiViewPagerAdapter) vp.getAdapter()).recyclerViews.size() > i) {
                    scrollListener.onScrolled(((AXMemojiViewPagerAdapter) vp.getAdapter()).recyclerViews.get(i), 0, 1);
                } else {
                    scrollListener.onScrolled(null, 0, 1);
                }
                if (categoryViews != null) categoryViews.setPageIndex(i);
                if (pagerListener2 != null) pagerListener2.onPageSelected(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                if (pagerListener2 != null) pagerListener2.onPageScrollStateChanged(i);
            }
        });

        add_vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                if (pagerListener2 != null) pagerListener2.onPageScrolled(i, v, i1);
            }

            @Override
            public void onPageSelected(int i) {
                if (((AXMemojiMoreViewPagerAdapter) add_vp.getAdapter()).recyclerViews.size() > i) {
                    scrollListener.onScrolled(((AXMemojiMoreViewPagerAdapter) add_vp.getAdapter()).recyclerViews.get(i), 0, 1);
                } else {
                    scrollListener.onScrolled(null, 0, 1);
                }
                if (categoryViews != null) categoryViews.setAddPageIndex(i);
                if (categoryViews != null) categoryViews.updateSaveSelection(i);
                if (pagerListener2 != null) pagerListener2.onPageSelected(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                if (pagerListener2 != null) pagerListener2.onPageScrollStateChanged(i);
            }
        });

    }

    ViewPager.OnPageChangeListener pagerListener2 = null;

    @Override
    public void setPageIndex(int index) {
        vp.setCurrentItem(index, true);
        if (!AXMemojiManager.getMemojiViewTheme().isAlwaysShowDividerEnabled()) {
            if (((AXMemojiViewPagerAdapter) vp.getAdapter()).recyclerViews.size() > index) {
                scrollListener.onScrolled(((AXMemojiViewPagerAdapter) vp.getAdapter()).recyclerViews.get(index), 0, 1);
            } else {
                scrollListener.onScrolled(null, 0, 1);
            }
        }
        if (categoryViews != null) categoryViews.setPageIndex(index);
    }

    @Override
    public void dismiss() {
        if (variantPopup != null) variantPopup.dismiss();
        AXMemojiManager.getRecentMemoji().persist();
        AXMemojiManager.getVariantMemoji().persist();
    }

    @Override
    public void setEditText(EditText editText) {
        super.setEditText(editText);
        variantPopup = new AXSimpleMemojiVariantPopup(editText.getRootView(), events);
    }

    @Override
    protected void setScrollListener(RecyclerView.OnScrollListener listener) {
        super.setScrollListener(listener);
        scrollListener2 = listener;
    }

    @Override
    protected void setPageChanged(ViewPager.OnPageChangeListener listener) {
        super.setPageChanged(listener);
        pagerListener2 = listener;
    }

    @Override
    protected void addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        ((AXMemojiViewPagerAdapter) vp.getAdapter()).itemDecoration = itemDecoration;
        ((AXMemojiMoreViewPagerAdapter) add_vp.getAdapter()).itemDecoration = itemDecoration;
        for (int i = 0; i < ((AXMemojiViewPagerAdapter) vp.getAdapter()).recyclerViews.size(); i++) {
            ((AXMemojiViewPagerAdapter) vp.getAdapter()).recyclerViews.get(i).addItemDecoration(itemDecoration);
        }

    }

    @Override
    protected void refresh() {
        super.refresh();
        if (categoryViews != null) {
            categoryViews.removeAllViews();
            categoryViews.clear();
            categoryViews.init();
        }
        add_vp.getAdapter().notifyDataSetChanged();
        vp.getAdapter().notifyDataSetChanged();
        vp.setCurrentItem(0, false);
        if (!AXMemojiManager.getMemojiViewTheme().isAlwaysShowDividerEnabled())
            scrollListener.onScrolled(null, 0, 1);
        if (categoryViews != null) categoryViews.setPageIndex(0);
    }

    @Override
    public int getPageIndex() {
        return vp.getCurrentItem();
    }

    public ViewPager getViewPager() {
        return vp;
    }

    public ViewPager getMoreViewPager() {
        return add_vp;
    }

    @Override
    public AXMemojiVariantPopup findVariant() {
        return variantPopup;
    }

    public void showAddPage(int duration) {
        categoryViews.showAddPage(duration);
    }

    void showAddPageNow(int duration) {
        add_vp.getAdapter().notifyDataSetChanged();
        add_vp.setCurrentItem(0, false);
        final int w = getContext().getResources().getDisplayMetrics().widthPixels;

        com.aghajari.memojiview.view.Utils.animator(add_vp, duration, w, 0).start();
        com.aghajari.memojiview.view.Utils.animator(vp, duration, 0, -w).start();
    }

    public void hideAddPage(int duration) {
        categoryViews.hideAddPage(duration);
    }

    void hideAddPageNow(int duration) {
        final int w = getContext().getResources().getDisplayMetrics().widthPixels;
        com.aghajari.memojiview.view.Utils.animator(add_vp, duration, 0, w).start();
        com.aghajari.memojiview.view.Utils.animator(vp, duration, -w, 0).start();

        vp.getAdapter().notifyDataSetChanged();
        //vp.setCurrentItem(0, false);
        if (!AXMemojiManager.getMemojiViewTheme().isAlwaysShowDividerEnabled())
            scrollListener.onScrolled(null, 0, 1);
        if (categoryViews != null) categoryViews.setPageIndex(0);
    }

    void setMoreCategoryIndex(int index) {
        add_vp.setCurrentItem(index, true);
        if (!AXMemojiManager.getMemojiViewTheme().isAlwaysShowDividerEnabled()) {
            if (((AXMemojiMoreViewPagerAdapter) add_vp.getAdapter()).recyclerViews.size() > index) {
                scrollListener.onScrolled(((AXMemojiMoreViewPagerAdapter) add_vp.getAdapter()).recyclerViews.get(index), 0, 1);
            } else {
                scrollListener.onScrolled(null, 0, 1);
            }
        }
        if (categoryViews != null) categoryViews.setAddPageIndex(index);
        if (categoryViews != null) categoryViews.updateSaveSelection(index);
    }

    public boolean isShowingAddPage() {
        return categoryViews.isShowingAddPage;
    }
}
