package com.moengage.apitest.pojo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor 
public class DataObject{
	private String tcId;
	private String description;
	private String apiName;
	private String apiType;
	private String apiUri;
	private String header;
	private String body;
	private String expectedOutput;
	private String skippedData;
	private String DependentTest;
}
