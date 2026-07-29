APP_ABI := armeabi-v7a arm64-v8a
APP_PLATFORM := android-21
APP_MODULES := edgar
APP_STL := c++_static
LOCAL_LDFLAGS += "-Wl,-z,max-page-size=16384"