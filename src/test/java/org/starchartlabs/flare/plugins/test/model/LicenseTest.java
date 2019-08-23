/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.test.model;

import org.gradle.api.Action;
import org.starchartlabs.flare.plugins.model.License;
import org.testng.Assert;
import org.testng.annotations.Test;

import groovy.lang.Closure;

public class LicenseTest {

    @Test
    public void constructNullName() throws Exception {
        License result = new License(null, "tag", "url", "distribution");

        Assert.assertNull(result.getName());
    }

    @Test
    public void constructNullTag() throws Exception {
        License result = new License("name", null, "url", "distribution");

        Assert.assertNull(result.getTag());
    }

    @Test
    public void constructNullUrl() throws Exception {
        License result = new License("name", "tag", null, "distribution");

        Assert.assertNull(result.getUrl());
    }

    @Test
    public void constructNullDistribution() throws Exception {
        License result = new License("name", "tag", "url", null);

        Assert.assertNull(result.getDistribution());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void configureClosureNullClosure() throws Exception {
        new License().configure((Closure<License>) null);
    }

    @Test
    public void configureClosure() throws Exception {
        License result = new License().configure(new TestClosure("name", "tag", "url", "distribution"));

        Assert.assertEquals(result.getName(), "name");
        Assert.assertEquals(result.getTag(), "tag");
        Assert.assertEquals(result.getUrl(), "url");
        Assert.assertEquals(result.getDistribution(), "distribution");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void configureActionNullAction() throws Exception {
        new License().configure((Action<License>) null);
    }

    @Test
    public void configureAction() throws Exception {
        License result = new License().configure(license -> {
            license.setName("name");
            license.setTag("tag");
            license.setUrl("url");
            license.setDistribution("distribution");
        });

        Assert.assertEquals(result.getName(), "name");
        Assert.assertEquals(result.getTag(), "tag");
        Assert.assertEquals(result.getUrl(), "url");
        Assert.assertEquals(result.getDistribution(), "distribution");
    }

    @Test
    public void getTest() throws Exception {
        License result = new License("name", "tag", "url", "distribution");

        Assert.assertEquals(result.getName(), "name");
        Assert.assertEquals(result.getTag(), "tag");
        Assert.assertEquals(result.getUrl(), "url");
        Assert.assertEquals(result.getDistribution(), "distribution");
    }

    @Test
    public void hashCodeEqualWhenDataEqual() throws Exception {
        License result1 = new License("name", "tag", "url", "distribution");
        License result2 = new License("name", "tag", "url", "distribution");

        Assert.assertEquals(result1.hashCode(), result2.hashCode());
    }

    @Test
    public void equalsNull() throws Exception {
        License result = new License("name", "tag", "url", "distribution");

        Assert.assertFalse(result.equals(null));
    }

    // Test is specifically for the mis-matched type case - warning is invalid
    @Test
    @SuppressWarnings("unlikely-arg-type")
    public void equalsDifferentClass() throws Exception {
        License result = new License("name", "tag", "url", "distribution");

        Assert.assertFalse(result.equals("string"));
    }

    @Test
    public void equalsSelf() throws Exception {
        License result = new License("name", "tag", "url", "distribution");

        Assert.assertTrue(result.equals(result));
    }

    @Test
    public void equalsDifferentData() throws Exception {
        License result1 = new License("name1", "tag", "url", "distribution");
        License result2 = new License("name2", "tag", "url", "distribution");

        Assert.assertFalse(result1.equals(result2));
    }

    @Test
    public void equalsSameData() throws Exception {
        License result1 = new License("name", "tag", "url", "distribution");
        License result2 = new License("name", "tag", "url", "distribution");

        Assert.assertTrue(result1.equals(result2));
    }

    @Test
    public void toStringTest() throws Exception {
        License obj = new License("name", "tag", "url", "distribution");

        String result = obj.toString();

        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains("name=name"));
        Assert.assertTrue(result.contains("tag=tag"));
        Assert.assertTrue(result.contains("url=url"));
        Assert.assertTrue(result.contains("distribution=distribution"));
    }

    private static class TestClosure extends Closure<License> {

        private static final long serialVersionUID = 1L;

        private final String name;

        private final String tag;

        private final String url;

        private final String distribution;

        public TestClosure(String name, String tag, String url, String distribution) {
            super(null);

            this.name = name;
            this.tag = tag;
            this.url = url;
            this.distribution = distribution;
        }

        @Override
        public License call() {
            ((License) getDelegate()).setName(name);
            ((License) getDelegate()).setTag(tag);
            ((License) getDelegate()).setUrl(url);
            ((License) getDelegate()).setDistribution(distribution);

            return ((License) getDelegate());
        }

    }

}
