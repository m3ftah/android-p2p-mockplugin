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
  androfleetPath = "path/to/your/androfleet/folder"
}
```

## Features

This plugin will add several tasks to the Android Studio environment under mockplugin/primary/ and mockplugin/secondary/

-**initMock** create all the temporary files needed to use the rest of the plugin correctly. It should always be executed first and only one time

-**cleanMock** allows to delete every temporary files created by the plugin. If you want to use the plugin again, run 'initMock' again

-**buildMock** allows you to build the project and add the built apk to your androfleet folder. Should be run again only if the code of appMock is modified

-**launchDocker** will start Docker and launch the nodes

-**launchCalabash** will run the calabash scripts on each respective node and create execution reports

-**reportNodes** will display the content of the reports created by the 'launchCalabash' task


## Compile the plugin yourself

The mockplugin was created using *Intellij IDEA 2017.1.3 Community version*.

Download the source code and open the project with Intellij IDEA.

After building the project, you can find the "uploadArchive" gradle task under the "Tasks/upload/" folder in the gradle sidebar (on the right). Double-click on it will create a repository **'Mock_Plugin/'** in the same repository than where your project is.

You can then use the plugin by following the instructions of *Use the plugin using the release version* above, the plugin repository will be the 'Mock_Plugin/' repository you just created.

**Note:** if you export the project several times you will need to stop the Daemon in Android Studio using ```./gradlew -stop``` in the terminal in order for the copyFiles task to work properly. You will need to do that every time you re-upload the plugin and want to copy the files again otherwise the mock.jar librarie contained in the plugin will not be read properly.