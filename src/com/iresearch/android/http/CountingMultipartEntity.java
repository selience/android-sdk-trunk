/**
 * 
 */

package com.iresearch.android.http;

import java.io.IOException;
import java.io.OutputStream;
import java.io.FilterOutputStream;
import com.iresearch.android.http.multipart.MultipartEntity;

public class CountingMultipartEntity extends MultipartEntity {

    private final ProgressListener listener;

    public CountingMultipartEntity(final ProgressListener listener) {
        super();
        this.listener = listener;
    }

    public CountingMultipartEntity(final String boundary, final ProgressListener listener) {
        super(boundary);
        this.listener = listener;
    }

    @Override
    public void writeTo(final OutputStream outstream) throws IOException {
        super.writeTo(new CountingOutputStream(outstream, this.listener));
    }

    public static class CountingOutputStream extends FilterOutputStream {

        private final ProgressListener listener;

        private long transferred;

        public CountingOutputStream(final OutputStream out, final ProgressListener listener) {
            super(out);
            this.listener = listener;
            this.transferred = 0;
        }

        public void write(byte[] b, int off, int len) throws IOException {
            out.write(b, off, len);
            this.transferred += len;
            this.listener.transferred(this.transferred);
        }

        public void write(int b) throws IOException {
            out.write(b);
            this.transferred++;
            this.listener.transferred(this.transferred);
        }
    }

    public static interface ProgressListener {

        void transferred(long num);
    }
}
