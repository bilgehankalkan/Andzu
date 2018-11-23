package com.canakkoca.andzu.utils;

import android.support.annotation.Nullable;

import com.canakkoca.andzu.base.AndzuApp;
import com.canakkoca.andzu.base.NetworkLog;
import com.canakkoca.andzu.base.NetworkLogDao;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

import static okhttp3.internal.Util.UTF_8;

/**
 * Created by can.akkoca on 4/11/2017.
 */
public class LoggingInterceptor implements Interceptor {

    private NetworkLogDao networkLogDao;

    public LoggingInterceptor() {
        if (AndzuApp.getAndzuApp() == null) {
            throw new IllegalStateException("You need to implement your " +
                    "Application class from AndzuApp");
        }
        networkLogDao = AndzuApp.getAndzuApp().getDaoSession().getNetworkLogDao();
    }

    private static String bodyToString(final Request request) {

        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final Exception e) {
            return "";
        }
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        long t1 = System.nanoTime();

        Response response = chain.proceed(request);

        if (isText(response.body().contentType())) {
            long t2 = System.nanoTime();

            NetworkLog networkLog = new NetworkLog();
            networkLog.setDate(new Date().getTime());
            networkLog.setDuration((t2 - t1) / 1e6d);
            networkLog.setErrorClientDesc("");
            networkLog.setHeaders(String.valueOf(response.headers()));
            networkLog.setRequestType(request.method());
            networkLog.setResponseCode(String.valueOf(response.code()));
            String body = response.body().string();
            networkLog.setResponseData(body);
            networkLog.setUrl(String.valueOf(request.url()));
            MediaType contentType = response.body().contentType();

            networkLog.setPostData(bodyToString(request));

            if (AndzuApp.getAndzuApp() != null) {
                networkLogDao.insert(networkLog);
            }

            return response.newBuilder().body(ResponseBody.create(contentType, body)).build();
        } else {
            return response;
        }
    }

    private boolean isText(@Nullable MediaType mediaType) {
        if (mediaType == null) {
            return false;
        }
        if (mediaType.type() != null && mediaType.type().equals("text")) {
            return true;
        }
        if (mediaType.subtype() != null) {
            return mediaType.subtype().equals("json") ||
                    mediaType.subtype().equals("xml") ||
                    mediaType.subtype().equals("html") ||
                    mediaType.subtype().equals("webviewhtml");
        }
        return false;
    }
}
