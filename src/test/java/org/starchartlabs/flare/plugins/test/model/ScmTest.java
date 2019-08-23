/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.test.model;

import org.gradle.api.Action;
import org.starchartlabs.flare.plugins.model.Scm;
import org.testng.Assert;
import org.testng.annotations.Test;

import groovy.lang.Closure;

public class ScmTest {

    @Test
    public void constructNullVcsUrl() throws Exception {
        Scm result = new Scm(null, "connection", "developerConnection");

        Assert.assertNull(result.getVcsUrl());
    }

    @Test
    public void constructNullConnection() throws Exception {
        Scm result = new Scm("vcsUrl", null, "developerConnection");

        Assert.assertNull(result.getConnection());
    }

    @Test
    public void constructNullDeveloperConnection() throws Exception {
        Scm result = new Scm("vcsUrl", "connection", null);

        Assert.assertNull(result.getDeveloperConnection());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void configureClosureNullClosure() throws Exception {
        new Scm().configure((Closure<Scm>) null);
    }

    @Test
    public void configureClosure() throws Exception {
        Scm result = new Scm().configure(new TestClosure("vcsUrl", "connection", "developerConnection"));

        Assert.assertEquals(result.getVcsUrl(), "vcsUrl");
        Assert.assertEquals(result.getConnection(), "connection");
        Assert.assertEquals(result.getDeveloperConnection(), "developerConnection");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void configureActionNullAction() throws Exception {
        new Scm().configure((Action<Scm>) null);
    }

    @Test
    public void configureAction() throws Exception {
        Scm result = new Scm().configure(scm -> {
            scm.setVcsUrl("vcsUrl");
            scm.setConnection("connection");
            scm.setDeveloperConnection("developerConnection");
        });

        Assert.assertEquals(result.getVcsUrl(), "vcsUrl");
        Assert.assertEquals(result.getConnection(), "connection");
        Assert.assertEquals(result.getDeveloperConnection(), "developerConnection");
    }

    @Test
    public void getTest() throws Exception {
        Scm result = new Scm("vcsUrl", "connection", "developerConnection");

        Assert.assertEquals(result.getVcsUrl(), "vcsUrl");
        Assert.assertEquals(result.getConnection(), "connection");
        Assert.assertEquals(result.getDeveloperConnection(), "developerConnection");
    }

    @Test
    public void hashCodeEqualWhenDataEqual() throws Exception {
        Scm result1 = new Scm("vcsUrl", "connection", "developerConnection");
        Scm result2 = new Scm("vcsUrl", "connection", "developerConnection");

        Assert.assertEquals(result1.hashCode(), result2.hashCode());
    }

    @Test
    public void equalsNull() throws Exception {
        Scm result = new Scm("vcsUrl", "connection", "developerConnection");

        Assert.assertFalse(result.equals(null));
    }

    // Test is specifically for the mis-matched type case - warning is invalid
    @Test
    @SuppressWarnings("unlikely-arg-type")
    public void equalsDifferentClass() throws Exception {
        Scm result = new Scm("vcsUrl", "connection", "developerConnection");

        Assert.assertFalse(result.equals("string"));
    }

    @Test
    public void equalsSelf() throws Exception {
        Scm result = new Scm("vcsUrl", "connection", "developerConnection");

        Assert.assertTrue(result.equals(result));
    }

    @Test
    public void equalsDifferentData() throws Exception {
        Scm result1 = new Scm("vcsUrl1", "connection", "developerConnection");
        Scm result2 = new Scm("vcsUrl2", "connection", "developerConnection");

        Assert.assertFalse(result1.equals(result2));
    }

    @Test
    public void equalsSameData() throws Exception {
        Scm result1 = new Scm("vcsUrl", "connection", "developerConnection");
        Scm result2 = new Scm("vcsUrl", "connection", "developerConnection");

        Assert.assertTrue(result1.equals(result2));
    }

    @Test
    public void toStringTest() throws Exception {
        Scm obj = new Scm("vcsUrl", "connection", "developerConnection");

        String result = obj.toString();

        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains("vcsUrl=vcsUrl"));
        Assert.assertTrue(result.contains("connection=connection"));
        Assert.assertTrue(result.contains("developerConnection=developerConnection"));
    }

    private static class TestClosure extends Closure<Scm> {

        private static final long serialVersionUID = 1L;

        private final String vcsUrl;

        private final String connection;

        private final String developerConnection;

        public TestClosure(String vcsUrl, String connection, String developerConnection) {
            super(null);

            this.vcsUrl = vcsUrl;
            this.connection = connection;
            this.developerConnection = developerConnection;
        }

        @Override
        public Scm call() {
            ((Scm) getDelegate()).setVcsUrl(vcsUrl);
            ((Scm) getDelegate()).setConnection(connection);
            ((Scm) getDelegate()).setDeveloperConnection(developerConnection);

            return ((Scm) getDelegate());
        }

    }

}
