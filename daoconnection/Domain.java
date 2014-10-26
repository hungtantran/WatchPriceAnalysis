package daoconnection;

import java.io.Serializable;

public class Domain implements Serializable {
	private static final long serialVersionUID = 1L;

	private int id;
	private String domain;

	// Getters / Setters
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
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
