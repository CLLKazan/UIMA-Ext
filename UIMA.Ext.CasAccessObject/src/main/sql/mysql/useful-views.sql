CREATE OR REPLACE VIEW annodoc
AS SELECT anno.id AS id, anno.anno_type AS anno_type, anno.txt AS txt, anno.start_offset AS start_offset, anno.end_offset AS end_offset, doc.uri AS doc, span.txt AS span, doc.launch_id AS launch_id
FROM annotation anno JOIN span ON anno.span_id = span.id JOIN document doc ON doc.id = span.doc_id