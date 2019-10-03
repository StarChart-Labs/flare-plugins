# Major Version Migration Notes

This details how to adjust use of `flare-plugins` when upgrading between major version releases

## 0.x to 1.x

### BinTray Credential Change

- The `bintray` credentials provided by the `multi-module-library` plug-in have been given a default of empty. If you were relying on the behavior of the build failing when the `BINTRAY_USER` or `BINTRAY_API_KEY` was not provided, your build will have to be adjusted to either define its own credential set without a default, or account for the lack of failure in that scenario
- If you had used any workarounds when utilizing the `bintray` credentials to prevent their loading unless a release was being performed to prevent failure when the `BINTRAY_USER` or `BINTRAY_API_KEY` was not provided, these workaround(s) may now be removed
- The `bintray` credentials provided by the `multi-module-library` plug-in have been made conditional on the inclusion of `com.jfrog.bintray` plug-in. If you depended on these credentials always being available outside use of the JFrog plug-in, apply the `org.starchartlabs.flare.bintray-credentials` plug-in to the projects that require it
