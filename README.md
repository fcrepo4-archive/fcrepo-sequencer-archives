fcrepo-sequencer-archives
=====================
Modeshape sequencer to process large tar/zip archive files.  Initial
proof-of-concept implementation extracts table of contents and stores as a
separate textfile datastream.


Configuration
-------------

To add this sequencer to a webapp such as kitchen-sink:

1. Build fcrepo-sequencer-archives and install it in your local Maven repository:

    ``` sh
    $ mvn install
    ```

2. In the webapp project, add a dependency to `pom.xml`:

    ``` xml
    <dependency>
      <groupId>org.fcrepo</groupId>
      <artifactId>fcrepo-sequencer-archive</artifactId>
      <version>${project.version}</version>
    </dependency>
    ```

3. And add a sequencing block to `repository.json`:

    ``` json
    "sequencing" : {
      "removeDerivedContentWithOriginal" : true,
      "sequencers" : {
        "ArchiveSequencer" : {
          "classname" : "org.fcrepo.sequencer.archive.ArchiveSequencer",
          "pathExpressions":["fedora://(*.zip)/jcr:content[@jcr:data] => ." ],
          "endpoint" : "http://localhost:8000"
        }
      }
    }, 
    ```
