package com.example.light.friends;


import android.content.ContentResolver;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.app.ListFragment;


import java.util.List;

/**
 * Created by light on 10/20/16.
 */

public class FriendsListFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<List<Friend>> {

    private static final String LOG_TAG = FriendsListFragment.class.getSimpleName();
    private FriendsCustomAdapter mAdapter;
    // unique id for loader
    private static final int LOADER_ID = 1;
    private ContentResolver mContentResolver;
    private List<Friend> mFriends;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);
        mContentResolver = getActivity().getContentResolver();
        // child fragment manager allows us to control fragment on screen
        mAdapter = new FriendsCustomAdapter(getActivity(), getChildFragmentManager());
        setEmptyText("No friends");
        setListAdapter(mAdapter);
        setListShown(false);

        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<List<Friend>> onCreateLoader(int i, Bundle args) {
        mContentResolver = getActivity().getContentResolver();
        return new FriendsListLoader(getActivity(), FriendsContract.URI_TABLE, mContentResolver);
    }

    @Override
    public void onLoadFinished(Loader<List<Friend>> loader, List<Friend> friends) {
        mAdapter.setData(friends);
        mFriends = friends;
        if(isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Friend>> loader) {
        mAdapter.setData(null);
    }
}
