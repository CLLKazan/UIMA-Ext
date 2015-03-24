#!/bin/bash
# check working directory
if [ ! -d lib ]; then
  echo 'Run this script from its directory!'
  exit 1
fi
# check arguments
me=`basename $0`
if [ -z "$1" ]; then
  echo "Usage: $me <output-file-name>"
  exit 1
fi
# prepare classpath string
function join() {
    local IFS=$1
    shift
    echo "$*"
}
app_classpath=$(join ':' lib/*.jar)
echo "app classpath: $app_classpath"
# setup
. setup.sh
# run
java ${java_opts} -cp ${app_classpath} ${uima_datapath} ${jni_opts} ${logback_opts} 'ru.kfu.itis.issst.uima.benchmarking.AEBenchmark' \
  --ae-name 'desc.tcrf-with-dict-desc' \
  --data 'desc/benchmark-col-reader-desc.xml' \
  -o "$1"