# Migrating from flare-publishing-plugins

Previously, StarChart Labs provided the flare-publishing-plugins project for a sub-set of the Gradle plug-ins which have since been moved into the flare-plugins project. This guide will help transition from the old project to flare-plugins

It is recommended that users evaluate if one of the pre-defined conventions fits their needs - using one of these conventions is the recommended use pattern. If not, individual plug-ins can be migrated as follows:

## org.starchartlabs.flare.source-jars

No change aside from switch the imported library is required - the plug-in ID is unchanged

## org.starchartlabs.flare.pom-source-jar-artifacts

The functionality of the `pom-source-jar-artifacts` plug-in has been combined with the `source-jars` plug-in, and will be conditionally applied if the `maven-publish` plug-in is used

## org.starchartlabs.flare.published-info-base

The DSL of the published info plug-in has been re-designed, and is now the metadata-base plug in. See the [new plug-ins](./PLUGINS.md) documentation for details on the new DSL form. All previous `publishedInfo` values are supported

### Example DSL Migration

*Legacy DSL*

```
publishedInfo{
   url 'http://something'
        
   scm{
      github 'StarChart-Labs', 'flare-plugins'
   }
        
   licenses{
      mit 'repo'
   }
        
   developers{
      github 'octocat', "Octo Cat"
      developer 'id', 'name', 'url'
   }
}
```

*Equivalent New DSL*

```
projectMetaData{
	url = 'http://something'
  
  github {
     repository 'StarChart-Labs', 'flare-plugins'
     
     developer 'octocat', "Octo Cat"
  }
	
	developers {
		developer 'id', 'name', 'url'
	}
	
	licenses {
		mit 'repo'
	}
}
```

## org.starchartlabs.flare.pom-published-info

The DSL applied has been switched to the project meta-data DSL - otherwise, the only change necessary is to update the plug-in ID to `org.starchartlabs.flare.metadata-pom`
