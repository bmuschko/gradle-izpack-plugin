package com.bmuschko.gradle.izpack

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import spock.lang.Requires
import spock.lang.Specification
import spock.lang.TempDir

@Requires({ javaVersion < 14 })
class IzPack4PluginFunctionalTest extends Specification {
    @TempDir
    File temporaryFolder

    File projectDir
    File buildFile
    File settingsFile

    def setup() {
        projectDir = temporaryFolder
        buildFile = new File(temporaryFolder, 'build.gradle')
        settingsFile = new File(temporaryFolder, 'settings.gradle')
    }

    def "can create installer with default settings"() {
        given:
        buildFile << buildFileDefault()
        settingsFile << settingsFile()
        def installerDir = new File(temporaryFolder, 'src/main/izpack')
        installerDir.mkdirs()
        new File(installerDir, 'install.xml') << installationFile()
        new File(temporaryFolder, 'build/assemble/izpack').mkdirs()

        when:
        build('izPackCreateInstaller')

        then:
        new File(projectDir, 'build/distributions/myizpack-1.0-installer.jar').isFile()
    }

    def "can create installer with custom settings"() {
        given:
        buildFile << buildFileDefault()
        buildFile << buildFileCustomSettings()
        settingsFile << settingsFile()
        def installerDir = new File(temporaryFolder, 'installer/izpack')
        installerDir.mkdirs()
        new File(installerDir, 'installer.xml') << installationFile()
        new File(temporaryFolder, 'build/my/izpack').mkdirs()

        when:
        build('izPackCreateInstaller')

        then:
        new File(projectDir, 'build/out/griffon-1.0-installer.jar').isFile()
    }

    private BuildResult build(String... arguments) {
        createAndConfigureGradleRunner(arguments).build()
    }

    private GradleRunner createAndConfigureGradleRunner(String... arguments) {
        GradleRunner.create().withProjectDir(projectDir).withArguments(arguments).withPluginClasspath()
    }


    private String buildFileDefault() {
        buildFileBase() + buildFileIzPackDependency()
    }

    private String buildFileBase() {
        """
            plugins {
                id 'com.bmuschko.izpack'
                id 'java'
            }
            
            version = '1.0'
            
            repositories {
                mavenCentral()
            }
        """
    }

    protected String buildFileIzPackDependency() {
        """
            dependencies {
                izpack 'org.codehaus.izpack:izpack-standalone-compiler:4.3.5'
            }
        """
    }

    private String buildFileCustomSettings() {
        """
            izpack {
                baseDir = file("\$buildDir/my/izpack")
                installFile = file('installer/izpack/installer.xml')
                outputFile = file("\$buildDir/out/griffon-\${version}-installer.jar")
                compression = 'deflate'
                compressionLevel = 9
                appProperties = ['app.group': 'Griffon', 'app.name': 'griffon', 'app.title': 'Griffon',
                                 'app.version': version, 'app.subpath': "Griffon-\$version"]
            }
        """
    }


    private String settingsFile() {
        """
            rootProject.name = 'myizpack'
        """
    }

    protected String installationFile() {
        """
            <installation version="1.0">
                <info>
                    <appname>Super extractor</appname>
                    <appversion>2.1 beta 6</appversion>
                    <appsubpath>myCompany/SExtractor</appsubpath>
                    <url>http://www.superextractor.com/</url>
                    <authors>
                        <author name="John John Doo" email="jjd@jjd-mail.com"/>
                        <author name="El Goyo" email="goyoman@mymail.org"/>
                    </authors>
                    <javaversion>1.2</javaversion>
                </info>
                <locale>
                    <langpack iso3="eng"/>
                </locale>
                <panels>
                    <panel classname="HelloPanel"/>
                </panels>
                <packs>
                    <pack name="Base" required="yes">
                        <description>Test</description>
                    </pack>
                </packs>
            </installation>
        """
    }
}
