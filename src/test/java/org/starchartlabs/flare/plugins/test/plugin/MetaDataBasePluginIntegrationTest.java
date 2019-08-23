/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.test.plugin;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.starchartlabs.alloy.core.Strings;
import org.starchartlabs.flare.plugins.test.IntegrationTestListener;
import org.starchartlabs.flare.plugins.test.TestGradleProject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(value = { IntegrationTestListener.class })
public class MetaDataBasePluginIntegrationTest {

    private static final Path BUILD_FILE_DIRECTORY = Paths.get("org", "starchartlabs", "flare", "plugins", "test",
            "metadata", "base");

    private static final Path STANDARD_SETUP_FILE = BUILD_FILE_DIRECTORY.resolve("standardSetup.gradle");

    private static final Path GITHUB_SETUP_FILE = BUILD_FILE_DIRECTORY.resolve("githubSetup.gradle");

    private Path standardSetupProjectPath;

    private Path githubSetupProjectPath;

    @BeforeClass
    public void setup() throws Exception {
        standardSetupProjectPath = TestGradleProject.builder(STANDARD_SETUP_FILE)
                .build()
                .getProjectDirectory();

        githubSetupProjectPath = TestGradleProject.builder(GITHUB_SETUP_FILE)
                .addFile(BUILD_FILE_DIRECTORY.resolve("developers.properties"), Paths.get("developers.properties"))
                .addFile(BUILD_FILE_DIRECTORY.resolve("contributors.properties"), Paths.get("contributors.properties"))
                .build()
                .getProjectDirectory();
    }

    @Test
    public void standardSetup() throws Exception {
        BuildResult result = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(standardSetupProjectPath.toFile())
                .withArguments("metaDataPrintout")
                .withGradleVersion("5.0")
                .build();

        Collection<String> expectedLines = new ArrayList<>();
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
            Assert.assertTrue(result.getOutput().contains(expectedLine),
                    Strings.format("Did not find expected line '%s'", expectedLine));
        }

        TaskOutcome outcome = result.task(":metaDataPrintout").getOutcome();
        Assert.assertTrue(TaskOutcome.SUCCESS.equals(outcome));
    }

    @Test
    public void githubSetup() throws Exception {
        BuildResult result = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(githubSetupProjectPath.toFile())
                .withArguments("metaDataPrintout")
                .withGradleVersion("5.0")
                .build();

        Collection<String> expectedLines = new ArrayList<>();
        expectedLines.add("url: https://github.com/owner/repository");

        expectedLines.add("scm.vcsUrl: https://github.com/owner/repository");
        expectedLines.add("scm.connection: scm:git:git://github.com/owner/repository.git");
        expectedLines.add("scm.developerConnection: scm:git:ssh://github.com/owner/repository.git");

        expectedLines.add("developer: dev-usernameonly:dev-usernameonly:https://github.com/dev-usernameonly");
        expectedLines.add("developer: dev-username:dev-name:https://github.com/dev-username");
        expectedLines
        .add("developer: dev-file-usernameonly:dev-file-usernameonly:https://github.com/dev-file-usernameonly");
        expectedLines.add("developer: dev-file-username:dev-file-name:https://github.com/dev-file-username");

        expectedLines.add("contributor: contrib-usernameonly:https://github.com/contrib-usernameonly");
        expectedLines.add("contributor: contrib-name:https://github.com/contrib-username");
        expectedLines.add("contributor: contrib-file-usernameonly:https://github.com/contrib-file-usernameonly");
        expectedLines.add("contributor: contrib-file-name:https://github.com/contrib-file-username");

        expectedLines.add("license: license/name:license/tag:license/url:license/distribution");
        expectedLines.add("license: The Apache Software License, Version 2.0:Apache 2.0"
                + ":http://www.apache.org/licenses/LICENSE-2.0.txt:repo");
        expectedLines.add("license: The MIT License:MIT:https://opensource.org/licenses/MIT:mit/distribution");
        expectedLines.add(
                "license: Eclipse Public License 1.0:EPL:https://opensource.org/licenses/EPL-1.0:epl/distribution");

        for (String expectedLine : expectedLines) {
            Assert.assertTrue(result.getOutput().contains(expectedLine),
                    Strings.format("Did not find expected line '%s'", expectedLine));
        }

        TaskOutcome outcome = result.task(":metaDataPrintout").getOutcome();
        Assert.assertTrue(TaskOutcome.SUCCESS.equals(outcome));
    }

}
