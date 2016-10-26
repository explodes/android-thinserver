package com.pluralsight.thinserver.server;

import android.support.annotation.NonNull;

abstract class LoopingThread extends Thread {

	private volatile boolean mAborted;

	public LoopingThread(@NonNull String name) {
		super(name);
	}

	@Override
	public void run() {
		enterLoop();
		while (!mAborted) {
			try {
				loop();
			} catch (Exception ex) {
				onError(ex);
			}
		}
		exitLoop();
	}

	public void abort() {
		mAborted = true;
		synchronized (this) {
			notify();
		}
	}

	protected boolean isAborted() {
		return mAborted;
	}

	protected abstract void onError(@NonNull Exception ex);

	protected abstract void loop() throws Exception;

	protected void enterLoop() {
	}

	protected void exitLoop() {
	}
}
