# Groupo (COP4656 Project 1 & 2)
Recreation of Yik Yak app for COP4656

## About the App
### Libraries & Sources
 * Firebase & Firestore
 * Google Maps API
 * Google Sign-In Authentication
 * [GeoFirestore Android library](https://github.com/imperiumlabs/GeoFirestore-Android/) for geohashing with Firestore
 * [Pull to Refresh interaction](https://guides.codepath.com/android/Implementing-Pull-to-Refresh-Guide#swiperefreshlayout-with-listview) with ListView

## Setup
### Google Sign In
Add your SHA certificate to Firebase by following the instructions below. This is required for Google Sign-in to work on any APKs (Android app executable file) you build on your machine.

You only need to do this once when you setup the project on your machine.

* [Generate a debug SHA certificate](https://developers.google.com/android/guides/client-auth)
* Copy the SHA-1 key
* In the Firebase Console, select our project
* Go to Project Settings > General, then at the bottom under "Project 1" click "Add fingerprint"

![Add fingerprint](https://i.imgur.com/fEbTWWT.png)

### Google Maps
Required for Google Maps API key usage for your APK builds.

* Using the same SHA-1 key, [add it as an item](https://console.cloud.google.com/apis/credentials/key/6f88ff34-ae9b-4266-ab1a-29cf66871642?project=cop4656-proj1&authuser=2&consoleReturnUrl=https:%2F%2Fcloud.google.com%2Fmaps-platform%2F%3Fapis%3Dmaps%26project%3Dcop4656-proj1&consoleUI=CLOUD)

![Add item](https://i.imgur.com/XSOjsH6.png)
