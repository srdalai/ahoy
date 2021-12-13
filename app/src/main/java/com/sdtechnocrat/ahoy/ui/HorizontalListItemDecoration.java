package com.sdtechnocrat.ahoy.ui;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sdtechnocrat.ahoy.utilities.Util;

import java.util.Objects;

public class HorizontalListItemDecoration extends RecyclerView.ItemDecoration {
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.set(Util.convertDpToPixel(parent.getContext(), 20), 0, Util.convertDpToPixel(parent.getContext(), 6), 0);
        } else if (parent.getChildAdapterPosition(view) == Objects.requireNonNull(parent.getAdapter()).getItemCount() - 1) {
            outRect.set(Util.convertDpToPixel(parent.getContext(), 6), 0, Util.convertDpToPixel(parent.getContext(), 20), 0);
        } else {
            outRect.set(Util.convertDpToPixel(parent.getContext(), 6), 0, Util.convertDpToPixel(parent.getContext(), 6), 0);
        }
    }
}
