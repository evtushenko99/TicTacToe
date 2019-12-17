package com.example.tictactoe;

import android.os.Parcel;
import android.os.Parcelable;



/**
 * Класс, описывающий клетку игрового поля
 */
public class Cell implements Parcelable {
    private GameState mGameState;

    public Cell(GameState gameState) {
        mGameState = gameState;
    }

    public Cell() { ;
    }

    protected Cell(Parcel in) {
        mGameState = GameState.valueOf(in.readString());
    }

    public static final Creator<Cell> CREATOR = new Creator<Cell>() {
        @Override
        public Cell createFromParcel(Parcel in) {
            return new Cell(in);
        }

        @Override
        public Cell[] newArray(int size) {
            return new Cell[size];
        }
    };

    public GameState getGameState() {
        return mGameState;
    }

    public void setGameState(GameState gameState) {
        mGameState = gameState;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mGameState.name());
    }
}
