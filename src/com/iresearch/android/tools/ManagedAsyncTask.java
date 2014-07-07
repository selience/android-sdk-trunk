package com.iresearch.android.tools;

import android.content.Context;
import android.os.AsyncTask;

public abstract class ManagedAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    private final AsyncTaskManager manager;
    private final Context context;
    private final String tag;

    public ManagedAsyncTask(final Context context, final AsyncTaskManager manager) {
        this(context, manager, null);
    }

    public ManagedAsyncTask(final Context context, final AsyncTaskManager manager, final String tag) {
        this.manager = manager;
        this.context = context;
        this.tag = tag;
    }

    public Context getContext() {
        return context;
    }

    public String getTag() {
        return tag;
    }

    @Override
    protected void finalize() throws Throwable {
        manager.remove(hashCode());
        super.finalize();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected void onPostExecute(final Result result) {
        super.onPostExecute(result);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

}
