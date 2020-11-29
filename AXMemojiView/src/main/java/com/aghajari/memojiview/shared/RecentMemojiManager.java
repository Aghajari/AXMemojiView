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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RecentMemojiManager implements RecentMemoji {
    static String PREFERENCE_NAME = "memoji-recent-manager";
    static String RECENT_MEMOJIS = "recent-saved-memojis";
    public static int MAX_RECENT = -1;

    private static HashMap<String, Integer> memojiUseHistory = new HashMap<>();
    private static ArrayList<Memoji> recentMemoji = new ArrayList<>();

    @NonNull
    private final Context context;

    @Override
    public boolean isEmpty() {
        return memojiUseHistory.isEmpty();
    }

    public RecentMemojiManager(@NonNull final Context context) {
        this.context = context.getApplicationContext();
        reload();
    }

    @Override
    public Collection<Memoji> getRecentMemojis() {
        return recentMemoji;
    }

    @Override
    public void reload() {
        loadRecentEmoji();
    }

    @Override
    public void addMemoji(@NonNull final Memoji memoji) {
        addRecentMemoji(memoji.getBase());
    }

    @Override
    public void persist() {
        saveRecentEmoji();
    }

    private SharedPreferences getPreferences() {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public void addRecentMemoji(Memoji memoji) {
        Integer count = memojiUseHistory.get(memoji.getBase().toString());
        if (count == null) {
            count = 0;
        }
        if (MAX_RECENT <= 0) MAX_RECENT = 48;
        if (count == 0 && memojiUseHistory.size() >= MAX_RECENT) {
            Memoji mMemoji = recentMemoji.get(recentMemoji.size() - 1);
            memojiUseHistory.remove(mMemoji.getBase().toString());
            recentMemoji.set(recentMemoji.size() - 1, memoji.getBase());
        } else {
            if (!memojiUseHistory.containsKey(memoji.getBase().toString()))
                recentMemoji.add(memoji.getBase());
        }
        memojiUseHistory.put(memoji.getBase().toString(), ++count);
    }

    public void sortEmoji() {
        recentMemoji.clear();
        for (HashMap.Entry<String, Integer> entry : memojiUseHistory.entrySet()) {
            Memoji memoji = AXMemojiManager.findMemoji(entry.getKey());
            if (memoji!=null) recentMemoji.add(memoji);
        }
        Collections.sort(recentMemoji, new Comparator<Memoji>() {
            @Override
            public int compare(Memoji lhs, Memoji rhs) {
                Integer count1 = memojiUseHistory.get(lhs.getBase().toString());
                Integer count2 = memojiUseHistory.get(rhs.getBase().toString());
                if (count1 == null) {
                    count1 = 0;
                }
                if (count2 == null) {
                    count2 = 0;
                }
                if (count1 > count2) {
                    return -1;
                } else if (count1 < count2) {
                    return 1;
                }
                return 0;
            }
        });
        if (MAX_RECENT <= 0) MAX_RECENT = 48;
        while (recentMemoji.size() > MAX_RECENT) {
            recentMemoji.remove(recentMemoji.size() - 1);
        }
    }

    public void saveRecentEmoji() {
        SharedPreferences preferences = this.getPreferences();
        StringBuilder stringBuilder = new StringBuilder();
        for (HashMap.Entry<String, Integer> entry : memojiUseHistory.entrySet()) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append("|");
            }
            stringBuilder.append(entry.getKey());
            stringBuilder.append("=");
            stringBuilder.append(entry.getValue());
        }
        preferences.edit().putString(RECENT_MEMOJIS, stringBuilder.toString()).commit();
    }

    public void clearRecentEmoji() {
        memojiUseHistory.clear();
        recentMemoji.clear();
        saveRecentEmoji();
    }

    public void loadRecentEmoji() {
        SharedPreferences preferences = getPreferences();

        String str;
        try {
            memojiUseHistory.clear();
            if (preferences.contains(RECENT_MEMOJIS)) {
                str = preferences.getString(RECENT_MEMOJIS, "");
                if (str != null && str.length() > 0) {
                    String[] args = str.split("\\|");
                    for (String arg : args) {
                        try {
                            String[] args2 = arg.split("=");
                            memojiUseHistory.put(args2[0], parseInt(args2[1]));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
            sortEmoji();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static Integer parseInt(CharSequence value) {
        if (value == null) {
            return 0;
        }
        int val = 0;
        try {
            Matcher matcher = Pattern.compile("[\\-0-9]+").matcher(value);
            if (matcher.find()) {
                String num = matcher.group(0);
                val = Integer.parseInt(num);
            }
        } catch (Exception ignore) {

        }
        return val;
    }
}
