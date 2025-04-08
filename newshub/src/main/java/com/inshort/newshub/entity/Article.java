package com.inshort.newshub.entity;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Document(collection = "newshub")
public class Article {
	
	@Field("_id")
	private String id;
	private String title;
	private String description;
	private String url;
	
	@Field(name = "publication_date")
	private String publicationDate;
	
	@Field(name = "source_name")
	private String sourceName;
	private List<String> category;
	
	@Field(name = "relevance_score")
	private float relevanceScore;
	
	private float latitude;
	private float longitude;
	
	private String summary;
}
