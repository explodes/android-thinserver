package com.pluralsight.thinserver.http;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Codes {
	public static final String SCHEME_HTTP = "http";
	public static final String SCHEME_SECURE = "https";

	public static final int CODE_OK = 200;
	public static final int CODE_MOVED_PERMANENTLY = 301;
	public static final int CODE_BAD_REQUEST = 400;
	public static final int CODE_UNAUTHORIZED = 401;
	public static final int CODE_FORBIDDEN = 403;
	public static final int CODE_NOT_FOUND = 404;
	public static final int CODE_METHOD_NOT_ALLOWED = 405;
	public static final int CODE_INTERNAL_SERVER_ERROR = 500;

	public static final String HEADER_CONTENT_LENGTH = "Content-Length";
	public static final String HEADER_CONTENT_RANGE = "Content-Range";
	public static final String HEADER_ACCEPT_RANGES = "Accept-Range";
	public static final String HEADER_ACCEPT_RANGES_BYTES = "bytes";
	public static final String HEADER_HOST = "Host";
	public static final String HEADER_SERVER = "Server";
	public static final String HEADER_CONTENT_TYPE = "Content-Type";
	public static final String HEADER_CONTENT_TYPE_APPLICATION_OCTET_STREAM = "application/octet-stream";
	public static final String HEADER_LOCATION = "Location";
	public static final String HEADER_X_DEBUG = "X-Debug";
	public static final String HEADER_X_DEBUG_DEFAULT = "null";
	public static final String HEADER_ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
	public static final String HEADER_ACCESS_CONTROL_ALLOW_ORIGIN_ALL = "*";


	@SuppressLint("UseSparseArrays")
	private static Map<Integer, String> REASONS = new HashMap<>();

	private static final String DEFAULT_REASON = "I'm a teapot";

	static {
		REASONS.put(100, "Continue");
		REASONS.put(101, "Switching Protocols");
		REASONS.put(CODE_OK, "OK");
		REASONS.put(201, "Created");
		REASONS.put(202, "Accepted");
		REASONS.put(203, "Non-Authoritative Information");
		REASONS.put(204, "No Content");
		REASONS.put(205, "Reset Content");
		REASONS.put(206, "Partial Content");
		REASONS.put(300, "Multiple Choices");
		REASONS.put(CODE_MOVED_PERMANENTLY, "Moved Permanently");
		REASONS.put(302, "Found");
		REASONS.put(303, "See Other");
		REASONS.put(304, "Not Modified");
		REASONS.put(305, "Use Proxy");
		REASONS.put(307, "Temporary Redirect");
		REASONS.put(CODE_BAD_REQUEST, "Bad Request");
		REASONS.put(CODE_UNAUTHORIZED, "Unauthorized");
		REASONS.put(402, "Payment Required");
		REASONS.put(CODE_FORBIDDEN, "Forbidden");
		REASONS.put(CODE_NOT_FOUND, "Not Found");
		REASONS.put(CODE_METHOD_NOT_ALLOWED, "Method Not Allowed");
		REASONS.put(406, "Not Acceptable");
		REASONS.put(407, "Proxy Authentication Required");
		REASONS.put(408, "Request Time-out");
		REASONS.put(409, "Conflict");
		REASONS.put(410, "Gone");
		REASONS.put(411, "Length Required");
		REASONS.put(412, "Precondition Failed");
		REASONS.put(413, "Request Entity Too Large");
		REASONS.put(414, "Request-URI Too Large");
		REASONS.put(415, "Unsupported Media Type");
		REASONS.put(416, "Requested range not satisfiable");
		REASONS.put(417, "Expectation Failed");
		REASONS.put(CODE_INTERNAL_SERVER_ERROR, "Internal Server Error");
		REASONS.put(501, "Not Implemented");
		REASONS.put(502, "Bad Gateway");
		REASONS.put(503, "Service Unavailable");
		REASONS.put(504, "Gateway Time-out");
		REASONS.put(505, "HTTP Version not supported");

		REASONS = Collections.unmodifiableMap(REASONS);
	}

	@NonNull
	public static String getReason(int code) {
		String reason = REASONS.get(code);
		if (reason == null) {
			return DEFAULT_REASON;
		} else {
			return reason;
		}
	}
}
