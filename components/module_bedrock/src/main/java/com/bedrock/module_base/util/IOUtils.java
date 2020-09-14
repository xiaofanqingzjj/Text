package com.bedrock.module_base.util;

import android.database.Cursor;

import java.io.Closeable;

public class IOUtils {
    private IOUtils() {
    }

    public static boolean closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
                return true;
            } catch (Throwable var2) {
            }
        }

        return false;
    }

    public static void closeQuietly(Cursor cursor) {
        if (cursor != null) {
            try {
                cursor.close();
            } catch (Throwable var2) {
            }
        }

    }
}
