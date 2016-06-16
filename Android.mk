LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional
LOCAL_STATIC_JAVA_LIBRARIES := kuwomusicautosdk nineoldandroids android-support-v4

LOCAL_SRC_FILES := $(call all-subdir-java-files)
#LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_CERTIFICATE := platform
LOCAL_PRIVILEGED_MODULE := true

LOCAL_SDK_VERSION := current
LOCAL_PROGUARD_ENABLED := disabled
LOCAL_PACKAGE_NAME := AutoUI

include $(BUILD_PACKAGE)
##################################################
include $(CLEAR_VARS) 

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := kuwomusicautosdk:libs/kuwomusicautosdk.jar \
	nineoldandroids:libs/nineoldandroids.jar
		
include $(BUILD_MULTI_PREBUILT)