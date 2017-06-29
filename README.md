# androfleet-plugin

## Use the plugin with the release version

To use the plugin, download and unzip the release AndrofleetPlugin.zip file. Place it where you want in your workspace. This is the plugin repository.

Necessary changes to use the androfleetplugin (add them all before building your application):

-Add the following lines to the **top-level build.gradle** in your Android Studio project repository

```
apply plugin: 'com.inriaspirals.androfleetplugin'

buildscript {
  repositories {
    maven {
      url uri('path/to/the/plugin/repository')
    }
  }
  dependencies {
    classpath 'com.inriaspirals.gradle:androfleetplugin:1.0'
  }
}
```

-To set the environment variables for the plugin, add the following lines to the **top-level build.gradle** in your Android Studio project repository
```
androfleet {
//the following values are example values
  nodes 2
  androidVersion 23
  androfleetPath = 'path/to/your/androfleet/folder'
  dataExchangePort = 'XXX.XX.XX.'
}
```

## Features

This plugin will add several tasks to the Android Studio environment under mockplugin/primary/ and mockplugin/secondary/

-**initMock** create all the temporary files needed to build the mocked project correctly. It should always be executed first and only one time

-**cleanMock** allows to delete every temporary files created by the plugin to mock the application. If you want to use the plugin again, run 'initMock' again

-**buildMock** allows you to build the project and add the built apk to your androfleet folder. Should be run again only if the code of appMock is modified

-**launchEmulators** will start Docker and launch the nodes

-**launchTests** will run the calabash scripts on each respective node and create execution reports

-**printTestReport** will display the content of the reports created by the 'launchTests' task

-**cleanAndrofleet** will delete every temporary files created by the plugin to launch the tests.


## Compile the plugin yourself

The androfleetplugin was created using *Intellij IDEA 2017.1.3 Community version*.

Download the source code and open the project with Intellij IDEA.

After building the project, you can find the "uploadArchive" gradle task under the "Tasks/upload/" folder in the gradle sidebar (on the right). Double-click on it will create a repository **'Androfleet_Plugin/'** in the same repository than where your project is.

You can then use the plugin by following the instructions of *Use the plugin with the release version* above, the plugin repository will be the 'Androfleet_Plugin/' repository you just created.

**Note:** if you export the project several times you will need to stop the Daemon in Android Studio using ```./gradlew -stop``` in the terminal in order for any task copying resource files to work properly. You will need to do that every time you re-upload the plugin and want to use those tasks again.
