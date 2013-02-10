CREATE OR REPLACE VIEW ANNODOC
AS SELECT anno.id AS id, anno.anno_type AS anno_type, anno.txt AS txt, anno.start_offset AS start_offset, anno.end_offset AS end_offset, doc.uri AS doc, SPAN.txt AS span, doc.launch_id AS launch_id
FROM ANNOTATION anno JOIN SPAN ON anno.span_id = SPAN.id JOIN DOCUMENT doc ON doc.id = SPAN.doc_id
