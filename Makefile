.PHONY: gradle
SHELL := bash

ifeq (${ENV},prd)
	GCP_PROJECT=ume-production-381217
else ifeq (${ENV},stg)
	GCP_PROJECT=ume-staging
else ifeq (${ENV},dev)
	GCP_PROJECT=ume-development
endif

PAY_ANYWHERE_IMAGE_TAG=pay-anywhere-api:latest
PAY_ANYWHERE_LOCAL_IMAGE_TAG=pay-anywhere-api:localenv
PAY_ANYWHERE_BUILD=:services:payments:pay-anywhere:dockerBuild
PAY_ANYWHERE_VERSION=0.3.3
PAY_ANYWHERE_GCP_IMAGE_TAG=gcr.io/${GCP_PROJECT}/pay-anywhere-api:${PAY_ANYWHERE_VERSION}

pay-anywhere-push:
	./gradlew ${PAY_ANYWHERE_BUILD}
	docker tag ${PAY_ANYWHERE_IMAGE_TAG} ${PAY_ANYWHERE_GCP_IMAGE_TAG}
	docker push ${PAY_ANYWHERE_GCP_IMAGE_TAG}

pay-anywhere-build-local:
	./gradlew ${PAY_ANYWHERE_BUILD}
	docker tag ${PAY_ANYWHERE_IMAGE_TAG} ${PAY_ANYWHERE_LOCAL_IMAGE_TAG}
	kind load docker-image --name devcluster ${PAY_ANYWHERE_LOCAL_IMAGE_TAG}