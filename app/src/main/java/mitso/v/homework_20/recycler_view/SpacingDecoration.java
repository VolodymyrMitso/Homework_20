package mitso.v.homework_20.recycler_view;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SpacingDecoration extends RecyclerView.ItemDecoration {

    private int mSpace;

    public SpacingDecoration(int mSpace) {
        this.mSpace = mSpace;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        outRect.left = mSpace;
        outRect.right = mSpace;
        outRect.bottom = mSpace;

        if (parent.getChildLayoutPosition(view) == 0)
            outRect.top = mSpace;
        else
            outRect.top = 0;
    }
}