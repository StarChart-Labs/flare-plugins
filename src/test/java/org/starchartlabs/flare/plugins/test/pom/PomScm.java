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

public class PomScm {

    private String url;

    private String connection;

    private String developerConnection;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getConnection() {
        return connection;
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }

    public String getDeveloperConnection() {
        return developerConnection;
    }

    public void setDeveloperConnection(String developerConnection) {
        this.developerConnection = developerConnection;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUrl(),
                getConnection(),
                getDeveloperConnection());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;

        if (obj instanceof PomScm) {
            PomScm compare = (PomScm) obj;

            result = Objects.equals(compare.getUrl(), getUrl())
                    && Objects.equals(compare.getConnection(), getConnection())
                    && Objects.equals(compare.getDeveloperConnection(), getDeveloperConnection());
        }

        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass()).omitNullValues()
                .add("url", getUrl())
                .add("connection", getConnection())
                .add("developerConnection", getDeveloperConnection())
                .toString();
    }

}
