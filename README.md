# Force-Update
[![Release](https://jitpack.io/v/User/Repo.svg)]
(https://jitpack.io/worldsnas/Force-Update)

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
	        compile 'com.github.worldsnas:Force-Update:1.0.0'
	}
```

## Getting Started
its only one line to use it

only make sure you are providing the application context to the constructor to avoid 
any leaks.

```java
new ForceUpdate(versionCode, "version check url", getApplicationContext()).run();
```

##Server Side

on the server side you have to make sure the response returned from the check version url is like the one below

```json
{
"uptodate": 1,
"latest_version": 180100001,
"apk_url": "url to download latest version from",
"your_version": 5
}
```


any contribute is appreciated
