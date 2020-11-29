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
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;

import com.aghajari.emojiview.utils.Utils;
import com.aghajari.memojiview.AXMemojiManager;
import com.aghajari.memojiview.memoji.Memoji;
import com.aghajari.memojiview.memoji.OnMemojiActions;
import com.aghajari.memojiview.R;

import java.util.List;

import static android.view.View.MeasureSpec.makeMeasureSpec;

class AXSimpleMemojiVariantPopup extends AXMemojiVariantPopup {
    private static final int MARGIN = 2;

    @NonNull
    final View rootView;
    @Nullable
    private PopupWindow popupWindow;

    @Nullable
    final OnMemojiActions listener;
    @Nullable
    AppCompatImageView rootImageView;

    public AXSimpleMemojiVariantPopup(@NonNull final View rootView, @Nullable final OnMemojiActions listener) {
        super(rootView, listener);
        this.rootView = rootView;
        this.listener = listener;
    }

    View content;
    boolean isShowing;

    public boolean isShowing() {
        return isShowing;
    }

    public void show(@NonNull final AppCompatImageView clickedImage, @NonNull final Memoji memoji, final boolean fromRecent) {
        dismiss();
        isShowing = true;
        rootImageView = clickedImage;

        content = initView(clickedImage.getContext(), memoji, clickedImage.getWidth() / 2, fromRecent);

        popupWindow = new PopupWindow(content, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                isShowing = false;
            }
        });
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
        popupWindow.setBackgroundDrawable(new BitmapDrawable(clickedImage.getContext().getResources(), (Bitmap) null));

        content.measure(makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        final Point location = Utils.locationOnScreen(clickedImage);
        final Point desiredLocation = new Point(
                location.x - content.getMeasuredWidth() / 2 + clickedImage.getWidth() / 2,
                location.y - content.getMeasuredHeight() + Utils.dpToPx(clickedImage.getContext(), 8)
        );

        popupWindow.showAtLocation(rootView, Gravity.NO_GRAVITY, desiredLocation.x, desiredLocation.y);
        rootImageView.getParent().requestDisallowInterceptTouchEvent(true);
        Utils.fixPopupLocation(popupWindow, desiredLocation);
    }

    public void dismiss() {
        isShowing = false;
        rootImageView = null;

        if (popupWindow != null) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    LinearLayout imageContainer;

    private View initView(@NonNull final Context context, @NonNull final Memoji memoji, final int width, final boolean fromRecent) {
        final View result = View.inflate(context, com.aghajari.emojiview.R.layout.emoji_skin_popup, null);
        imageContainer = (LinearLayout) result.findViewById(com.aghajari.emojiview.R.id.container);
        CardView cardView = (CardView) result.findViewById(com.aghajari.emojiview.R.id.cardview);
        cardView.setCardBackgroundColor(AXMemojiManager.getMemojiViewTheme().getVariantPopupBackgroundColor());

        final List<Memoji> variants = memoji.getBase().getVariants();
        variants.add(0, memoji.getBase());

        final LayoutInflater inflater = LayoutInflater.from(context);

        for (final Memoji variant : variants) {
            final AppCompatImageView memojiImage = (AppCompatImageView) inflater.inflate(R.layout.emoji_item, imageContainer, false);
            final ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) memojiImage.getLayoutParams();
            final int margin = Utils.dpToPx(context, MARGIN);

            // Use the same size for Emojis as in the picker.
            layoutParams.width = width;
            layoutParams.setMargins(margin, margin, margin, margin);
            AXMemojiManager.getMemojiProvider().getLoader().onLoadMemoji(memojiImage, variant);

            memojiImage.setPadding(0, 0, 0, 0);
            memojiImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    if (listener != null && rootImageView != null) {
                        if (rootImageView instanceof AXMemojiImageView) {
                            ((AXMemojiImageView) rootImageView).setMemoji(variant);
                        }
                        AXMemojiManager.getMemojiProvider().getLoader().onLoadMemoji(rootImageView, variant);
                        listener.onClick(rootImageView, variant, fromRecent, true);
                    }
                }
            });

            imageContainer.addView(memojiImage);
        }

        cardView.getLayoutParams().height = width + Utils.dp(context, 12);
        return result;
    }
}
