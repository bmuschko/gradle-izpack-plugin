# Gradle IzPack plugin [![Build Status](https://travis-ci.org/bmuschko/gradle-izpack-plugin.svg?branch=master)](https://travis-ci.org/bmuschko/gradle-izpack-plugin)

![IzPack Logo](http://izpack.org/img-izpack/logo-medium.png)

<table border=1>
    <tr>
        <td>
            Over the past couple of years this plugin has seen many releases. Thanks to everyone involved! 
            Unfortunately, I don't have much time to contribute anymore. In practice this means far less activity, 
            responsiveness on issues and new releases from my end.
        </td>
    </tr>
    <tr>
        <td>
            I am 
            <a href="https://discuss.gradle.org/t/looking-for-new-owners-for-gradle-plugins/9735">actively looking for contributors</a> 
            willing to take on maintenance and implementation of the project. If you are interested and would love to see this 
            plugin continue to thrive, shoot me a <a href="mailto:benjamin.muschko@gmail.com">mail</a>.
        </td>
    </tr>
</table>

The plugin provides a one-stop solution for packaging, distributing and deploying applications for the Java platform
using [IzPack](http://izpack.org/).

## Usage

To use the IzPack plugin, include in your build script:

    plugins {
        id 'com.bmuschko.izpack'
    }

The plugin JAR needs to be defined in the classpath of your build script. It is directly available on
[Bintray](https://bintray.com/bmuschko/gradle-plugins/com.bmuschko%3Agradle-izpack-plugin).
Alternatively, you can download it from GitHub and deploy it to your local repository. The following code snippet shows an example on how to retrieve it from Bintray:

    buildscript {
        repositories {
            jcenter()
        }

        dependencies {
            classpath 'com.bmuschko:gradle-izpack-plugin:3.1'
        }
    }

To define the IzPack standalone compiler dependency please use the `izpack` configuration name in your `dependencies` closure.

For IzPack v5

    dependencies {
        izpack 'org.codehaus.izpack:izpack-ant:5.1.3'
    }

or for IzPack v4

    dependencies {
        izpack 'org.codehaus.izpack:izpack-standalone-compiler:4.3.5'
    }

## Tasks

The IzPack plugin defines the following tasks:

* `izPackCreateInstaller`: Creates an IzPack-based installer.

## Convention properties

The IzPack plugin defines the following convention properties in the `izpack` closure:

* `baseDir`: The base directory of compilation process (defaults to `build/assemble/izpack`).
* `installerType`: The installer type (defaults to `standard`). You can select between `standard` and `web`.
* `installFile`: The location of the [IzPack installation file](http://izpack.org/documentation/installation-files.html) (defaults to `src/main/izpack/install.xml`).
* `outputFile`: The installer output directory and filename (defaults to `build/distributions/<projectname>-<version>-installer.jar`).
* `compression`: The compression of the installation (defaults to `default`). You can select between `default`, `deflate` and `raw`.
* `compressionLevel`: The compression level of the installation (defaults to -1 for no compression). Valid values are -1 to 9.
* `appProperties`: The `Map` of application properties to be used for the compilation process (defaults to empty `Map`).

### Example

    izpack {
        baseDir = file("$buildDir/assemble/izpack")
        installFile = file('installer/izpack/installer.xml')
        outputFile = file("$buildDir/distributions/griffon-${version}-installer.jar")
        compression = 'deflate'
        compressionLevel = 9
        appProperties = ['app.group': 'Griffon', 'app.name': 'griffon', 'app.title': 'Griffon',
                         'app.version': version, 'app.subpath': "Griffon-$version"]
    }