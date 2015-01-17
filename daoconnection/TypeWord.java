package daoconnection;

import java.io.Serializable;

public class TypeWord implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private Integer typeTableId;
	private String typeWord;
	
	public TypeWord() {
	}
	
	public TypeWord(Integer id, Integer typeTableId, String typeWord) {
		this.id = id;
		this.typeTableId = typeTableId;
		this.typeWord = typeWord;
	}
	
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

	public String getTypeWord() {
		return typeWord;
	}

	public void setTypeWord(String typeWord) {
		this.typeWord = typeWord;
	}

	// Object overrides
	@Override
	public boolean equals(Object other) {
		return (other instanceof TypeWord) && this.id == ((TypeWord) other).id;
	}

	@Override
	public int hashCode() {
		Integer id = this.id;
		return this.getClass().hashCode() + id.hashCode();
	}

	@Override
	public String toString() {
		return String.format("TypeWord[id=%d, typeTableId=%d, typeWord=%s]", this.id,
				this.typeTableId, this.typeWord);
	}
}
