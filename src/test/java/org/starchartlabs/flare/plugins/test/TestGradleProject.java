/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.starchartlabs.alloy.core.Preconditions;

/**
 * Represents a simple test project setup for Gradle plug-ins
 *
 * @author romeara
 * @since 0.1.0
 */
public class TestGradleProject {

    private final Path projectDirectory;

    private TestGradleProject(Path projectDirectory) {
        this.projectDirectory = Objects.requireNonNull(projectDirectory);
    }

    public Path getProjectDirectory() {
        return projectDirectory;
    }

    public static final Builder builder(Path buildFile) {
        return new Builder(buildFile);
    }

    public static final class Builder {

        private static final Path MAIN_TEMPLATE = Paths.get("org", "starchartlabs", "flare", "plugins", "test",
                "Main.template");

        private static final Path TEST_TEMPLATE = Paths.get("org", "starchartlabs", "flare", "plugins", "test",
                "Test.template");

        private final Path buildFile;

        private final Optional<Builder> parent;

        private final Set<JavaFile> javaFiles;

        private final Set<TestFile> testFiles;

        private final Map<Path, Path> targetToSourceFiles;

        private final Map<String, Builder> subprojects;

        protected Builder(Path buildFile) {
            this(buildFile, null);
        }

        private Builder(Path buildFile, Builder parent) {
            this.buildFile = Objects.requireNonNull(buildFile);
            this.parent = Optional.ofNullable(parent);

            javaFiles = new HashSet<>();
            testFiles = new HashSet<>();
            targetToSourceFiles = new HashMap<>();
            subprojects = new HashMap<>();
        }

        public Builder addJavaFile(String packageName, String className) {
            javaFiles.add(new JavaFile(packageName, className));

            return this;
        }

        public Builder addTestFile(String packageName, String className, String targetClassQualifiedName) {
            testFiles.add(new TestFile(packageName, className, targetClassQualifiedName));

            return this;
        }

        public Builder addFile(Path classpathSourcePath, Path targetPath) {
            targetToSourceFiles.put(targetPath, classpathSourcePath);

            return this;
        }

        public Builder subProject(String subprojectName, Path buildFile) {
            Builder subProjectBuilder = new Builder(buildFile, this);
            subprojects.put(subprojectName, subProjectBuilder);

            return subProjectBuilder;
        }

        public Builder and() {
            return parent.orElse(this);
        }

        public TestGradleProject build() throws IOException {
            Preconditions.checkArgument(!parent.isPresent(), "Standard build not permitted for sub-project builders");

            Path projectDirectory = Files.createTempDirectory("gradleTestProject");

            return build(projectDirectory);
        }

        private TestGradleProject build(Path projectDirectory) throws IOException {
            try (BufferedWriter writer = Files.newBufferedWriter(projectDirectory.resolve("build.gradle"),
                    StandardOpenOption.CREATE)) {
                try (ClasspathReader reader = new ClasspathReader(buildFile)) {
                    List<String> lines = reader.lines()
                            .collect(Collectors.toList());

                    for (String line : lines) {
                        writer.write(line);
                        writer.newLine();
                    }
                }
            }

            for (JavaFile file : javaFiles) {
                file.create(projectDirectory);
            }

            for (TestFile file : testFiles) {
                file.create(projectDirectory);
            }

            for (Entry<Path, Path> targetSourceFile : targetToSourceFiles.entrySet()) {
                try (BufferedWriter writer = Files.newBufferedWriter(
                        projectDirectory.resolve(targetSourceFile.getKey()),
                        StandardOpenOption.CREATE)) {
                    try (ClasspathReader reader = new ClasspathReader(targetSourceFile.getValue())) {
                        List<String> lines = reader.lines()
                                .collect(Collectors.toList());

                        for (String line : lines) {
                            writer.write(line);
                            writer.newLine();
                        }
                    }
                }
            }

            for (Entry<String, Builder> subproject : subprojects.entrySet()) {
                Path subProjectDirectory = projectDirectory.resolve(subproject.getKey());
                Files.createDirectories(subProjectDirectory);

                subproject.getValue().build(subProjectDirectory);
            }

            // Create settings.gradle
            if (!subprojects.isEmpty()) {
                Set<String> includeLines = subprojects.keySet().stream()
                        .map(name -> "include '" + name + "'")
                        .collect(Collectors.toSet());

                try (BufferedWriter writer = Files.newBufferedWriter(projectDirectory.resolve("settings.gradle"))) {
                    for (String line : includeLines) {
                        writer.write(line + "\n");
                    }
                }
            }

            return new TestGradleProject(projectDirectory);
        }

        private static final class JavaFile {

            private final String packageName;

            private final String className;

            public JavaFile(String packageName, String className) {
                this.packageName = Objects.requireNonNull(packageName);
                this.className = Objects.requireNonNull(className);
            }

            public void create(Path parent) throws IOException {
                Path relativeDirectory = Paths.get("src", "main").resolve(Paths.get("java", packageName.split("\\.")));
                Path directory = parent.resolve(relativeDirectory);
                Files.createDirectories(directory);

                try (BufferedWriter writer = Files.newBufferedWriter(directory.resolve(className + ".java"),
                        StandardOpenOption.CREATE)) {
                    try (ClasspathReader reader = new ClasspathReader(MAIN_TEMPLATE)) {
                        List<String> lines = reader.lines()
                                .map(line -> line.replaceAll("\\$\\{package}", packageName))
                                .map(line -> line.replaceAll("\\$\\{class}", className))
                                .collect(Collectors.toList());

                        for (String line : lines) {
                            writer.write(line);
                            writer.newLine();
                        }
                    }
                }
            }

        }

        private static final class TestFile {

            private final String packageName;

            private final String className;

            private final String targetClassQualifiedName;

            public TestFile(String packageName, String className, String targetClassQualifiedName) {
                this.packageName = Objects.requireNonNull(packageName);
                this.className = Objects.requireNonNull(className);
                this.targetClassQualifiedName = Objects.requireNonNull(targetClassQualifiedName);
            }

            public void create(Path parent) throws IOException {
                Path relativeDirectory = Paths.get("src", "test").resolve(Paths.get("java", packageName.split("\\.")));
                Path directory = parent.resolve(relativeDirectory);
                Files.createDirectories(directory);

                try (BufferedWriter writer = Files.newBufferedWriter(directory.resolve(className + ".java"),
                        StandardOpenOption.CREATE)) {
                    try (ClasspathReader reader = new ClasspathReader(TEST_TEMPLATE)) {
                        List<String> lines = reader.lines()
                                .map(line -> line.replaceAll("\\$\\{package}", packageName))
                                .map(line -> line.replaceAll("\\$\\{class}", className))
                                .map(line -> line.replaceAll("\\$\\{targetClassQualifiedName}", targetClassQualifiedName))
                                .collect(Collectors.toList());

                        for (String line : lines) {
                            writer.write(line);
                            writer.newLine();
                        }
                    }
                }

            }

        }

    }

}
