#!/bin/bash
if [[ -z $1 || -z $2  ]] ; then
  echo "Usage: <opencorpora-dict-xml> <output-file>"
  exit 1
fi
java_app_arguments="--dict-extension-class,ru.ksu.niimm.cll.uima.morph.ruscorpora.RNCDictionaryExtension"
java_app_arguments="$java_app_arguments,-i,$1,-o,$2"
mvn exec:java -Pwith-logging-impl -Dexec.mainClass=ru.ksu.niimm.cll.uima.morph.opencorpora.resource.XmlDictionaryParserLauncher \
  -Dexec.arguments="$java_app_arguments"
