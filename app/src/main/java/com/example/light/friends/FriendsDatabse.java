package com.example.light.friends;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import static android.content.ContentValues.TAG;

/**
 * Created by light on 10/18/16.
 */

public class FriendsDatabse extends SQLiteOpenHelper {
    private static final String TAG = FriendsDatabse.class.getSimpleName();
    private static final String DATABASE_NAME = "friends.db";
    private static final int DATABASE_VERSION = 2;
    private final Context mContext;

    interface Tables{
        String FRIENDS = "friends";
    }

    public FriendsDatabse(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "+ Tables.FRIENDS + " ("
            + BaseColumns._ID + "INTEGER PRIMARY KEY AUTOINCREMENT,"
            + FriendsContract.FriendsColumns.FRIENDS_NAME + " TEXT NOT NULL,"
            + FriendsContract.FriendsColumns.FRIENDS_EMAIL + " TEXT NOT NULL,"
            + FriendsContract.FriendsColumns.FRIENDS_PHONE + " TEXT NOT NULL)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        int version = oldVersion;
        if (version == 1){
            // Add some extra fields to the database without deleting existing data
            version = 2;
        }
        if(version != DATABASE_VERSION){
            db.execSQL("DROP TABLE IF EXISTS " + Tables.FRIENDS);
            onCreate(db);
        }

    }

    public static void deleteDatabase(Context context){
        context.deleteDatabase(DATABASE_NAME);
    }
}
