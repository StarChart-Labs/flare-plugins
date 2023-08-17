# Collaborator information

## One-time Setup

To publish to the Gradle plug-in portal, first you must [setup an account](https://guides.gradle.org/publishing-plugins-to-gradle-plugin-portal/#create_an_account_on_the_gradle_plugin_portal)

## Releasing

* Update the version number to remove the "-SNAPSHOT" designation. All version numbers should be a fully-qualified semantic version of form `<major>.<minor>.<micro>`
* Change the header "Unreleased" in CHANGE_LOG.md to the target release number, and create a new "Unreleased" header above it
* Run a full build via `./gradlew clean build`
  * If there are any errors, stash the changes to the version number and changelog until the issue can be corrected and merged to master as a separate commit/issue
* Commit the version number and CHANGE_LOG updates
* Tag the git repository with the fully-qualified semantic version number
* Push commit and tag to GitHub
* Run the "Publish" workflow on the tag/commited branch, which will automatically:
  * Publish plug-ins via `./gradlew publishPlugins`
* Verify each plug-in deployed by visiting its URL (of the form `https://plugins.gradle.org/plugin/<your-plugin-id>`)
* Change version number to `<released version> + 1 micro` + `-SNAPSHOT`
* Commit to git
* Push changes and tag to GitHub
* Create a release on GitHub including all binary and source jars
  * The release description should include the release notes for that version
* Change the `next-release` milestone to the released version number, move any unresolved tickets/pull-requests to a new `next-release` milestone, and close the version'd milestone
  * The milestone description should include the release notes for that version
