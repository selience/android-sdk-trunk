package com.iresearch.cn.android.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @className ParcelUtils
 * @create 2014年4月16日 上午11:08:26
 * @author lilong (dreamxsky@gmail.com)
 * @description Parcel工具类，可用于从Parcel读取或写入特殊类型数据
 */
public class ParcelUtils {

	public static void writeString(String str, Parcel out) {
		if (str != null) {
			out.writeInt(1);
			out.writeString(str);
		} else {
			out.writeInt(0);
		}
	}

	public static String readString(Parcel in) {
		int flag = in.readInt();
		if (flag == 1) {
			return in.readString();
		} else {
			return null;
		}
	}
	
    public static void writeBoolean(boolean b, Parcel out) {
        out.writeInt(b ? 1 : 0);
    }
	
    public static boolean readBoolean(Parcel in) {
        return in.readInt() == 1;
    }

    public static void writeInt(Parcel out, int value) {
		out.writeInt(value);
	}

	public static int readInt(Parcel in) {
		return in.readInt();
	}

	public static void writeLong(Parcel out, long value) {
		out.writeLong(value);
	}

	public static long readLong(Parcel in) {
		return in.readLong();
	}

	public static void writeFloat(Parcel out, float value) {
		out.writeFloat(value);
	}

	public static float readFloat(Parcel in) {
		return in.readFloat();
	}

	public static void writeDouble(Parcel out, double value) {
		out.writeDouble(value);
	}

	public static Double readDouble(Parcel in) {
		return in.readDouble();
	}
    
	@SuppressWarnings("unchecked")
	public static <T extends Parcelable> T readParcelable(Parcel in,
			ClassLoader loader) {
		return (T) in.readParcelable(loader);
	}

	public static void writeParcelable(Parcel out, Parcelable value,
			int parcelableFlags) {
		out.writeParcelable(value, parcelableFlags);
	}
	
    /**
     * Read a HashMap from a Parcel, class of key and value are both String
     * 
     * @param in
     * @return
     */
    public static Map<String, String> readHashMapStringAndString(Parcel in) {
        if (in == null) {
            return null;
        }

        int size = in.readInt();
        if (size == -1) {
            return null;
        }

        Map<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < size; i++) {
            String key = in.readString();
            map.put(key, in.readString());
        }
        return map;
    }

    /**
     * Write a HashMap to a Parcel, class of key and value are both String
     * 
     * @param map
     * @param out
     * @param flags
     */
    public static void writeHashMapStringAndString(Map<String, String> map, Parcel out, int flags) {
        if (map != null) {
            out.writeInt(map.size());
            for (Entry<String, String> entry : map.entrySet()) {
                out.writeString(entry.getKey());
                out.writeString(entry.getValue());
            }
        } else {
            out.writeInt(-1);
        }
    }

    /**
     * Read a HashMap from a Parcel, class of key is String, class of Value can parcelable
     * 
     * @param <V>
     * @param in
     * @param loader
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <V extends Parcelable> Map<String, V> readHashMapStringKey(Parcel in, ClassLoader loader) {
        if (in == null) {
            return null;
        }

        int size = in.readInt();
        if (size == -1) {
            return null;
        }

        Map<String, V> map = new HashMap<String, V>();
        for (int i = 0; i < size; i++) {
            String key = in.readString();
            map.put(key, (V)in.readParcelable(loader));
        }
        return map;
    }

    /**
     * Write a HashMap to a Parcel, class of key is String, class of Value can parcelable
     * 
     * @param map
     * @param out
     * @param flags
     */
    public static <V extends Parcelable> void writeHashMapStringKey(Map<String, V> map, Parcel out, int flags) {
        if (map != null) {
            out.writeInt(map.size());

            for (Entry<String, V> entry : map.entrySet()) {
                out.writeString(entry.getKey());
                out.writeParcelable(entry.getValue(), flags);
            }
        } else {
            out.writeInt(-1);
        }
    }
}
