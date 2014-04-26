#Module description
Tools for calculating agreement in annotated corpus.

##Usage example
```bash
$ mvn exec:java -Dexec.mainClass="ru.kfu.itis.issst.corpus.statistics.app.XmiCorpusUnitsExtractor" -Dexec.args="-corpus /home/fsqcds/Dropbox/nlp/dynasty/student-corpus -unit ru.kfu.cll.uima.tokenizer.fstype.W -class ru.kfu.itis.issst.evex.Person -class ru.kfu.itis.issst.evex.Organization -class ru.kfu.itis.issst.evex.Weapon -output /tmp/student-units.tsv"
$ mvn exec:java -Dexec.mainClass="ru.kfu.itis.issst.corpus.statistics.app.UnitsTSVAgreementCalculator" -Dexec.args="-tsv /tmp/student-units.tsv -annotators 2"
```
