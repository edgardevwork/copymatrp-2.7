cmake_minimum_required(VERSION 3.20)

include_directories(
        ${CMAKE_CURRENT_LIST_DIR}/opus/include
)
find_package(shadowhook REQUIRED CONFIG)
add_subdirectory(opus)
add_subdirectory(samp)
target_compile_options(opus PRIVATE -O3)

include_directories(${CMAKE_CURRENT_LIST_DIR}/openal-soft/include)

# Подавляем скрытие символов, чтобы libGTASA.so видел внутренности
set(CMAKE_CXX_VISIBILITY_PRESET default)
set(CMAKE_C_VISIBILITY_PRESET default)
set(CMAKE_VISIBILITY_INLINES_HIDDEN OFF)

# Подключаем внутренний CMake библиотеки OpenAL
add_subdirectory(openal-soft)

# Переименовываем финальные .so файлы под требования клиента
if(${ANDROID_ABI} STREQUAL "arm64-v8a")
    set_target_properties(OpenAL PROPERTIES OUTPUT_NAME "OpenAL64")
elseif(${ANDROID_ABI} STREQUAL "armeabi-v7a")
    set_target_properties(OpenAL PROPERTIES OUTPUT_NAME "OpenAL32")
endif()
