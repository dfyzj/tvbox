package com.sun.jna;

import java.lang.reflect.Method;
public interface FunctionMapper {
    String getFunctionName(NativeLibrary nativeLibrary, Method method);
}
