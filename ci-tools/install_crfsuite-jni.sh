#!/bin/sh
set -ex
curl -L -O https://github.com/rgareev/crfsuite4j/archive/crfsuite4j-0.1.tar.gz
tar xzvf crfsuite4j-0.1.tar.gz
cd crfsuite4j-crfsuite4j-0.1/crfsuite-jni && make

