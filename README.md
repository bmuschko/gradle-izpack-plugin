# Gradle IzPack plugin

![IzPack Logo](http://docs.codehaus.org/download/attachments/62914563/IZPACK)

The plugin provides a one-stop solution for packaging, distributing and deploying applications for the Java platform
using [IzPack](http://izpack.org/).

## Usage

To use the IzPack plugin, include in your build script:

    apply plugin: 'izpack'

The plugin JAR needs to be defined in the classpath of your build script. It is directly available on
[Maven Central](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22org.gradle.api.plugins%22%20AND%20a%3A%22gradle-izpack-plugin%22).
Alternatively, you can download it from GitHub and deploy it to your local repository. The following code snippet shows an
example on how to retrieve it from Maven Central:

    buildscript {
        repositories {
            mavenCentral()
        }

        dependencies {
            classpath 'org.gradle.api.plugins:gradle-izpack-plugin:0.2.2'
        }
    }

To define the IzPack standalone compiler dependency please use the `izpack` configuration name in your `dependencies` closure.

    dependencies {
        izpack 'org.codehaus.izpack:izpack-standalone-compiler:4.3.4'
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