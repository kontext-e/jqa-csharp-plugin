# jQAssistant C# Plugin

[![GitHub license](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](LICENSE)

This is a **C#** parser for [jQAssistant](https://jqassistant.org/). 
It enables jQAssistant to scan and to analyze **C#** files.

## Getting Started

Download the jQAssistant command line tool for your system: [jQAssistant - Get Started](https://jqassistant.org/get-started/).

## Installation
### Installation through jQAssistant

Starting with jQAssistant 1.12.0 it is possible to declare the plugins in the .jqassistant.yaml file (see: [jQAssistant User Manual](https://jqassistant.github.io/jqassistant/doc/1.12.2/manual/index.html#_yaml_files))

Simply add the following to the plugins section of the config
```yaml
  - group-id: de.kontext-e.jqassistant.plugin
    artifact-id: jqassistant.plugin.csharp
    version: 0.2.3
```

### Manual Installation

Next download the latest version from the release tab. Put the `jqa-csharp-plugin-*.jar` into the plugins folder of the jQAssistant commandline tool.

## Usage

Now scan your C# project:

```bash
jqassistant.sh scan -f <C#-project-folder>
```

You can then start a local Neo4j server to start querying the database at [http://localhost:7474](http://localhost:7474):

```bash
jqassistant.sh server
```

## Model

![Neo4j model for the jQAssistant C# plugin](./drawio/DatabaseSchema.drawio.svg)

## Contribute

We really appreciate your help! If you want to contribute please have a look at the [CONTRIBUTING.md](CONTRIBUTING.md).