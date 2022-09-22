# SpaceKit-Android

This repository contains the Android `SpaceKit-Sample` project, and hosts the Gradle/Maven 
`com.dentreality.spacekit:android` artifact which contains the SpaceKit SDK.

The iOS repository can be found [here](https://github.com/DentReality/SpaceKit-iOS).

## Requirements

* Minimum supported Android version: 24/7.0/Nougat
* Device must include a backwards-facing camera
* Must be an [ARCore Supported Device](https://developers.google.com/ar/devices) 

## Installation

###Access to the Gradle package

Access to the `com.dentreality.spacekit:android` library via Gradle requires the creation of a Github Personal Access Token (PAT) with the appropriate scopes specified. To create one, go to the [Personal Access Tokens](https://github.com/settings/tokens) area of your GitHub user settings, click "Generate new token" and create a token that includes the `read:packages` and `read:org` scopes. Once you have created your token, save the key and add it to your `~/.gradle/gradle.properties` file along with your github username:

```
dentMavenUser=[your github username]
dentMavenKey=[your PAT key]
```

###Google Maps dependency

The SpaceKit SDK includes a dependency on the Google [Maps SDK for Android](https://developers.google.com/maps/documentation/android-sdk/overview), which requires the use of an API key. For convenience, we have included a debug key valid for this sample app but when you integrate the SpaceKit SDK into your own app, you will need to create your own key. Details on how to do this can be found [here](https://developers.google.com/maps/documentation/android-sdk/get-api-key).

###Customising the sample app with your own data

The sample project includes dummy data for an imaginary location. If you would like to view your own data you will need two files:

1. The zip file containing your HMDF data 
2. The JSON file containing your sample product data

Both of these files should be placed in `app/src/main/assets`. You will then need to reference these file names in the [DataLoaderFragment](https://github.com/DentReality/SpaceKit-Android/blob/main/app/src/main/java/com/dentreality/spacekit/sample/DataLoaderFragment.kt) (in the `SpaceKit.initialise` call) and [ProductDatabase](https://github.com/DentReality/SpaceKit-Android/blob/main/app/src/main/java/com/dentreality/spacekit/sample/ProductDatabase.kt) (as `sampleFileName`) respectively.
