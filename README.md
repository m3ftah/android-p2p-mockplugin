# android-p2p-mockplugin

## Use the plugin using the release version

To use the plugin, download and unzip the release MockPlugin.zip file. Place it where you want in your workspace. This is the plugin repository.

Necessary changes to use the mockplugin:

Add the following lines to the **top-level build.gradle** in your Android Studio project repository

```
apply plugin: 'com.inriaspirals.mockplugin'

buildscript {
  repositories {
    maven {
      url uri('path/to/the/plugin/repository')
    }
  }
  dependencies {
    classpath 'com.inriaspirals.gradle:mockplugin:1.0'
  }
}
```

## Compile the plugin yourself

The mockplugin was created using *Intellij IDEA 2017.1.3 Community version*.

Download the source code and open the project with Intellij IDEA.

After building the project, you can find the "uploadArchive" gradle task under the "Tasks/upload/" folder in the gradle sidebar (on the right). Double-click on it will create a repository **'MockPlugin/'** in the same repository than where your project is.

You can then use the plugin by following the instructions of *Use the plugin using the release version* above, the plugin repository will be the 'MockPlugin/' repository you just created. 


## Content

The plugin will add tasks to Android Studio.

- **copyFiles** will copy all the app/ repository (except the build/ subfolder) into an appMock/ repository at the root repository of your Android Studio project. Il will also implement the mock.jar library inside appMock/libs/

- **editFiles** will first call 'copyFiles', then it will change the imports of android.net.wifi.p2p into imports of mock.net.wifi.p2p inside every .java files. It will also edit the settings.gradle file to allow Android Studio to compile the appMock project