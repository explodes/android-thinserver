package com.pluralsight.thinserver.aborts;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.pluralsight.thinserver.http.Codes;

public class Abort404Exception extends AbortCodeException {

	public Abort404Exception(@NonNull Uri uri) {
		this(uri.toString());
	}

	public Abort404Exception(@NonNull String url) {
		super(url);
	}

	@Override
	public int getCode() {
		return Codes.CODE_NOT_FOUND;
	}
}
