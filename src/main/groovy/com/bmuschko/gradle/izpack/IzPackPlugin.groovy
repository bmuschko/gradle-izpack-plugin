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
        project.tasks.register('izPackCreateInstaller', CreateInstallerTask) {
            description = 'Creates an IzPack-based installer'
            group = 'installation'

            classpath.from(project.configurations.getByName(IZPACK_CONFIGURATION_NAME))
            baseDir.convention(izPackExtension.baseDir.orElse(project.layout.buildDirectory.dir('assemble/izpack')))
            installerType.convention(izPackExtension.installerType.orElse(InstallerType.STANDARD.name))
            installFile.convention(izPackExtension.installFile.orElse(project.layout.projectDirectory.file('src/main/izpack/install.xml')))
            outputFile.convention(izPackExtension.outputFile.orElse(
                project.layout.buildDirectory.file(project.provider {
                    def name = project.name
                    def version = project.version
                    def fileName = version && version != 'unspecified' ? "${name}-${version}-installer.jar" : "${name}-installer.jar"
                    "distributions/${fileName}"
                })
            ))
            compression.convention(izPackExtension.compression.orElse(Compression.DEFAULT.name))
            compressionLevel.convention(izPackExtension.compressionLevel.orElse(-1))
            appProperties.convention(izPackExtension.appProperties)
        }
    }
}
