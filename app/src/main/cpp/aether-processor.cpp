#include <jni.h>
#include <string>
#include <vector>
#include <android/log.h>

#define LOG_TAG "AetherProcessor"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

extern "C" JNIEXPORT jstring JNICALL
Java_com_aethersight_companion_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string status = "Aether-Sight Native Telemetry Engine Active";
    LOGI("Engine initialized successfully.");
    return env->NewStringUTF(status.c_str());
}

extern "C" JNIEXPORT jfloatArray JNICALL
Java_com_aethersight_companion_BluetoothManager_processSensorPacket(
        JNIEnv* env,
        jobject /* this */,
        jbyteArray rawData) {
    
    jsize length = env->GetArrayLength(rawData);
    jbyte* bytes = env->GetByteArrayElements(rawData, nullptr);

    std::vector<float> metrics = {0.0f, 0.0f, 0.0f};
    if (length > 1) {
        metrics[0] = static_cast<float>(bytes[1]); // Extract Heart Rate payload
    }

    jfloatArray result = env->NewFloatArray(metrics.size());
    env->SetFloatArrayRegion(result, 0, metrics.size(), metrics.data());

    env->ReleaseByteArrayElements(rawData, bytes, JNI_ABORT);
    return result;
}
