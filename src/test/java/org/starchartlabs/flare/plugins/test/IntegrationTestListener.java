/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.test;

import org.gradle.testkit.runner.InvalidPluginMetadataException;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

/**
 * Listener which converts Gradle test kit exception to a more useful message
 *
 * @author romeara
 * @since 0.1.0
 */
public class IntegrationTestListener extends TestListenerAdapter {

    @Override
    public void onTestFailure(ITestResult tr) {
        if (tr.getThrowable() instanceof InvalidPluginMetadataException) {
            Assert.fail("Gradle test kit requires '" + getTestName(tr)
                    + "' to be run from Gradle, or Gradle refresh to be run", tr.getThrowable());
        }
    }

    private String getTestName(ITestResult tr) {
        return tr.getMethod().getTestClass().getRealClass().getSimpleName() + "." + tr.getMethod().getMethodName()
                + "()";
    }

}
