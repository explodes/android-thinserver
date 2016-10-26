package com.pluralsight.thinserver.response;

import android.content.UriMatcher;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.pluralsight.thinserver.aborts.AbortCodeException;

import java.io.InputStream;

public interface RequestHandler {

	@NonNull
	UriMatcher getUriMatcher();

	Response handle(@NonNull String method, @NonNull Uri uri, int matchCode, @NonNull Headers headers, @NonNull InputStream body) throws AbortCodeException;

}
