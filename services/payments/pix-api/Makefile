.PHONY: gradle
SHELL := bash

IMAGE_TAG=pix-api:latest
LOCAL_IMAGE_TAG=pix-api:localenv

ifeq (${ENV},prd)
	GCP_PROJECT=ume-production-381217
else ifeq (${ENV},stg)
	GCP_PROJECT=ume-staging
else ifeq (${ENV},dev)
	GCP_PROJECT=ume-development
endif

VERSION=0.2.0
GCP_IMAGE_TAG=gcr.io/${GCP_PROJECT}/pix-api:${VERSION}

push:
	./gradlew dockerBuild
	docker tag ${IMAGE_TAG} ${GCP_IMAGE_TAG}
	docker push ${GCP_IMAGE_TAG}

build-local: 
	./gradlew dockerBuild
	docker tag ${IMAGE_TAG} ${LOCAL_IMAGE_TAG}
	kind load docker-image --name devcluster ${LOCAL_IMAGE_TAG}
