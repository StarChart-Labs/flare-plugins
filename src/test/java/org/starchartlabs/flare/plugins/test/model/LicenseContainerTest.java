/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.test.model;

import java.util.Collections;

import org.gradle.api.Action;
import org.gradle.api.publish.maven.MavenPomLicense;
import org.gradle.api.publish.maven.MavenPomLicenseSpec;
import org.mockito.Mockito;
import org.starchartlabs.flare.plugins.model.License;
import org.starchartlabs.flare.plugins.model.LicenseContainer;
import org.testng.Assert;
import org.testng.annotations.Test;

import groovy.lang.Closure;

public class LicenseContainerTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void configureClosureNullClosure() throws Exception {
        new LicenseContainer().configure((Closure<LicenseContainer>) null);
    }

    @Test
    public void configureClosure() throws Exception {
        LicenseContainer result = new LicenseContainer()
                .configure(new TestClosure("name", "tag", "url", "distribution"));

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getLicenses().size(), 1);
        Assert.assertEquals(result.getLicenses().iterator().next(), new License("name", "tag", "url", "distribution"));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void configureActionNullAction() throws Exception {
        new LicenseContainer().configure((Action<LicenseContainer>) null);
    }

    @Test
    public void configureAction() throws Exception {
        LicenseContainer result = new LicenseContainer()
                .configure(container -> container.license("name", "tag", "url", "distribution"));

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getLicenses().size(), 1);
        Assert.assertEquals(result.getLicenses().iterator().next(), new License("name", "tag", "url", "distribution"));
    }

    @Test
    public void licenseFields() throws Exception {
        LicenseContainer result = new LicenseContainer().license("name", "tag", "url", "distribution");

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getLicenses().size(), 1);
        Assert.assertEquals(result.getLicenses().iterator().next(), new License("name", "tag", "url", "distribution"));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void licenseClosureNullClosure() throws Exception {
        new LicenseContainer().license((Closure<License>) null);
    }

    @Test
    public void licenseClosure() throws Exception {
        LicenseContainer result = new LicenseContainer()
                .license(new TestLicenseClosure("name", "tag", "url", "distribution"));

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getLicenses().size(), 1);
        Assert.assertEquals(result.getLicenses().iterator().next(), new License("name", "tag", "url", "distribution"));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void licenseActionNullAction() throws Exception {
        new LicenseContainer().license((Action<License>) null);
    }

    @Test
    public void licenseAction() throws Exception {
        LicenseContainer result = new LicenseContainer().license(license -> {
            license.setName("name");
            license.setTag("tag");
            license.setUrl("url");
            license.setDistribution("distribution");
        });

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getLicenses().size(), 1);
        Assert.assertEquals(result.getLicenses().iterator().next(), new License("name", "tag", "url", "distribution"));
    }

    @Test
    public void apache2() throws Exception {
        LicenseContainer result = new LicenseContainer().apache2();

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getLicenses().size(), 1);
        Assert.assertEquals(result.getLicenses().iterator().next(),
                new License("The Apache Software License, Version 2.0", "Apache 2.0",
                        "http://www.apache.org/licenses/LICENSE-2.0.txt", "repo"));
    }

    @Test
    public void apache2CustomDistribution() throws Exception {
        LicenseContainer result = new LicenseContainer().apache2("distribution");

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getLicenses().size(), 1);
        Assert.assertEquals(result.getLicenses().iterator().next(),
                new License("The Apache Software License, Version 2.0", "Apache 2.0",
                        "http://www.apache.org/licenses/LICENSE-2.0.txt", "distribution"));
    }

    @Test
    public void mit() throws Exception {
        LicenseContainer result = new LicenseContainer().mit();

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getLicenses().size(), 1);
        Assert.assertEquals(result.getLicenses().iterator().next(),
                new License("The MIT License", "MIT", "https://opensource.org/licenses/MIT", "repo"));
    }

    @Test
    public void mitCustomDistribution() throws Exception {
        LicenseContainer result = new LicenseContainer().mit("distribution");

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getLicenses().size(), 1);
        Assert.assertEquals(result.getLicenses().iterator().next(),
                new License("The MIT License", "MIT", "https://opensource.org/licenses/MIT", "distribution"));
    }

    @Test
    public void epl() throws Exception {
        LicenseContainer result = new LicenseContainer().epl();

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getLicenses().size(), 1);
        Assert.assertEquals(result.getLicenses().iterator().next(),
                new License("Eclipse Public License 1.0", "EPL", "https://opensource.org/licenses/EPL-1.0", "repo"));
    }

    @Test
    public void eplCustomDistribution() throws Exception {
        LicenseContainer result = new LicenseContainer().epl("distribution");

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getLicenses().size(), 1);
        Assert.assertEquals(result.getLicenses().iterator().next(),
                new License("Eclipse Public License 1.0", "EPL", "https://opensource.org/licenses/EPL-1.0",
                        "distribution"));
    }

    @Test
    public void getPomConfiguration() throws Exception {
        Action<MavenPomLicense> action = (a -> {
        });
        License license = Mockito.mock(License.class);
        Mockito.when(license.getPomConfiguration()).thenReturn(action);

        MavenPomLicenseSpec pomLicenses = Mockito.mock(MavenPomLicenseSpec.class);

        LicenseContainer licenseContainer = new LicenseContainer();
        licenseContainer.setLicenses(Collections.singletonList(license));

        try {
            licenseContainer.getPomConfiguration().execute(pomLicenses);
        } finally {
            Mockito.verify(license).getPomConfiguration();
            Mockito.verify(pomLicenses).license(action);

            Mockito.verifyNoMoreInteractions(pomLicenses,
                    license);
        }
    }

    private static class TestClosure extends Closure<LicenseContainer> {

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
        public LicenseContainer call() {
            ((LicenseContainer) getDelegate()).license(name, tag, url, distribution);

            return ((LicenseContainer) getDelegate());
        }

    }

    private static class TestLicenseClosure extends Closure<License> {

        private static final long serialVersionUID = 1L;

        private final String name;

        private final String tag;

        private final String url;

        private final String distribution;

        public TestLicenseClosure(String name, String tag, String url, String distribution) {
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
