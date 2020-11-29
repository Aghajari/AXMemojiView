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


package com.aghajari.memojiview.shared;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.aghajari.memojiview.AXMemojiManager;
import com.aghajari.memojiview.memoji.Memoji;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public final class VariantMemojiManager implements VariantMemoji {
    private static final String PREFERENCE_NAME = "variant-memoji-manager";
    private static final String MEMOJI_DELIMITER = "~";
    private static final String VARIANT_MEMOJIS = "variant-memojis";
    static final int MEMOJI_GUESS_SIZE = 5;

    @NonNull
    private final Context context;
    @NonNull
    private List<Memoji> variantsList = new ArrayList<>(0);

    public VariantMemojiManager(@NonNull final Context context) {
        this.context = context.getApplicationContext();
        initFromSharedPreferences();
    }

    @NonNull
    @Override
    public Memoji getVariant(final Memoji desiredEmoji) {
        if (variantsList.isEmpty()) {
            initFromSharedPreferences();
        }

        final Memoji baseMemoji = desiredEmoji.getBase();

        for (int i = 0; i < variantsList.size(); i++) {
            final Memoji memoji = variantsList.get(i);

            if (baseMemoji.equals(memoji.getBase())) {
                return memoji;
            }
        }

        return desiredEmoji;
    }

    @Override
    public void addVariant(@NonNull final Memoji newVariant) {
        final Memoji newVariantBase = newVariant.getBase();

        for (int i = 0; i < variantsList.size(); i++) {
            final Memoji variant = variantsList.get(i);

            if (variant.getBase().equals(newVariantBase)) {
                if (variant.equals(newVariant)) {
                    return; // Same skin-tone was used.
                } else {
                    variantsList.remove(i);
                    variantsList.add(newVariant);

                    return;
                }
            }
        }

        variantsList.add(newVariant);
    }

    @Override
    public void persist() {
        if (variantsList.size() > 0) {
            final StringBuilder stringBuilder = new StringBuilder(variantsList.size() * MEMOJI_GUESS_SIZE);

            for (int i = 0; i < variantsList.size(); i++) {
                stringBuilder.append(variantsList.get(i).toString()).append(MEMOJI_DELIMITER);
            }

            stringBuilder.setLength(stringBuilder.length() - MEMOJI_DELIMITER.length());

            getPreferences().edit().putString(VARIANT_MEMOJIS, stringBuilder.toString()).apply();
        } else {
            getPreferences().edit().remove(VARIANT_MEMOJIS).apply();
        }
    }

    public void initFromSharedPreferences() {
        final String savedRecentVariants = getPreferences().getString(VARIANT_MEMOJIS, "");
        System.out.println(savedRecentVariants + "hi");
        if (savedRecentVariants.length() > 0) {
            final StringTokenizer stringTokenizer = new StringTokenizer(savedRecentVariants, MEMOJI_DELIMITER);
            variantsList = new ArrayList<>(stringTokenizer.countTokens());

            while (stringTokenizer.hasMoreTokens()) {
                final String token = stringTokenizer.nextToken();
                final Memoji memoji = AXMemojiManager.findMemoji(token);
                System.out.println(token);
                if (memoji != null) {
                    variantsList.add(memoji);
                }
            }
        }
    }

    private SharedPreferences getPreferences() {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }
}
