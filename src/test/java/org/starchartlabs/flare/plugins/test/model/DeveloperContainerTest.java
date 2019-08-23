/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.test.model;

import org.gradle.api.Action;
import org.starchartlabs.flare.plugins.model.Developer;
import org.starchartlabs.flare.plugins.model.DeveloperContainer;
import org.testng.Assert;
import org.testng.annotations.Test;

import groovy.lang.Closure;

public class DeveloperContainerTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void configureClosureNullClosure() throws Exception {
        new DeveloperContainer().configure((Closure<DeveloperContainer>) null);
    }

    @Test
    public void configureClosure() throws Exception {
        DeveloperContainer result = new DeveloperContainer().configure(new TestClosure("id", "name", "url"));

        Assert.assertEquals(result.getDevelopers().size(), 1);
        Assert.assertEquals(result.getDevelopers().iterator().next(), new Developer("id", "name", "url"));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void configureActionNullAction() throws Exception {
        new DeveloperContainer().configure((Action<DeveloperContainer>) null);
    }

    @Test
    public void configureAction() throws Exception {
        DeveloperContainer result = new DeveloperContainer().configure(container -> {
            container.developer("id", "name", "url");
        });

        Assert.assertEquals(result.getDevelopers().size(), 1);
        Assert.assertEquals(result.getDevelopers().iterator().next(), new Developer("id", "name", "url"));
    }

    @Test
    public void contributorFields() throws Exception {
        DeveloperContainer result = new DeveloperContainer().developer("id", "name", "url");

        Assert.assertEquals(result.getDevelopers().size(), 1);
        Assert.assertEquals(result.getDevelopers().iterator().next(), new Developer("id", "name", "url"));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void contributorClosureNullClosure() throws Exception {
        new DeveloperContainer().developer((Closure<Developer>) null);
    }

    @Test
    public void coontributorClosure() throws Exception {
        DeveloperContainer result = new DeveloperContainer()
                .developer(new TestDeveloperClosure("id", "name", "url"));

        Assert.assertEquals(result.getDevelopers().size(), 1);
        Assert.assertEquals(result.getDevelopers().iterator().next(), new Developer("id", "name", "url"));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void contributorActionNullAction() throws Exception {
        new DeveloperContainer().developer((Action<Developer>) null);
    }

    @Test
    public void contributorAction() throws Exception {
        DeveloperContainer result = new DeveloperContainer().developer(developer -> {
            developer.setId("id");
            developer.setName("name");
            developer.setUrl("url");
        });

        Assert.assertEquals(result.getDevelopers().size(), 1);
        Assert.assertEquals(result.getDevelopers().iterator().next(), new Developer("id", "name", "url"));
    }

    @Test
    public void getTest() throws Exception {
        DeveloperContainer result = new DeveloperContainer().developer("id", "name", "url");

        Assert.assertEquals(result.getDevelopers().size(), 1);
        Assert.assertEquals(result.getDevelopers().iterator().next(), new Developer("id", "name", "url"));
    }

    @Test
    public void hashCodeEqualWhenDataEqual() throws Exception {
        DeveloperContainer result1 = new DeveloperContainer().developer("id", "name", "url");
        DeveloperContainer result2 = new DeveloperContainer().developer("id", "name", "url");

        Assert.assertEquals(result1.hashCode(), result2.hashCode());
    }

    @Test
    public void equalsNull() throws Exception {
        DeveloperContainer result = new DeveloperContainer().developer("id", "name", "url");

        Assert.assertFalse(result.equals(null));
    }

    // Test is specifically for the mis-matched type case - warning is invalid
    @Test
    @SuppressWarnings("unlikely-arg-type")
    public void equalsDifferentClass() throws Exception {
        DeveloperContainer result = new DeveloperContainer().developer("id", "name", "url");

        Assert.assertFalse(result.equals("string"));
    }

    @Test
    public void equalsSelf() throws Exception {
        DeveloperContainer result = new DeveloperContainer().developer("id", "name", "url");

        Assert.assertTrue(result.equals(result));
    }

    @Test
    public void equalsDifferentData() throws Exception {
        DeveloperContainer result1 = new DeveloperContainer().developer("id1", "name", "url");
        DeveloperContainer result2 = new DeveloperContainer().developer("id2", "name", "url");

        Assert.assertFalse(result1.equals(result2));
    }

    @Test
    public void equalsSameData() throws Exception {
        DeveloperContainer result1 = new DeveloperContainer().developer("id", "name", "url");
        DeveloperContainer result2 = new DeveloperContainer().developer("id", "name", "url");

        Assert.assertTrue(result1.equals(result2));
    }

    @Test
    public void toStringTest() throws Exception {
        DeveloperContainer obj = new DeveloperContainer().developer("id", "name", "url");

        String result = obj.toString();

        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains("developers"));
        Assert.assertTrue(result.contains("id=id"));
        Assert.assertTrue(result.contains("name=name"));
        Assert.assertTrue(result.contains("url=url"));
    }

    private static class TestClosure extends Closure<DeveloperContainer> {

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
        public DeveloperContainer call() {
            ((DeveloperContainer) getDelegate()).developer(id, name, url);

            return ((DeveloperContainer) getDelegate());
        }

    }

    private static class TestDeveloperClosure extends Closure<Developer> {

        private static final long serialVersionUID = 1L;

        private final String id;

        private final String name;

        private final String url;

        public TestDeveloperClosure(String id, String name, String url) {
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
