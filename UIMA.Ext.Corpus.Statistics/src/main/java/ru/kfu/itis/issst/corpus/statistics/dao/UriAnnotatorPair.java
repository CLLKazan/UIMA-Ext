package ru.kfu.itis.issst.corpus.statistics.dao;

import java.net.URI;

public class UriAnnotatorPair {
	public final URI uri;
	public final String annotatorId;

	public UriAnnotatorPair(URI uri, String annotatorId) {
		this.uri = uri;
		this.annotatorId = annotatorId;
	}

	public URI getUri() {
		return uri;
	}

	public String getAnnotatorId() {
		return annotatorId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((annotatorId == null) ? 0 : annotatorId.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UriAnnotatorPair other = (UriAnnotatorPair) obj;
		if (annotatorId == null) {
			if (other.annotatorId != null)
				return false;
		} else if (!annotatorId.equals(other.annotatorId))
			return false;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}
}