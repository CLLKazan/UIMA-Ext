#!/bin/bash
if [[ $# != 1 ]]
then
	printf "%b" "usage: parse-rnc-corpus <path-to-ruscorpora_1M>\n"
	exit 1
fi
ruscorpora1M="$1"
if ! [[ -d "${ruscorpora1M}/texts" ]]
then
	printf "%b" "${ruscorpora1M}/texts is not existing directory\n"
	exit 1
fi
. setup.sh
java -Xmx1500m -cp lib/${project.build.finalName}.jar -Duima.datapath="${opencorpora_home}" ru.ksu.niimm.cll.uima.morph.ruscorpora.RusCorporaParserBootstrap \
	--enable-dictionary-aligning \
	--ruscorpora-text-dir "${ruscorpora1M}"/texts \
	-o "${ruscorpora1M}"/texts.xmi

