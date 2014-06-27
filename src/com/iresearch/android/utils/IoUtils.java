
package com.iresearch.android.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IoUtils {
    private static final int BUFFER_SIZE = 1500;

    /**
     * Silently close the given {@link Closeable}s, ignoring any
     * {@link IOException}.<br/>
     * {@code null} objects are ignored.
     * 
     * @param toClose The {@link Closeable}s to close.
     */
    public static void closeSilently(Closeable... toClose) {
        for (Closeable closeable : toClose) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    // Ignored
                }
            }
        }
    }

    /**
     * Copy the contents of the given {@link InputStream} into the given
     * {@link OutputStream}.<br/>
     * Note: the given {@link InputStream} and {@link OutputStream} won't be
     * closed.
     * 
     * @param in The {@link InputStream} to read.
     * @param out The {@link OutputStream} to write to.
     * @return the actual number of bytes that were read.
     * @throws IOException If a error occurs while reading or writing.
     */
    public static long copy(InputStream in, OutputStream out) throws IOException {
        long res = 0;
        byte[] buffer = new byte[BUFFER_SIZE];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
            out.flush();
            res += read;
        }
        return res;
    }

    /**
     * Fully reads the given {@link InputStream} into a {@link String}.<br/>
     * The encoding inside the {@link InputStream} is assumed to be
     * {@code UTF-8}.<br/>
     * Note: the given {@link InputStream} won't be closed.
     * 
     * @param in The {@link InputStream} to read.
     * @return a String containing the contents of the given {@link InputStream}
     *         .
     * @throws IOException If a error occurs while reading.
     */
    public static String readFully(InputStream in) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        byte[] buffer = new byte[BUFFER_SIZE];
        int read;
        while ((read = in.read(buffer)) != -1) {
            stringBuilder.append(new String(buffer, 0, read, "utf-8"));
        }
        return stringBuilder.toString();
    }

}
