package com.bmuschko.gradle.izpack

class IzPack5PluginFunctionalTest extends IzPack4PluginFunctionalTest {

    @Override
    protected String buildFileIzPackDependency() {
        """
            dependencies {
                izpack 'org.codehaus.izpack:izpack-ant:5.1.3'
            }
        """
    }

    @Override
    protected String installationFile() {
        """
            <izpack:installation version="5.0"
                     xmlns:izpack="http://izpack.org/schema/installation"
                     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                     xsi:schemaLocation="http://izpack.org/schema/installation http://izpack.org/schema/5.0/izpack-installation-5.0.xsd">
                     
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
            </izpack:installation>
        """
    }
}
