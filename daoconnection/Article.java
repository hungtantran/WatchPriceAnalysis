package daoconnection;

import java.io.Serializable;

public class Article implements Serializable {
	private static final long serialVersionUID = 1L;

	private int id;
	private String link;
	private int domainTableId1;
	private Integer domainTableId2;
	private Integer domainTableId3;
	private String articleName;
	private int typeTable1;
	private Integer typeTable2;
	private String keywords;
	private String timeCreated;
	private String dateCreated;
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

	public int getDomainTableId1() {
		return domainTableId1;
	}

	public void setDomainTableId1(int domainTableId1) {
		this.domainTableId1 = domainTableId1;
	}

	public Integer getDomainTableId2() {
		return domainTableId2;
	}

	public void setDomainTableId2(Integer domainTableId2) {
		this.domainTableId2 = domainTableId2;
	}

	public Integer getDomainTableId3() {
		return domainTableId3;
	}

	public void setDomainTableId3(Integer domainTableId3) {
		this.domainTableId3 = domainTableId3;
	}

	public String getArticleName() {
		return articleName;
	}

	public void setArticleName(String articleName) {
		this.articleName = articleName;
	}

	public int getTypeTable1() {
		return typeTable1;
	}

	public void setTypeTable1(int typeTable1) {
		this.typeTable1 = typeTable1;
	}

	public Integer getTypeTable2() {
		return typeTable2;
	}

	public void setTypeTable2(Integer typeTable2) {
		this.typeTable2 = typeTable2;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getTimeCreated() {
		return timeCreated;
	}

	public void setTimeCreated(String timeCreated) {
		this.timeCreated = timeCreated;
	}

	public String getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
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
		return (other instanceof Article) && (this.id == ((Article) other).id);
	}

	@Override
	public int hashCode() {
		Integer id = this.id;
		return this.getClass().hashCode() + id.hashCode();
	}

	@Override
	public String toString() {
		return String
				.format("Article[id=%d, link=%s, domainTableId1=%d, domainTableId2=%d, domainTableId3=%d, articleName=%s, typeTable1=%d, typeTable2=%d, keywords=%s, timeCreated=%s, dateCreated=%s, timeCrawled=%s, dateCrawled=%s]",
						this.id, this.link, this.domainTableId1,
						this.domainTableId2, this.domainTableId3,
						this.articleName, this.typeTable1, this.typeTable2,
						this.keywords, this.timeCreated, this.dateCreated,
						this.timeCrawled, this.dateCrawled);
	}
}
