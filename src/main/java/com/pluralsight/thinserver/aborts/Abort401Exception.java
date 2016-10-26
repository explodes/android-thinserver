package com.pluralsight.thinserver.aborts;

import android.support.annotation.NonNull;

import com.pluralsight.thinserver.http.Codes;

public class Abort401Exception extends AbortCodeException {


	public Abort401Exception() {
		super("401 Unauthorized");
	}

	public Abort401Exception(@NonNull String reason) {
		super(reason);
	}

	@Override
	public int getCode() {
		return Codes.CODE_UNAUTHORIZED;
	}
}
