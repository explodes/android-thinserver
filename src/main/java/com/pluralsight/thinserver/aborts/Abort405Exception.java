package com.pluralsight.thinserver.aborts;

import android.support.annotation.NonNull;

import com.pluralsight.thinserver.http.Codes;

public class Abort405Exception extends AbortCodeException {

	public Abort405Exception(@NonNull String method) {
		super(method);
	}

	@Override
	public int getCode() {
		return Codes.CODE_METHOD_NOT_ALLOWED;
	}
}
