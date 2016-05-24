[![Build Status](https://travis-ci.org/brailleapps/braille-utils.impl.svg?branch=master)](https://travis-ci.org/brailleapps/braille-utils.impl)

# braille-utils.imp #
Provides embosser, table and paper implementations for the purpose of communicating with embossers.

## Using ##
Download the [latest release](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22braille-utils.impl%22) from maven central, add it to your runtime environment.
Access the implementations via the following APIs in [braille-utils.api](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22braille-utils.api%22):
  - `EmbosserCatalog`
  - `TableCatalog`
  - `PaperCatalog`
  
_Or_, in an OSGi environment, use:
  - `EmbosserCatalogService`
  - `TableCatalogService`
  - `PaperCatalogService`

## Building ##
Build with `gradlew build` (Windows) or `./gradlew build` (Mac/Linux)

## Requirements & Compatibility ##
- Requires JDK 7
- Compatible with SPI and OSGi