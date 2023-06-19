## Futurae Adaptive SDK

This is the Futurae adaptive SDK for Android. The Adaptive SDK is a data collection SDK that can be used stand-alone or together with Futurae SDK.

### Installation
In your application `build.gradle` file add:
```
repositories {
    maven {
        url "https://artifactory.futurae.com/artifactory/futurae-mobile"
        credentials {
            username = "anonymous"
            password = ""
        }
    }
}
...
dependencies {
    implementation 'com.futurae.sdk:adaptive:x.x.x'
}
```
### User guide

For usage instructions together with [Futurae SDK](https://github.com/Futurae-Technologies/android-sdk) please refer to the [Futurae adaptive SDK guide](https://www.futurae.com/docs/guide/futurae-sdks/mobile-sdk/).