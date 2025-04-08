package com.inshort.newshub.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.inshort.newshub.client.GeminiClient;
import com.inshort.newshub.constant.ArticlesConstant;
import com.inshort.newshub.entity.Article;
import com.inshort.newshub.exception.RecordNotFoundException;
import com.inshort.newshub.repository.ArticleRepository;
import com.inshort.newshub.service.ArticleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {
	
	 final ArticleRepository articlesRepository;
	 final MongoTemplate mongoTemplate;
	 final GeminiClient geminiClient;
	
	@Override
	public List<Article> getAllArticles() {
		List<Article> results = articlesRepository.findAll();
		return results;
	}

	@Override
	public Article getArticlesById(String id) throws Exception {
		if (id == null || id.isEmpty()) {
			throw new IllegalArgumentException("Article ID cannot be null or empty");
		}
		log.info("Fetching article with id: {}", id);
		return articlesRepository.findById(id)
				.orElseThrow(() -> new RecordNotFoundException("Article not found with id: " + id));
	}

	@Override
	public PageImpl<Article> getArticlesByCategory(String category, int pageNo, int pageSize, String sortBy, String sortOrder) {
		log.info("Fetching articles for category: {}", category);
		if (pageNo < 0 || pageSize <= 0) {
            throw new IllegalArgumentException("Page number must be non-negative and page size must be greater than zero");
        }
		Sort sort = Sort.by(
				sortOrder.equalsIgnoreCase(ArticlesConstant.ASC) ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
		Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
		Query query = new Query();
		query.addCriteria(Criteria.where(ArticlesConstant.CATEGORY).is(category));
		List<Article> articleList = mongoTemplate.find(query, Article.class);
		long count = articleList.size();
		query.with(pageable);
		List<Article> paginatedArticle = mongoTemplate.find(query, Article.class);
		for (Article article : paginatedArticle) {
	        String summary = geminiClient.summarize(article.getDescription());
	        article.setSummary(summary);
	    }
		return new PageImpl<>(paginatedArticle, pageable, count);
	}

	@Override
	public List<Article> getArticlesByScore(double score) {
		if (score < 0) {
			throw new IllegalArgumentException("Score must be non-negative");
		}
		if (score > 1) {
			throw new IllegalArgumentException("Score must be less than or equal to 1");
		}
		log.info("Fetching articles with relevance score greater than or equal to: {}", score);
		return articlesRepository.findByRelevanceScoreGreaterThanEqual(score);
	}

	@Override
	public PageImpl<Article> getArticlesBySource(String source,  int pageNo, int pageSize, String sortBy, String sortOrder) {
		log.info("Fetching articles from source: {}", source);
		if (pageNo < 0 || pageSize <= 0) {
			throw new IllegalArgumentException(
					"Page number must be non-negative and page size must be greater than zero");
		}
		Sort sort = Sort.by(
				sortOrder.equalsIgnoreCase(ArticlesConstant.ASC) ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
		 Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
		 
		 Query query = new Query();
		 query.addCriteria(Criteria.where(ArticlesConstant.SOURCE_NAME).regex("^" + source + "$", "i"));
		 
		List<Article> articleList = mongoTemplate.find(query, Article.class);
		long count = articleList.size();
		query.with(pageable);
		List<Article> paginatedJobs = mongoTemplate.find(query, Article.class);
		return new PageImpl<>(paginatedJobs, pageable, count);
	}

	@Override
	public List<Article> getArticlesNearby(double latitude, double longitude, double radius) {
		if (radius < 0) {
			throw new IllegalArgumentException("Radius must be non-negative");
		}
		log.info("Fetching articles within radius: {} km from latitude: {}, longitude: {}", radius, latitude, longitude);
		 return articlesRepository.findAll().stream()
	                .filter(article -> calculateDistance(article.getLatitude(), article.getLongitude(), latitude, longitude) <= radius)
	                .collect(Collectors.toList());
	}
	
	private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; 

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; 
        return distance;
    }

	@Override
	public List<Article> searchArticles(String query) {
		if (query == null || query.isEmpty()) {
			throw new IllegalArgumentException("Query string cannot be null or empty");
		}
		log.info("Searching articles with query: {}", query);
		Query mongoQuery = new Query();
		mongoQuery.addCriteria(new Criteria().orOperator(Criteria.where(ArticlesConstant.TITLE).regex(query, "i"),
				Criteria.where(ArticlesConstant.DESCRIPTION).regex(query, "i")));
		return mongoTemplate.find(mongoQuery, Article.class);
	}
	
//	public List<Article> processUserQuery(String userQuery, double latitude, double longitude) throws IOException {
//	    GeminiResponse llmData = geminiClient.extractQueryInsights(userQuery);
//	    List<Article> results = new ArrayList<>();
//	 
//	    switch (llmData.getIntent()) {
//        case "category":
//            results = getArticlesByCategory(llmData.getEntities().get(0), 0, 10, "publicationDate", "desc").getContent();
//            break;
//        case "source":
//            results = getArticlesBySource(llmData.getEntities().get(0), 0, 10, "publicationDate", "desc").getContent();
//            break;
//        case "score":
//            results = getArticlesByScore(0.7);
//            break;
//        case "search":
//            results = searchArticles(userQuery);
//            break;
//        case "nearby":
//            results = getArticlesNearby(latitude, longitude, 10);
//            break;
//    }
//	 
//	    for (Article article : results) {
//	        String summary = geminiClient.summarize(article.getDescription());
//	        article.setSummary(summary);
//	    }
//	 
//	    return results;
//	}
	

}
