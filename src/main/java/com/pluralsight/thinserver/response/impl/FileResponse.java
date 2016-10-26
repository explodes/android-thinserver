package com.pluralsight.thinserver.response.impl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pluralsight.thinserver.aborts.Abort400Exception;
import com.pluralsight.thinserver.http.Codes;
import com.pluralsight.thinserver.response.Headers;
import com.pluralsight.thinserver.response.SimpleResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FileResponse extends SimpleResponse {

	@NonNull
	private final File mFile;

	public FileResponse(@NonNull File file) throws Abort400Exception {
		if (!file.exists() || !file.canRead())
			throw new Abort400Exception("Unable to find readable file");
		mFile = file;
	}

	@Override
	protected void addHeaders(@NonNull Headers headers) {
		headers.set(Codes.HEADER_CONTENT_TYPE, Codes.HEADER_CONTENT_TYPE_APPLICATION_OCTET_STREAM);
	}

	@Nullable
	@Override
	public InputStream getResponseStream() throws FileNotFoundException {
		return new FileInputStream(mFile);
	}

	@Override
	public int getCode() {
		return Codes.CODE_OK;
	}

	@Override
	public long getContentLength() {
		return mFile.length();
	}

}