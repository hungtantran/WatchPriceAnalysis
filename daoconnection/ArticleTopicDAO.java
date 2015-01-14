package daoconnection;

import java.sql.SQLException;
import java.util.List;

public interface ArticleTopicDAO {
	public boolean createRelation() throws SQLException;
	
	public List<ArticleTopic> getArticleTopicByTopic(Topic topic) throws SQLException;
	
	public Integer createArticleTopic(ArticleTopic articleTopic) throws SQLException;
	
	public boolean removeArticleTopicByTopic(ArticleTopic articleTopic) throws SQLException;
}
