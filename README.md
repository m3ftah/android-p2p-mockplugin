# androfleet-plugin

## Use the plugin with the release version

To use the plugin, download and unzip the release file **AndrofleetPlugin.zip**. Place it where you want in your workspace.
This is the plugin's repository.

Necessary changes to use the androfleetplugin (add them all before building your application):

-Add the following lines to the **top-level build.gradle** in your Android Studio project repository

```
apply plugin: 'com.inriaspirals.androfleetplugin'

buildscript {
  repositories {
    maven {
      url uri('path/to/the/plugin/release/repository')
    }
  }
  dependencies {
    classpath 'com.inriaspirals.gradle:androfleetplugin:1.0'
  }
}
```

-To set the extension variables for the plugin, add the following lines to the **top-level build.gradle** in your Android Studio project repository
```
androfleet {
  nodes X  //Number of nodes to work with
  androidVersion XX  //The android version to realise the tests on
  androfleetPath = 'path/to/your/androfleet/folder'  //This is the repository containing the calabash scripts
  dataExchangePort = XXXX  //The exchange port for the docker nodes
}
```

### Features

This plugin will add several tasks to the Android Studio environment under **"androfleet/mock/"** and **"androfleet/tests/"**
in the gradle sidebar:

-**initMock** create all the temporary files needed to build the mocked project correctly. It should always be executed
first and only one time

-**cleanMock** allows to delete every temporary files created by the plugin to mock the application. If you want to use
the plugin again, run 'initMock' again

-**buildMock** allows you to build the project and add the built apk to your androfleet folder. Should be run again only
if the code of appMock is modified

-**launchEmulators** will start Docker and launch the nodes

-**launchTests** will run the calabash scripts on each respective node and create execution reports

-**launchMongo** will create a mongo DB inside a docker container to stock the results of the tests

-**requestMongoByFeatures** & **requestMongoByNodes** will print a report including all failing elements of the test, 
one sorted by features and the other one sorted by nodes

-**cleanAndrofleet** will delete every temporary files created by the plugin to launch the tests.


## Compile the plugin yourself

The androfleetplugin was created using *Intellij IDEA 2017.1.3 Community version*.

Download the source code and open the project with Intellij IDEA.

Download the source code of Androfleet available here: https://github.com/m3ftah/androfleet 

In the build.gradle of the plugin you will need to change two lines with local paths values
```
def androfleetdir = "path/to/the/Androfleet/folder/you/downloaded/on/the/github/above"
def sdkpath = "path/to/your/Android/SDK"
```

After building the plugin's project, you can find the "uploadArchive" gradle task under the "Tasks/upload/" folder in 
the gradle sidebar (on the right). Double-click on it will create a repository **"Androfleet_Plugin/"** in the same 
repository than where the plugin's project is.

This "uploadArchive" task will first compile various resources from the Androfleet repository, then copy some files
into "androfleetplugin/src/main/resources" (libraries, python scripts, etc) and finally it will export the plugin as an
archive containing all those resources. The archives will be inside the **"Androfleet_Plugin/"** repository mentionned
above.

You can then use the plugin in the Android project you want to test by following the instructions of **Use the plugin
with the release version** above, the plugin's release repository will be the "Androfleet_Plugin/" repository you just
created.

### Notes

Don't mix-up the required folders, there are three different folders:

-the plugin's release folder, whose absolute path must be declared in the maven enclosure of the build.gradle of the Android 
project you want to test ;

-the folder containing the calabash scripts for Androfleet, whose absolute path must be declared in the androfleet
enclosure of the build.gradle of the Android project you want to test ;

-the folder containing the Androfleet resources, which is used only if you want to build the plugin yourself, and whose
absolute path must be declared inside the build.gradle of the plugin's project.

