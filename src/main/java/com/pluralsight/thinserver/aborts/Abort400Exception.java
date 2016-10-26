package com.pluralsight.thinserver.aborts;

import android.support.annotation.NonNull;

import com.pluralsight.thinserver.http.Codes;

public class Abort400Exception extends AbortCodeException {

	public Abort400Exception() {
		super("400 Bad Request");
	}

	public Abort400Exception(@NonNull String reason) {
		super(reason);
	}

	@Override
	public int getCode() {
		return Codes.CODE_BAD_REQUEST;
	}
}
