[![Build Status](https://api.cirrus-ci.com/github/PolyBooks/sdp_polyBooks.svg)](https://cirrus-ci.com/github/PolyBooks/sdp_polyBooks) [![Maintainability](https://api.codeclimate.com/v1/badges/88d946d75c7ee7b15a2c/maintainability)](https://codeclimate.com/github/PolyBooks/sdp_polyBooks/maintainability) [![Test Coverage](https://api.codeclimate.com/v1/badges/88d946d75c7ee7b15a2c/test_coverage)](https://codeclimate.com/github/PolyBooks/sdp_polyBooks/test_coverage)

# sdp_polyBooks

## Build Variants
There are two build variants and source sets in this project: debug and release.

Debug run on the mocked databases and is used to run the tests, both locally or on the continuous-integration.

Release is the real version for the user, using the real databases, and which could potentially be put on an app store.
However, it release signing keys which are not pushed to the git repository for security concerns.
To have access to it, please contact the developers of this project, which are able to send you a Key.jks and a keystore.properties files. 
After receiving those files you should copy-paste them to the root level of this project (at the same level as the app folder, gradle folder, or .cirrus.yml)
Then you should be able to launch the release version. If you're still facing problems, common troubleshooting on Android Studio include:
- Build -> Clean Project -> Rebuild Project
- File -> Invalidate Caches/Restart

Menu icons made by Freepik from www.flaticon.com