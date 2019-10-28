build:
	mvn clean install

deploy-snapshot:
	gpg --fast-import cd/gpg.asc
	mvn deploy -P release --settings ./cd/maven-settings.xml -DskipTests


deploy:
	gpg --fast-import cd/gpg.asc
	mvn versions:set -DnewVersion=${TRAVIS_TAG}
	mvn deploy -P release --settings ./cd/maven-settings.xml -DskipTests