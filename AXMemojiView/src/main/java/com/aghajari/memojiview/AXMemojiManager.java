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


package com.aghajari.memojiview;

import android.content.Context;

import androidx.annotation.NonNull;

import com.aghajari.memojiview.memoji.Memoji;
import com.aghajari.memojiview.memoji.MemojiCategory;
import com.aghajari.memojiview.memoji.MemojiCharacter;
import com.aghajari.memojiview.memoji.MemojiLoader;
import com.aghajari.memojiview.memoji.MemojiProvider;
import com.aghajari.memojiview.shared.MemojiSaver;
import com.aghajari.memojiview.shared.OnSavedMemojiCharactersChanged;
import com.aghajari.memojiview.shared.RecentMemoji;
import com.aghajari.memojiview.shared.RecentMemojiManager;
import com.aghajari.memojiview.shared.VariantMemoji;
import com.aghajari.memojiview.shared.VariantMemojiManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * AXMemojiView
 * @version : 1.0.0
 */
public class AXMemojiManager {

    private AXMemojiManager() {
    }

    static MemojiProvider provider = null;
    static RecentMemoji recentMemoji = null;
    static VariantMemoji variantMemoji = null;
    static AXMemojiTheme memojiTheme = null;
    static MemojiSaver memojiSaver = null;
    static HashMap<String, Memoji> memojis = new HashMap<>();
    static OnSavedMemojiCharactersChanged onSavedMemojiCharactersChanged;
    static boolean recentVariantEnabled = true;
    static boolean rippleEnabled = true;

    public static boolean isInstalled() {
        return provider != null;
    }

    public static void install(Context context, MemojiProvider provider) {
        if (provider == null || context == null) return;
        AXMemojiManager.provider = provider;
        memojis.clear();

        for (MemojiCategory category : provider.getCategories()) {
            for (MemojiCharacter character : category.getMemojiCharacters()) {
                for (Memoji memoji : character.getMemojis()) {
                    memojis.put(memoji.toString(), memoji);
                    if (memoji.getVariants() != null) {
                        for (Memoji v : memoji.getVariants()) {
                            memojis.put(v.toString(), v);
                        }
                    }
                }
            }
        }

        if (recentMemoji == null) recentMemoji = new RecentMemojiManager(context);
        if (variantMemoji == null) variantMemoji = new VariantMemojiManager(context);
        if (memojiTheme == null) memojiTheme = new AXMemojiTheme();
        if (memojiSaver == null) memojiSaver = new MemojiSaver(context);
    }

    public static void install(Context context, String provider_json_data, MemojiLoader loader) {
        install(context, readData(provider_json_data, loader));
    }

    private static MemojiProvider readData(String json, MemojiLoader loader) {
        try {
            JSONObject object = new JSONObject(json);

            List<MemojiCategory> categories = new ArrayList<>();
            Iterator<String> keys = object.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject category = object.getJSONObject(key);
                Iterator<String> keys2 = category.keys();

                List<MemojiCharacter> characters = new ArrayList<>();
                while (keys2.hasNext()) {
                    String character = keys2.next();
                    JSONArray memojis = category.getJSONArray(character);

                    List<Memoji> memojisList = new ArrayList<>();
                    for (int i = 0; i < memojis.length(); i++) {
                        JSONObject data = memojis.getJSONObject(i);
                        String emoji = data.getString("emoji");
                        String posture = data.getString("posture");
                        JSONArray images = data.getJSONArray("images");
                        if (images.length() == 0) continue;
                        JSONObject base = images.getJSONObject(0);
                        Memoji memoji = new Memoji(emoji, posture, key, character, base.getString("name"));
                        if (images.length() > 1) {
                            Memoji[] variants = new Memoji[images.length() - 1];
                            for (int i2 = 1; i2 < images.length(); i2++) {
                                JSONObject v = images.getJSONObject(i2);
                                variants[i2 - 1] = new Memoji(emoji, posture, key, character, v.getString("name"));
                            }
                            memoji.setVariants(variants);
                        }
                        memojisList.add(memoji);
                    }
                    characters.add(new JsonMemojiCharacter(character, memojisList));
                }
                categories.add(new JsonMemojiCategory(key, characters));
            }
            return new JsonMemojiProvider(loader, categories);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class JsonMemojiProvider implements MemojiProvider {
        MemojiLoader loader;
        List<MemojiCategory> categories;

        public JsonMemojiProvider(MemojiLoader loader, List<MemojiCategory> categories) {
            this.loader = loader;
            this.categories = categories;
        }

        @NonNull
        @Override
        public List<MemojiCategory> getCategories() {
            return categories;
        }

        @NonNull
        @Override
        public MemojiLoader getLoader() {
            return loader;
        }
    }

    private static class JsonMemojiCategory extends MemojiCategory {
        String name;
        List<MemojiCharacter> characters;

        public JsonMemojiCategory(String name, List<MemojiCharacter> characters) {
            this.name = name;
            this.characters = characters;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public List<MemojiCharacter> getMemojiCharacters() {
            return characters;
        }
    }

    private static class JsonMemojiCharacter extends MemojiCharacter {
        String name;
        List<Memoji> memojis;

        public JsonMemojiCharacter(String name, List<Memoji> memojis) {
            this.name = name;
            this.memojis = memojis;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public List<Memoji> getMemojis() {
            return memojis;
        }
    }

    public static MemojiProvider getMemojiProvider() {
        return provider;
    }

    public static HashMap<String, Memoji> getAllMemojis() {
        return memojis;
    }

    public static Memoji findMemoji(String key) {
        return memojis.get(key);
    }

    public static RecentMemoji getRecentMemoji() {
        return recentMemoji;
    }

    public static VariantMemoji getVariantMemoji() {
        return variantMemoji;
    }

    public static void setRecentMemoji(RecentMemoji recentMemoji) {
        AXMemojiManager.recentMemoji = recentMemoji;
    }

    public static void setVariantMemoji(VariantMemoji variantMemoji) {
        AXMemojiManager.variantMemoji = variantMemoji;
    }

    public static Memoji findSelectedVariantMemoji(Memoji memoji) {
        if (variantMemoji == null) return memoji;
        return variantMemoji.getVariant(memoji.getBase());
    }

    public static boolean isRecentVariantEnabled() {
        return recentVariantEnabled;
    }

    public static void setRecentVariantEnabled(boolean recentVariantEnabled) {
        AXMemojiManager.recentVariantEnabled = recentVariantEnabled;
    }

    public static boolean isRippleEnabled() {
        return rippleEnabled;
    }

    public static void setRippleEnabled(boolean rippleEnabled) {
        AXMemojiManager.rippleEnabled = rippleEnabled;
    }

    public static void setMemojiViewTheme(AXMemojiTheme memojiTheme) {
        AXMemojiManager.memojiTheme = memojiTheme;
    }

    public static AXMemojiTheme getMemojiViewTheme() {
        return memojiTheme;
    }

    public static MemojiSaver getMemojiSaver() {
        return memojiSaver;
    }

    public static void setMemojiSaver(MemojiSaver memojiSaver) {
        AXMemojiManager.memojiSaver = memojiSaver;
    }

    public static HashMap<String, Memoji> getMemojis() {
        return memojis;
    }

    public static void setOnSavedMemojiCharactersChanged(OnSavedMemojiCharactersChanged onSavedMemojiCharactersChanged) {
        AXMemojiManager.onSavedMemojiCharactersChanged = onSavedMemojiCharactersChanged;
    }

    public static OnSavedMemojiCharactersChanged getOnSavedMemojiCharactersChanged() {
        return onSavedMemojiCharactersChanged;
    }

    public static MemojiCharacter findMemojiCharacter (Memoji memoji){
        if (provider==null) return null;
        for (MemojiCategory category : provider.getCategories()){
            for (MemojiCharacter character : category.getMemojiCharacters()){
                if (character.getName().equals(memoji.getCharacter())) return character;
            }
        }
        return null;
    }

    public static Memoji getLastRecentMemoji(){
        ArrayList<Memoji> memojis = (ArrayList<Memoji>) recentMemoji.getRecentMemojis();
        if (memojis!=null && memojis.size()>0) return memojis.get(0);
        return null;
    }

    public static MemojiCharacter findPageMemojiCharacter(){
        try {
            Memoji memoji = getLastRecentMemoji();
            if (memoji != null) return findMemojiCharacter(memoji);
            return memojiSaver.getSavedCharacters().get(0);
        }catch (Exception e){
            return null;
        }
    }

}
