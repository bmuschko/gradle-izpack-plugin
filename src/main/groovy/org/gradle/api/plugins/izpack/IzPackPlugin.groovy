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
package org.gradle.api.plugins.izpack

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * <p>A {@link org.gradle.api.Plugin} that provides tasks for packaging, distributing and deploying applications for the
 * Java platform with IzPack.</p>
 *
 * @author Benjamin Muschko
 */
class IzPackPlugin implements Plugin<Project> {
    static final String IZPACK_CONFIGURATION_NAME = 'izpack'

    @Override
    void apply(Project project) {
        project.configurations.add(IZPACK_CONFIGURATION_NAME).setVisible(false).setTransitive(true)
               .setDescription('The IzPack standalone compiler libraries to be used for this project.')

        IzPackPluginConvention izPackConvention = new IzPackPluginConvention()
        project.convention.plugins.cargo = izPackConvention

        configureCreateInstallerTask(project, izPackConvention)
    }

    private void configureCreateInstallerTask(Project project, IzPackPluginConvention izPackConvention) {
        project.tasks.withType(CreateInstallerTask).whenTaskAdded { CreateInstallerTask createInstallerTask ->
            createInstallerTask.conventionMapping.map('classpath') { project.configurations.getByName(IZPACK_CONFIGURATION_NAME).asFileTree }
            createInstallerTask.conventionMapping.map('baseDir') { getBaseDirectory(project, izPackConvention) }
            createInstallerTask.conventionMapping.map('installerType') { getInstallerType(izPackConvention) }
            createInstallerTask.conventionMapping.map('installFile') { getInstallFile(project, izPackConvention) }
            createInstallerTask.conventionMapping.map('outputFile') { getOutputFile(project, izPackConvention) }
            createInstallerTask.conventionMapping.map('compression') { getCompression(izPackConvention) }
            createInstallerTask.conventionMapping.map('compressionLevel') { getCompressionLevel(izPackConvention) }
            createInstallerTask.conventionMapping.map('appProperties') { izPackConvention.appProperties }
        }

        CreateInstallerTask createInstallerTask = project.tasks.add('izPackCreateInstaller', CreateInstallerTask)
        createInstallerTask.description = 'Creates an IzPack-based installer'
        createInstallerTask.group = 'installation'
    }

    private File getBaseDirectory(Project project, IzPackPluginConvention izPackConvention) {
        izPackConvention.baseDir ?: new File(project.buildDir, 'assemble/izpack')
    }

    private String getInstallerType(IzPackPluginConvention izPackConvention) {
        izPackConvention.installerType ?: InstallerType.STANDARD.name
    }

    private File getInstallFile(Project project, IzPackPluginConvention izPackConvention) {
        File installFileDir = new File('src/main/izpack')
        izPackConvention.installFile ?: new File(installFileDir, 'install.xml')
    }

    private File getOutputFile(Project project, IzPackPluginConvention izPackConvention) {
        File outputDir = new File(project.buildDir, 'distributions')
        StringBuilder outputFile = new StringBuilder()
        outputFile <<= project.name

        if(project.version && project.version != 'unspecified') {
            outputFile <<= "-${project.version}"
        }

        outputFile <<= '-installer.jar'
        izPackConvention.outputFile ?: new File(outputDir, outputFile.toString())
    }

    private String getCompression(IzPackPluginConvention izPackConvention) {
        izPackConvention.compression ?: Compression.DEFAULT.name
    }

    private Integer getCompressionLevel(IzPackPluginConvention izPackConvention) {
        izPackConvention.compressionLevel ?: -1
    }
}
