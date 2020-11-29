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

import com.aghajari.memojiview.AXMemojiManager;
import com.aghajari.memojiview.memoji.MemojiCategory;
import com.aghajari.memojiview.memoji.MemojiCharacter;

import java.util.ArrayList;
import java.util.List;

public class MemojiSaver {
    private Context context;
    static String PREFERENCE_NAME = "memoji-data-manager";
    public static List<MemojiCharacter> savedCharacters = new ArrayList<>();

    public MemojiSaver(Context context) {
        this.context = context;
        if (savedCharacters == null || savedCharacters.isEmpty()) getSavedCharacters();
    }

    public void saveCharacter(MemojiCharacter character) {
        SharedPreferences preferences = getPreferences();
        String res = preferences.getString("memoji_characters", "");
        if (res == null || res.length() == 0) {
            res = character.getName().replace("|", "") + "|";
        } else {
            res = res + "|" + character.getName().replace("|", "");
        }
        preferences.edit().putString("memoji_characters", res).commit();
        if (savedCharacters != null) savedCharacters.add(character);
        else getSavedCharacters();

        if (AXMemojiManager.getOnSavedMemojiCharactersChanged() != null)
            AXMemojiManager.getOnSavedMemojiCharactersChanged().onChanged(character, true);
    }

    public void deleteCharacter(MemojiCharacter character) {
        if (savedCharacters.contains(character)) {
            savedCharacters.remove(character);
            String res = "";
            if (savedCharacters.size() > 0) {
                for (MemojiCharacter character_n : savedCharacters) {
                    res = res + character_n.getName().replace("|", "") + "|";
                }
            }
            getPreferences().edit().putString("memoji_characters", res).commit();
        }

        if (AXMemojiManager.getOnSavedMemojiCharactersChanged() != null)
            AXMemojiManager.getOnSavedMemojiCharactersChanged().onChanged(character, false);

        if (savedCharacters.isEmpty() && AXMemojiManager.getRecentMemoji().isEmpty()) {
            savedCharacters.add(AXMemojiManager.getMemojiProvider().getCategories().get(0).getMemojiCharacters().get(0));
        }
    }

    public void clear() {
        getPreferences().edit().remove("memoji_characters").commit();
    }

    public List<MemojiCharacter> getSavedCharacters() {
        SharedPreferences preferences = getPreferences();
        String str = preferences.getString("memoji_characters", "");
        if (str != null && str.length() > 0) {
            ArrayList<MemojiCharacter> characters = new ArrayList<>();
            String[] args = str.split("\\|");
            for (String arg : args) {
                for (MemojiCategory category : AXMemojiManager.getMemojiProvider().getCategories()) {
                    for (MemojiCharacter character : category.getMemojiCharacters()) {
                        if (character.getName().replace("|", "").equals(arg)) {
                            if (!characters.contains(character)) characters.add(character);
                        }
                    }
                }
            }
            savedCharacters = characters;
        } else {
            savedCharacters = new ArrayList<>();
        }

        if (savedCharacters.isEmpty() && AXMemojiManager.getRecentMemoji().isEmpty()) {
            savedCharacters.add(AXMemojiManager.getMemojiProvider().getCategories().get(0).getMemojiCharacters().get(0));
        }
        return savedCharacters;
    }

    private SharedPreferences getPreferences() {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }
}
