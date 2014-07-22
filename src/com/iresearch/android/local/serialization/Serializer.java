
package com.iresearch.android.local.serialization;

import java.io.InputStream;
import java.io.OutputStream;

public interface Serializer<T> {
    T read(InputStream in);

    boolean write(OutputStream out, T value);
}
