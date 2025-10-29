/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bmuschko.gradle.izpack

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * <p>A {@link org.gradle.api.Plugin} that provides tasks for packaging, distributing and deploying applications for the
 * Java platform with IzPack.</p>
 */
class IzPackPlugin implements Plugin<Project> {
    public static final String IZPACK_CONFIGURATION_NAME = 'izpack'
    public static final String IZPACK_EXTENSION_NAME = 'izpack'

    @Override
    void apply(Project project) {
        project.getLogger().warn("You are using the legacy plugin id. Please use the 'org.izpack' plugin id instead.")
        project.configurations.create(IZPACK_CONFIGURATION_NAME).setVisible(false).setTransitive(true)
               .setDescription('The IzPack standalone compiler libraries to be used for this project.')
        IzPackPluginExtension izPackExtension = project.extensions.create(IZPACK_EXTENSION_NAME, IzPackPluginExtension)
        configureCreateInstallerTask(project, izPackExtension)
    }

    private void configureCreateInstallerTask(Project project, IzPackPluginExtension izPackExtension) {
        project.tasks.withType(CreateInstallerTask).whenTaskAdded { CreateInstallerTask createInstallerTask ->
            createInstallerTask.conventionMapping.map('classpath') { project.configurations.getByName(IZPACK_CONFIGURATION_NAME).asFileTree }
            createInstallerTask.conventionMapping.map('baseDir') { getBaseDirectory(project, izPackExtension) }
            createInstallerTask.conventionMapping.map('installerType') { getInstallerType(izPackExtension) }
            createInstallerTask.conventionMapping.map('installFile') { getInstallFile(project, izPackExtension) }
            createInstallerTask.conventionMapping.map('outputFile') { getOutputFile(project, izPackExtension) }
            createInstallerTask.conventionMapping.map('compression') { getCompression(izPackExtension) }
            createInstallerTask.conventionMapping.map('compressionLevel') { getCompressionLevel(izPackExtension) }
            createInstallerTask.conventionMapping.map('appProperties') { izPackExtension.appProperties }
        }

        CreateInstallerTask createInstallerTask = project.tasks.create('izPackCreateInstaller', CreateInstallerTask)
        createInstallerTask.description = 'Creates an IzPack-based installer'
        createInstallerTask.group = 'installation'
    }

    private File getBaseDirectory(Project project, IzPackPluginExtension izPackExtension) {
        izPackExtension.baseDir ?: new File(project.buildDir, 'assemble/izpack')
    }

    private String getInstallerType(IzPackPluginExtension izPackExtension) {
        izPackExtension.installerType ?: InstallerType.STANDARD.name
    }

    private File getInstallFile(Project project, IzPackPluginExtension izPackExtension) {
        File installFileDir = new File(project.projectDir, 'src/main/izpack')
        izPackExtension.installFile ?: new File(installFileDir, 'install.xml')
    }

    private File getOutputFile(Project project, IzPackPluginExtension izPackExtension) {
        File outputDir = new File(project.buildDir, 'distributions')
        StringBuilder outputFile = new StringBuilder()
        outputFile <<= project.name

        if(project.version && project.version != 'unspecified') {
            outputFile <<= "-${project.version}"
        }

        outputFile <<= '-installer.jar'
        izPackExtension.outputFile ?: new File(outputDir, outputFile.toString())
    }

    private String getCompression(IzPackPluginExtension izPackExtension) {
        izPackExtension.compression ?: Compression.DEFAULT.name
    }

    private Integer getCompressionLevel(IzPackPluginExtension izPackExtension) {
        izPackExtension.compressionLevel ?: -1
    }
}
