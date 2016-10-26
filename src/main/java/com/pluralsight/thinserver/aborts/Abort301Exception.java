package com.pluralsight.thinserver.aborts;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pluralsight.thinserver.http.Codes;
import com.pluralsight.thinserver.response.Headers;

public class Abort301Exception extends AbortCodeException {

	@NonNull
	private final String mLocation;

	public Abort301Exception(@NonNull Uri location) {
		this(location.toString());
	}

	public Abort301Exception(@NonNull String location) {
		super(location);
		mLocation = location;
	}

	@Nullable
	@Override
	public Headers getHeaders() {
		Headers headers = new Headers();
		headers.set(Codes.HEADER_LOCATION, mLocation);
		return headers;
	}

	@Override
	public int getCode() {
		return Codes.CODE_MOVED_PERMANENTLY;
	}
}
