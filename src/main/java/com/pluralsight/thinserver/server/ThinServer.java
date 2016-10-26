package com.pluralsight.thinserver.server;

import android.content.UriMatcher;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.pluralsight.thinserver.aborts.Abort404Exception;
import com.pluralsight.thinserver.aborts.Abort500Exception;
import com.pluralsight.thinserver.aborts.AbortCodeException;
import com.pluralsight.thinserver.http.Codes;
import com.pluralsight.thinserver.http.HttpRequestParser;
import com.pluralsight.thinserver.response.Headers;
import com.pluralsight.thinserver.response.RequestHandler;
import com.pluralsight.thinserver.response.Response;
import com.pluralsight.thinserver.util.IpUtils;
import com.pluralsight.thinserver.util.WriterUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThinServer {

	private static final String VERSION = "1.0";

	private static final String SERVER_NAME = "ThinServer_" + VERSION;

	@Nullable
	public static InetAddress getLocalIpAddress() {
		String addrStr = IpUtils.getIPAddress(true);
		if (TextUtils.isEmpty(addrStr)) return null;
		try {
			return InetAddress.getAllByName(addrStr)[0];
		} catch (UnknownHostException e) {
			Log.e(TAG, "Unable to get local ip address", e);
			return null;
		}
	}

	private static final String TAG = "ThinServer";

	private static final String HTTP_VERSION = "HTTP/1.1";

	private static final int SHUTDOWN_WAIT_SECONDS = 10;

	private static final boolean SECURE = false;

	private static final int BACKLOG = 10;

	private static final int NUM_WORKERS = 5;

	private static int threadId = 0;

	@NonNull
	private final List<RequestHandler> mRequestHandlers = new ArrayList<>();

	@NonNull
	private final Logger mLogger = new Logger();

	@NonNull
	private InetAddress mAddress;

	@Nullable
	private SocketThread mThread;

	private int mPort;

	private boolean mDebugLoggingEnabled;

	public ThinServer(@NonNull InetAddress bindAddress, int port) {
		mAddress = bindAddress;
		mPort = port;
	}

	public void enableDebugLogging(boolean enabled) {
		mDebugLoggingEnabled = enabled;
	}

	@NonNull
	public InetAddress getAddress() {
		return mAddress;
	}

	public int getPort() {
		return mPort;
	}

	public synchronized void start() throws IOException {
		if (isRunning()) throw new IllegalStateException("Already running");

		mThread = new SocketThread(new ServerSocket(mPort, BACKLOG, mAddress));
		mThread.start();
	}

	public synchronized boolean isRunning() {
		return mThread != null && !mThread.isAborted();
	}

	public synchronized void stop() {
		if (!isRunning()) throw new IllegalStateException("Not running");
		// if we're running it isn't null, but we don't want the linter to complain
		if (mThread != null) {
			mThread.abort();
		}
	}

	public void addRequestHandler(@NonNull RequestHandler handler) {
		synchronized (mRequestHandlers) {
			mRequestHandlers.add(handler);
		}
	}

	public void removeRequestHandler(@NonNull RequestHandler handler) {
		synchronized (mRequestHandlers) {
			mRequestHandlers.remove(handler);
		}
	}

	private class SocketThread extends LoopingThread {

		@NonNull
		private final ServerSocket mServer;

		private ExecutorService mExecutor;

		public SocketThread(@NonNull ServerSocket socket) {
			super("server-" + (++threadId));
			mServer = socket;
		}

		@Override
		protected void enterLoop() {
			mExecutor = Executors.newFixedThreadPool(NUM_WORKERS);
		}

		@Override
		protected void loop() throws Exception {
			Socket socket = mServer.accept();
			mExecutor.execute(new HttpWorker(socket));
		}

		@Override
		protected void onError(@NonNull Exception ex) {
			if (isAborted()) return;

			mLogger.e(TAG, "Error in accept-thread", ex);
		}

		@Override
		protected void exitLoop() {
			closeServer();
			mExecutor.shutdown();
			try {
				mExecutor.awaitTermination(SHUTDOWN_WAIT_SECONDS, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				mLogger.e(TAG, "Unable to shutdown in a timely fashion", e);
			}
		}

		@Override
		public void abort() {
			closeServer();
			super.abort();
		}

		private void closeServer() {
			if (!mServer.isClosed()) {
				try {
					mServer.close();
				} catch (IOException e) {
					mLogger.w(TAG, "Unable to close server", e);
				}
			}
		}
	}

	private class HttpWorker implements Runnable {

		@NonNull
		private final Socket mSocket;

		@NonNull
		private final OutputStreamWriter out;

		public HttpWorker(@NonNull Socket socket) throws IOException {
			mSocket = socket;
			out = new OutputStreamWriter(mSocket.getOutputStream(), Charset.forName("utf-8"));
		}

		@Override
		public void run() {

			try {
				HttpRequestParser parser = new HttpRequestParser(mAddress.getHostAddress(), SECURE);
				InputStream in = mSocket.getInputStream();
				parser.parseRequest(in);

				Uri uri = parser.getUri();

				mLogger.d(TAG, parser.getMethod() + " " + uri.toString());

				RequestHandler handler = null;
				int uriMatch = UriMatcher.NO_MATCH;
				synchronized (mRequestHandlers) {
					for (RequestHandler match : mRequestHandlers) {
						UriMatcher matcher = match.getUriMatcher();
						uriMatch = matcher.match(uri);
						if (uriMatch != UriMatcher.NO_MATCH) {
							handler = match;
							break;
						}
					}
				}

				if (handler == null) throw new Abort404Exception(uri);

				Response response = handler.handle(parser.getMethod(), uri, uriMatch, parser.getRequestHeaders(), in);
				writeResponse(response);

			} catch (AbortCodeException ex) {
				writeErrorResponseSafe(ex);
			} catch (Exception ex) {
				writeErrorResponseSafe(new Abort500Exception(ex));
				onError(ex);
			} finally {
				closeSocket();
			}

		}

		private void closeSocket() {
			if (!mSocket.isClosed()) {
				try {
					mSocket.close();
				} catch (IOException e) {
					mLogger.w(TAG, "Unable to close connection", e);
				}
			}
		}

		private void onError(@NonNull Exception ex) {
			mLogger.e(TAG, "worker error during run", ex);
		}

		private void writeErrorResponseSafe(@NonNull AbortCodeException ex) {
			try {
				writeErrorResponse(ex);
			} catch (Exception e) {
				onError(e);
			}
		}

		private void writeErrorResponse(@NonNull AbortCodeException ex) throws IOException {
			WriterUtils.writeStatusLine(HTTP_VERSION, ex.getCode(), out);
			Headers headers = ex.getHeaders();
			if (headers == null) headers = new Headers();

			Headers additional = additionalErrorHeaders(ex);

			WriterUtils.writeHeaders(headers, additional, out);
		}

		private void writeResponse(@NonNull Response response) throws IOException {
			WriterUtils.writeStatusLine(HTTP_VERSION, response.getCode(), out);

			Headers headers = response.getHeaders();
			Headers additional = additionalResponseHeaders(response);
			WriterUtils.writeHeaders(headers, additional, out);

			OutputStream out = mSocket.getOutputStream();
			response.writeToOutputStream(out);
		}

		@NonNull
		private Headers standardResponseHeaders() {
			Headers headers = new Headers();
			headers.set(Codes.HEADER_HOST, mAddress.getCanonicalHostName());
			headers.set(Codes.HEADER_SERVER, SERVER_NAME);
			return headers;
		}

		@NonNull
		private Headers additionalErrorHeaders(@NonNull AbortCodeException ex) {
			Headers headers = standardResponseHeaders();
			if (mDebugLoggingEnabled) {
				String message = ex.getMessage();
				if (message == null) message = Codes.HEADER_X_DEBUG_DEFAULT;
				headers.set(Codes.HEADER_X_DEBUG, message);
			}
			return headers;
		}

		@NonNull
		private Headers additionalResponseHeaders(@NonNull Response response) {
			Headers additional = standardResponseHeaders();

			long contentLength = response.getContentLength();
			if (contentLength != Response.CONTENT_LENGTH_UNDEFINED) {
				additional.set(Codes.HEADER_CONTENT_LENGTH, String.valueOf(contentLength));
			}

			return additional;
		}
	}

	private class Logger {

		public void d(@NonNull String tag, @NonNull String message) {
			if (mDebugLoggingEnabled) {
				Log.d(TAG, message);
			}
		}

		public void w(@NonNull String tag, @NonNull String message) {
			if (mDebugLoggingEnabled) {
				Log.w(TAG, message);
			}
		}

		public void w(@NonNull String tag, @NonNull String message, @NonNull Throwable t) {
			if (mDebugLoggingEnabled) {
				Log.w(TAG, message, t);
			}
		}

		public void e(@NonNull String tag, @NonNull String message, @NonNull Throwable t) {
			Log.e(TAG, message, t);
		}

	}

}
