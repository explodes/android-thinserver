package com.pluralsight.thinserver.aborts;

import android.support.annotation.Nullable;

import com.pluralsight.thinserver.response.Headers;

public abstract class AbortCodeException extends Exception {

	public AbortCodeException() {
	}

	public AbortCodeException(String message) {
		super(message);
	}

	public AbortCodeException(String message, Throwable cause) {
		super(message, cause);
	}

	public AbortCodeException(Throwable cause) {
		super(cause);
	}

	public abstract int getCode();

	@Nullable
	public Headers getHeaders() {
		return null;
	}
}
