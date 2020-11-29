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

import android.graphics.Color;

import com.aghajari.emojiview.AXEmojiTheme;

public class AXMemojiTheme extends AXEmojiTheme {

    public AXMemojiTheme() {
        this.setSelectionColor(Color.LTGRAY);
        this.setAddBackgroundColor(Color.rgb(218, 233, 251));
        this.setAddColor(Color.rgb(48, 99, 215));
        this.setSelectedColor(getAddColor());
        this.setSaveBackgroundColor(getAddBackgroundColor());
        this.setSaveColor(getAddColor());
        this.setSaveIcon(R.drawable.add);
        this.setDeleteBackgroundColor(Color.rgb(234, 213, 213));
        this.setDeleteColor(Color.rgb(215, 35, 57));
        this.setDeleteIcon(R.drawable.remove);
    }

    private int addBackgroundColor;
    private int addColor;
    private int saveBackgroundColor;
    private int saveColor;
    private int saveIcon;
    private int deleteBackgroundColor;
    private int deleteColor;
    private int deleteIcon;
    private boolean emojiVisible = true;

    public int getAddBackgroundColor() {
        return addBackgroundColor;
    }

    public void setAddBackgroundColor(int addBackgroundColor) {
        this.addBackgroundColor = addBackgroundColor;
    }

    public int getAddColor() {
        return addColor;
    }

    public void setAddColor(int addColor) {
        this.addColor = addColor;
    }

    public int getSaveBackgroundColor() {
        return saveBackgroundColor;
    }

    public void setSaveBackgroundColor(int saveBackgroundColor) {
        this.saveBackgroundColor = saveBackgroundColor;
    }

    public int getSaveColor() {
        return saveColor;
    }

    public void setSaveColor(int saveColor) {
        this.saveColor = saveColor;
    }

    public int getSaveIcon() {
        return saveIcon;
    }

    public void setSaveIcon(int saveIcon) {
        this.saveIcon = saveIcon;
    }

    public int getDeleteBackgroundColor() {
        return deleteBackgroundColor;
    }

    public void setDeleteBackgroundColor(int deleteBackgroundColor) {
        this.deleteBackgroundColor = deleteBackgroundColor;
    }

    public int getDeleteColor() {
        return deleteColor;
    }

    public void setDeleteColor(int deleteColor) {
        this.deleteColor = deleteColor;
    }

    public int getDeleteIcon() {
        return deleteIcon;
    }

    public void setDeleteIcon(int deleteIcon) {
        this.deleteIcon = deleteIcon;
    }

    public void setEmojiEnabled(boolean emojiVisible) {
        this.emojiVisible = emojiVisible;
    }

    public boolean isEmojiEnabled() {
        return emojiVisible;
    }
}
