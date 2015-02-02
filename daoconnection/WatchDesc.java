package daoconnection;

import java.io.Serializable;

public class WatchDesc implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private String link;
	private Integer domainTableId1;
	private Integer topicTableId1;
	private Integer topicTableId2;
	private String watchName;
	private Integer price1;
	private Integer price2;
	private String keywords;
	private String refNo;
	private String movement;
	private String caliber;
	private String watchCondition;
	private Integer watchYear;
	private String caseMaterial;
	private String dialColor;
	private String gender;
	private String location1;
	private String location2;
	private String location3;
	private String timeCreated;
	private String dateCreated;
	private String timeCrawled;
	private String dateCrawled;

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

	public Integer getDomainTableId1() {
		return domainTableId1;
	}

	public void setDomainTableId1(Integer domainTableId1) {
		this.domainTableId1 = domainTableId1;
	}

	public Integer getTopicTableId1() {
		return topicTableId1;
	}

	public void setTopicTableId1(Integer topicTableId1) {
		this.topicTableId1 = topicTableId1;
	}

	public Integer getTopicTableId2() {
		return topicTableId2;
	}

	public void setTopicTableId2(Integer topicTableId2) {
		this.topicTableId2 = topicTableId2;
	}

	public String getWatchName() {
		return watchName;
	}

	public void setWatchName(String watchName) {
		this.watchName = watchName;
	}

	public Integer getPrice1() {
		return price1;
	}

	public void setPrice1(Integer price1) {
		this.price1 = price1;
	}

	public Integer getPrice2() {
		return price2;
	}

	public void setPrice2(Integer price2) {
		this.price2 = price2;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}

	public String getMovement() {
		return movement;
	}

	public void setMovement(String movement) {
		this.movement = movement;
	}

	public String getCaliber() {
		return caliber;
	}

	public void setCaliber(String caliber) {
		this.caliber = caliber;
	}

	public String getWatchCondition() {
		return watchCondition;
	}

	public void setWatchCondition(String watchCondition) {
		this.watchCondition = watchCondition;
	}

	public Integer getWatchYear() {
		return watchYear;
	}

	public void setWatchYear(Integer watchYear) {
		this.watchYear = watchYear;
	}

	public String getCaseMaterial() {
		return caseMaterial;
	}

	public void setCaseMaterial(String caseMaterial) {
		this.caseMaterial = caseMaterial;
	}

	public String getDialColor() {
		return dialColor;
	}

	public void setDialColor(String dialColor) {
		this.dialColor = dialColor;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getLocation1() {
		return location1;
	}

	public void setLocation1(String location1) {
		this.location1 = location1;
	}

	public String getLocation2() {
		return location2;
	}

	public void setLocation2(String location2) {
		this.location2 = location2;
	}

	public String getLocation3() {
		return location3;
	}

	public void setLocation3(String location3) {
		this.location3 = location3;
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
		return (other instanceof WatchDesc)
				&& this.link == ((WatchDesc) other).link;
	}

	@Override
	public int hashCode() {
		Integer id = this.id;
		return this.getClass().hashCode() + id.hashCode();
	}

	@Override
	public String toString() {
		return String.format(
			"WatchDesc[id=%d, link=%s, domainTableId1=%d, domainTableId2=%d, domainTableId3=%d, watchName=%s, price1=%d, price2=%d, keywords=%s, refNo=%s, movement=%s, caliber=%s, watchCondition=%s, watchYear=%d, caseMaterial=%s, dialColor=%s, gender=%s, location1=%s, location2=%s, location3=%s, timeCreated=%s, dateCreated=%s, timeCrawled=%s, dateCrawled=%s]",
			this.id, this.link, this.domainTableId1,
			this.topicTableId1, this.topicTableId2,
			this.watchName, this.price1, this.price2,
			this.keywords, this.refNo, this.movement,
			this.caliber, this.watchCondition, this.watchYear,
			this.caseMaterial, this.dialColor, this.gender,
			this.location1, this.location2, this.location3,
			this.timeCreated, this.dateCreated, this.timeCrawled,
			this.dateCrawled);
	}
}
