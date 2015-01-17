package daoconnection;

import java.io.Serializable;

public class LinkQueue implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private String link;
	private Integer domainTableId1;
	private Integer priority;
	private Integer persistent;
	private String timeCrawled;
	private String dateCrawled;
	
	public LinkQueue() {
	}
	
	public LinkQueue(Integer id, String link, Integer domainTableId1, Integer priority, Integer persistent, String timeCrawled, String dateCrawled) {
		this.id = id;
		this.link = link;
		this.domainTableId1 = domainTableId1;
		this.priority = priority;
		this.persistent = persistent;
		this.timeCrawled = timeCrawled;
		this.dateCrawled = dateCrawled;
	}
	
	// Getters / Setters
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public int getDomainTableId1() {
		return domainTableId1;
	}

	public void setDomainTableId1(Integer domainTableId1) {
		this.domainTableId1 = domainTableId1;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Integer getPersistent() {
		return persistent;
	}

	public void setPersistent(Integer persistent) {
		this.persistent = persistent;
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
		return (other instanceof LinkQueue)
				&& this.link == ((LinkQueue) other).link;
	}

	@Override
	public int hashCode() {
		Integer id = this.id;
		return this.getClass().hashCode() + id.hashCode();
	}

	@Override
	public String toString() {
		return String
				.format("LinkQueue[id=%d, link=%s, domainTableId1=%d, priority=%d, persistent=%d, timeCrawled=%s, dateCrawled=%s]",
						this.id, this.link, this.domainTableId1, this.priority,
						this.persistent, this.timeCrawled, this.dateCrawled);
	}
}
