package com.inshort.newshub.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inshort.newshub.constant.ArticlesConstant;
import com.inshort.newshub.entity.Article;
import com.inshort.newshub.service.ArticleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping(value = "/api/v1/news")
public class ArticleController {

	private final ArticleService articleService;

	@GetMapping("/")
	public ResponseEntity<List<Article>> getAllArticle() {
		List<Article> newsList = articleService.getAllArticles();
		return new ResponseEntity<>(newsList, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Article> getArticleById(@PathVariable String id) throws Exception {
		if (id == null || id.isEmpty() ) {
			throw new IllegalArgumentException(
					"The provided ID is invalid. Please ensure the ID is not null or empty.");
		}
		Article news = articleService.getArticlesById(id);
		return new ResponseEntity<>(news, HttpStatus.OK);
	}

	@GetMapping("/category")
	public ResponseEntity<PageImpl<Article>> getArticlesByCategory(@RequestParam String category,
			@RequestParam(name = ArticlesConstant.PAGE_NO, defaultValue = ArticlesConstant.ZERO) int pageNo,
			@RequestParam(name = ArticlesConstant.PAGE_SIZE, defaultValue = ArticlesConstant.FIVE) int pageSize, 
			@RequestParam(defaultValue = ArticlesConstant.PUBLICATION_DATE) String sortBy,
			@RequestParam(defaultValue = ArticlesConstant.DESC) String sortOrder) {
		if (category == null || category.isEmpty() ) {
			throw new IllegalArgumentException(
					"The provided category is invalid. Please ensure the category is not null or empty.");
		}
		return new ResponseEntity<>(articleService.getArticlesByCategory(category, pageNo, pageSize,sortBy, sortOrder), HttpStatus.OK);
	}

	@GetMapping("/score")
	public ResponseEntity<List<Article>> getArticlesByScore(@RequestParam double score) {
		List<Article> articles = articleService.getArticlesByScore(score);
		return new ResponseEntity<>(articles, HttpStatus.OK);
	}

	@GetMapping("/source")
	public ResponseEntity<PageImpl<Article>> getArticlesBySource(@RequestParam String source,
			@RequestParam(name = ArticlesConstant.PAGE_NO, defaultValue = ArticlesConstant.ZERO) int pageNo,
			@RequestParam(name = ArticlesConstant.PAGE_SIZE, defaultValue = ArticlesConstant.FIVE) int pageSize, 
			@RequestParam(defaultValue = ArticlesConstant.PUBLICATION_DATE) String sortBy,
			@RequestParam(defaultValue = ArticlesConstant.DESC) String sortOrder) {
		if (source == null || source.isEmpty() ) {
			throw new IllegalArgumentException(
					"The provided source is invalid. Please ensure the source is not null or empty.");
		}
		return new ResponseEntity<>(articleService.getArticlesBySource(source, pageNo, pageSize,sortBy, sortOrder), HttpStatus.OK);
	}

	@GetMapping("/nearby")
	public ResponseEntity<List<Article>> getArticlesNearby(@RequestParam double latitude,
			@RequestParam double longitude, @RequestParam double radius) {
		List<Article> articles = articleService.getArticlesNearby(latitude, longitude, radius);
		return new ResponseEntity<>(articles, HttpStatus.OK);
	}

	@GetMapping("/search")
	public ResponseEntity<List<Article>> searchArticles(@RequestParam String query) {
		if (query == null || query.isEmpty()) {
			throw new IllegalArgumentException(
					"The provided query is invalid. Please ensure the query is not null or empty.");
		}
		return new ResponseEntity<>(articleService.searchArticles(query), HttpStatus.OK);
	}
	
//	@PostMapping("/query")
//	public ResponseEntity<List<Article>> handleQuery(@RequestBody Article article) throws IOException {
//        List<Article> articles = articleService.processUserQuery(query, latitude, longitude);
//        return new ResponseEntity<>(articles, HttpStatus.OK);
//    }

}
