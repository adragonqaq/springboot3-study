package com.lzl.fbstreamhttp.services;

import org.springframework.ai.chat.client.advisor.api.AdvisedRequest;
import org.springframework.ai.chat.client.advisor.api.AdvisedResponse;
import org.springframework.ai.chat.client.advisor.api.StreamAroundAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAroundAdvisorChain;
import reactor.core.publisher.Flux;

public class LoggingAdvisor implements StreamAroundAdvisor {


	@Override
	public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
		System.out.println("Request: " + advisedRequest);
		return chain.nextAroundStream(advisedRequest);
	}

	@Override
	public int getOrder() {
		return 0;
	}

	@Override
	public String getName() {
		return this.getClass().getName();
	}
}