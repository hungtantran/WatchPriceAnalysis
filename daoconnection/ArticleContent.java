package daoconnection;

import java.io.Serializable;

public class ArticleContent implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer articleTableId;
	private String content;

	// Getter / Setters
	public Integer getArticleTableId() {
		return articleTableId;
	}

	public void setArticleTableId(Integer articleTableId) {
		this.articleTableId = articleTableId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	// Object overrides
	// ---------------------------------------------------------------------------

	@Override
	public boolean equals(Object other) {
		return (other instanceof ArticleContent) ? this.articleTableId == ((ArticleContent) other).articleTableId
				: (other == this);
	}

	@Override
	public int hashCode() {
		Integer id = this.articleTableId;
		return this.getClass().hashCode() + id.hashCode();
	}

	@Override
	public String toString() {
		return String.format("Article Content[id=%d, content=%s]",
				this.articleTableId, this.content);
	}
}
