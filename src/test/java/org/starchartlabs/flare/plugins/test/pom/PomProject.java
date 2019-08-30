/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.test.pom;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import org.starchartlabs.alloy.core.MoreObjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PomProject {

    private String url;

    private PomScm scm;

    private List<PomDeveloper> developers;

    private List<PomContributor> contributors;

    private List<PomLicense> licenses;

    public PomProject() {

    }

    public PomProject(String url, PomScm scm, List<PomDeveloper> developers, List<PomContributor> contributors,
            List<PomLicense> licenses) {
        this.url = url;
        this.scm = scm;
        this.developers = developers;
        this.contributors = contributors;
        this.licenses = licenses;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public PomScm getScm() {
        return scm;
    }

    public void setScm(PomScm scm) {
        this.scm = scm;
    }

    public List<PomDeveloper> getDevelopers() {
        return developers;
    }

    public void setDevelopers(List<PomDeveloper> developers) {
        this.developers = developers;
    }

    public List<PomContributor> getContributors() {
        return contributors;
    }

    public void setContributors(List<PomContributor> contributors) {
        this.contributors = contributors;
    }

    public List<PomLicense> getLicenses() {
        return licenses;
    }

    public void setLicenses(List<PomLicense> licenses) {
        this.licenses = licenses;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUrl(),
                getScm(),
                getDevelopers(),
                getContributors(),
                getLicenses());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;

        if (obj instanceof PomProject) {
            PomProject compare = (PomProject) obj;

            result = Objects.equals(compare.getUrl(), getUrl())
                    && Objects.equals(compare.getScm(), getScm())
                    && Objects.equals(compare.getDevelopers(), getDevelopers())
                    && Objects.equals(compare.getContributors(), getContributors())
                    && Objects.equals(compare.getLicenses(), getLicenses());
        }

        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass()).omitNullValues()
                .add("url", getUrl())
                .add("scm", getScm())
                .add("developers", getDevelopers())
                .add("contributors", getContributors())
                .add("licenses", getLicenses())
                .toString();
    }

}
