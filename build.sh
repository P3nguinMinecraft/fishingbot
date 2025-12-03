#!/bin/bash
set -e

rm -rf FishingbotBuild/*
./gradlew clean build

mkdir -p FishingbotBuild
cp build/libs/*.jar FishingbotBuild/
git archive --format=zip --output=FishingbotBuild/fishingbot-source.zip HEAD 2>/dev/null || \
    (cd .. && zip -r Fishingbot/FishingbotBuild/fishingbot-source.zip Fishingbot/src Fishingbot/gradle Fishingbot/*.gradle Fishingbot/*.properties Fishingbot/gradlew* -x "*.class" -x "*/.gradle/*")

rm -rf build/
rm -rf .gradle/
rm -rf run/logs/
rm -rf run/crash-reports/
