package com.lin.cardlib;


import com.lin.cardlib.utils.ReItemTouchHelper;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
/**
 * @author yuqirong
 * modified by linchen
 */

public class CardLayoutManager extends RecyclerView.LayoutManager {

    private final RecyclerView mRecyclerView;
    private final ReItemTouchHelper mItemTouchHelper;
    private CardConfig mConfig;

    public CardLayoutManager(@NonNull ReItemTouchHelper itemTouchHelper, CardConfig cardConfig) {
        this.mRecyclerView = itemTouchHelper.getRecyclerView();
        this.mItemTouchHelper = itemTouchHelper;
        mConfig = cardConfig;
    }


    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        detachAndScrapAttachedViews(recycler);
        int itemCount = getItemCount();
        int showCount = mConfig.getShowCount();
        float defaultScale = mConfig.getCardScale();
        if (itemCount > showCount) {
            for (int position = showCount; position >= 0; position--) {
                View view = recycler.getViewForPosition(position);
                addView(view);
                measureChildWithMargins(view, 0, 0);
                int widthSpace = getWidth() - getDecoratedMeasuredWidth(view);
                int heightSpace = getHeight() - getDecoratedMeasuredHeight(view);
                layoutDecoratedWithMargins(view,
                        widthSpace / 2, heightSpace / 2,
                        widthSpace / 2 + getDecoratedMeasuredWidth(view),
                        heightSpace / 2 + getDecoratedMeasuredHeight(view));

                if (position == showCount) {
                    view.setScaleX(1 - (position - 1) * defaultScale);
                    view.setScaleY(1 - (position - 1) * defaultScale);
                    switch (mConfig.getStackDirection()) {
                        case ReItemTouchHelper.UP:
                            view.setTranslationY(-(position - 1) * view.getMeasuredHeight() / mConfig.getCardTranslateDistance());
                            break;
                        case ReItemTouchHelper.RIGHT:
                            view.setTranslationX((position - 1) * view.getMeasuredWidth() / mConfig.getCardTranslateDistance());
                            break;
                        case ReItemTouchHelper.LEFT:
                            view.setTranslationX(-(position - 1) * view.getMeasuredWidth() / mConfig.getCardTranslateDistance());
                            break;
                        case ReItemTouchHelper.DOWN:
                        default:
                            view.setTranslationY((position - 1) * view.getMeasuredHeight() / mConfig.getCardTranslateDistance());
                            break;
                    }
                } else if (position > 0) {
                    view.setScaleX(1 - position * defaultScale);
                    view.setScaleY(1 - position * defaultScale);
                    switch (mConfig.getStackDirection()) {
                        case ReItemTouchHelper.UP:
                            view.setTranslationY(-position * view.getMeasuredHeight() / mConfig.getCardTranslateDistance());
                            break;
                        case ReItemTouchHelper.RIGHT:
                            view.setTranslationX(position * view.getMeasuredWidth() / mConfig.getCardTranslateDistance());
                            break;
                        case ReItemTouchHelper.LEFT:
                            view.setTranslationX(-position * view.getMeasuredWidth() / mConfig.getCardTranslateDistance());
                            break;
                        case ReItemTouchHelper.DOWN:
                        default:
                            view.setTranslationY(position * view.getMeasuredHeight() / mConfig.getCardTranslateDistance());
                            break;
                    }
                } else {
                    view.setOnTouchListener(mOnTouchListener);
                }
            }
        } else {
            for (int position = itemCount - 1; position >= 0; position--) {
                View view = recycler.getViewForPosition(position);
                addView(view);
                measureChildWithMargins(view, 0, 0);
                int widthSpace = getWidth() - getDecoratedMeasuredWidth(view);
                int heightSpace = getHeight() - getDecoratedMeasuredHeight(view);
                layoutDecoratedWithMargins(view, widthSpace / 2, heightSpace / 2,
                        widthSpace / 2 + getDecoratedMeasuredWidth(view),
                        heightSpace / 2 + getDecoratedMeasuredHeight(view));

                if (position > 0) {
                    view.setScaleX(1 - position * defaultScale);
                    view.setScaleY(1 - position * defaultScale);
                    switch (mConfig.getStackDirection()) {
                        case ReItemTouchHelper.UP:
                            view.setTranslationY(-position * view.getMeasuredHeight() / mConfig.getCardTranslateDistance());
                            break;
                        case ReItemTouchHelper.RIGHT:
                            view.setTranslationX(position * view.getMeasuredWidth() / mConfig.getCardTranslateDistance());
                            break;
                        case ReItemTouchHelper.LEFT:
                            view.setTranslationX(-position * view.getMeasuredWidth() / mConfig.getCardTranslateDistance());
                            break;
                        case ReItemTouchHelper.DOWN:
                        default:
                            view.setTranslationY(position * view.getMeasuredHeight() / mConfig.getCardTranslateDistance());
                            break;
                    }
                } else {
                    view.setOnTouchListener(mOnTouchListener);
                }
            }
        }
    }


    private float touchDownX;
    private float touchDownY;
    private final View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            RecyclerView.ViewHolder childViewHolder = mRecyclerView.getChildViewHolder(v);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touchDownX = event.getX();
                    touchDownY = event.getY();
                    return false;
                case MotionEvent.ACTION_MOVE:
                    boolean needSwipe = (Math.abs(touchDownX - event.getX()) >= ViewConfiguration.get(
                            mRecyclerView.getContext()).getScaledTouchSlop())
                            || (Math.abs(touchDownY - event.getY()) >= ViewConfiguration.get(
                            mRecyclerView.getContext()).getScaledTouchSlop());
                    if (needSwipe) {
                        mItemTouchHelper.startSwipe(childViewHolder);
                        return false;
                    }
                    return true;
            }
            return v.onTouchEvent(event);
        }
    };
}