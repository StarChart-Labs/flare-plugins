/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.test.model;

import org.gradle.api.Action;
import org.gradle.api.provider.Property;
import org.gradle.api.publish.maven.MavenPomContributor;
import org.mockito.Mockito;
import org.starchartlabs.flare.plugins.model.Contributor;
import org.testng.Assert;
import org.testng.annotations.Test;

import groovy.lang.Closure;

public class ContributorTest {

    @Test
    public void constructNullName() throws Exception {
        Contributor result = new Contributor(null, "url");

        Assert.assertNull(result.getName());
    }

    @Test
    public void constructNullUrl() throws Exception {
        Contributor result = new Contributor("name", null);

        Assert.assertNull(result.getUrl());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void configureClosureNullClosure() throws Exception {
        new Contributor("name", "url").configure((Closure<Contributor>) null);
    }

    @Test
    public void configureClosure() throws Exception {
        Contributor result = new Contributor().configure(new TestClosure("name", "url"));

        Assert.assertEquals(result.getName(), "name");
        Assert.assertEquals(result.getUrl(), "url");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void configureActionNullAction() throws Exception {
        new Contributor("name", "url").configure((Action<Contributor>) null);
    }

    @Test
    public void configureAction() throws Exception {
        Contributor result = new Contributor().configure(contributor -> {
            contributor.setName("name");
            contributor.setUrl("url");
        });

        Assert.assertEquals(result.getName(), "name");
        Assert.assertEquals(result.getUrl(), "url");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getPomConfiguration() throws Exception {
        Property<String> nameProperty = Mockito.mock(Property.class);
        Property<String> urlProperty = Mockito.mock(Property.class);

        MavenPomContributor pomContributor = Mockito.mock(MavenPomContributor.class);
        Mockito.when(pomContributor.getName()).thenReturn(nameProperty);
        Mockito.when(pomContributor.getUrl()).thenReturn(urlProperty);

        Contributor contributor = new Contributor("name", "url");

        try {
            Action<MavenPomContributor> pomConfiguration = contributor.getPomConfiguration();

            Assert.assertNotNull(pomConfiguration);

            pomConfiguration.execute(pomContributor);
        } finally {
            Mockito.verify(pomContributor).getName();
            Mockito.verify(pomContributor).getUrl();

            Mockito.verify(nameProperty).set(contributor.getName());
            Mockito.verify(urlProperty).set(contributor.getUrl());
        }
    }

    @Test
    public void getTest() throws Exception {
        Contributor result = new Contributor("name", "url");

        Assert.assertEquals(result.getName(), "name");
        Assert.assertEquals(result.getUrl(), "url");
    }

    @Test
    public void hashCodeEqualWhenDataEqual() throws Exception {
        Contributor result1 = new Contributor("name", "url");
        Contributor result2 = new Contributor("name", "url");

        Assert.assertEquals(result1.hashCode(), result2.hashCode());
    }

    @Test
    public void equalsNull() throws Exception {
        Contributor result = new Contributor("name", "url");

        Assert.assertFalse(result.equals(null));
    }

    // Test is specifically for the mis-matched type case - warning is invalid
    @Test
    @SuppressWarnings("unlikely-arg-type")
    public void equalsDifferentClass() throws Exception {
        Contributor result = new Contributor("name", "url");

        Assert.assertFalse(result.equals("string"));
    }

    @Test
    public void equalsSelf() throws Exception {
        Contributor result = new Contributor("name", "url");

        Assert.assertTrue(result.equals(result));
    }

    @Test
    public void equalsDifferentData() throws Exception {
        Contributor result1 = new Contributor("name1", "url");
        Contributor result2 = new Contributor("name2", "url");

        Assert.assertFalse(result1.equals(result2));
    }

    @Test
    public void equalsSameData() throws Exception {
        Contributor result1 = new Contributor("name", "url");
        Contributor result2 = new Contributor("name", "url");

        Assert.assertTrue(result1.equals(result2));
    }

    @Test
    public void toStringTest() throws Exception {
        Contributor obj = new Contributor("name", "url");

        String result = obj.toString();

        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains("name=name"));
        Assert.assertTrue(result.contains("url=url"));
    }

    private static class TestClosure extends Closure<Contributor> {

        private static final long serialVersionUID = 1L;

        private final String name;

        private final String url;

        public TestClosure(String name, String url) {
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
