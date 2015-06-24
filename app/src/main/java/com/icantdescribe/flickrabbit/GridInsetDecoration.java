package com.icantdescribe.flickrabbit;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class GridInsetDecoration extends RecyclerView.ItemDecoration {
    private int insetHorizontal;
    private int insetVertical;
    private int edgeInsetHorizontal;
    private int edgeInsetVertical;

    public GridInsetDecoration(Context context) {
        insetHorizontal = context.getResources()
                .getDimensionPixelSize(R.dimen.grid_horizontal_spacing);
        insetVertical = context.getResources()
                .getDimensionPixelOffset(R.dimen.grid_vertical_spacing);
        edgeInsetHorizontal = context.getResources()
                .getDimensionPixelSize(R.dimen.activity_vertical_margin);
        edgeInsetVertical = context.getResources()
                .getDimensionPixelOffset(R.dimen.grid_vertical_spacing);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        GridLayoutManager.LayoutParams layoutParams
                = (GridLayoutManager.LayoutParams) view.getLayoutParams();

        int position = layoutParams.getViewPosition();
        if (position == RecyclerView.NO_POSITION) {
            outRect.set(0, 0, 0, 0);
            return;
        }

        // add edge margin only if item edge is not the grid edge
        int itemSpanIndex = layoutParams.getSpanIndex();
        // is left grid edge?
        outRect.left = itemSpanIndex == 0 ? edgeInsetHorizontal : insetHorizontal;
        // is top grid edge?
        outRect.top = itemSpanIndex == position ? edgeInsetVertical : insetVertical;
        outRect.right = insetHorizontal;
        outRect.bottom = edgeInsetVertical;
    }

}
