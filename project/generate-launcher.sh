#!/bin/bash

VERSION=1.0.0-M3
CACHE_VERSION=v1

"$(dirname "$0")/../cli/target/pack/bin/coursier" bootstrap \
  com.github.alexarchambault:coursier-cli_2.11:$VERSION \
  --no-default \
  -r central \
  -D "\${user.home}/.coursier/bootstrap/$VERSION" \
  -b \
  -f -o coursier \
  -M coursier.cli.Coursier \
  -P coursier.cache="\${user.home}/.coursier/cache/$CACHE_VERSION"