package com.bmuschko.gradle.izpack

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class IzPackPluginFunctionalTest extends Specification {
    @Rule TemporaryFolder temporaryFolder = new TemporaryFolder()

    File projectDir
    File buildFile
    File settingsFile

    def setup() {
        projectDir = temporaryFolder.root
        buildFile = temporaryFolder.newFile('build.gradle')
        settingsFile = temporaryFolder.newFile('settings.gradle')
        buildFile << """
            plugins {
                id 'com.bmuschko.izpack'
                id 'java'
            }
            
            version = '1.0'
            
            repositories {
                mavenCentral()
            }
            
            dependencies {
                izpack 'org.codehaus.izpack:izpack-standalone-compiler:4.3.4'
            }
        """
        settingsFile << """
            rootProject.name = 'myizpack'
        """
    }

    def "can create installer with default settings"() {
        given:
        def installerDir = temporaryFolder.newFolder('src', 'main', 'izpack')
        new File(installerDir, 'install.xml') << installationFile()
        temporaryFolder.newFolder('build', 'assemble', 'izpack')

        when:
        build('izPackCreateInstaller')

        then:
        new File(projectDir, 'build/distributions/myizpack-1.0-installer.jar').isFile()
    }

    def "can create installer with custom settings"() {
        given:
        buildFile << """
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
        def installerDir = temporaryFolder.newFolder('installer', 'izpack')
        new File(installerDir, 'installer.xml') << installationFile()
        temporaryFolder.newFolder('build', 'my', 'izpack')

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

    static String installationFile() {
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
