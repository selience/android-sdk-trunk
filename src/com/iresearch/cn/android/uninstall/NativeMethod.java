package com.iresearch.cn.android.uninstall;

import com.iresearch.cn.android.log.XLog;

/**
 * @file NativeMethod.java
 * @create 2013-5-31 上午11:17:49
 * @author lilong@qiyi.com
 * @description TODO Java调用C进行数据传递
 * http://blog.csdn.net/furongkang/article/details/6857610
 */
public class NativeMethod {

    /*
        Android中使用JNI七个步骤:
    
        1. 创建一个android工程；
        2. JAVA代码中写声明native方法；
        3. 用javah工具生成头文件，进入bin\classes目录，javah -classpath . -jni 包.类名；
        4. 创建jni目录,引入头文件,根据头文件实现c代码；
        5. 编写Android.mk文件；
        6. Ndk编译生成动态库；参考：http://www.cnblogs.com/yaozhongxiao/archive/2012/03/06/2382225.html
        7. Java代码load动态库，调用native代码； 
    */
    
	static {
		// 静态代码块，先于构造函数执行,在此会对C/C++代码的库即：*.so文件进行加载
		// 注意最终生成的动态库文件后缀名为.so, 加载时候不要加上后缀名，而在Linux开发环境中加载动态库时候需要加上后缀,切记这点
		System.loadLibrary("native");
	}
	
	// 声明native函数，此函数的实现是在C/C++代码中通过动态库进行调用；
	// 把一个java中的字符串传递给c语言,C语言处理下字符串,处理完毕返回给java；
	public native String sayHi(String s);
	
	// 两个java中的int传递c语言 ,c语言处理这个相加的逻辑,把相加的结果返回给java
    public native int add(int x ,int y);
    
    // 把一个java中int类型的数组传递给C语言,C语言里面把数组的每一个元素的值 都增加5, 然后在把处理完毕的数组，返回给java
    public native int[] intMethod(int[] iNum); 
    
    // C调用java中的静态方法print
    public native void callPrint();
    
    // C调用java中的实例方法calculate
    public native void callMethod();
    
    // 调用方法
    public static void print(String s) {
        XLog.i("C调用Java中的静态方法:" + s);
    }

    // 调用方法
    public void calculate(int x, int y) {
        XLog.i("C调用Java中的实例方法:" + x+"x"+y+"="+(x*y));
    } 
    
}
