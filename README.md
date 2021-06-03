[![Build Status](https://api.cirrus-ci.com/github/PolyBooks/sdp_polyBooks.svg)](https://cirrus-ci.com/github/PolyBooks/sdp_polyBooks) [![Maintainability](https://api.codeclimate.com/v1/badges/88d946d75c7ee7b15a2c/maintainability)](https://codeclimate.com/github/PolyBooks/sdp_polyBooks/maintainability) [![Test Coverage](https://api.codeclimate.com/v1/badges/88d946d75c7ee7b15a2c/test_coverage)](https://codeclimate.com/github/PolyBooks/sdp_polyBooks/test_coverage)

# sdp_polyBooks

## Build Variants
There are two build variants and source sets in this project: debug and release.

### Debug
Debug run on the mocked databases and is used to run the tests, both locally or on the continuous-integration.
To run the database emulator locally, you need to set-up as follows:

For Windows:
- Download the Firebase CLI here: https://firebase.google.com/docs/cli#install-cli-windows
- Put the .exe in the firebase folder to benefit from the parameters set-up.
- Run the .exe, log in and execute:
- firebase emulators:start --only auth,database,firestore &
- Then you can run gradlew connectedCheck or individual tests.

For Linux:
From the root of the project run the following commands:
```
$ cd firebase
$ curl -sL https://firebase.tools/bin/linux/latest -o firebase-tools
$ chmod u+x firebase-tools
$ ./firebase-tools emulators:start --only auth,database,firestore &
```

For MacOS:
From the root of the project run the following commands:
```
$ cd firebase
$ curl -sL https://firebase.tools/bin/macos/latest -o firebase-tools
$ chmod u+x firebase-tools
$ ./firebase-tools emulators:start --only auth,database,firestore &
```

### Release
Release is the real version for the user, using the real databases, and which could potentially be put on an app store.

However, release requires signing keys which are not pushed to the git repository for security concerns.
To have access to them, please contact the developers of this project, which are able to send you a Key.jks and a keystore.properties files. 
After receiving those files you should copy-paste them to the root level of this project (at the same level as the app folder, gradle folder, or .cirrus.yml)
Then you should be able to launch the release version. 

If you're still facing problems with build variants, common troubleshooting on Android Studio include:
- Build -> Clean Project -> Rebuild Project (usually after changing the build variant setting)
- File -> Invalidate Caches/Restart (usually after changing the build variant setting)

Menu icons made by Freepik from www.flaticon.com
