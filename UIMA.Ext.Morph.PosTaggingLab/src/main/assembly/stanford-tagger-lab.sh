#!/bin/bash
. setup.sh
java_opts='-Xmx28000m'
# invoke
java "${java_opts}" -cp lib/${project.build.finalName}.jar "${logback_opts}" "${uima_datapath}" "${opencorpora_opts}" ru.kfu.itis.issst.uima.morph.stanford.StanfordPosTaggerLab -c "${corpus_xmi_dir}" --corpus-split-dir "${corpus_split_dir}" -p "${gram_categories}"