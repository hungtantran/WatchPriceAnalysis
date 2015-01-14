package daoconnection;

import java.io.Serializable;

public class ArticleTopic implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private Integer articleTableId;
	private Integer topicTableId;

	// Getters / Setters
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getArticleTableId() {
		return articleTableId;
	}

	public void setArticleTableId(Integer articleTableId) {
		this.articleTableId = articleTableId;
	}

	public Integer getTopicTableId() {
		return topicTableId;
	}

	public void setTopicTableId(Integer topicTableId) {
		this.topicTableId = topicTableId;
	}

	// Object overrides
	@Override
	public boolean equals(Object other) {
		return (other instanceof ArticleTopic)
				&& this.articleTableId == ((ArticleTopic) other).articleTableId
				&& this.topicTableId == ((ArticleTopic) other).topicTableId;
	}

	@Override
	public int hashCode() {
		Integer id = this.id;
		return this.getClass().hashCode() + id.hashCode();
	}

	@Override
	public String toString() {
		return String.format(
				"ArticleTopic[id=%d, articleTableId=%d, topicTableId=%d]",
				this.id, this.articleTableId, this.toString());
	}
}
