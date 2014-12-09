#!/bin/sh
set -ex
curl -L -O https://github.com/downloads/chokkan/crfsuite/crfsuite-0.12.tar.gz
tar xzvf crfsuite-0.12.tar.gz
cd crfsuite-0.12 && ./configure CFLAGS="-fPIC" && make && sudo make install
