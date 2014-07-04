#!/bin/bash
. setup.sh
java -Xmx1400m -cp lib/${project.build.finalName}.jar ru.ksu.niimm.cll.uima.morph.opencorpora.resource.XmlDictionaryParser "${opencorpora_home}/dict.opcorpora.xml" "${opencorpora_home}/dict.opcorpora.ser"