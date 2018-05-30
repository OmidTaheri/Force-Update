# Force-Update
[![Release](https://jitpack.io/v/User/Repo.svg)]
(https://jitpack.io/#worldsnas/Force-Update)

Small library for app force update

It downloads and launch installer intent on its own

It supports resume and retries for 100 times in case of failure


## Installation

```groovy
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
dependencies {
	        compile 'com.github.worldsnas:Force-Update:2.1.1'
	}
```

## Getting Started
for performance and other problems core downloader changed to IntentService therefore.

String version_check_url : the url needed to check the version
int current_version : current version code of the app



```java

    ForceUpdate.start(Context, "version check url", current_version);

```
to check and download and show package installer in the background

or
```java

    ForceUpdate.startActivity(activity, "version check url", current_version);

        @Override
            protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                super.onActivityResult(requestCode, resultCode, data);

                if (Constants.REQUEST_CODE_FORCE_UPDATE == requestCode) {
                    if (resultCode == Constants.RESULT_UPTODATE) {
        //uptodate
                    } else{
                    // something failed you can check other result codes
                        finish();
                    }
                }

            }
```

## Server Side

on the server side you have to make sure the version check endpoint is a POST Request and the response returned from it is like the one below

```json
{
"latest_version": 180100001,
"apk_url": "url to download latest version from",
"your_version": 5,
"force_update":1
}
```

which 'force_update' is the key to force your app update or not.
so if force_update is equal to 1 app start force updating and vice versa.


any contribute is appreciated
