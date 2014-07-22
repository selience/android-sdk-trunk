
package com.iresearch.android.local.serialization;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import com.iresearch.android.local.util.Util;

public class ObjectSerializer<T> implements Serializer<T> {

    @Override
    @SuppressWarnings("unchecked")
    public T read(InputStream in) {
        ObjectInputStream input = null;
        try {
            input = new ObjectInputStream(new BufferedInputStream(in));
            return (T)input.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Util.closeSilently(input);
            Util.closeSilently(in);
        }
        return null;
    }

    @Override
    public boolean write(OutputStream out, T value) {
        ObjectOutputStream output = null;
        try {
            output = new ObjectOutputStream(new BufferedOutputStream(out));
            output.writeObject(value);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Util.closeSilently(output);
            Util.closeSilently(out);
        }
        return false;
    }

}
