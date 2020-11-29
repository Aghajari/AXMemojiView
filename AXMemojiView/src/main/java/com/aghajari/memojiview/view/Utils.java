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

import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;

import com.aghajari.emojiview.view.AXEmojiLayout;
import com.aghajari.memojiview.R;

public class Utils {

    public static int getGridCount(Context context) {
        int w = context.getResources().getDisplayMetrics().widthPixels;
        int c_w = getColumnWidth(context);
        return w / c_w;
    }

    public static int getColumnWidth(Context context) {
        return (int) context.getResources().getDimension(R.dimen.memoji_grid_view_column_width);
    }

    static ValueAnimator animator(final View view, int duration, int from, int to) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(from, to);
        valueAnimator.setDuration(duration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ((AXEmojiLayout.LayoutParams) view.getLayoutParams()).left = (int) valueAnimator.getAnimatedValue();
                view.requestLayout();
            }
        });
        return valueAnimator;
    }
}
