package com.cos.navercrawapp.domain;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;

import reactor.core.publisher.Flux;

//db.runCommand( { convertToCapped: 'naver_realtime', size: 8192 } )
public interface NaverNewsRepository extends ReactiveMongoRepository<NaverNews, String> {
	@Tailable // 커서를 계속 열어두는 annotation
	@Query(value = "{}")
	Flux<NaverNews> mFindAll();
}
