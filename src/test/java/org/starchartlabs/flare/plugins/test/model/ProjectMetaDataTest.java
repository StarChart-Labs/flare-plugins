/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.test.model;

import org.gradle.api.Action;
import org.gradle.api.provider.Property;
import org.gradle.api.publish.maven.MavenPom;
import org.gradle.api.publish.maven.MavenPomContributorSpec;
import org.gradle.api.publish.maven.MavenPomDeveloperSpec;
import org.gradle.api.publish.maven.MavenPomLicenseSpec;
import org.gradle.api.publish.maven.MavenPomScm;
import org.mockito.Mockito;
import org.starchartlabs.flare.plugins.model.Contributor;
import org.starchartlabs.flare.plugins.model.ContributorContainer;
import org.starchartlabs.flare.plugins.model.Developer;
import org.starchartlabs.flare.plugins.model.DeveloperContainer;
import org.starchartlabs.flare.plugins.model.GitHubMetaData;
import org.starchartlabs.flare.plugins.model.License;
import org.starchartlabs.flare.plugins.model.LicenseContainer;
import org.starchartlabs.flare.plugins.model.ProjectMetaData;
import org.starchartlabs.flare.plugins.model.Scm;
import org.testng.Assert;
import org.testng.annotations.Test;

import groovy.lang.Closure;

public class ProjectMetaDataTest {

    private static final Scm SCM = new Scm("vcsUrl", "connection", "developerConnection");

    private static final DeveloperContainer DEVELOPERS = new DeveloperContainer().developer("id", "name", "url");

    private static final ContributorContainer CONTRIBUTORS = new ContributorContainer().contributor("name", "url");

    private static final LicenseContainer LICENSES = new LicenseContainer().license("name", "tag", "url",
            "distribution");

    @Test(expectedExceptions = NullPointerException.class)
    public void githubClosureNullClosure() throws Exception {
        new ProjectMetaData().github((Closure<GitHubMetaData>) null);
    }

    @Test
    public void githubClosure() throws Exception {
        ProjectMetaData result = new ProjectMetaData()
                .github(new TestGitHubMetaDataClosure("owner", "repository"));

        Assert.assertEquals(result.getUrl(), "https://github.com/owner/repository");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void githubActionNullAction() throws Exception {
        new ProjectMetaData().github((Action<GitHubMetaData>) null);
    }

    @Test
    public void githubAction() throws Exception {
        ProjectMetaData result = new ProjectMetaData()
                .github(githubMeta -> githubMeta.repository("owner", "repository"));

        Assert.assertEquals(result.getUrl(), "https://github.com/owner/repository");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void scmClosureNullClosure() throws Exception {
        new ProjectMetaData().scm((Closure<Scm>) null);
    }

    @Test
    public void scmClosure() throws Exception {
        ProjectMetaData result = new ProjectMetaData()
                .scm(new TestScmClosure("vcsUrl"));

        Assert.assertEquals(result.getScm().getVcsUrl(), "vcsUrl");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void scmActionNullAction() throws Exception {
        new ProjectMetaData().scm((Action<Scm>) null);
    }

    @Test
    public void scmAction() throws Exception {
        ProjectMetaData result = new ProjectMetaData()
                .scm(s -> s.setVcsUrl("vcsUrl"));

        Assert.assertEquals(result.getScm().getVcsUrl(), "vcsUrl");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void setScmNullScm() throws Exception {
        new ProjectMetaData().setScm(null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void developersClosureNullClosure() throws Exception {
        new ProjectMetaData().developers((Closure<DeveloperContainer>) null);
    }

    @Test
    public void developersClosure() throws Exception {
        ProjectMetaData result = new ProjectMetaData()
                .developers(new TestDevelopersClosure("id", "name", "url"));

        Assert.assertEquals(result.getDevelopers().size(), 1);
        Assert.assertEquals(result.getDevelopers().iterator().next(), new Developer("id", "name", "url"));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void developersActionNullAction() throws Exception {
        new ProjectMetaData().developers((Action<DeveloperContainer>) null);
    }

    @Test
    public void developersAction() throws Exception {
        ProjectMetaData result = new ProjectMetaData()
                .developers(devs -> devs.developer("id", "name", "url"));

        Assert.assertEquals(result.getDevelopers().size(), 1);
        Assert.assertEquals(result.getDevelopers().iterator().next(), new Developer("id", "name", "url"));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void contributorsClosureNullClosure() throws Exception {
        new ProjectMetaData().contributors((Closure<ContributorContainer>) null);
    }

    @Test
    public void contributorsClosure() throws Exception {
        ProjectMetaData result = new ProjectMetaData()
                .contributors(new TestContributorsClosure("name", "url"));

        Assert.assertEquals(result.getContributors().size(), 1);
        Assert.assertEquals(result.getContributors().iterator().next(), new Contributor("name", "url"));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void contributorsActionNullAction() throws Exception {
        new ProjectMetaData().contributors((Action<ContributorContainer>) null);
    }

    @Test
    public void contributorsAction() throws Exception {
        ProjectMetaData result = new ProjectMetaData()
                .contributors(contribs -> contribs.contributor("name", "url"));

        Assert.assertEquals(result.getContributors().size(), 1);
        Assert.assertEquals(result.getContributors().iterator().next(), new Contributor("name", "url"));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void licensesClosureNullClosure() throws Exception {
        new ProjectMetaData().licenses((Closure<LicenseContainer>) null);
    }

    @Test
    public void licensesClosure() throws Exception {
        ProjectMetaData result = new ProjectMetaData()
                .licenses(new TestLicensesClosure("name", "tag", "url", "distribution"));

        Assert.assertEquals(result.getLicenses().size(), 1);
        Assert.assertEquals(result.getLicenses().iterator().next(), new License("name", "tag", "url", "distribution"));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void licensesActionNullAction() throws Exception {
        new ProjectMetaData().licenses((Action<LicenseContainer>) null);
    }

    @Test
    public void licensesAction() throws Exception {
        ProjectMetaData result = new ProjectMetaData()
                .licenses(lics -> lics.license("name", "tag", "url", "distribution"));

        Assert.assertEquals(result.getLicenses().size(), 1);
        Assert.assertEquals(result.getLicenses().iterator().next(), new License("name", "tag", "url", "distribution"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getPomConfiguration() throws Exception {
        Action<MavenPomScm> scmAction = (a -> {
        });
        Action<MavenPomDeveloperSpec> developersAction = (a -> {
        });
        Action<MavenPomContributorSpec> contributorsAction = (a -> {
        });
        Action<MavenPomLicenseSpec> licensesAction = (a -> {
        });

        Scm scm = Mockito.mock(Scm.class);
        DeveloperContainer developers = Mockito.mock(DeveloperContainer.class);
        ContributorContainer contributors = Mockito.mock(ContributorContainer.class);
        LicenseContainer licenses = Mockito.mock(LicenseContainer.class);

        Mockito.when(scm.getPomConfiguration()).thenReturn(scmAction);
        Mockito.when(developers.getPomConfiguration()).thenReturn(developersAction);
        Mockito.when(contributors.getPomConfiguration()).thenReturn(contributorsAction);
        Mockito.when(licenses.getPomConfiguration()).thenReturn(licensesAction);

        Property<String> urlProperty = Mockito.mock(Property.class);

        MavenPom pom = Mockito.mock(MavenPom.class);
        Mockito.when(pom.getUrl()).thenReturn(urlProperty);

        ProjectMetaData projectMetaData = new ProjectMetaData("url", scm, developers, contributors, licenses);

        try {
            projectMetaData.getPomConfiguration().execute(pom);
        } finally {
            Mockito.verify(pom).getUrl();
            Mockito.verify(urlProperty).set(projectMetaData.getUrl());

            Mockito.verify(scm).getPomConfiguration();
            Mockito.verify(pom).scm(scmAction);

            Mockito.verify(developers).getPomConfiguration();
            Mockito.verify(pom).developers(developersAction);

            Mockito.verify(contributors).getPomConfiguration();
            Mockito.verify(pom).contributors(contributorsAction);

            Mockito.verify(licenses).getPomConfiguration();
            Mockito.verify(pom).licenses(licensesAction);

            Mockito.verifyNoMoreInteractions(
                    pom,
                    urlProperty,
                    scm,
                    developers,
                    contributors,
                    licenses);
        }

    }

    @Test
    public void getTest() throws Exception {
        ProjectMetaData result = new ProjectMetaData("url", SCM, DEVELOPERS, CONTRIBUTORS, LICENSES);

        Assert.assertEquals(result.getUrl(), "url");
        Assert.assertEquals(result.getScm(), SCM);
        Assert.assertEquals(result.getDevelopers(), DEVELOPERS.getDevelopers());
        Assert.assertEquals(result.getContributors(), CONTRIBUTORS.getContributors());
        Assert.assertEquals(result.getLicenses(), LICENSES.getLicenses());
    }

    @Test
    public void hashCodeEqualWhenDataEqual() throws Exception {
        ProjectMetaData result1 = new ProjectMetaData("url", SCM, DEVELOPERS, CONTRIBUTORS, LICENSES);
        ProjectMetaData result2 = new ProjectMetaData("url", SCM, DEVELOPERS, CONTRIBUTORS, LICENSES);

        Assert.assertEquals(result1.hashCode(), result2.hashCode());
    }

    @Test
    public void equalsNull() throws Exception {
        ProjectMetaData result = new ProjectMetaData("url", SCM, DEVELOPERS, CONTRIBUTORS, LICENSES);

        Assert.assertFalse(result.equals(null));
    }

    // Test is specifically for the mis-matched type case - warning is invalid
    @Test
    @SuppressWarnings("unlikely-arg-type")
    public void equalsDifferentClass() throws Exception {
        ProjectMetaData result = new ProjectMetaData("url", SCM, DEVELOPERS, CONTRIBUTORS, LICENSES);

        Assert.assertFalse(result.equals("string"));
    }

    @Test
    public void equalsSelf() throws Exception {
        ProjectMetaData result = new ProjectMetaData("url", SCM, DEVELOPERS, CONTRIBUTORS, LICENSES);

        Assert.assertTrue(result.equals(result));
    }

    @Test
    public void equalsDifferentData() throws Exception {
        ProjectMetaData result1 = new ProjectMetaData("url1", SCM, DEVELOPERS, CONTRIBUTORS, LICENSES);
        ProjectMetaData result2 = new ProjectMetaData("url2", SCM, DEVELOPERS, CONTRIBUTORS, LICENSES);

        Assert.assertFalse(result1.equals(result2));
    }

    @Test
    public void equalsSameData() throws Exception {
        ProjectMetaData result1 = new ProjectMetaData("url", SCM, DEVELOPERS, CONTRIBUTORS, LICENSES);
        ProjectMetaData result2 = new ProjectMetaData("url", SCM, DEVELOPERS, CONTRIBUTORS, LICENSES);

        Assert.assertTrue(result1.equals(result2));
    }

    @Test
    public void toStringTest() throws Exception {
        ProjectMetaData obj = new ProjectMetaData("url", SCM, DEVELOPERS, CONTRIBUTORS, LICENSES);

        String result = obj.toString();

        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains("url=url"));
        Assert.assertTrue(result.contains("scm=" + SCM.toString()));
        Assert.assertTrue(result.contains("developers=" + DEVELOPERS.getDevelopers().toString()));
        Assert.assertTrue(result.contains("contributors=" + CONTRIBUTORS.getContributors().toString()));
        Assert.assertTrue(result.contains("licenses=" + LICENSES.getLicenses().toString()));
    }

    private static final class TestGitHubMetaDataClosure extends Closure<GitHubMetaData> {

        private static final long serialVersionUID = 1L;

        private final String owner;

        private final String repository;

        public TestGitHubMetaDataClosure(String owner, String repository) {
            super(null);

            this.owner = owner;
            this.repository = repository;
        }

        @Override
        public GitHubMetaData call() {
            ((GitHubMetaData) getDelegate()).repository(owner, repository);

            return ((GitHubMetaData) getDelegate());
        }

    }

    private static final class TestScmClosure extends Closure<Scm> {

        private static final long serialVersionUID = 1L;

        private final String vcsUrl;

        public TestScmClosure(String vcsUrl) {
            super(null);

            this.vcsUrl = vcsUrl;
        }

        @Override
        public Scm call() {
            ((Scm) getDelegate()).setVcsUrl(vcsUrl);

            return ((Scm) getDelegate());
        }

    }

    private static final class TestDevelopersClosure extends Closure<DeveloperContainer> {

        private static final long serialVersionUID = 1L;

        private final String id;

        private final String name;

        private final String url;

        public TestDevelopersClosure(String id, String name, String url) {
            super(null);

            this.id = id;
            this.name = name;
            this.url = url;
        }

        @Override
        public DeveloperContainer call() {
            ((DeveloperContainer) getDelegate()).developer(id, name, url);

            return ((DeveloperContainer) getDelegate());
        }

    }

    private static final class TestContributorsClosure extends Closure<ContributorContainer> {

        private static final long serialVersionUID = 1L;

        private final String name;

        private final String url;

        public TestContributorsClosure(String name, String url) {
            super(null);

            this.name = name;
            this.url = url;
        }

        @Override
        public ContributorContainer call() {
            ((ContributorContainer) getDelegate()).contributor(name, url);

            return ((ContributorContainer) getDelegate());
        }

    }

    private static final class TestLicensesClosure extends Closure<LicenseContainer> {

        private static final long serialVersionUID = 1L;

        private final String name;

        private final String tag;

        private final String url;

        private final String distribution;

        public TestLicensesClosure(String name, String tag, String url, String distribution) {
            super(null);

            this.name = name;
            this.tag = tag;
            this.url = url;
            this.distribution = distribution;
        }

        @Override
        public LicenseContainer call() {
            ((LicenseContainer) getDelegate()).license(name, tag, url, distribution);

            return ((LicenseContainer) getDelegate());
        }

    }

}
