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

import org.gradle.api.DefaultTask
import org.gradle.api.InvalidUserDataException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

/**
 * IzPack compilation task.
 */
@CacheableTask
abstract class CreateInstallerTask extends DefaultTask {
    @Classpath
    abstract ConfigurableFileCollection getClasspath()

    @InputDirectory
    @Optional
    @PathSensitive(PathSensitivity.RELATIVE)
    abstract DirectoryProperty getBaseDir()

    @Input
    @Optional
    abstract Property<String> getInstallerType()

    @InputFile
    @PathSensitive(PathSensitivity.RELATIVE)
    abstract RegularFileProperty getInstallFile()

    @OutputFile
    @Optional
    abstract RegularFileProperty getOutputFile()

    @Input
    @Optional
    abstract Property<String> getCompression()

    @Input
    @Optional
    abstract Property<Integer> getCompressionLevel()

    @Input
    @Optional
    abstract MapProperty<String, String> getAppProperties()

    @TaskAction
    void start() {
        validateConfiguration()
        compile()
    }

    void validateConfiguration() {
        String type = getInstallerType().get()
        if(type && !InstallerType.getInstallerTypeForName(type)) {
            throw new InvalidUserDataException("Unsupported installer type: '${type}'. Please pick a valid one: ${InstallerType.getNames()}")
        } else {
            logger.info "Installer type = ${type}"
        }

        String comp = getCompression().get()
        if(comp && !Compression.getCompressionForName(comp)) {
            throw new InvalidUserDataException("Unsupported compression: '${comp}'. Please pick a valid one: ${Compression.getNames()}")
        } else {
            logger.info "Compression = ${comp}"
        }

        Integer level = getCompressionLevel().get()
        if(level && (level < -1 || level > 9)) {
            throw new InvalidUserDataException("Unsupported compression level: ${level}. Please pick a value between -1 and 9!")
        } else {
            logger.info "Compression level = ${level}"
        }
    }

    void compile() {
        File baseDirFile = getBaseDir().get().asFile
        File installFileFile = getInstallFile().get().asFile
        File outputFileFile = getOutputFile().get().asFile

        logger.info "Starting to create IzPack installer from base directory '${baseDirFile.canonicalPath}' and install file '${installFileFile.canonicalPath}'."

        ant.taskdef(name: 'izpack', classpath: getClasspath().asPath, classname: 'com.izforge.izpack.ant.IzPackTask')

        getAppProperties().get().entrySet().each {
            ant.property(name: it.key, value: it.value)
        }

        ant.izpack(basedir: baseDirFile.canonicalPath,
                   output: outputFileFile.canonicalPath,
                   installerType: getInstallerType().get(),
                   compression: getCompression().get(),
                   compressionlevel: getCompressionLevel().get()) {
            config(installFileFile.text)
        }

        logger.info("Finished creating IzPack installer.")
    }
}
