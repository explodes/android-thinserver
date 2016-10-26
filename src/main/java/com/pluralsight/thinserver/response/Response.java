package com.pluralsight.thinserver.response;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.OutputStream;

public interface Response {

	long CONTENT_LENGTH_UNDEFINED = -1;

	/**
	 * Get all response headers for this response
	 */
	@NonNull
	Headers getHeaders();

	/**
	 * Write any response body directly to the output stream
	 */
	void writeToOutputStream(@NonNull OutputStream out) throws IOException;

	/**
	 * Get the HTTP status code of this response
	 */
	int getCode();

	/**
	 * Get the length of the response body or CONTENT_LENGTH_UNDEFINED (-1) if unknown
	 */
	long getContentLength();
}
