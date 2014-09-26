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

Note that content (and format) of OpenCorpora dictionary is changing from time to time. You can download the version that was used for the experiments from [here](TODO).

## UIMA-Ext resources
A specific version of UIMA-Ext that was used for experiments is available in [the GitHub repository]({{site.github.repository_url}}) tagged **RuSSIR-2014-YSC-paper** (FIXME tag name).
`UIMA.Ext.Morph.PosTaggingLab` is a main module required for experiments. It defines all other required dependencies in its POM. It also contains a definition of assembly that contains all required jars, templates of configuration files and shell scripts. This assembly results in a compressed tarball `morph-pos-tagging-lab-0.3-SNAPSHOT-assembly-with-deps.tar.gz`. You can download it from [here](TODO).
Alternatively, you can build it yourself by running `mvn install` in `UIMA.Ext.Parent`.

<a name="ptlab-installation"></a>
###PosTaggingLab installation
Unpack morph-pos-tagging-lab-0.3-SNAPSHOT-assembly-with-deps.tar.gz into some directory. Let's call this directory *PTLab dir* for further references.

Then, in this directory: `cp setup.sh.template setup.sh`

Edit `setup.sh`. Define variable `opencorpora_home`: an actual path to a directory that contains the dictionary XML-file.

## <a name="corpus-split"></a> Definition of a corpus split
Lists of document names in each set (training, development and test) are available in [`UIMA.Ext.Morph.PosTaggingLab/data`](TODO) folder. Two its subfolders `rnc-corpus-split` and `rnc-corpus-split.xmi` contain the same files with the exception that the latter lists files with `.xmi` extension.

## Resource pre-processing
All evaluated taggers are wrapped into UIMA annotators. Other components of the framework implementation are also heavily based on UIMA platform. Therefore, there are Java applications that converts an original corpus and dictionary resources into UIMA-Ext data structures and perform their alignment as described in the paper.

### Dictionary preparation
To compile the dictionary run `compile-dictionary.sh`. It creates file `dict.opcorpora.ser` in `${opencorpora_home}` (see section about [installation](#ptlab-installation)).

TODO link to RNCDictionaryExtension.java.

### <a name="corpus-preparation"></a> Corpus preparation
To parse xhtml files of the corpus and produce UIMA XMI files run (from PTLab dir):

`./parse-rnc-corpus.sh <path-to-ruscorpora_1M>`

This creates directory `<path-to-ruscorpora_1M>/texts.xmi` that is called *the corpus XMI dir* below.

Option `--enable-dictionary-alignining` in `parse-rnc-corpus.sh` turns on the corpus pre-processing procedures described in the paper. For a full picture look at sources of [`ru.ksu.niimm.cll.uima.morph.ruscorpora.DictionaryAligningTagMapper2`](TODO-insert-proper-link-here).

## How to run experiments
### Common information
Each experiment is implemented as a Java application that configures a workflow instance using [DKPro Lab](https://code.google.com/p/dkpro-lab/). Read a documentation of this framework for better understanding. Slides on https://www.werc.tu-darmstadt.de/fileadmin/user_upload/GROUP_WERC/LKE/tutorials/ML-tutorial-5a.pdf seems to be a good start.

Each workflow is implemented as a class with name ending on `*Lab`, e.g., `ru.kfu.itis.issst.uima.morph.hunpos.HunposLab` or `ru.ksu.niimm.cll.uima.morph.ml.TieredPosTaggerLab`. All lab classes for taggers evaluated in the paper can be found in module `UIMA.Ext.Morph.PosTaggingLab`.

### Launching
Each lab class accepts one option `--parameters-file <path-to-parameters-file>` from command line. It is not required to provide a value for this option: by default a lab application attempts to read its parameters from file '<lab-class-name>.parameters' in a current working directory. Templates for these parameter files are provided in subfolder `parameter.templates` of PTLab dir. For most of the taggers there are also shell script templates in PTLab dir, e.g, `maxent-lab.sh.template`.

To summarize, there is an example on how to launch Lab for OpenNLP MaxEnt tagger (given that we are in PTLab dir and `setup.sh` is prepared):

```bash
cp parameter.templates/HunposLab.parameters .
# adjust parameters to your environment
vim HunposLab.parameters
cp maxent-lab.sh.template maxent-lab.sh
chmod u+x maxent-lab.sh
./maxent-lab.sh
```

For details about particular parameters see sections below.

### Workflow parameters
Each parameter defined in a parameter file is translated into an instance of DKPro-Lab **Dimension**. Values for a single parameter are separated by semicolon:

```ini
featureCutoff=5;0
previousTagsInHistory=2;1;3
```

### A typical workflow
Workflow usually contains tasks such as:

* Trimming corpus tags to particular grammatical categories.
Parameter `srcCorpusDir` should contain a path to the corpus XMI directory (see [Corpus preparation](#corpus-preparation) section).
Target grammatical categories are defined by parameter `posCategories`:

```ini
posCategories=POST,RNC_INIT,Prnt,Apro,Anum,NMbr,GNdr,CAse,PErs,ASpc,TEns,VOic
```

The example value is actually the one that was used for the experiments in the paper. Here, `POST,RNC_INIT,Prnt,Apro,Anum` represents Part-of-Speech category jointly. The framework exploits [the grammatical model of OpenCorpora dictionary](http://opencorpora.org/dict.php?act=gram).

* Extracting features from a training set of the corpus. Parameter `corpusSplitInfoDir` should contain a path to directory with files that define lists of documents in training, dev and training sets. See ["Definition of a corpus split"](#corpus-split) section.

* Training a model. Each tagger can declare its specific parameters.

* Running a trained tagger on a development set of the corpus.

* Evaluating a tagger output on a development set.

## How to evaluate another tagger

## Miscellaneous
### Export to TSV
### R utils to compute statistical significance
## Questions

