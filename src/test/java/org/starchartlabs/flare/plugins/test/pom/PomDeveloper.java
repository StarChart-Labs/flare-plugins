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

public class PomDeveloper {

    private String id;

    private String name;

    private String url;

    public PomDeveloper() {

    }

    public PomDeveloper(String id, String name, String url) {
        this.id = id;
        this.name = name;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    @Override
    public int hashCode() {
        return Objects.hash(getId(),
                getName(),
                getUrl());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;

        if (obj instanceof PomDeveloper) {
            PomDeveloper compare = (PomDeveloper) obj;

            result = Objects.equals(compare.getId(), getId())
                    && Objects.equals(compare.getName(), getName())
                    && Objects.equals(compare.getUrl(), getUrl());
        }

        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass()).omitNullValues()
                .add("id", getId())
                .add("name", getName())
                .add("url", getUrl())
                .toString();
    }

}
