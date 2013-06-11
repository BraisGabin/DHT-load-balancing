package com.braisgabin.dhtbalanced.utils;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

/**
 * Loader allows to load every type of data asynchronously.
 * 
 * Loaders may be used across multiple Activities (assuming they aren't bound to
 * the LoaderManager), so NEVER hold a reference to the context directly. Doing
 * so will cause you to leak an entire Activity's context. The superclass
 * constructor will store a reference to the Application Context instead, and
 * can be retrieved with a call to getContext().
 * 
 * @param <T>
 * 
 *            Idea: http://www.androiddesignpatterns.com/2012/08/implementing-
 *            loaders.html
 */
public abstract class Loader<T> extends AsyncTaskLoader<T> {

	// We hold a reference to the Loader's data here.
	private T mData;

	/**
	 * @param ctx
	 *            - Current context.
	 */
	public Loader(Context ctx) {
		super(ctx);
	}

	/**
	 * This method performs the asynchronous load.
	 */
	@Override
	public abstract T loadInBackground();

	/**
	 * This method deliver the results to the registered listener.
	 */
	@Override
	public void deliverResult(T data) {
		if (isReset()) {
			// The Loader has been reset; ignore the result and invalidate the data.
			onReleaseResources(data);
			return;
		}

		// Hold a reference to the old data so it doesn't get garbage collected.
		// The old data may still be in use (i.e. bound to an adapter, etc.), so
		// we must protect it until the new data has been delivered.
		T oldData = mData;
		mData = data;

		if (isStarted()) {
			// If the Loader is in a started state, deliver the results to the
			// client. The superclass method does this for us.
			super.deliverResult(data);
		}

		// Invalidate the old data as we don't need it any more.
		if (oldData != null && oldData != data) {
			onReleaseResources(oldData);
		}
	}

	/**
	 * This method implement the Loader's state-dependent behavior.
	 */
	@Override
	protected void onStartLoading() {
		if (mData != null) {
			// Deliver any previously loaded data immediately.
			deliverResult(mData);
		}

		// Begin monitoring the underlying data source.
		onRegisterDataObserver();

		if (takeContentChanged() || mData == null) {
			// When the observer detects a change, it should call onContentChanged()
			// on the Loader, which will cause the next call to takeContentChanged()
			// to return true. If this is ever the case (or if the current data is
			// null), we force a new load.

			forceLoad();
		}
	}

	@Override
	protected void onStopLoading() {
		// The Loader is in a stopped state, so we should attempt to cancel the
		// current load (if there is one).

		// Note that we leave the observer as is; Loaders in a stopped state
		// should still monitor the data source for changes so that the Loader
		// will know to force a new load if it is ever started again.
	}

	@Override
	protected void onReset() {
		// Ensure the loader has been stopped.
		onStopLoading();

		// At this point we can release the resources associated with 'mData'.
		if (mData != null) {
			onReleaseResources(mData);
			mData = null;
		}

		// The Loader is being reset, so we should stop monitoring for changes.
		onUnregisterDataObserver();
	}

	@Override
	public void onCanceled(T data) {
		// Attempt to cancel the current asynchronous load.
		super.onCanceled(data);

		// The load has been canceled, so we should release the resources
		// associated with 'data'.
		onReleaseResources(data);
	}

	/**
	 * Observer which receives the resources.
	 * 
	 * @param data
	 *            - the asked resources.
	 */
	protected abstract void onReleaseResources(T data);

	/**
	 * Observer which receives notifications when the data changes.
	 */
	protected abstract void onRegisterDataObserver();

	protected abstract void onUnregisterDataObserver();
}
