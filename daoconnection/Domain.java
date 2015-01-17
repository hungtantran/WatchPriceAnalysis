package daoconnection;

import java.io.Serializable;

public class Domain implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private String domain;
	private String domainString;
	
	public Domain() {
	}
	
	public Domain(Integer id, String domain, String domainString) {
		this.id = id;
		this.domain = domain;
		this.domainString = domainString;
	}
	
	// Getters / Setters
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	public String getDomainString() {
		return domainString;
	}

	public void setDomainString(String domainString) {
		this.domainString = domainString;
	}

	// Object overrides
	@Override
	public boolean equals(Object other) {
		return (other instanceof Domain) && this.id == ((Domain) other).id;
	}

	@Override
	public int hashCode() {
		Integer id = this.id;
		return this.getClass().hashCode() + id.hashCode();
	}

	@Override
	public String toString() {
		return String.format("Domain[id=%d, domain=%d]", this.id, this.domain);
	}
}
