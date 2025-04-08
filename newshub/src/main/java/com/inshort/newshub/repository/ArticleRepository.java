package com.inshort.newshub.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.inshort.newshub.entity.Article;

@Repository
public interface ArticleRepository extends MongoRepository<Article, String>{
	
	List<Article> findByCategory(String category);
	
	List<Article> findBySourceNameEqualsIgnoreCase(String sourceName, Pageable pageable);
	
	List<Article> findByRelevanceScoreGreaterThanEqual(double score);
}
