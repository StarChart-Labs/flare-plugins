/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.test.model;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.starchartlabs.flare.plugins.model.Contributor;
import org.starchartlabs.flare.plugins.model.Developer;
import org.starchartlabs.flare.plugins.model.GitHubMetaData;
import org.starchartlabs.flare.plugins.model.ProjectMetaData;
import org.testng.Assert;
import org.testng.annotations.Test;

public class GitHubMetaDataTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullDelegate() throws Exception {
        new GitHubMetaData(null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void repositoryNullOwner() throws Exception {
        ProjectMetaData delegate = new ProjectMetaData();

        new GitHubMetaData(delegate).repository(null, "repository");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void repositoryNullRepository() throws Exception {
        ProjectMetaData delegate = new ProjectMetaData();

        new GitHubMetaData(delegate).repository("owner", null);
    }

    @Test
    public void repository() throws Exception {
        ProjectMetaData delegate = new ProjectMetaData();

        new GitHubMetaData(delegate).repository("owner", "repository");

        Assert.assertEquals(delegate.getUrl(), "https://github.com/owner/repository");

        Assert.assertEquals(delegate.getScm().getVcsUrl(), "https://github.com/owner/repository");
        Assert.assertEquals(delegate.getScm().getConnection(), "scm:git:git://github.com/owner/repository.git");
        Assert.assertEquals(delegate.getScm().getDeveloperConnection(),
                "scm:git:ssh://github.com/owner/repository.git");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void developerUsernameOnlyNullUsername() throws Exception {
        ProjectMetaData delegate = new ProjectMetaData();

        new GitHubMetaData(delegate).developer(null);
    }

    @Test
    public void developerUsernameOnly() throws Exception {
        ProjectMetaData delegate = new ProjectMetaData();

        new GitHubMetaData(delegate).developer("username");

        Assert.assertEquals(delegate.getDevelopers().size(), 1);
        Assert.assertTrue(delegate.getDevelopers()
                .contains(new Developer("username", "username", "https://github.com/username")));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void developerNullUsername() throws Exception {
        ProjectMetaData delegate = new ProjectMetaData();

        new GitHubMetaData(delegate).developer(null, "name");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void developerNullName() throws Exception {
        ProjectMetaData delegate = new ProjectMetaData();

        new GitHubMetaData(delegate).developer("username", null);
    }

    @Test
    public void developer() throws Exception {
        ProjectMetaData delegate = new ProjectMetaData();

        new GitHubMetaData(delegate).developer("username", "name");

        Assert.assertEquals(delegate.getDevelopers().size(), 1);
        Assert.assertTrue(delegate.getDevelopers()
                .contains(new Developer("username", "name", "https://github.com/username")));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void developersNullFile() throws Exception {
        ProjectMetaData delegate = new ProjectMetaData();

        new GitHubMetaData(delegate).developers(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void developersFileDoesntExist() throws Exception {
        File file = Paths.get("nope").toFile();

        ProjectMetaData delegate = new ProjectMetaData();

        new GitHubMetaData(delegate).developers(file);
    }

    @Test
    public void developers() throws Exception {
        Path developersFile = Files.createTempFile("developers", ".proprties");
        developersFile.toFile().deleteOnExit();

        Files.write(developersFile, Arrays.asList("usernameonly", "username, name"));

        ProjectMetaData delegate = new ProjectMetaData();

        new GitHubMetaData(delegate).developers(developersFile.toFile());

        Assert.assertEquals(delegate.getDevelopers().size(), 2);
        Assert.assertTrue(delegate.getDevelopers()
                .contains(new Developer("usernameonly", "usernameonly", "https://github.com/usernameonly")));
        Assert.assertTrue(delegate.getDevelopers()
                .contains(new Developer("username", "name", "https://github.com/username")));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void contributorUsernameOnlyNullUsername() throws Exception {
        ProjectMetaData delegate = new ProjectMetaData();

        new GitHubMetaData(delegate).contributor(null);
    }

    @Test
    public void contributorUsernameOnly() throws Exception {
        ProjectMetaData delegate = new ProjectMetaData();

        new GitHubMetaData(delegate).contributor("username");

        Assert.assertEquals(delegate.getContributors().size(), 1);
        Assert.assertTrue(
                delegate.getContributors().contains(new Contributor("username", "https://github.com/username")));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void contributorNullUsername() throws Exception {
        ProjectMetaData delegate = new ProjectMetaData();

        new GitHubMetaData(delegate).contributor(null, "name");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void contributorNullName() throws Exception {
        ProjectMetaData delegate = new ProjectMetaData();

        new GitHubMetaData(delegate).contributor("username", null);
    }

    @Test
    public void contributor() throws Exception {
        ProjectMetaData delegate = new ProjectMetaData();

        new GitHubMetaData(delegate).contributor("username", "name");

        Assert.assertEquals(delegate.getContributors().size(), 1);
        Assert.assertTrue(delegate.getContributors().contains(new Contributor("name", "https://github.com/username")));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void contributorsNullFile() throws Exception {
        ProjectMetaData delegate = new ProjectMetaData();

        new GitHubMetaData(delegate).contributors(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void contributorsFileDoesntExist() throws Exception {
        File file = Paths.get("nope").toFile();

        ProjectMetaData delegate = new ProjectMetaData();

        new GitHubMetaData(delegate).contributors(file);
    }

    @Test
    public void contributors() throws Exception {
        Path contributorsFile = Files.createTempFile("contributors", ".proprties");
        contributorsFile.toFile().deleteOnExit();

        Files.write(contributorsFile, Arrays.asList("usernameonly", "username, name"));

        ProjectMetaData delegate = new ProjectMetaData();

        new GitHubMetaData(delegate).contributors(contributorsFile.toFile());

        Assert.assertEquals(delegate.getContributors().size(), 2);
        Assert.assertTrue(delegate.getContributors()
                .contains(new Contributor("usernameonly", "https://github.com/usernameonly")));
        Assert.assertTrue(delegate.getContributors()
                .contains(new Contributor("name", "https://github.com/username")));
    }

}
