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


package com.aghajari.memojiview.memoji;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aghajari.emojiview.AXEmojiUtils;
import com.aghajari.emojiview.emoji.Emoji;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

public class Memoji implements Serializable {
    private static final long serialVersionUID = 3L;
    private static final List<Memoji> EMPTY_EMOJI_LIST = emptyList();

    @NonNull
    private final String emoji, posture, category, character, image;

    @NonNull
    private List<Memoji> variants;

    @Nullable
    private Memoji base;

    public Memoji(@NonNull String emoji, @NonNull String posture, @NonNull String category, @NonNull String character, @NonNull String image) {
        this.emoji = emoji;
        this.posture = posture;
        this.category = category;
        this.character = character;
        this.image = image;
    }

    @NonNull
    public String getCategory() {
        return category;
    }

    @NonNull
    public String getEmoji() {
        return emoji;
    }

    @Nullable
    public Emoji getLoadedEmoji() {
        return AXEmojiUtils.getEmojis(emoji).get(0).emoji;
    }

    @NonNull
    public String getImage() {
        return image;
    }

    @NonNull
    public String getPosture() {
        return posture;
    }

    @NonNull
    public String getCharacter() {
        return character;
    }

    @Override
    public int hashCode() {
        int result = emoji.hashCode();
        result = 31 * result + posture.hashCode();
        result = 31 * result + category.hashCode();
        result = 31 * result + character.hashCode();
        result = 31 * result + image.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return category + "\\" + character + "\\" + image;
    }


    public void setVariants(Memoji[] variants) {
        this.variants = variants.length == 0 ? EMPTY_EMOJI_LIST : asList(variants);
        for (final Memoji variant : variants) {
            variant.base = this;
        }
    }

    /**
     * @return other variants of this memoji
     */
    @NonNull
    public List<Memoji> getVariants() {
        return new ArrayList<>(variants);
    }


    /**
     * @return the base of memoji, or this instance if it doesn't have any other base
     */
    @NonNull
    public Memoji getBase() {
        Memoji result = this;

        while (result.base != null) {
            result = result.base;
        }

        return result;
    }

    public boolean hasVariants() {
        return !variants.isEmpty();
    }


    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Memoji memoji = (Memoji) o;
        return emoji.equals(memoji.emoji) && category.equals(memoji.category) && character.equals(memoji.character)
                && posture.equals(memoji.posture) && image.equals(memoji.image);
    }
}
