package daoconnection;

import java.io.Serializable;

public class ArticleTopic implements Serializable {
	private static final long serialVersionUID = 1L;

	private int id;
	private int articleTableId;
	private int topicTableId;

	// Getters / Setters
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getArticleTableId() {
		return articleTableId;
	}

	public void setArticleTableId(int articleTableId) {
		this.articleTableId = articleTableId;
	}

	public int getTopicTableId() {
		return topicTableId;
	}

	public void setTopicTableId(int topicTableId) {
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
