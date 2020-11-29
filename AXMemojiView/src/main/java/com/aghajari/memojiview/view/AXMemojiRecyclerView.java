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
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


class AXMemojiRecyclerView extends RecyclerView {
    FindVariantListener variantListener;

    public AXMemojiRecyclerView(@NonNull Context context, FindVariantListener variantListener) {
        super(context);
        this.variantListener = variantListener;
        GridLayoutManager lm = new GridLayoutManager(context, Utils.getGridCount(context));
        this.setLayoutManager(lm);

        setOverScrollMode(OVER_SCROLL_NEVER);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (variantListener.findVariant() != null && variantListener.findVariant().onTouch(event, this))
            return true;
        return super.dispatchTouchEvent(event);
    }
}
