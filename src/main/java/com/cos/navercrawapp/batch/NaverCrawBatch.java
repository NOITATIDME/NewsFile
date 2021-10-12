package com.cos.navercrawapp.batch;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.cos.navercrawapp.domain.NaverNews;
import com.cos.navercrawapp.domain.NaverNewsRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@Component
public class NaverCrawBatch {

	// 8Byte
	private long aid = 277493;
	private final NaverNewsRepository naverNewsRepository;
	
	@Scheduled(cron = "0 24 11 * * *", zone = "Asia/Seoul")
	public void 네이버뉴스크롤링() {
		System.out.println("배치 프로그램 시작======================");	
		
		int successcount = 0;
		int errorcount = 0;
		int crawcount = 0;
		
		List<NaverNews> naverNewsList = new ArrayList<>();
		
		while(true) {
		String aidStr = String.format("%010d", aid);
		System.out.println("aidStr : " + aidStr);
		String url = "https://news.naver.com/main/read.naver?mode=LSD&mid=shm&sid1=103&oid=437&aid=" + aidStr;
		
		try {
			Document doc = Jsoup.connect(url).get();
			
			Element companyElement = doc.selectFirst(".press_logo img");
			String companyAttr = companyElement.attr("title"); // title 또는 alt
			Element titleElement = doc.selectFirst("#articleTitle");
			Element createdAtElement = doc.selectFirst(".t11");
			
			String company = companyAttr;
			String title = titleElement.text();
			String createdAt = createdAtElement.text();
			
			LocalDate today = LocalDate.now();
			//System.out.println(today);
			
			LocalDate yesterday = today.minusDays(1);
			//System.out.println(yesterday);
			
			createdAt = createdAt.substring(0, 10);
			createdAt = createdAt.replace(".", "-");
			
			if(today.toString().equals(createdAt)) {
				System.out.println("createdAt : " + createdAt);
				
				break; // while문 빠져나가고 중지
			}
			
			if(yesterday.toString().equals(createdAt)) { //List 컬렉션에 모았다가 DB에 save() 하기
				System.out.println("어제 기사입니다.");
				
				naverNewsList.add(NaverNews.builder()
						.title(title)
						.company(company)
						.createdAt(Timestamp.valueOf(LocalDateTime.now().minusHours(1)))
						.build()
			   );
				
				crawcount++;
			}
			successcount++;
		} catch (Exception e) {
			System.out.println("해당 주소에 페이지를 찾을 수 없습니다." + e.getMessage());
			errorcount++;
		}
			aid++;
		
	}	// end of while
	
		System.out.println("배치 프로그램 종료======================");
		System.out.println("성공횟수 : " + successcount);
		System.out.println("실패횟수 : " + errorcount);
		System.out.println("크롤링 횟수 : " + crawcount);
		System.out.println("마지막 aid 값 : " + aid);
		System.out.println("컬렉션에 담은 크기 : " + naverNewsList.size());
		Flux.fromIterable(naverNewsList)
			.flatMap(naverNewsRepository::save)
			.subscribe();
		
		
	}
	
}
