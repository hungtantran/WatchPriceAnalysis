package daoconnection;

import java.sql.SQLException;

public interface ArticleDAO {
	public boolean createRelation() throws SQLException;
	
	public Integer createArticle(Article article) throws SQLException;

	public Article getArticle(Integer articleTableId) throws SQLException;

	public Article getNextArticle(boolean startFromBeginning) throws SQLException;

	public boolean deleteArticle(Integer articleTableId) throws SQLException;
}
