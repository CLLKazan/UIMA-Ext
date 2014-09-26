---
layout: default
title: How to use a PoS-tagger evaluation framework
---

How to use a PoS-tagger evaluation framework
============================================

## Introduction
To reproduce results that are described in the paper you should obtain third-party resources, get a build of UIMA-Ext's Morph.PosTaggingLab module, prepare resources and launch experiments.

## Third-party resources
To obtain an RNC corpus follow instructions given at http://ruscorpora.ru/en/corpora-usage.html.

Link to download a morphological dictionary of [OpenCorpora](http://opencorpora.org/) project:  http://opencorpora.org/files/export/dict/dict.opcorpora.xml.bz2.

## UIMA-Ext resources
A specific version of UIMA-Ext that was used for experiments is available in [the GitHub repository]({{site.github.repository_url}}) tagged **RuSSIR-2014-YSC-paper** (FIXME tag name).
`UIMA.Ext.Morph.PosTaggingLab` is a main module required for experiments. It defines all other required dependencies in its POM. It also contains a definition of assembly that contains all required jars, templates of configuration files and shell scripts. This assembly results in a compressed tarball `morph-pos-tagging-lab-0.3-SNAPSHOT-assembly-with-deps.tar.gz`. You can download it from [here](TODO).
Alternatively, you can build it yourself by running `mvn install` in `UIMA.Ext.Parent`.

## Definition of a corpus split
Lists of document names in each set (training, development and test) are available in [`UIMA.Ext.Morph.PosTaggingLab/data`](TODO) folder. Two its subfolders `rnc-corpus-split` and `rnc-corpus-split.xmi` contain the same files with the exception that the latter lists files with `.xmi` extension.

## Resource pre-processing
All evaluated taggers are wrapped into UIMA annotators. Other components of the framework implementation are also heavily based on UIMA platform.

### Dictionary preparation
### Corpus preparation

## How to run experiments

## How to evaluate another tagger

## Miscellaneous
### Export to TSV
### R utils to compute statistical significance


