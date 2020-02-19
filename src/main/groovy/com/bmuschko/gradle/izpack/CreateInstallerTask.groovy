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
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

/**
 * IzPack compilation task.
 */
@CacheableTask
class CreateInstallerTask extends DefaultTask {
    @InputFiles
    @Classpath
    FileCollection classpath

    @InputDirectory
    @Optional
    @PathSensitive(PathSensitivity.RELATIVE)
    File baseDir

    @Input
    String installerType

    @InputFile
    @PathSensitive(PathSensitivity.RELATIVE)
    File installFile

    @OutputFile
    File outputFile

    @Input
    String compression

    @Input
    Integer compressionLevel

    @Input
    Map appProperties

    @TaskAction
    void start() {
        validateConfiguration()
        compile()
    }

    void validateConfiguration() {
        if(getInstallerType() && !InstallerType.getInstallerTypeForName(getInstallerType())) {
            throw new InvalidUserDataException("Unsupported installer type: '${getInstallerType()}'. Please pick a valid one: ${InstallerType.getNames()}")
        } else {
            logger.info "Installer type = ${getInstallerType()}"
        }

        if(getCompression() && !Compression.getCompressionForName(getCompression())) {
            throw new InvalidUserDataException("Unsupported compression: '${getCompression()}'. Please pick a valid one: ${Compression.getNames()}")
        } else {
            logger.info "Compression = ${getCompression()}"
        }

        if(getCompressionLevel() && (getCompressionLevel() < -1 || getCompressionLevel() > 9)) {
            throw new InvalidUserDataException("Unsupported compression level: ${getCompressionLevel()}. Please pick a value between -1 and 9!")
        } else {
            logger.info "Compression level = ${getCompressionLevel()}"
        }
    }

    void compile() {
        logger.info "Starting to create IzPack installer from base directory '${getBaseDir().canonicalPath}' and install file '${getInstallFile().canonicalPath}'."

        ant.taskdef(name: 'izpack', classpath: getClasspath().asPath, classname: 'com.izforge.izpack.ant.IzPackTask')

        getAppProperties().entrySet().each {
            ant.property(name: it.key, value: it.value)
        }

        ant.izpack(basedir: getBaseDir().canonicalPath,
                   output: getOutputFile().canonicalPath,
                   installerType: getInstallerType(),
                   compression: getCompression(),
                   compressionlevel: getCompressionLevel()) {
            config(getInstallFile().text)
        }

        logger.info("Finished creating IzPack installer.")
    }
}
