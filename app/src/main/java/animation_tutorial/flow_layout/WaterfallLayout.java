package animation_tutorial.flow_layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class WaterfallLayout extends ViewGroup {

    private int columns = 3;
    private int hSpace = 20;
    private int vSpace = 20;
    private int childWidth = 0;
    private int top[];


    public WaterfallLayout(Context context) {
        this(context, null);
    }

    public WaterfallLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaterfallLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.top = new int[columns];
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        childWidth = (sizeWidth - (columns - 1) * hSpace) / columns;

        int wrapWidth;
        int childCount = getChildCount();
        if (childCount < columns) {
            wrapWidth = childCount * childWidth + (childCount - 1) * hSpace;
        } else {
            wrapWidth = sizeWidth;
        }
        clearTop();
        for (int i = 0; i < childCount; i++) {
            View child = this.getChildAt(i);
            int childHeight = child.getMeasuredHeight() * childWidth / child.getMeasuredWidth();
            int minColumn = getMinHeightColumn();
            top[minColumn] += vSpace + childHeight;
        }
        int wrapHeight;
        wrapHeight = getMaxHeight();
        setMeasuredDimension(widthMode == MeasureSpec.AT_MOST ? wrapWidth : sizeWidth, wrapHeight);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        clearTop();
        for (int i = 0; i < childCount; i++) {
            View child = this.getChildAt(i);
            int childHeight = child.getMeasuredHeight() * childWidth / child.getMeasuredWidth();
            int minColumn = getMinHeightColumn();
            int cLeft = minColumn * (childWidth + hSpace);
            int cTop = top[minColumn];
            int cRight = cLeft + childWidth;
            int cBottom = cTop + childHeight;
            top[minColumn] += vSpace + childHeight;
            child.layout(cLeft, cTop, cRight, cBottom);
        }
    }

    private int getMinHeightColumn() {
        int minColumn = 0;
        for (int i = 0; i < columns; i++) {
            if (top[i] < top[minColumn]) {
                minColumn = i;
            }
        }
        return minColumn;
    }

    private int getMaxHeight() {
        int maxHeight = 0;
        for (int i = 0; i < columns; i++) {
            if (top[i] > maxHeight) {
                maxHeight = top[i];
            }
        }
        return maxHeight;
    }

    private void clearTop() {
        for (int i = 0; i < columns; i++) {
            top[i] = 0;
        }
    }

}
