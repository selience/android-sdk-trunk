package com.iresearch.android.task;

import android.content.Context;

/**
 * Default implementation of the delegate handler interface with all callbacks methods defined to
 * have empty bodies. Subclass this if you do not need to implement all methods of
 * {@link AsyncTaskHandler}. This class also handles a Context reference for you, so it's
 * always preferred to use this class over implementing the handler interface yourself.
 * 
 * @author Matthias Kaeppler
 * 
 * @param <ContextT>
 * @param <ProgressT>
 * @param <ReturnT>
 */
public abstract class AsyncTaskDefaultHandler<ContextT extends Context, ProgressT, ReturnT>
        implements AsyncTaskHandler<ContextT, ProgressT, ReturnT> {

    private ContextT context;

    public AsyncTaskDefaultHandler(ContextT context) {
        this.context = context;
    }

    @Override
    public final ContextT getContext() {
        return context;
    }

    @Override
    public final void setContext(ContextT context) {
        this.context = context;
    }

    @Override
    public boolean onTaskStarted(ContextT context) {
        return false;
    }

    @Override
    public boolean onTaskProgress(ContextT context, ProgressT... progress) {
        return false;
    }

    @Override
    public boolean onTaskCompleted(ContextT context, ReturnT result) {
        return false;
    }

    @Override
    public boolean onTaskSuccess(ContextT context, ReturnT result) {
        return false;
    }

    @Override
    public boolean onTaskFailed(ContextT context, Exception error) {
        return false;
    }

}
