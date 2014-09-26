#!/bin/bash
. setup.sh
java -Xmx1500m -cp lib/${project.build.finalName}.jar ru.ksu.niimm.cll.uima.morph.opencorpora.resource.XmlDictionaryParserLauncher \
	-i "${opencorpora_home}/dict.opcorpora.xml" \
	-o "${opencorpora_home}/dict.opcorpora.ser" \
	--dict-extension-class ru.ksu.niimm.cll.uima.morph.ruscorpora.RNCDictionaryExtension 
