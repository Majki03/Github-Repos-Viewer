package com.example.github_repos_viewer.service;

import com.example.github_repos_viewer.exception.UserNotFoundException;
import com.example.github_repos_viewer.model.BranchInfo;
import com.example.github_repos_viewer.model.GithubBranch;
import com.example.github_repos_viewer.model.GithubRepository;
import com.example.github_repos_viewer.model.RepositoryInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GitHubService {

    @Value("${github.api.base-url}")
    private String githubApiBaseUrl;

    private final RestTemplate restTemplate;

    public GitHubService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    public List<RepositoryInfo> getUserRepositories(String username) {
        // Nagłówki wymagane przez GitHub API (Accept: application/vnd.github.v3+json)
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // URL do pobierania repozytoriów użytkownika
        String reposUrl = githubApiBaseUrl + "/users/" + username + "/repos";

         ResponseEntity<List<GithubRepository>> response;
        try {
            response = restTemplate.exchange(
                    reposUrl,
                    org.springframework.http.HttpMethod.GET,
                    new org.springframework.http.HttpEntity<>(headers),
                    new ParameterizedTypeReference<List<GithubRepository>>() {}
            );
        } catch (HttpClientErrorException.NotFound ex) {
            // Obsługa błędu 404 - użytkownik nie istnieje
            throw new UserNotFoundException("User '" + username + "' not found on GitHub.");
        } catch (HttpClientErrorException ex) {
            // Obsługa innych błędów HTTP
            throw new RuntimeException("Error communicating with GitHub API: " + ex.getMessage(), ex);
        }

         List<GithubRepository> githubRepositories = response.getBody();

        if (githubRepositories == null) {
            return Collections.emptyList();
        }

        // Filtruj repozytoria, które nie są forkami i mapuj je do RepositoryInfo
        return githubRepositories.stream()
                .filter(repo -> !repo.isFork())
                .map(this::mapToRepositoryInfo)
                .collect(Collectors.toList());
    }

    private RepositoryInfo mapToRepositoryInfo(GithubRepository githubRepository) {
        List<BranchInfo> branches = getRepositoryBranches(githubRepository.getBranchesUrl());
        return new RepositoryInfo(
                githubRepository.getName(),
                githubRepository.getOwner().getLogin(),
                branches
        );
    }

    private List<BranchInfo> getRepositoryBranches(String branchesUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        ResponseEntity<List<GithubBranch>> response;
        try {
            response = restTemplate.exchange(
                    branchesUrl,
                    org.springframework.http.HttpMethod.GET,
                    new org.springframework.http.HttpEntity<>(headers),
                    new ParameterizedTypeReference<List<GithubBranch>>() {}
            );
        } catch (HttpClientErrorException ex) {
            // W przypadku błędu przy pobieraniu gałęzi, zwracamy pustą listę
            // lub logujemy błąd, w zależności od wymagań.
            // Na potrzeby zadania, pusty, żeby nie zatrzymać głównego przepływu.
            System.err.println("Could not retrieve branches for URL: " + branchesUrl + " - " + ex.getMessage());
            return Collections.emptyList();
        }

        List<GithubBranch> githubBranches = response.getBody();

        if (githubBranches == null) {
            return Collections.emptyList();
        }

        // Mapuj gałęzie do BranchInfo
        return githubBranches.stream()
                .map(branch -> new BranchInfo(branch.getName(), branch.getCommit().getSha()))
                .collect(Collectors.toList());
    }
}