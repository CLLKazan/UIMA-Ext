#!/bin/awk -f
BEGIN {
	FS = "\t"
	OFS = "\t"
	lastWf = "%NULL%"
	# variable that gather interpretations for lastWordform
	tls = ""
}
# each line
{
	if($1 == lastWf) {
		for (i=2; i<=NF; i++) tls = tls FS $i
	} else {
		if(lastWf != "%NULL%") print lastWf, tls
		lastWf = $1
		tls = $2
		for (i=3; i<=NF; i++) tls = tls FS $i
	}
}
END {
	if(lastWf != "%NULL%") print lastWf, tls
}