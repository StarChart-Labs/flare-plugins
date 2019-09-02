/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.test.model;

import java.util.Collections;

import org.gradle.api.Action;
import org.gradle.api.publish.maven.MavenPomContributor;
import org.gradle.api.publish.maven.MavenPomContributorSpec;
import org.mockito.Mockito;
import org.starchartlabs.flare.plugins.model.Contributor;
import org.starchartlabs.flare.plugins.model.ContributorContainer;
import org.testng.Assert;
import org.testng.annotations.Test;

import groovy.lang.Closure;

public class ContributorContrainerTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void configureClosureNullClosure() throws Exception {
        new ContributorContainer().configure((Closure<ContributorContainer>) null);
    }

    @Test
    public void configureClosure() throws Exception {
        ContributorContainer result = new ContributorContainer().configure(new TestClosure("name", "url"));

        Assert.assertEquals(result.getContributors().size(), 1);
        Assert.assertEquals(result.getContributors().iterator().next(), new Contributor("name", "url"));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void configureActionNullAction() throws Exception {
        new ContributorContainer().configure((Action<ContributorContainer>) null);
    }

    @Test
    public void configureAction() throws Exception {
        ContributorContainer result = new ContributorContainer().configure(container -> {
            container.contributor("name", "url");
        });

        Assert.assertEquals(result.getContributors().size(), 1);
        Assert.assertEquals(result.getContributors().iterator().next(), new Contributor("name", "url"));
    }

    @Test
    public void contributorFields() throws Exception {
        ContributorContainer result = new ContributorContainer().contributor("name", "url");

        Assert.assertEquals(result.getContributors().size(), 1);
        Assert.assertEquals(result.getContributors().iterator().next(), new Contributor("name", "url"));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void contributorClosureNullClosure() throws Exception {
        new ContributorContainer().contributor((Closure<Contributor>) null);
    }

    @Test
    public void coontributorClosure() throws Exception {
        ContributorContainer result = new ContributorContainer()
                .contributor(new TestContributorClosure("name", "url"));

        Assert.assertEquals(result.getContributors().size(), 1);
        Assert.assertEquals(result.getContributors().iterator().next(), new Contributor("name", "url"));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void contributorActionNullAction() throws Exception {
        new ContributorContainer().contributor((Action<Contributor>) null);
    }

    @Test
    public void contributorAction() throws Exception {
        ContributorContainer result = new ContributorContainer().contributor(contributor -> {
            contributor.setName("name");
            contributor.setUrl("url");
        });

        Assert.assertEquals(result.getContributors().size(), 1);
        Assert.assertEquals(result.getContributors().iterator().next(), new Contributor("name", "url"));
    }

    @Test
    public void getPomConfiguration() throws Exception {
        Action<MavenPomContributor> action = (a -> {
        });
        Contributor contributor = Mockito.mock(Contributor.class);
        Mockito.when(contributor.getPomConfiguration()).thenReturn(action);

        MavenPomContributorSpec pomContributors = Mockito.mock(MavenPomContributorSpec.class);

        ContributorContainer developerContainer = new ContributorContainer();
        developerContainer.setContributors(Collections.singletonList(contributor));

        try {
            developerContainer.getPomConfiguration().execute(pomContributors);
        } finally {
            Mockito.verify(contributor).getPomConfiguration();
            Mockito.verify(pomContributors).contributor(action);

            Mockito.verifyNoMoreInteractions(pomContributors,
                    contributor);
        }
    }

    @Test
    public void getTest() throws Exception {
        ContributorContainer result = new ContributorContainer().contributor("name", "url");

        Assert.assertEquals(result.getContributors().size(), 1);
        Assert.assertEquals(result.getContributors().iterator().next(), new Contributor("name", "url"));
    }

    @Test
    public void hashCodeEqualWhenDataEqual() throws Exception {
        ContributorContainer result1 = new ContributorContainer().contributor("name", "url");
        ContributorContainer result2 = new ContributorContainer().contributor("name", "url");

        Assert.assertEquals(result1.hashCode(), result2.hashCode());
    }

    @Test
    public void equalsNull() throws Exception {
        ContributorContainer result = new ContributorContainer().contributor("name", "url");

        Assert.assertFalse(result.equals(null));
    }

    // Test is specifically for the mis-matched type case - warning is invalid
    @Test
    @SuppressWarnings("unlikely-arg-type")
    public void equalsDifferentClass() throws Exception {
        ContributorContainer result = new ContributorContainer().contributor("name", "url");

        Assert.assertFalse(result.equals("string"));
    }

    @Test
    public void equalsSelf() throws Exception {
        ContributorContainer result = new ContributorContainer().contributor("name", "url");

        Assert.assertTrue(result.equals(result));
    }

    @Test
    public void equalsDifferentData() throws Exception {
        ContributorContainer result1 = new ContributorContainer().contributor("name1", "url");
        ContributorContainer result2 = new ContributorContainer().contributor("name2", "url");

        Assert.assertFalse(result1.equals(result2));
    }

    @Test
    public void equalsSameData() throws Exception {
        ContributorContainer result1 = new ContributorContainer().contributor("name", "url");
        ContributorContainer result2 = new ContributorContainer().contributor("name", "url");

        Assert.assertTrue(result1.equals(result2));
    }

    @Test
    public void toStringTest() throws Exception {
        ContributorContainer obj = new ContributorContainer().contributor("name", "url");

        String result = obj.toString();

        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains("contributors"));
        Assert.assertTrue(result.contains("name=name"));
        Assert.assertTrue(result.contains("url=url"));
    }

    private static class TestClosure extends Closure<ContributorContainer> {

        private static final long serialVersionUID = 1L;

        private final String name;

        private final String url;

        public TestClosure(String name, String url) {
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

    private static class TestContributorClosure extends Closure<Contributor> {

        private static final long serialVersionUID = 1L;

        private final String name;

        private final String url;

        public TestContributorClosure(String name, String url) {
            super(null);

            this.name = name;
            this.url = url;
        }

        @Override
        public Contributor call() {
            ((Contributor) getDelegate()).setName(name);
            ((Contributor) getDelegate()).setUrl(url);

            return ((Contributor) getDelegate());
        }

    }

}
