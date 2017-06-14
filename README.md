# android-p2p-mockplugin

## Use the plugin using the release version

To use the plugin, download and unzip the release MockPlugin.zip file. Place it where you want in your workspace. This is the plugin repository.

Necessary changes to use the mockplugin (add them all before building your application):

-Add the following lines to the **top-level build.gradle** in your Android Studio project repository

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
-Add the following lines to the **app-level build.gradle** in your Android Studio project repository

```
dependencies {
  compile files ('libs/mock.jar')
}
```
-To set the environment variables for the plugin, add the following lines to the **top-level build.gradle** in your Android Studio project repository
```
androfleet {
//the following values are example values
  nodes 2
  androidVersion 23
}
```
## Compile the plugin yourself

The mockplugin was created using *Intellij IDEA 2017.1.3 Community version*.

Download the source code and open the project with Intellij IDEA.

After building the project, you can find the "uploadArchive" gradle task under the "Tasks/upload/" folder in the gradle sidebar (on the right). Double-click on it will create a repository **'Mock_Plugin/'** in the same repository than where your project is.

You can then use the plugin by following the instructions of *Use the plugin using the release version* above, the plugin repository will be the 'Mock_Plugin/' repository you just created.

**Note:** if you export the project several times you will need to stop the Daemon in Android Studio using ```./gradlew -stop``` in the terminal in order for the copyFiles task to work properly. You will need to do that every time you re-upload the plugin and want to copy the files again otherwise the mock.jar librarie contained in the plugin will not be read properly.