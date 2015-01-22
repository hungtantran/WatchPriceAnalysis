package daoconnection;

import java.sql.SQLException;

public interface ArticleContentDAO {
	public boolean createRelation() throws SQLException;
	
	public ArticleContent getArticleContent(Integer articleTableId) throws SQLException;
	
	public ArticleContent getNextArticleContent(boolean startFromBeginning) throws SQLException;
	
	public boolean createArticleContent(ArticleContent articleContent) throws SQLException;
	
	public boolean updateArticleContent(ArticleContent articleContent) throws SQLException;
	
	public boolean deleteArticleContent(Integer articleTableId) throws SQLException;
}
