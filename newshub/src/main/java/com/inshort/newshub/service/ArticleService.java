package com.inshort.newshub.service;

import java.util.List;

import org.springframework.data.domain.PageImpl;

import com.inshort.newshub.entity.Article;

public interface ArticleService {

	List<Article> getAllArticles();

	Article getArticlesById(String id) throws Exception;

	PageImpl<Article> getArticlesByCategory(String category, int pageNo, int pageSize, String sortBy, String sortOrder);

	List<Article> getArticlesByScore(double score);

	PageImpl<Article> getArticlesBySource(String source,  int pageNo, int pageSize, String sortBy, String sortOrder);

	List<Article> getArticlesNearby(double latitude, double longitude, double radius);

	List<Article> searchArticles(String query);

//	List<Article> processUserQuery(String query, double latitude, double longitude) throws IOException;

}
