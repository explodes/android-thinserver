package com.pluralsight.thinserver.response;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pluralsight.thinserver.util.WriterUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class SimpleResponse implements Response {

	@Override
	public final void writeToOutputStream(@NonNull OutputStream out) throws IOException {
		InputStream in = getResponseStream();
		if (in != null) {
			WriterUtils.writeStreamToStream(in, out);
		}
	}

	@NonNull
	@Override
	public final Headers getHeaders() {
		Headers headers = new Headers();
		addHeaders(headers);
		return headers;
	}

	/**
	 * Add any headers special to this response here
	 */
	protected abstract void addHeaders(@NonNull Headers headers);

	/**
	 * Get an input stream containing the response body, if any
	 */
	@Nullable
	protected abstract InputStream getResponseStream() throws IOException;
}
