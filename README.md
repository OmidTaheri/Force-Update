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
	        compile 'com.github.worldsnas:Force-Update:2.0.0'
	}
```

## Getting Started
for performance and other problems core downloader changed to IntentService therefore no nned for context any more
to run the service you need to pass a bundle to to the firing intent containing two fields:

String version_check_url : the url needed to check the version
int current_version : current version code of the app



```java

Intent firstCheckDownload = new Intent(Intent.ACTION_SYNC, null, MainActivity.this, ForceUpdateService.class);
                Bundle bundle = new Bundle();

                bundle.putString(VERSION_CHECK_URL, "http://example.com/version_check");
                bundle.putInt(CURRENT_VERSION, 1);

                firstCheckDownload.putExtras(bundle);
                startService(firstCheckDownload);

```

## Server Side

on the server side you have to make sure the version check endpoint is a POST Request and the response returned from it is like the one below

```json
{
"uptodate": 1,
"latest_version": 180100001,
"apk_url": "url to download latest version from",
"your_version": 5
}
```

which 'uptodate' is the key to force your app update or not


any contribute is appreciated
