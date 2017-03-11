package com.michalkowol.hackernews;

import com.softwareberg.HttpClient;
import com.softwareberg.HttpRequest;
import com.softwareberg.JsonMapper;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.List;

import static com.softwareberg.HttpMethod.GET;

@Singleton
class HackerNewsService {

    private final HttpClient httpClient;
    private final JsonMapper jsonMapper;

    @Inject
    HackerNewsService(HttpClient httpClient, JsonMapper jsonMapper) {
        this.httpClient = httpClient;
        this.jsonMapper = jsonMapper;
    }

    private List<Integer> topStories() {
        String response = httpClient.execute(new HttpRequest(GET, "https://hacker-news.firebaseio.com/v0/topstories.json")).join().getBody();
        return Arrays.asList(jsonMapper.read(response, Integer[].class));
    }

    private HackerNews storyById(int id) {
        String response = httpClient.execute(new HttpRequest(GET, "https://hacker-news.firebaseio.com/v0/item/" + id + ".json")).join().getBody();
        return jsonMapper.read(response, HackerNews.class);
    }

    HackerNews topStory() {
        int topStoryId = topStories().stream().findFirst().orElse(1);
        return storyById(topStoryId);
    }
}
