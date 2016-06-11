#!/bin/bash
# USAGE: ./release.sh 1.1.1

DIR=$(dirname $0)
VERSION_NAME=$1
ARR=(${VERSION_NAME//./ })
VERSION_CODE=$((  1000000*${ARR[0]} + 1000*${ARR[1]} + ${ARR[2]}  ))

git stash

sed -i \
	-e "s/versionName \".*\"/versionName \"$VERSION_NAME\"/" \
	-e "s/versionCode .*/versionCode $VERSION_CODE/" \
	$DIR/build.gradle

git tag $VERSION_NAME
git push
git push --tags