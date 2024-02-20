# Change Log
All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## [Unreleased] - 2023-11-08

## [0.1.6] - 2023-02-20
### Changed
- The report pages opening in the browser through `clojure.java.shell` lib.
- Counteragents html-report.
- Added some hash-tags to `shell.clj` source. More effectively navigation.
- The add-measurements function changed.
- Changed the format of report.
### Added
- Added custom html content in the data base and protocol funcitons of shell.
- The custom fragments of html on a second page of protocols.

## [0.1.5]

## [0.1.4] - 2023-10-18
### Fixed
- Metrology-calc function. Fixed variation calculating.

## [0.1.3] - 2023-10-16
### Fixed
- Metrology-calc function. Change discrete function to round.
- Sync values used in calculating and view table.

## [0.1.2] - 2023-10-10
### Changed
- The measurements generating algorithm. Values for the channel
depends on each other.

## [0.1.1] - 2023-08-04
### Changed
- Documentation on how to make the widgets.

### Removed
- `make-widget-sync` - we're all async, all the time.

### Fixed
- Fixed widget maker to keep working when daylight savings switches over.

...some the white space...

## 0.1.0 - 2023-08-04
### Added
- New Clojure project with leiningen.
- It has a metrology and a chemistry libs rewritten to the Clojure. 

Added
Chanded
Deprecated
Removed
Fixed
