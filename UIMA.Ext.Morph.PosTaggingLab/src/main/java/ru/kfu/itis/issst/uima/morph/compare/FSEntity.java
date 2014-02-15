package ru.kfu.itis.issst.uima.morph.compare;

public class FSEntity {

	private Long id;
	private String docUri;

	public FSEntity(Long id, String docUri) {
		this.id = id;
		this.docUri = docUri;
	}

	public Long getId() {
		return id;
	}

	public String getDocUri() {
		return docUri;
	}
}
