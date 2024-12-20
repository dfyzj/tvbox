package com.sun.jna.win32;

import com.sun.jna.DefaultTypeMapper;
import com.sun.jna.FromNativeContext;
import com.sun.jna.StringArray;
import com.sun.jna.ToNativeContext;
import com.sun.jna.TypeConverter;
import com.sun.jna.TypeMapper;
import com.sun.jna.WString;
public class W32APITypeMapper extends DefaultTypeMapper {
    public static final TypeMapper ASCII;
    public static final TypeMapper DEFAULT;
    public static final TypeMapper UNICODE;

    static {
        W32APITypeMapper w32APITypeMapper = new W32APITypeMapper(true);
        UNICODE = w32APITypeMapper;
        W32APITypeMapper w32APITypeMapper2 = new W32APITypeMapper(false);
        ASCII = w32APITypeMapper2;
        if (Boolean.getBoolean("w32.ascii")) {
            w32APITypeMapper = w32APITypeMapper2;
        }
        DEFAULT = w32APITypeMapper;
    }

    public W32APITypeMapper(boolean z) {
        if (z) {
            TypeConverter typeConverter = new TypeConverter() {
                @Override
                public Object fromNative(Object obj, FromNativeContext fromNativeContext) {
                    if (obj == null) {
                        return null;
                    }
                    return obj.toString();
                }

                @Override
                public Class<?> nativeType() {
                    return WString.class;
                }

                @Override
                public Object toNative(Object obj, ToNativeContext toNativeContext) {
                    if (obj == null) {
                        return null;
                    }
                    return obj instanceof String[] ? new StringArray((String[]) obj, true) : new WString(obj.toString());
                }
            };
            addTypeConverter(String.class, typeConverter);
            addToNativeConverter(String[].class, typeConverter);
        }
        addTypeConverter(Boolean.class, new TypeConverter() {
            @Override
            public Object fromNative(Object obj, FromNativeContext fromNativeContext) {
                return ((Integer) obj).intValue() != 0 ? Boolean.TRUE : Boolean.FALSE;
            }

            @Override
            public Class<?> nativeType() {
                return Integer.class;
            }

            @Override
            public Object toNative(Object obj, ToNativeContext toNativeContext) {
                return Integer.valueOf(Boolean.TRUE.equals(obj) ? 1 : 0);
            }
        });
    }
}
