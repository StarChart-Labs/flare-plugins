/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.test.pom;

import java.util.Objects;

import javax.annotation.Nullable;

import org.starchartlabs.alloy.core.MoreObjects;

public class PomLicense {

    private String name;

    private String url;

    private String distribution;

    public PomLicense() {

    }

    public PomLicense(String name, String url, String distribution) {
        this.name = name;
        this.url = url;
        this.distribution = distribution;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDistribution() {
        return distribution;
    }

    public void setDistribution(String distribution) {
        this.distribution = distribution;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(),
                getUrl(),
                getDistribution());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;

        if (obj instanceof PomLicense) {
            PomLicense compare = (PomLicense) obj;

            result = Objects.equals(compare.getName(), getName())
                    && Objects.equals(compare.getUrl(), getUrl())
                    && Objects.equals(compare.getDistribution(), getDistribution());
        }

        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass()).omitNullValues()
                .add("name", getName())
                .add("url", getUrl())
                .add("distribution", getDistribution())
                .toString();
    }

}
