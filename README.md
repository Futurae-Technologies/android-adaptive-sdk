## Futurae Adaptive SDK

This is the Futurae adaptive SDK for Android. The Adaptive SDK is a data collection SDK that can be used stand-alone or together with Futurae SDK.

### Installation
Add the maven repository for Github packages and dependency in your `build.gradle`:
```
repositories {
    maven {
        name = "GitHubPackages"
        url = "https://maven.pkg.github.com/Futurae-Technologies/android-adaptive-sdk"
        credentials {
            username = GITHUB_ACTOR
            password = GITHUB_TOKEN
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