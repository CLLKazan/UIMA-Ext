SELECT w.id as ID, w.coveredtext as TEXT, g.STRINGVALUE as GOLD, 
fbase.STRINGVALUE AS FBASE, DBASE.STRINGVALUE as DBASE,
HUNPOS.STRINGVALUE as HUNPOS, HUNPOS_WITH_DICT.STRINGVALUE as HUNPOS_WITH_DICT,
STANFORD.STRINGVALUE as STANFORD,
MAXENT.STRINGVALUE as MAXENT, MAXENT_WITH_DICT.STRINGVALUE as MAXENT_WITH_DICT,
TCRF.STRINGVALUE as TCRF, TCRF_WITH_DICT.STRINGVALUE as TCRF_WITH_DICT
FROM WORD w join GOLD g on g.fsid = w.id 
LEFT OUTER JOIN FBASE on FBASE.FSID = w.id LEFT OUTER JOIN DBASE on DBASE.FSID = w.id
JOIN HUNPOS on HUNPOS.FSID = w.id JOIN HUNPOS_WITH_DICT on HUNPOS_WITH_DICT.FSID = w.id
JOIN STANFORD on STANFORD.FSID = w.id
JOIN MAXENT on MAXENT.FSID = w.id JOIN MAXENT_WITH_DICT on MAXENT_WITH_DICT.FSID = w.id
JOIN TCRF on TCRF.FSID = w.id JOIN TCRF_WITH_DICT on TCRF_WITH_DICT.FSID = w.id