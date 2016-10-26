package com.pluralsight.thinserver.http;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.pluralsight.thinserver.response.Headers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class HttpRequestParser {

	public class HttpFormatException extends Exception {
		public HttpFormatException(String message) {
			super(message);
		}
	}

	private static final String SPACE = " ";
	private static final String CRLF = "\r\n";
	private static final String PROTOCOL_SEP = "://";
	private static final String HEADER_SEP = ":";

	private String mMethod;

	private Uri mUri;

	@NonNull
	private final String mUriPrefix;

	@NonNull
	private final Headers mRequestHeaders;

	public HttpRequestParser(@NonNull String host, boolean secure) {
		mUriPrefix = (secure ? Codes.SCHEME_SECURE : Codes.SCHEME_HTTP) + PROTOCOL_SEP + host;
		mRequestHeaders = new Headers();
	}

	public void parseRequest(@NonNull InputStream stream) throws IOException, HttpFormatException {
		Scanner scanner = new Scanner(stream);

		scanner.useDelimiter(SPACE);

		setMethod(scanner.next());
		setUri(scanner.next());


		scanner.useDelimiter(CRLF);
		scanner.next(); // read until next CRLF go to next line

		// does this leave the input stream at the beginning of the body content?
		while (scanner.hasNext()) {
			String next = scanner.next();
			if (TextUtils.isEmpty(next)) break;
			appendHeaderParameter(next);
		}

	}

	private void setMethod(@NonNull String method) throws HttpFormatException {
		if (TextUtils.isEmpty(method)) throw new HttpFormatException("Invalid method: " + method);
		mMethod = method;
	}

	private void setUri(@NonNull String uri) throws HttpFormatException {
		if (TextUtils.isEmpty(uri)) throw new HttpFormatException("Invalid url: " + uri);
		mUri = Uri.parse(mUriPrefix + uri);
	}

	private void appendHeaderParameter(@NonNull String header) throws HttpFormatException {
		int idx = header.indexOf(HEADER_SEP);
		if (idx == -1) throw new HttpFormatException("Invalid Header Parameter: " + header);
		String key = header.substring(0, idx);
		String value = header.substring(idx + 1, header.length());
		// trim left-hand space
		if (value.charAt(0) == ' ') value = value.substring(1);
		mRequestHeaders.set(key, value);
	}

	@NonNull
	public Headers getRequestHeaders() {
		return mRequestHeaders;
	}

	public Uri getUri() {
		return mUri;
	}

	public String getMethod() {
		return mMethod;
	}

}