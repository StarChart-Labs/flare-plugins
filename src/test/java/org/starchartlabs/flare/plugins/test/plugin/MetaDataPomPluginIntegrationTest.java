/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.test.plugin;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.starchartlabs.flare.plugins.test.IntegrationTestListener;
import org.starchartlabs.flare.plugins.test.TestGradleProject;
import org.starchartlabs.flare.plugins.test.pom.PomContributor;
import org.starchartlabs.flare.plugins.test.pom.PomDeveloper;
import org.starchartlabs.flare.plugins.test.pom.PomLicense;
import org.starchartlabs.flare.plugins.test.pom.PomProject;
import org.starchartlabs.flare.plugins.test.pom.PomScm;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

@Listeners(value = { IntegrationTestListener.class })
public class MetaDataPomPluginIntegrationTest {

    private static final Path BUILD_FILE_DIRECTORY = Paths.get("org", "starchartlabs", "flare", "plugins", "test",
            "metadata", "pom");

    private static final Path STANDARD_SETUP_FILE = BUILD_FILE_DIRECTORY.resolve("build.gradle");

    private Path standardSetupProjectPath;

    @BeforeClass
    public void setup() throws Exception {
        standardSetupProjectPath = TestGradleProject.builder(STANDARD_SETUP_FILE)
                .build()
                .getProjectDirectory();
    }

    @Test
    public void standardSetup() throws Exception {
        BuildResult result = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(standardSetupProjectPath.toFile())
                .withArguments("generatePomFileForMavenPublication", "--stacktrace")
                .withGradleVersion("5.0")
                .build();

        TaskOutcome outcome = result.task(":generatePomFileForMavenPublication").getOutcome();
        Assert.assertTrue(TaskOutcome.SUCCESS.equals(outcome));

        // POM validation
        PomScm scm = new PomScm("http://scm/url", "scm/connection", "scm/developerConnection");
        List<PomDeveloper> developers = Collections
                .singletonList(new PomDeveloper("developer/id", "developer/name", "developer/url"));
        List<PomContributor> contributors = Collections
                .singletonList(new PomContributor("contributor/name", "contributor/url"));
        List<PomLicense> licenses = Collections
                .singletonList(new PomLicense("license/name", "license/url", "license/distribution"));

        PomProject expectedProject = new PomProject("http://url", scm, developers, contributors, licenses);

        validatePom(standardSetupProjectPath, "maven", expectedProject);
    }

    private void validatePom(Path projectDirectory, String publication, PomProject expected) throws Exception {
        Path generatedPom = projectDirectory.resolve("build").resolve("publications").resolve(publication)
                .resolve("pom-default.xml");
        XmlMapper xmlMapper = new XmlMapper();
        PomProject value = xmlMapper.readValue(generatedPom.toFile(), PomProject.class);

        Assert.assertEquals(value, expected);
    }

}
