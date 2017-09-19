# Module Installation 
The Broadleaf Google Storage module requires [configuration](#configuration-changes) and [third-party property configuration](#third-party-property-configuration)


## Configuration Changes
**Step 0** Add the Broadleaf snapshots repository to your **parent** `pom.xml` if not already there:

```xml
<repository>
    <id>public snapshots</id>
    <name>public snapshots</name>
    <url>http://nexus.broadleafcommerce.org/nexus/content/repositories/snapshots/</url>
</repository>
```

**Step 1.**  Add the dependency management section to your **parent** `pom.xml`:
    
```xml
<dependency>
    <groupId>org.broadleafcommerce</groupId>
    <artifactId>broadleaf-google-storage</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <type>jar</type>
    <scope>compile</scope>
</dependency>
```

**Step 2.**  Add the dependency into your `core/pom.xml`:
    
```xml
<dependency>
    <groupId>org.broadleafcommerce</groupId>
    <artifactId>broadleaf-google-storage</artifactId>
</dependency>
```

## Third Party Property Configuration
This module requires you to configure properties specific to your Google Storage account.   

### Credentials
Broadleaf requires access to your Google Storage account via their client library API. See [Generating Your API Key](https://cloud.google.com/docs/authentication/getting-started) for more information.  

Once you generate and download your JSON API key, you will need to copy the values and add the following properties to your `common-shared.properties` file located in your core project.
 
```properties
broadleaf.google.storage.projectId=<projectId>
broadleaf.google.storage.bucket=<bucket>
broadleaf.google.storage.pathToJsonKeyFile=<pathToJsonFile>
broadleaf.google.storage.subdirectory=<subdirectory>
```

### Storage Location Information 
Broadleaf also needs to know where to upload your files within your Google Storage account. The properties below indicate which container the files should be stored in.

```properties
# The project Id, not name! to upload files to
broadleaf.google.storage.projectId=
# The name of the bucket to upload files to
broadleaf.google.storage.bucket=
# Subdirectory within the bucket that the files should be accessible from
broadleaf.google.storage.subdirectory=
```

> Project and bucket should already be created.
