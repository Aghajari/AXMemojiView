package com.aghajari.memojiview.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.aghajari.memojiview.AXMemojiManager;
import com.aghajari.memojiview.memoji.Memoji;

public class AXMemojiImageView extends AppCompatImageView {

    private Memoji memoji;

    public AXMemojiImageView(@NonNull Context context) {
        super(context);
    }

    public AXMemojiImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AXMemojiImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setMemoji(Memoji memoji) {
        this.memoji = memoji;
    }

    public Memoji getMemoji() {
        return memoji;
    }

}
