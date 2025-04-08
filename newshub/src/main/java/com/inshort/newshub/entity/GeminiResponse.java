package com.inshort.newshub.entity;

import java.util.List;

import lombok.Data;

@Data
public class GeminiResponse {
	
	private List<String> entities;
	private String intent;
}
