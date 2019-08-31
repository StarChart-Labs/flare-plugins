/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.test.model;

import org.gradle.api.Action;
import org.gradle.api.provider.Property;
import org.gradle.api.publish.maven.MavenPomDeveloper;
import org.mockito.Mockito;
import org.starchartlabs.flare.plugins.model.Developer;
import org.testng.Assert;
import org.testng.annotations.Test;

import groovy.lang.Closure;

public class DeveloperTest {

    @Test
    public void constructNullId() throws Exception {
        Developer result = new Developer(null, "name", "url");

        Assert.assertNull(result.getId());
    }

    @Test
    public void constructNullName() throws Exception {
        Developer result = new Developer("id", null, "url");

        Assert.assertNull(result.getName());
    }

    @Test
    public void constructNullUrl() throws Exception {
        Developer result = new Developer("id", "name", null);

        Assert.assertNull(result.getUrl());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void configureClosureNullClosure() throws Exception {
        new Developer().configure((Closure<Developer>) null);
    }

    @Test
    public void configureClosure() throws Exception {
        Developer result = new Developer().configure(new TestClosure("id", "name", "url"));

        Assert.assertEquals(result.getId(), "id");
        Assert.assertEquals(result.getName(), "name");
        Assert.assertEquals(result.getUrl(), "url");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void configureActionNullAction() throws Exception {
        new Developer().configure((Action<Developer>) null);
    }

    @Test
    public void configureAction() throws Exception {
        Developer result = new Developer().configure(developer -> {
            developer.setId("id");
            developer.setName("name");
            developer.setUrl("url");
        });

        Assert.assertEquals(result.getId(), "id");
        Assert.assertEquals(result.getName(), "name");
        Assert.assertEquals(result.getUrl(), "url");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getPomConfiguration() throws Exception {
        Property<String> idProperty = Mockito.mock(Property.class);
        Property<String> nameProperty = Mockito.mock(Property.class);
        Property<String> urlProperty = Mockito.mock(Property.class);

        MavenPomDeveloper pomDeveloper = Mockito.mock(MavenPomDeveloper.class);
        Mockito.when(pomDeveloper.getId()).thenReturn(idProperty);
        Mockito.when(pomDeveloper.getName()).thenReturn(nameProperty);
        Mockito.when(pomDeveloper.getUrl()).thenReturn(urlProperty);

        Developer developer = new Developer("id", "name", "url");

        try {
            Action<MavenPomDeveloper> pomConfiguration = developer.getPomConfiguration();

            Assert.assertNotNull(pomConfiguration);

            pomConfiguration.execute(pomDeveloper);
        } finally {
            Mockito.verify(pomDeveloper).getId();
            Mockito.verify(pomDeveloper).getName();
            Mockito.verify(pomDeveloper).getUrl();

            Mockito.verify(idProperty).set(developer.getId());
            Mockito.verify(nameProperty).set(developer.getName());
            Mockito.verify(urlProperty).set(developer.getUrl());
        }
    }

    @Test
    public void getTest() throws Exception {
        Developer result = new Developer("id", "name", "url");

        Assert.assertEquals(result.getId(), "id");
        Assert.assertEquals(result.getName(), "name");
        Assert.assertEquals(result.getUrl(), "url");
    }

    @Test
    public void hashCodeEqualWhenDataEqual() throws Exception {
        Developer result1 = new Developer("id", "name", "url");
        Developer result2 = new Developer("id", "name", "url");

        Assert.assertEquals(result1.hashCode(), result2.hashCode());
    }

    @Test
    public void equalsNull() throws Exception {
        Developer result = new Developer("id", "name", "url");

        Assert.assertFalse(result.equals(null));
    }

    // Test is specifically for the mis-matched type case - warning is invalid
    @Test
    @SuppressWarnings("unlikely-arg-type")
    public void equalsDifferentClass() throws Exception {
        Developer result = new Developer("id", "name", "url");

        Assert.assertFalse(result.equals("string"));
    }

    @Test
    public void equalsSelf() throws Exception {
        Developer result = new Developer("id", "name", "url");

        Assert.assertTrue(result.equals(result));
    }

    @Test
    public void equalsDifferentData() throws Exception {
        Developer result1 = new Developer("id1", "name", "url");
        Developer result2 = new Developer("id2", "name", "url");

        Assert.assertFalse(result1.equals(result2));
    }

    @Test
    public void equalsSameData() throws Exception {
        Developer result1 = new Developer("id", "name", "url");
        Developer result2 = new Developer("id", "name", "url");

        Assert.assertTrue(result1.equals(result2));
    }

    @Test
    public void toStringTest() throws Exception {
        Developer obj = new Developer("id", "name", "url");

        String result = obj.toString();

        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains("id=id"));
        Assert.assertTrue(result.contains("name=name"));
        Assert.assertTrue(result.contains("url=url"));
    }

    private static class TestClosure extends Closure<Developer> {

        private static final long serialVersionUID = 1L;

        private final String id;

        private final String name;

        private final String url;

        public TestClosure(String id, String name, String url) {
            super(null);

            this.id = id;
            this.name = name;
            this.url = url;
        }

        @Override
        public Developer call() {
            ((Developer) getDelegate()).setId(id);
            ((Developer) getDelegate()).setName(name);
            ((Developer) getDelegate()).setUrl(url);

            return ((Developer) getDelegate());
        }

    }

}
