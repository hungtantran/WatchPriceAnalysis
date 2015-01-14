package daoconnection;

import java.io.Serializable;

public class WatchPageContent implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer watchTableId;
	private String content;

	// Getters / Setters
	public Integer getWatchTableId() {
		return watchTableId;
	}

	public void setWatchTableId(Integer watchTableId) {
		this.watchTableId = watchTableId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	// Object overrides
	@Override
	public boolean equals(Object other) {
		return (other instanceof WatchPageContent)
				&& this.watchTableId == ((WatchPageContent) other).watchTableId;
	}

	@Override
	public int hashCode() {
		Integer id = this.watchTableId;
		return this.getClass().hashCode() + id.hashCode();
	}

	@Override
	public String toString() {
		return String.format("WathcPageContent[watchTableId=%d, content=%s]",
				this.watchTableId, this.content);
	}
}
