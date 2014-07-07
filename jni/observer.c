
#include <string.h>
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <android/log.h>
#include <unistd.h>
#include <sys/inotify.h>
#include <sys/prctl.h>
#include <fcntl.h>
#include <errno.h>
#include <sys/file.h>
#include <sys/types.h>
#include <sys/stat.h>

/* 宏定义begin */
//清0宏
#define MEM_ZERO(pDest, destSize) memset(pDest, 0, destSize)
//LOG宏定义
#define LOG_INFO(tag, msg) __android_log_write(ANDROID_LOG_INFO, tag, msg)
#define LOG_DEBUG(tag, msg) __android_log_write(ANDROID_LOG_DEBUG, tag, msg)
#define LOG_WARN(tag, msg) __android_log_write(ANDROID_LOG_WARN, tag, msg)
#define LOG_ERROR(tag, msg) __android_log_write(ANDROID_LOG_ERROR, tag, msg)

/* 内部全局变量begin */
static char c_TAG[] = "_uninstall_statistics";
static jboolean b_IS_COPY = JNI_TRUE;

/**
 * 回调到主线程
 */
jstring callbackFromJNI(JNIEnv* env, jclass cls) {
	jstring tag = (*env)->NewStringUTF(env, c_TAG);

	jmethodID gJinMethod = (*env)->GetStaticMethodID(env, cls, "uninstallCallback", "()V");
	if (gJinMethod == 0 || gJinMethod == NULL) {
		return (*env)->NewStringUTF(env, "-2");
	}
	(*env)->CallStaticVoidMethod(env, cls, gJinMethod);

	return "jni callback to main thread!";
}
/**
 * JNI调用
 * lockfilepath 	进程锁定文件路径
 * observerpath 	监控的文件路径
 * feedbackurl 		反馈url
 * version android	系统版本号
 */
jstring Java_com_iresearch_android_uninstall_UninstallObserver_startObserver(JNIEnv* env,
		jobject thiz, jstring lockfilepath,jstring observerpath, jstring feedbackurl, jint version) {
	jstring tag = (*env)->NewStringUTF(env, c_TAG);
	//初始化log
	LOG_DEBUG((*env)->GetStringUTFChars(env, tag, &b_IS_COPY),
			(*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, "init OK"), &b_IS_COPY));
	int retId = checkexit((*env)->GetStringUTFChars(env, lockfilepath, &b_IS_COPY));
	if (retId == -1) {
		LOG_ERROR((*env)->GetStringUTFChars(env, tag, &b_IS_COPY),
				(*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, "retId == -1"), &b_IS_COPY));
	} else if (retId == -2) {
		LOG_ERROR((*env)->GetStringUTFChars(env, tag, &b_IS_COPY),
				(*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, "retId == -2"), &b_IS_COPY));
	} else if (retId == -3) {
		LOG_ERROR((*env)->GetStringUTFChars(env, tag, &b_IS_COPY),
				(*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, "retId == -3"), &b_IS_COPY));
	} else if (retId == 0) {
		LOG_ERROR((*env)->GetStringUTFChars(env, tag, &b_IS_COPY),
				(*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, "retId == 0"), &b_IS_COPY));
		//fork子进程，以执行轮询任务
		pid_t pid = fork();
		if (pid < 0) {
			//出错log
			LOG_ERROR((*env)->GetStringUTFChars(env, tag, &b_IS_COPY),
					(*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, "fork failed !!!"), &b_IS_COPY));
		} else if (pid == 0) {
			prctl(PR_SET_NAME, "father", NULL, NULL, NULL);
			//子进程注册目录监听器
			int fileDescriptor = inotify_init();
			if (fileDescriptor < 0) {
				LOG_DEBUG((*env)->GetStringUTFChars(env, tag, &b_IS_COPY),
						(*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, "inotify_init failed !!!"), &b_IS_COPY));
				exit(1);
			}
			int watchDescriptor;
			watchDescriptor = inotify_add_watch(fileDescriptor,
					(*env)->GetStringUTFChars(env, observerpath, NULL), IN_DELETE);
			if (watchDescriptor < 0) {
				LOG_DEBUG((*env)->GetStringUTFChars(env, tag, &b_IS_COPY),
						(*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, "inotify_add_watch failed !!!"), &b_IS_COPY));
				exit(1);
			}
			//分配缓存，以便读取event，缓存大小=一个struct inotify_event的大小，这样一次处理一个event
			void *p_buf = malloc(sizeof(struct inotify_event));
			if (p_buf == NULL) {
				LOG_DEBUG((*env)->GetStringUTFChars(env, tag, &b_IS_COPY),
						(*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, "malloc failed !!!"), &b_IS_COPY));
				exit(1);
			}
			//开始监听
			LOG_DEBUG((*env)->GetStringUTFChars(env, tag, &b_IS_COPY),
					(*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, "start observer"), &b_IS_COPY));
			//read会阻塞进程，
			size_t readBytes = read(fileDescriptor, p_buf,
					sizeof(struct inotify_event));
			//走到这里说明收到目录被删除的事件，注销监听器
			free(p_buf);
			inotify_rm_watch(fileDescriptor, IN_DELETE);
			//目录不存在log
			LOG_DEBUG((*env)->GetStringUTFChars(env, tag, &b_IS_COPY),
					(*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, "uninstalled"), &b_IS_COPY));

			//回调到主线程
			callbackFromJNI(env, thiz);

			if (version >= 17) {
				//4.2以上的系统由于用户权限管理更严格，需要加上 --user 0
				execlp("am", "am", "start", "--user", "0", "-a",
						"android.intent.action.VIEW", "-d",
						(*env)->GetStringUTFChars(env, feedbackurl, NULL),
						(char *) NULL);
			} else {
				execlp("am", "am", "start", "-a", "android.intent.action.VIEW",
						"-d", (*env)->GetStringUTFChars(env, feedbackurl, NULL),
						(char *) NULL);
			}
			//扩展：可以执行其他shell命令，am(即activity manager)，可以打开某程序、服务，broadcast intent，等等
		} else {
			//父进程直接退出，使子进程被init进程领养，以避免子进程僵死
		}
	} else {
		LOG_ERROR((*env)->GetStringUTFChars(env, tag, &b_IS_COPY),
				(*env)->GetStringUTFChars(env, (*env)->NewStringUTF(env, "retId == else"), &b_IS_COPY));
	}
	return (*env)->NewStringUTF(env, "hello, from native jnicall");
}
/**
 * 检查文件是否被锁定
 */
int checkexit(char* pfile) {
	if (pfile == NULL)
		return -1;
	int lockfd = open(pfile, O_RDWR);
	if (lockfd == -1)
		return -2;
	int iret = flock(lockfd, LOCK_EX | LOCK_NB);
	if (iret == -1)
		return -3;
	return 0;
}

