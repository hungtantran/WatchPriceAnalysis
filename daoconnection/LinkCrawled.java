package daoconnection;

import java.io.Serializable;

public class LinkCrawled implements Serializable {
	private static final long serialVersionUID = 1L;

	private int id;
	private String link;
	private Integer priority;
	private int domainTableId1;
	private String timeCrawled;
	private String dateCrawled;

	// Getters / Setters
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public int getDomainTableId1() {
		return domainTableId1;
	}

	public void setDomainTableId1(int domainTableId1) {
		this.domainTableId1 = domainTableId1;
	}

	public String getTimeCrawled() {
		return timeCrawled;
	}

	public void setTimeCrawled(String timeCrawled) {
		this.timeCrawled = timeCrawled;
	}

	public String getDateCrawled() {
		return dateCrawled;
	}

	public void setDateCrawled(String dateCrawled) {
		this.dateCrawled = dateCrawled;
	}

	// Object overrides
	@Override
	public boolean equals(Object other) {
		return (other instanceof LinkCrawled)
				&& this.link == ((LinkCrawled) other).link;
	}

	@Override
	public int hashCode() {
		Integer id = this.id;
		return this.getClass().hashCode() + id.hashCode();
	}

	@Override
	public String toString() {
		return String
				.format("LinkCrawled[id=%d, link=%s, priority=%d, domainTableId1=%d, timeCrawled=%s, dateCrawled=%d]",
						this.id, this.link, this.priority, this.domainTableId1,
						this.timeCrawled, this.dateCrawled);
	}
}
