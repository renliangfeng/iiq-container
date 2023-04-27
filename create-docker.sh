#!/bin/bash
#
# Stops and cleans current environment and rebuilds IIQ
#

set -o pipefail
function onFailure {
    echo "###############################"
    echo "#### Unexpected failure in command - review logs above"
    exit 99
}

START_IIQ_CONTAINER_BUILD="$(date)"

START_IIQ_BUILD="$1 $(date)"

SSB_INSTALL_DIR_NAME=iiq-app
IIQ_DOCKER_IMAGE_NAME=iiq-image
if [ ! -z "$2" ]
then
    IIQ_DOCKER_IMAGE_NAME="$2"
fi

if [ ! -z "$1" ]
then
    echo "#### Running IIQ SSB build for env [$1] ####"
    export SPTARGET=$1
    pushd ./$SSB_INSTALL_DIR_NAME
    ./build.sh clean war
    END_IIQ_BUILD="$(date)"
    popd
else
    echo "#### Please environment (dev, uat or prod) by passing the value in the first parameter"
    onFailure
fi

echo "#### Copy IIQ build output from SSB folder to volumn folder"

cp -R ./$SSB_INSTALL_DIR_NAME/build/deploy/identityiq.war ./iiq-app-docker/volumes/

pushd ./iiq-app-docker
docker build -t ${IIQ_DOCKER_IMAGE_NAME} .
popd
END_CONTAINER_IIQ_BUILD="$(date)"

echo "#### Start IIQ Container build:     ${START_IIQ_CONTAINER_BUILD}; IIQ Docker image name:  ${IIQ_DOCKER_IMAGE_NAME} #####"
echo "#### Start IIQ build:     ${START_IIQ_BUILD}"
echo "#### End IIQ build:       ${END_IIQ_BUILD}"
echo "#### End IIQ Container build:       ${END_CONTAINER_IIQ_BUILD}"
