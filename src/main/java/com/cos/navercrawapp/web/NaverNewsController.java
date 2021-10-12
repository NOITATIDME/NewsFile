package com.cos.navercrawapp.web;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cos.navercrawapp.domain.NaverNews;
import com.cos.navercrawapp.domain.NaverNewsRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RequiredArgsConstructor
@RestController
public class NaverNewsController {

	private final NaverNewsRepository naverNewsRepository;
	
	@GetMapping(value = "/naverNews", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<NaverNews> home(){
		return naverNewsRepository.mFindAll()
				.subscribeOn(Schedulers.boundedElastic());
	}
	
}
