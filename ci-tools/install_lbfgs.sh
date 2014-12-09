#!/bin/sh
set -ex
curl -L -O https://github.com/downloads/chokkan/liblbfgs/liblbfgs-1.10.tar.gz
tar xzvf liblbfgs-1.10.tar.gz
cd liblbfgs-1.10 && ./configure CFLAGS="-fPIC" && make && sudo make install
