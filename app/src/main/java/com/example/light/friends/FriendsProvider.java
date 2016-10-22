package com.example.light.friends;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.CancellationSignal;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import static com.example.light.friends.FriendsDatabse.Tables.FRIENDS;

/**
 * Created by light on 10/18/16.
 */

public class FriendsProvider extends ContentProvider {
    private FriendsDatabse mOpenHelper;

    private static String TAG = FriendsProvider.class.getSimpleName();
    private  static final UriMatcher sUriMatcher = buildUriMatcher();

    private static final int FRINEDS = 100;
    private static final int FRINEDS_ID = 101;

    private static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FriendsContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, "friends", FRINEDS);
        matcher.addURI(authority,"friends/*", FRINEDS_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new FriendsDatabse(getContext());
        return true;
    }

    private void deleteDatabase() {
        mOpenHelper.close();
        FriendsDatabse.deleteDatabase(getContext());
        mOpenHelper = new FriendsDatabse(getContext());
    }

    // Calling process can use this method to check if uri is valid and get something back from
    // content provider.
    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FRINEDS:
                return FriendsContract.Friends.CONTENT_TYPE;
            case FRINEDS_ID:
                return FriendsContract.Friends.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
    }

    // public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, CancellationSignal cancellationSignal)
    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        final int match = sUriMatcher.match(uri);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(FRIENDS);

        switch (match) {
            case FRINEDS:
                // do nothing
                break;
            case FRINEDS_ID:
                String id = FriendsContract.Friends.getFriendId(uri);
                //where clause select * where _ID == id
                queryBuilder.appendWhere(BaseColumns._ID + "=" + id);
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }

        // db: database
        // projection: list of columns you want to return
        // selection: is a where clause but we are ignoring that in this case
        // selectionArgs: is the arguments in the selection but we pass none
        //
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        return cursor;
        //return super.query(uri, projection, selection, selectionArgs, sortOrder, cancellationSignal);
    }

    //ContentValues: the list of content values for the database like name, phone and email address
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // output uri that is passes
        Log.v(TAG,"insert(uri=" + uri + ", values" + values.toString());

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        // since it is insert we are going to use FRIENDS and not FRIENDS_ID
        switch(match) {

            case FRINEDS:
                // column hack is always null
                long recordId = db.insertOrThrow(FriendsDatabse.Tables.FRIENDS,null,values);
                // returns the id inserted
                return FriendsContract.Friends.buildFriendUri(String.valueOf(recordId));

            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.v(TAG,"update(uri=" + uri + ", values" + values.toString());
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        String selectionCriteria = selection;
        switch(match) {
            case FRINEDS:
                // do nothing
                break;
            case FRINEDS_ID:
                String id = FriendsContract.Friends.getFriendId(uri);
                selectionCriteria = BaseColumns._ID + "=" + id
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
                break;

            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }

        //  returns an integer which shows the number of records updated
        return db.update(FriendsDatabse.Tables.FRIENDS, values, selectionCriteria, selectionArgs);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.v(TAG,"delete(uri=" + uri);

        if(uri.equals(FriendsContract.URI_TABLE)) {
            deleteDatabase();
            return 0;
        }

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch(match) {
            case FRINEDS_ID:
                String id = FriendsContract.Friends.getFriendId(uri);
                String selectionCriteria = BaseColumns._ID + "=" + id
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");

                return db.delete(FriendsDatabse.Tables.FRIENDS, selectionCriteria,selectionArgs);

            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);

        }
    }

}
