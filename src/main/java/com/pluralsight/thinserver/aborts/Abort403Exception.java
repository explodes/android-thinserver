package com.pluralsight.thinserver.aborts;

import android.support.annotation.NonNull;

import com.pluralsight.thinserver.http.Codes;

public class Abort403Exception extends AbortCodeException {


	public Abort403Exception() {
		super("403 Forbidden");
	}

	public Abort403Exception(@NonNull String reason) {
		super(reason);
	}

	@Override
	public int getCode() {
		return Codes.CODE_FORBIDDEN;
	}
}
