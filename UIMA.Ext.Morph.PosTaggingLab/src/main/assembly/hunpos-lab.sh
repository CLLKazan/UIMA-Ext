#!/bin/bash
. setup.sh
# HunPos installation dir
hunpos_home=~/third-party/hunpos-1.0-linux
hunpos_opts='-Dhunpos.home='${hunpos_home}
# invoke
java "${java_opts}" -cp lib/${project.build.finalName}.jar "${logback_opts}" "${uima_datapath}" "${opencorpora_opts}" "${hunpos_opts}" ru.kfu.itis.issst.uima.morph.hunpos.HunposLab -c "${corpus_xmi_dir}" --corpus-split-dir "${corpus_split_dir}" -p "${gram_categories}"