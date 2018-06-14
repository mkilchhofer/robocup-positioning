#!/bin/bash
export MAVEN_OPTS=-Drevision=$(date +%Y%m%d)-SNAPSHOT
[ "${TRAVIS_TAG}" ] && export MAVEN_OPTS=-Drevision=${TRAVIS_TAG}

echo "Start building with MAVEN_OPTS set to '${MAVEN_OPTS}'"
