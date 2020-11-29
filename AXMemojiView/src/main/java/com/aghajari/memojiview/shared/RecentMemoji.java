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

import androidx.annotation.NonNull;

import com.aghajari.memojiview.memoji.Memoji;

import java.util.Collection;

/**
 * Interface for providing some custom implementation for recent memojis.
 */
public interface RecentMemoji {
    /**
     * Returns the recent memojis. Could be loaded from a database, shared preferences or just hard
     * coded.<br>
     * <p>
     * This method will be called more than one time hence it is recommended to hold a collection of
     * recent memojis.
     */
    @NonNull
    Collection<Memoji> getRecentMemojis();

    /**
     * Should add the memoji to the recent ones. After calling this method, {@link #getRecentMemojis()}
     * should return the memoji that was just added.
     */
    void addMemoji(@NonNull Memoji memoji);

    /**
     * Should persist all memojis.
     */
    void persist();

    /**
     * request to reload recent data
     */
    void reload();

    /**
     * Returns true if recent is empty
     */
    boolean isEmpty();
}
