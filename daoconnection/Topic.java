package daoconnection;

import java.io.Serializable;

public class Topic implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private Integer typeTableId;
	private String topic;

	// Getters / Setters
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getTypeTableId() {
		return typeTableId;
	}

	public void setTypeTableId(Integer typeTableId) {
		this.typeTableId = typeTableId;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	// Object overrides
	@Override
	public boolean equals(Object other) {
		return (other instanceof Topic) && this.id == ((Topic) other).id;
	}

	@Override
	public int hashCode() {
		Integer id = this.id;
		return this.getClass().hashCode() + id.hashCode();
	}

	@Override
	public String toString() {
		return String.format("Topic[id=%d, typeTableId=%d, topic=%s]", this.id,
				this.typeTableId, this.topic);
	}
}
