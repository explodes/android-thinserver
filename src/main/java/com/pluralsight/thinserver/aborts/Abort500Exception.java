package com.pluralsight.thinserver.aborts;

import android.support.annotation.NonNull;

import com.pluralsight.thinserver.http.Codes;

public class Abort500Exception extends AbortCodeException {

	public Abort500Exception(@NonNull String description) {
		super(description);
	}

	public Abort500Exception(@NonNull Exception cause) {
		super(cause.getMessage(), cause);
	}

	@Override
	public int getCode() {
		return Codes.CODE_INTERNAL_SERVER_ERROR;
	}
}
