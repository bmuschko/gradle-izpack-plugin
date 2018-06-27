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

/**
 * Available compressions.
 */
enum Compression {
    DEFAULT('default'), DEFLATE('deflate'), RAW('raw')

    static final Map COMPRESSIONS

    static {
        COMPRESSIONS = [:]

        values().each { compression ->
            COMPRESSIONS.put(compression.name, compression)
        }
    }

    final String name

    private Compression(String name) {
        this.name = name
    }

    static getCompressionForName(name) {
        COMPRESSIONS[name]
    }

    static getNames() {
        COMPRESSIONS.keySet()
    }
}