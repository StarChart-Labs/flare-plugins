/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.test.plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.starchartlabs.alloy.core.Strings;
import org.starchartlabs.flare.plugins.test.TestGradleProject;
import org.starchartlabs.flare.plugins.test.pom.PomContributor;
import org.starchartlabs.flare.plugins.test.pom.PomDeveloper;
import org.starchartlabs.flare.plugins.test.pom.PomLicense;
import org.starchartlabs.flare.plugins.test.pom.PomProject;
import org.starchartlabs.flare.plugins.test.pom.PomScm;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class MultiModuleLibraryPluginIntegrationTest {

    private static final Path BUILD_FILE_DIRECTORY = Paths.get("org", "starchartlabs", "flare", "plugins", "test",
            "multi", "module", "library");

    private static final Path SINGLE_PROJECT_BUILD_FILE = BUILD_FILE_DIRECTORY.resolve("singleProjectBuild.gradle");

    private static final Path ROOT_PROJECT_BUILD_FILE = BUILD_FILE_DIRECTORY.resolve("rootProjectBuild.gradle");

    private static final Path SUB_PROJECT_BUILD_FILE = BUILD_FILE_DIRECTORY.resolve("subProjectBuild.gradle");

    private static final Path DEPENDENCIES_FILE = BUILD_FILE_DIRECTORY.resolve("dependencies.properties");

    private static final Path DEVELOPERS_FILE = BUILD_FILE_DIRECTORY.resolve("developers.properties");

    private static final Path CONTRIBUTORS_FILE = BUILD_FILE_DIRECTORY.resolve("contributors.properties");

    private Path singleProjectPath;

    private Path multiModuleProjectPath;

    private BuildResult singleProjectBuildResult;

    private BuildResult multiModuleProjectBuildResult;

    @BeforeClass
    public void setupProjects() throws Exception {
        singleProjectPath = TestGradleProject.builder(SINGLE_PROJECT_BUILD_FILE)
                .addJavaFile("org.starchartlabs.flare.merge.coverage.reports", "Main")
                .addTestFile("org.starchartlabs.flare.test.merge.coverage.reports", "MainTest",
                        "org.starchartlabs.flare.merge.coverage.reports.Main")
                .addFile(DEPENDENCIES_FILE, Paths.get("dependencies.properties"))
                .build()
                .getProjectDirectory();

        multiModuleProjectPath = TestGradleProject.builder(ROOT_PROJECT_BUILD_FILE)
                .addFile(DEPENDENCIES_FILE, Paths.get("dependencies.properties"))
                .addFile(DEVELOPERS_FILE, Paths.get("developers.properties"))
                .addFile(CONTRIBUTORS_FILE, Paths.get("contributors.properties"))
                .subProject("one", SUB_PROJECT_BUILD_FILE)
                .addJavaFile("org.starchartlabs.flare.merge.coverage.reports.one", "Mainone")
                .addTestFile("org.starchartlabs.flare.test.merge.coverage.reports.one", "MainoneTest",
                        "org.starchartlabs.flare.merge.coverage.reports.one.Mainone")
                .and()
                .subProject("two", SUB_PROJECT_BUILD_FILE)
                .addJavaFile("org.starchartlabs.flare.merge.coverage.reports.two", "Maintwo")
                .addTestFile("org.starchartlabs.flare.test.merge.coverage.reports.two", "MaintwoTest",
                        "org.starchartlabs.flare.merge.coverage.reports.two.Maintwo")
                .and()
                .build()
                .getProjectDirectory();

        singleProjectBuildResult = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(singleProjectPath.toFile())
                .withArguments("build", "--info")
                .withGradleVersion("5.0")
                .build();

        multiModuleProjectBuildResult = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(multiModuleProjectPath.toFile())
                .withArguments("build", "--info")
                .withGradleVersion("5.0")
                .build();
    }

    @Test
    public void singleProjectBuildSuccessful() throws Exception {
        TaskOutcome outcome = singleProjectBuildResult.task(":build").getOutcome();
        Assert.assertTrue(TaskOutcome.SUCCESS.equals(outcome));
    }

    @Test(dependsOnMethods = { "singleProjectBuildSuccessful" })
    public void singleProjectMergeCoverageReports() throws Exception {
        // Check merge coverage reports application
        TaskOutcome outcome = singleProjectBuildResult.task(":mergeCoverageReports").getOutcome();
        Assert.assertTrue(TaskOutcome.SUCCESS.equals(outcome));

        Path reportOutput = singleProjectPath.resolve(Paths.get("build", "reports", "jacoco", "report.xml"));

        Assert.assertTrue(Files.exists(reportOutput));

        boolean coveredMainFile = Files.lines(reportOutput)
                .map(String::trim)
                .anyMatch(line -> line.contains(
                        "<class name=\"org/starchartlabs/flare/merge/coverage/reports/Main\" sourcefilename=\"Main.java\">"));

        Assert.assertTrue(coveredMainFile, "Coverage report missing line for expected source file");
    }

    @Test(dependsOnMethods = { "singleProjectBuildSuccessful" })
    public void singleProjectDependencyConstraints() throws Exception {
        String expectedLine = "Applied configuration ':compile' dependency constraint: org.testng:testng:6.14.3";

        Assert.assertTrue(singleProjectBuildResult.getOutput().contains(expectedLine),
                Strings.format("Did not find expected line '%s'", expectedLine));
    }

    @Test(dependsOnMethods = { "singleProjectBuildSuccessful" })
    public void singleProjectManagedCredentials() throws Exception {
        String expectedLine = "Credentials configured: bintray";

        Assert.assertTrue(singleProjectBuildResult.getOutput().contains(expectedLine),
                Strings.format("Did not find expected line '%s'", expectedLine));
    }

    @Test(dependsOnMethods = { "singleProjectBuildSuccessful" })
    public void singleProjectIncreasedTestLogging() throws Exception {
        List<String> expectedLines = new ArrayList<>();
        expectedLines.add("test Exception format: FULL");
        expectedLines.add("test Quiet logging: [SKIPPED, FAILED]");
        expectedLines.add("test Info logging: [PASSED, SKIPPED, FAILED, STANDARD_OUT, STANDARD_ERROR]");
        expectedLines.add("test Debug logging: [STARTED, PASSED, SKIPPED, FAILED, STANDARD_OUT, STANDARD_ERROR]");

        for (String expectedLine : expectedLines) {
            Assert.assertTrue(singleProjectBuildResult.getOutput().contains(expectedLine),
                    Strings.format("Did not find expected line '%s'", expectedLine));
        }
    }

    @Test(dependsOnMethods = { "singleProjectBuildSuccessful" })
    public void singleProjectSourceJars() throws Exception {
        List<String> expectedLines = new ArrayList<>();

        // Check source/javadoc jar tasks
        expectedLines.add("Artifact Verification: maven:sources");
        expectedLines.add("Artifact Verification: maven:javadoc");

        for (String expectedLine : expectedLines) {
            Assert.assertTrue(singleProjectBuildResult.getOutput().contains(expectedLine),
                    Strings.format("Did not find expected line '%s'", expectedLine));
        }

        // Check source/javadoc jar tasks
        Path sourcesJar = singleProjectPath.resolve("build").resolve("libs")
                .resolve(singleProjectPath.getFileName().toString() + "-sources.jar");
        Path javadocJar = singleProjectPath.resolve("build").resolve("libs")
                .resolve(singleProjectPath.getFileName().toString() + "-javadoc.jar");

        verifyFile(sourcesJar.toFile(), "org/starchartlabs/flare/merge/coverage/reports/Main.java");
        verifyFile(javadocJar.toFile(), "org/starchartlabs/flare/merge/coverage/reports/Main.html");
    }

    @Test(dependsOnMethods = { "singleProjectBuildSuccessful" })
    public void singleProjectMetaDataBase() throws Exception {
        List<String> expectedLines = new ArrayList<>();

        // Check meta data configuration
        expectedLines.add("url: http://url");

        expectedLines.add("scm.vcsUrl: http://scm/url");
        expectedLines.add("scm.connection: scm/connection");
        expectedLines.add("scm.developerConnection: scm/developerConnection");

        expectedLines.add("developer: developer/id:developer/name:developer/url");

        expectedLines.add("contributor: contributor/name:contributor/url");

        expectedLines.add("license: license/name:license/tag:license/url:license/distribution");
        expectedLines.add("license: The Apache Software License, Version 2.0:Apache 2.0"
                + ":http://www.apache.org/licenses/LICENSE-2.0.txt:repo");
        expectedLines.add("license: The MIT License:MIT:https://opensource.org/licenses/MIT:mit/distribution");
        expectedLines.add(
                "license: Eclipse Public License 1.0:EPL:https://opensource.org/licenses/EPL-1.0:epl/distribution");

        for (String expectedLine : expectedLines) {
            Assert.assertTrue(singleProjectBuildResult.getOutput().contains(expectedLine),
                    Strings.format("Did not find expected line '%s'", expectedLine));
        }
    }

    @Test(dependsOnMethods = { "singleProjectBuildSuccessful" })
    public void singleProjectMetaDataPom() throws Exception {
        // Validate generated POM
        PomScm scm = new PomScm("http://scm/url", "scm/connection", "scm/developerConnection");
        List<PomDeveloper> developers = Collections
                .singletonList(new PomDeveloper("developer/id", "developer/name", "developer/url"));
        List<PomContributor> contributors = Collections
                .singletonList(new PomContributor("contributor/name", "contributor/url"));
        List<PomLicense> licenses = Arrays.asList(new PomLicense("license/name", "license/url", "license/distribution"),
                new PomLicense("The Apache Software License, Version 2.0",
                        "http://www.apache.org/licenses/LICENSE-2.0.txt", "repo"),
                new PomLicense("The MIT License", "https://opensource.org/licenses/MIT", "mit/distribution"),
                new PomLicense("Eclipse Public License 1.0", "https://opensource.org/licenses/EPL-1.0",
                        "epl/distribution"));

        PomProject expectedProject = new PomProject("http://url", scm, developers, contributors, licenses);

        validatePom(singleProjectPath, "maven", expectedProject);
    }

    @Test
    public void multiModuleProjectBuildSuccessful() throws Exception {
        TaskOutcome outcome = multiModuleProjectBuildResult.task(":build").getOutcome();
        Assert.assertTrue(TaskOutcome.SUCCESS.equals(outcome));
    }

    @Test(dependsOnMethods = { "multiModuleProjectBuildSuccessful" })
    public void multiModuleProjectMergeCoverageReports() throws Exception {
        TaskOutcome outcome = multiModuleProjectBuildResult.task(":mergeCoverageReports").getOutcome();
        Assert.assertTrue(TaskOutcome.SUCCESS.equals(outcome));

        Path reportOutput = multiModuleProjectPath.resolve(Paths.get("build", "reports", "jacoco", "report.xml"));

        Assert.assertTrue(Files.exists(reportOutput));

        boolean coveredMainOneFile = Files.lines(reportOutput)
                .map(String::trim)
                .anyMatch(line -> line.contains(
                        "<class name=\"org/starchartlabs/flare/merge/coverage/reports/one/Mainone\" sourcefilename=\"Mainone.java\">"));
        boolean coveredMainTwoFile = Files.lines(reportOutput)
                .map(String::trim)
                .anyMatch(line -> line.contains(
                        "<class name=\"org/starchartlabs/flare/merge/coverage/reports/two/Maintwo\" sourcefilename=\"Maintwo.java\">"));

        Assert.assertTrue(coveredMainOneFile, "Coverage report missing line for expected source file Mainone");
        Assert.assertTrue(coveredMainTwoFile, "Coverage report missing line for expected source file Maintwo");
    }

    @Test(dependsOnMethods = { "multiModuleProjectBuildSuccessful" })
    public void multiModuleProjectDependencyContraints() throws Exception {
        List<String> expectedLines = new ArrayList<>();

        expectedLines.add("Applied configuration ':one:compile' dependency constraint: org.testng:testng:6.14.3");
        expectedLines.add("Applied configuration ':two:compile' dependency constraint: org.testng:testng:6.14.3");

        for (String expectedLine : expectedLines) {
            Assert.assertTrue(multiModuleProjectBuildResult.getOutput().contains(expectedLine),
                    Strings.format("Did not find expected line '%s'", expectedLine));
        }
    }

    @Test(dependsOnMethods = { "multiModuleProjectBuildSuccessful" })
    public void multiModuleProjectManagedCredentials() throws Exception {
        String expectedLine = "Credentials configured: bintray";

        Assert.assertTrue(multiModuleProjectBuildResult.getOutput().contains(expectedLine),
                Strings.format("Did not find expected line '%s'", expectedLine));
    }

    @Test(dependsOnMethods = { "multiModuleProjectBuildSuccessful" })
    public void multiModuleProjectIncreasedTestLogging() throws Exception {
        List<String> expectedLines = new ArrayList<>();

        expectedLines.add("test Exception format: FULL");
        expectedLines.add("test Quiet logging: [SKIPPED, FAILED]");
        expectedLines.add("test Info logging: [PASSED, SKIPPED, FAILED, STANDARD_OUT, STANDARD_ERROR]");
        expectedLines.add("test Debug logging: [STARTED, PASSED, SKIPPED, FAILED, STANDARD_OUT, STANDARD_ERROR]");

        for (String expectedLine : expectedLines) {
            Assert.assertTrue(multiModuleProjectBuildResult.getOutput().contains(expectedLine),
                    Strings.format("Did not find expected line '%s'", expectedLine));
        }
    }

    @Test(dependsOnMethods = { "multiModuleProjectBuildSuccessful" })
    public void multiModuleProjectSourceJars() throws Exception {
        List<String> expectedLines = new ArrayList<>();

        for (String subProjectName : Arrays.asList("one", "two")) {
            Path sourcesJar = multiModuleProjectPath.resolve(subProjectName).resolve("build").resolve("libs")
                    .resolve(subProjectName + "-sources.jar");
            Path javadocJar = multiModuleProjectPath.resolve(subProjectName).resolve("build").resolve("libs")
                    .resolve(subProjectName + "-javadoc.jar");

            verifyFile(sourcesJar.toFile(), "org/starchartlabs/flare/merge/coverage/reports/" + subProjectName + "/Main"
                    + subProjectName + ".java");
            verifyFile(javadocJar.toFile(), "org/starchartlabs/flare/merge/coverage/reports/" + subProjectName + "/Main"
                    + subProjectName + ".html");

            expectedLines.add("Artifact Verification: " + subProjectName + ":maven:sources");
            expectedLines.add("Artifact Verification: " + subProjectName + ":maven:javadoc");
        }

        for (String expectedLine : expectedLines) {
            Assert.assertTrue(multiModuleProjectBuildResult.getOutput().contains(expectedLine),
                    Strings.format("Did not find expected line '%s'", expectedLine));
        }
    }

    @Test(dependsOnMethods = { "multiModuleProjectBuildSuccessful" })
    public void multiModuleProjectMetaDataBase() throws Exception {
        List<String> expectedLines = new ArrayList<>();

        // Check source/javadoc jar tasks
        for (String subProjectName : Arrays.asList("one", "two")) {
            expectedLines.add("url: https://github.com/owner/" + subProjectName);

            expectedLines.add("scm.vcsUrl: https://github.com/owner/" + subProjectName);
            expectedLines.add("scm.connection: scm:git:git://github.com/owner/" + subProjectName + ".git");
            expectedLines.add("scm.developerConnection: scm:git:ssh://github.com/owner/" + subProjectName + ".git");

            expectedLines
            .add("developer: dev-file-usernameonly:dev-file-usernameonly:https://github.com/dev-file-usernameonly");
            expectedLines.add("developer: dev-file-username:dev-file-name:https://github.com/dev-file-username");

            expectedLines.add("contributor: contrib-file-usernameonly:https://github.com/contrib-file-usernameonly");
            expectedLines.add("contributor: contrib-file-name:https://github.com/contrib-file-username");

            expectedLines.add("license: The MIT License:MIT:https://opensource.org/licenses/MIT:repo");
        }

        for (String expectedLine : expectedLines) {
            Assert.assertTrue(multiModuleProjectBuildResult.getOutput().contains(expectedLine),
                    Strings.format("Did not find expected line '%s'", expectedLine));
        }
    }

    @Test(dependsOnMethods = { "multiModuleProjectBuildSuccessful" })
    public void multiModuleProjectMetaDataPom() throws Exception {
        // Check source/javadoc jar tasks
        for (String subProjectName : Arrays.asList("one", "two")) {
            PomScm scm = new PomScm("https://github.com/owner/" + subProjectName,
                    "scm:git:git://github.com/owner/" + subProjectName + ".git",
                    "scm:git:ssh://github.com/owner/" + subProjectName + ".git");
            List<PomDeveloper> developers = Arrays.asList(
                    new PomDeveloper("dev-file-usernameonly", "dev-file-usernameonly",
                            "https://github.com/dev-file-usernameonly"),
                    new PomDeveloper("dev-file-username", "dev-file-name", "https://github.com/dev-file-username"));
            List<PomContributor> contributors = Arrays.asList(
                    new PomContributor("contrib-file-usernameonly", "https://github.com/contrib-file-usernameonly"),
                    new PomContributor("contrib-file-name", "https://github.com/contrib-file-username"));
            List<PomLicense> licenses = Arrays.asList(
                    new PomLicense("The MIT License", "https://opensource.org/licenses/MIT", "repo"));

            PomProject expectedProject = new PomProject("https://github.com/owner/" + subProjectName, scm, developers,
                    contributors, licenses);

            validatePom(multiModuleProjectPath.resolve(subProjectName), "maven", expectedProject);
        }
    }

    private void verifyFile(File archiveFile, String expectedFilePath) throws IOException {
        boolean found = false;

        try (JarFile jarFile = new JarFile(archiveFile)) {
            found = Collections.list(jarFile.entries()).stream()
                    .map(JarEntry::getName)
                    .anyMatch(name -> Objects.equals(name, expectedFilePath));
        }

        Assert.assertTrue(found, "Did not find expected file " + expectedFilePath);
    }

    private void validatePom(Path projectDirectory, String publication, PomProject expected) throws Exception {
        Path generatedPom = projectDirectory.resolve("build").resolve("publications").resolve(publication)
                .resolve("pom-default.xml");
        XmlMapper xmlMapper = new XmlMapper();
        PomProject value = xmlMapper.readValue(generatedPom.toFile(), PomProject.class);

        Assert.assertEquals(value, expected);
    }

}
