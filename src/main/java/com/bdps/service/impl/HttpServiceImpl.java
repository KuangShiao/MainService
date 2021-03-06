package com.bdps.service.impl;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bdps.service.HttpService;

public class HttpServiceImpl implements HttpService {

	private static Logger logger = LoggerFactory.getLogger(HttpServiceImpl.class);

	@Override
	public String httpGet(String url) throws Exception {
		
		URIBuilder builder = new URIBuilder(url);
		builder.setParameter("http.protocol.content-charset", "UTF-8");		
		HttpGet httpGet = new HttpGet(builder.build());
		
		HttpClient httpClient = HttpClientBuilder.create().build();
        HttpResponse response = httpClient.execute(httpGet);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
        	return EntityUtils.toString(response.getEntity());
        } else {
        	logger.error("httpGet error status: {}", response.getStatusLine());
        }
        
    	return "";
	}

}
