package com.example.tictactoe;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.example.tictactoe.GameState.END;
import static com.example.tictactoe.GameState.PLAYING;

public class TicTacToeView extends View {
    private static final String BUTTON_TEXT = "Continue";
    private static final float STROKE_WIDTH = 4f;
    private static final float INDENT = 20f;
    private static final float TEXT_SIZE = 80f;
    private Paint mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mScorePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint mGridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mButtonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mButtonTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mFloatTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mFloatPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF mBorder;
    private RectF mScoreBoard;
    private RectF mButton;

    private final int mNumberOfColumns = 3;
    private int elementW, elementH;
    private boolean isCircle = true;
    private List<Drawable> mElements = new ArrayList<>(9);
    private GameState mGameState = PLAYING;
    private int size;
    private int mVictoriesOfCircle = 0;
    private int mVictoriesOfCross = 0;

    private final Drawable circle = getResources().getDrawable(R.drawable.circle, null);
    private final Drawable cross = getResources().getDrawable(R.drawable.cross, null);
    private SavedState ourState;
    private TicTacToeField mTicTacToeField;

    public TicTacToeView(Context context) {
        this(context, null, 0);
    }

    public TicTacToeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TicTacToeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        mTicTacToeField = new TicTacToeField(mNumberOfColumns);
        configureBackground();
        configureGrid();
        configureText();
        configureFloatBoard();
    }

    private void configureFloatBoard() {
        mButtonPaint.setColor(getResources().getColor(R.color.colorPrimary));
        mButtonPaint.setStyle(Paint.Style.FILL_AND_STROKE);


        mFloatPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mFloatPaint.setColor(Color.GRAY);
        mFloatPaint.setAlpha(50);
    }

    private void configureText() {
        mTextPaint.setColor(Color.BLUE);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setStrokeWidth(STROKE_WIDTH);
        mTextPaint.setTextSize(TEXT_SIZE);

        mFloatTextPaint = new Paint();
        mFloatTextPaint.setStyle(Paint.Style.FILL);
        mFloatTextPaint.setStrokeWidth(STROKE_WIDTH);
        mFloatTextPaint.setTextSize(2 * TEXT_SIZE);


        mButtonTextPaint.setColor(Color.parseColor("#ffffff"));
        mButtonTextPaint.setStyle(Paint.Style.FILL);
        mButtonTextPaint.setTextSize(TEXT_SIZE);
    }

    private void configureGrid() {
        mGridPaint.setColor(Color.GRAY);
        mGridPaint.setStyle(Paint.Style.STROKE);
        mGridPaint.setStrokeWidth(STROKE_WIDTH);
    }

    private void configureBackground() {
        mBackgroundPaint.setColor(Color.RED);
        mBackgroundPaint.setStyle(Paint.Style.STROKE);
        mBackgroundPaint.setStrokeWidth(STROKE_WIDTH);

        mScorePaint.setColor(Color.GREEN);
        mScorePaint.setStyle(Paint.Style.STROKE);
        mScorePaint.setStrokeWidth(2 * STROKE_WIDTH);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        size = Math.min(w, h);


        mBorder = new RectF(getPaddingLeft() + INDENT, getTop() + getPaddingTop() + INDENT, size - getPaddingRight() - INDENT, size - INDENT);
        if (w > h) {
            mScoreBoard = new RectF(size + 4*INDENT, getPaddingTop() + INDENT, w - getPaddingRight() - INDENT, h - INDENT);
        } else
            mScoreBoard = new RectF(getPaddingLeft() + INDENT, size + 4*INDENT, size - getPaddingRight() - INDENT, h - INDENT);

        elementH = (size - (int) STROKE_WIDTH) / mNumberOfColumns;
        elementW = (size - (int) STROKE_WIDTH) / mNumberOfColumns;

        mButton = new RectF(mScoreBoard.left + INDENT, (int) (mScoreBoard.centerY()), (int) (mScoreBoard.right - INDENT), mScoreBoard.centerY() + TEXT_SIZE + 2 * INDENT);


        circle.setBounds((int) (mScoreBoard.left + elementW / 2), (int) (mScoreBoard.bottom - elementH - INDENT),
                (int) (mScoreBoard.left + elementW), (int) (mScoreBoard.bottom - elementH / 2 - INDENT));
        cross.setBounds((int) (mScoreBoard.right - elementW), (int) (mScoreBoard.bottom - elementH - INDENT),
                (int) (mScoreBoard.right - elementW / 2), (int) (mScoreBoard.bottom - elementH / 2 - INDENT));


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (mGameState) {
            case PLAYING:
                if (mBorder.contains(x, y))
                    drawCrossOrCircle(event);
                break;
            case END:
                if (mButton.contains(x, y)) {
                    repeat();
                }
                break;
        }
        return true;
    }

    private void repeat() {
        mTicTacToeField = new TicTacToeField(mNumberOfColumns);
        mElements.clear();
        mGameState = PLAYING;
        invalidate();
    }

    private void drawCrossOrCircle(MotionEvent event) {
        int x = (int) (event.getX() / elementW);
        int y = (int) (event.getY() / elementH);
        Drawable o;
        if (!mTicTacToeField.isFull() && mTicTacToeField.isEmptyCell(x, y)) {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (isCircle) {
                    mTicTacToeField.setFigure(x, y, TicTacToeField.Figure.CIRCLE);
                    o = getResources().getDrawable(R.drawable.circle, null);
                } else {
                    mTicTacToeField.setFigure(x, y, TicTacToeField.Figure.CROSS);
                    o = getResources().getDrawable(R.drawable.cross, null);
                }
                setBound(o, x, y);
                isCircle = !isCircle;
                invalidate();
            }
            if (mTicTacToeField.getWinner() != TicTacToeField.Figure.NONE || mTicTacToeField.isFull()) {
                mGameState = END;
                if (mTicTacToeField.getWinner() == TicTacToeField.Figure.CROSS) {
                    mVictoriesOfCross++;
                } else if (mTicTacToeField.getWinner() == TicTacToeField.Figure.CIRCLE) {
                    mVictoriesOfCircle++;
                }
                invalidate();
            }
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRoundRect(mBorder, INDENT, INDENT, mBackgroundPaint);

        drawGrid(canvas);
        drawElements(canvas);
        if (mGameState == END) {
            drawFloatBoard(canvas);
        }
        drawScoreBoard(canvas);
    }

    private void drawFloatBoard(Canvas canvas) {
        canvas.drawRoundRect(mScoreBoard, INDENT, INDENT, mFloatPaint);
        canvas.drawRoundRect(mButton, INDENT, INDENT, mButtonPaint);
        String winner = mTicTacToeField.getWinner().toString();

        canvas.drawText(winner, (int) ( mScoreBoard.left + elementW - INDENT ), mScoreBoard.top + 2 * TEXT_SIZE + INDENT, mFloatTextPaint);
        canvas.drawText(BUTTON_TEXT, (int) (mButton.left + elementW - INDENT ), mButton.top + TEXT_SIZE + INDENT, mButtonTextPaint);

    }

    private void drawScoreBoard(Canvas canvas) {

        canvas.drawRoundRect(mScoreBoard, INDENT, INDENT, mScorePaint);
        if (mGameState == END) {
            setAlpha(30);
        } else {
            setAlpha(255);
        }

        canvas.drawText(Integer.toString(mVictoriesOfCircle),(int) (circle.getBounds().centerX() - INDENT ), (int) (circle.getBounds().bottom + TEXT_SIZE + INDENT), mTextPaint);

        canvas.drawText(Integer.toString(mVictoriesOfCross), (int) (cross.getBounds().centerX() - INDENT ), (int) (cross.getBounds().bottom + TEXT_SIZE + INDENT), mTextPaint);
        circle.draw(canvas);
        cross.draw(canvas);
    }

    private void setAlpha(int alpha) {
        circle.setAlpha(alpha);
        cross.setAlpha(alpha);
        mBackgroundPaint.setAlpha(alpha);
        mTextPaint.setAlpha(alpha);
    }


    private void drawElements(Canvas canvas) {
        if (ourState != null) restoreElements();
        for (Drawable cross : mElements) {
            cross.draw(canvas);
        }
    }

    private void drawGrid(Canvas canvas) {
        for (int i = 0; i < mNumberOfColumns - 1; i++) {
            // vertical line
            canvas.drawLine(elementW * (i + 1), INDENT, elementW * (i + 1), size - INDENT, mGridPaint);
            // horizontal line
            canvas.drawLine(INDENT, elementH * (i + 1), size - INDENT, elementH * (i + 1), mGridPaint);

        }

    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState state = new SavedState(superState);
        state.mData = mTicTacToeField;
        state.mGameState = mGameState;
        state.victoriesOfCircle = mVictoriesOfCircle;
        state.victoriesOfCross = mVictoriesOfCross;
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        ourState = (SavedState) state;
        mTicTacToeField = ourState.mData;
        mGameState = ourState.mGameState;
        mVictoriesOfCircle = ourState.victoriesOfCircle;
        mVictoriesOfCross = ourState.victoriesOfCross;
        invalidate();
    }

    private void restoreElements() {
        Drawable o;
        TicTacToeField.Figure figure;
        mElements.clear();
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                figure = mTicTacToeField.getFigure(x, y);
                if (figure == TicTacToeField.Figure.CIRCLE) {
                    o = getResources().getDrawable(R.drawable.circle, null);
                    setBound(o, x, y);
                } else if (figure == TicTacToeField.Figure.CROSS) {
                    o = getResources().getDrawable(R.drawable.cross, null);
                    setBound(o, x, y);
                }

            }
        }
    }

    private void setBound(Drawable o, int i, int j) {
        o.setBounds((int) (elementW * i + INDENT), (int) (elementH * j + INDENT), (int) (elementW * i + elementW - INDENT), (int) (elementH * j + elementH - INDENT));
        mElements.add(o);
    }

    private static class SavedState extends BaseSavedState {

        private TicTacToeField mData = new TicTacToeField(3);
        private GameState mGameState;
        private int victoriesOfCircle;
        private int victoriesOfCross;


        private static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);

            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        public SavedState(Parcel source) {
            super(source);
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            mData.writeToParcel(out, flags);
            out.writeString(mGameState.toString());
            out.writeInt(victoriesOfCircle);
            out.writeInt(victoriesOfCross);


        }

    }
}
