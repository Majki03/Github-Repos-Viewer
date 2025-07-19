package com.example.github_repos_viewer;

import com.example.github_repos_viewer.model.ErrorResponse;
import com.example.github_repos_viewer.model.RepositoryInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GithubReposViewerApplicationTests {

	@LocalServerPort
    private int port;

	@Autowired
    private TestRestTemplate restTemplate;

	@DisplayName("Given an existing GitHub user, when requesting repositories, then return non-forked repos with branches and last commit SHA")
	@Test
	void shouldReturnNonForkedReposForExistingUser() {
		String username = "octocat";
        String url = "http://localhost:" + port + "/api/github/users/" + username + "/repos";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                List.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isNotEmpty();

        assertThat(response.getBody().toString()).contains("Spoon-Knife");
        assertThat(response.getBody().toString()).contains("ownerLogin");
        assertThat(response.getBody().toString()).contains("branches");
        assertThat(response.getBody().toString()).contains("lastCommitSha");
	}

	@DisplayName("Given a non-existing GitHub user, when requesting repositories, then return 404 with custom error format")
    @Test
    void shouldReturn404ForNonExistingUser() {
        String username = "nonexistentuser1234567890abcdef";
        String url = "http://localhost:" + port + "/api/github/users/" + username + "/repos";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();

        assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getBody().getMessage()).contains("User '" + username + "' not found on GitHub.");
    }
}