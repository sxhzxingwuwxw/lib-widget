package com.android.lib.media.net;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by weici on 2021/1/14.
 * Describe:
 */
public class Executor {
    private static final ExecutorService mExecutorService = Executors.newFixedThreadPool(20);

    public static void AsyncRun(Runnable runnable) {
        mExecutorService.execute(runnable);
    }
}
