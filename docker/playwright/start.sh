#! /usr/bin/env bash

ls -lah /root/.cache/ms-playwright/
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -pl spring-petclinic-test-service -D exec.args="install chromium"
mvn -pl spring-petclinic-test-service clean verify site -Dmaven.plugin.validation=VERBOSE
