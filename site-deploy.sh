# generate output which is used during site generation
mvn -Prun-its clean verify
mvn site site:stage scm-publish:publish-scm
