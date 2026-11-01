
HERMETICA app

Initially generated with Google GEMINI AI Studio then once daily free limit hit, continued with Claude Opus 5.5


NO GEMINI features were used, therefore there is no need for an API KEY to be set.


$ gradle assembleDebug
Starting a Gradle Daemon (subsequent builds will be faster)
Calculating task graph as no cached configuration is available for tasks: assembleDebug

> Task :app:stripDebugDebugSymbols
Unable to strip the following libraries, packaging them as they are: libandroidx.graphics.path.so, libdatastore_shared_counter.so. Run with --info option to learn more.

BUILD SUCCESSFUL in 32s
39 actionable tasks: 15 executed, 24 from cache
Configuration cache entry stored.

connect android device. check it with:
$ adb devices

when happy it is recognised, install with:

$ adb install -r app/build/outputs/apk/debug/app-debug.apk

