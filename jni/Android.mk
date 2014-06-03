LOCAL_PATH := $(call my-dir)	#编译时的目录

include $(CLEAR_VARS)		#清除之前的一些系统变量
LOCAL_MODULE:=uninstall		#编译的目标对象
LOCAL_SRC_FILES:=observer.c	#编译的源文件
LOCAL_C_INCLUDES:= $(LOCAL_PATH)/include
LOCAL_LDLIBS += -L$(SYSROOT)/usr/lib -llog	#增加 log函数对应的log库  liblog.so; libthread_db.a
include $(BUILD_SHARED_LIBRARY)	#指明要编译成动态库, BUILD_STATIC_LIBRARY － 指明要编译成静态库

include $(CLEAR_VARS)
LOCAL_MODULE:=native
LOCAL_SRC_FILES:=native.c
LOCAL_LDLIBS += -llog
include $(BUILD_SHARED_LIBRARY)