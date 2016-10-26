package com.pluralsight.thinserver.util;

import android.support.annotation.NonNull;

import com.pluralsight.thinserver.http.Codes;
import com.pluralsight.thinserver.response.Headers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

public class WriterUtils {

	private static final char SPACE = ' ';
	private static final String CRLF = "\r\n";

	private static final int STREAM_COPY_BLOCK_SIZE = 4096;

	public static void writeStatusLine(@NonNull String httpVersion, int code, @NonNull OutputStreamWriter out) throws IOException {
		out.write(httpVersion);
		out.write(SPACE);
		out.write(String.valueOf(code));
		out.write(SPACE);
		out.write(Codes.getReason(code));
		out.write(CRLF);
		flush(out);
	}

	public static void writeHeaders(@NonNull Headers headers, @NonNull Headers additionalHeaders, @NonNull OutputStreamWriter out) throws IOException {
		// write all response headers
		for (Map.Entry<String, String> header : headers.entries()) {
			String key = header.getKey();
			String value = header.getValue();
			writeHeader(key, value, out);
		}
		// write additional headers *if they weren't explicit* in response headers
		for (Map.Entry<String, String> header : additionalHeaders.entries()) {
			String key = header.getKey();
			if (!headers.contains(key)) {
				String value = header.getValue();
				writeHeader(key, value, out);
			}
		}
		out.write(CRLF);
		flush(out);
	}

	public static void writeStreamToStream(@NonNull InputStream in, @NonNull OutputStream out) throws IOException {
		byte[] buff = new byte[STREAM_COPY_BLOCK_SIZE];
		int bytesRead;
		while ((bytesRead = in.read(buff)) > 0) {
			out.write(buff, 0, bytesRead);
		}
		out.flush();
	}

	private static void flush(@NonNull OutputStreamWriter out) throws IOException {
		out.flush();
	}

	private static void writeHeader(@NonNull String key, @NonNull String value, @NonNull OutputStreamWriter out) throws IOException {
		out.write(key);
		out.write(": ");
		out.write(value);
		out.write(CRLF);
	}

}
