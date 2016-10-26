package com.pluralsight.thinserver.response;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Headers {

	@NonNull
	private final HashMap<String, String> mHeaders;

	public Headers() {
		mHeaders = new HashMap<>();
	}

	public boolean contains(@NonNull String header) {
		return mHeaders.containsKey(header);
	}

	@Nullable
	public String get(@NonNull String header) {
		return mHeaders.get(header);
	}

	public void set(@NonNull String header, @Nullable String value) {
		if (value == null) {
			mHeaders.remove(header);
		} else {
			mHeaders.put(header, value);
		}
	}

	@NonNull
	public Set<Map.Entry<String, String>> entries() {
		return mHeaders.entrySet();
	}


}
