/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.test.plugin;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.starchartlabs.flare.plugins.test.IntegrationTestListener;
import org.starchartlabs.flare.plugins.test.TestGradleProject;
import org.starchartlabs.flare.plugins.test.pom.PomContributor;
import org.starchartlabs.flare.plugins.test.pom.PomDeveloper;
import org.starchartlabs.flare.plugins.test.pom.PomLicense;
import org.starchartlabs.flare.plugins.test.pom.PomProject;
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
        validatePom(standardSetupProjectPath, "maven");
    }

    private void validatePom(Path projectDirectory, String publication) throws Exception {
        Path generatedPom = projectDirectory.resolve("build").resolve("publications").resolve(publication)
                .resolve("pom-default.xml");
        XmlMapper xmlMapper = new XmlMapper();
        PomProject value = xmlMapper.readValue(generatedPom.toFile(), PomProject.class);

        Assert.assertEquals(value.getUrl(), "http://url");

        Assert.assertEquals(value.getScm().getUrl(), "http://scm/url");
        Assert.assertEquals(value.getScm().getConnection(), "scm/connection");
        Assert.assertEquals(value.getScm().getDeveloperConnection(), "scm/developerConnection");

        Assert.assertEquals(value.getDevelopers().size(), 1);
        PomDeveloper developer = value.getDevelopers().iterator().next();

        Assert.assertEquals(developer.getId(), "developer/id");
        Assert.assertEquals(developer.getName(), "developer/name");
        Assert.assertEquals(developer.getUrl(), "developer/url");

        Assert.assertEquals(value.getContributors().size(), 1);
        PomContributor contributor = value.getContributors().iterator().next();

        Assert.assertEquals(contributor.getName(), "contributor/name");
        Assert.assertEquals(contributor.getUrl(), "contributor/url");

        Assert.assertEquals(value.getLicenses().size(), 1);
        PomLicense license = value.getLicenses().iterator().next();

        Assert.assertEquals(license.getName(), "license/name");
        Assert.assertEquals(license.getUrl(), "license/url");
        Assert.assertEquals(license.getDistribution(), "license/distribution");
    }

}
