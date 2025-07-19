package com.example.github_repos_viewer.controller;

import com.example.github_repos_viewer.model.RepositoryInfo;
import com.example.github_repos_viewer.service.GitHubService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/github")
public class GitHubController {
    
    private final GitHubService gitHubService;

    public GitHubController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    @GetMapping(value = "/users/{username}/repos", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<RepositoryInfo>> getUserRepositories(
            @PathVariable String username,
            @RequestHeader(value = "Accept", defaultValue = "application/json") String acceptHeader) {

        List<RepositoryInfo> repositories = gitHubService.getUserRepositories(username);
        return ResponseEntity.ok(repositories);
    }
}
