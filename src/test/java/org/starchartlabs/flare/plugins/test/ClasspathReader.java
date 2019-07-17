/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.test;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Reader which provides access to resources on the classpath
 *
 * @author romeara
 * @since 0.1.0
 */
public class ClasspathReader implements Closeable {

    private final BufferedReader reader;

    public ClasspathReader(Path classpathPath) {
        reader = new BufferedReader(new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream(classpathPath.toString()), StandardCharsets.UTF_8));
    }

    public Stream<String> lines() {
        return reader.lines();
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

}
