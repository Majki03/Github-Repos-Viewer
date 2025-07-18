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
		String username = "octocat"; // Użytkownik z publicznymi repozytoriami niebędącymi forkami
        String url = "http://localhost:" + port + "/api/github/users/" + username + "/repos";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Wykonaj żądanie GET
        ResponseEntity<List> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                List.class
        );

		// Sprawdź status odpowiedzi
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isNotEmpty();

        // Możemy sprawdzić, czy odpowiedź zawiera kluczowe pola
        // Niestety, `TestRestTemplate` z `List.class` nie deserializuje automatycznie do `List<RepositoryInfo>`
        // bez dodatkowej konfiguracji lub użycia `ParameterizedTypeReference`.
        // Dlatego sprawdzamy surową listę map.
        // Jeśli chcielibyśmy dokładniej testować, musielibyśmy użyć Jackson ObjectMapper bezpośrednio
        // lub skonfigurować TestRestTemplate do używania ParameterizedTypeReference.
        // Na potrzeby zadania "happy path" i minimalnego mockowania, to wystarczy.

        // Przykładowe sprawdzenie zawartości (może być bardziej szczegółowe, ale dla "happy path" jest OK)
        // Zakładamy, że dla "octocat" zawsze są jakieś repozytoria, np. "Spoon-Knife"
        assertThat(response.getBody().toString()).contains("Spoon-Knife"); // Sprawdź czy jest nazwa repo
        assertThat(response.getBody().toString()).contains("ownerLogin"); // Sprawdź czy jest login ownera
        assertThat(response.getBody().toString()).contains("branches"); // Sprawdź czy jest lista gałęzi
        assertThat(response.getBody().toString()).contains("lastCommitSha"); // Sprawdź czy jest SHA
	}

	@DisplayName("Given a non-existing GitHub user, when requesting repositories, then return 404 with custom error format")
    @Test
    void shouldReturn404ForNonExistingUser() {
        String username = "nonexistentuser1234567890abcdef"; // Mało prawdopodobne, że taki użytkownik istnieje
        String url = "http://localhost:" + port + "/api/github/users/" + username + "/repos";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Wykonaj żądanie GET
        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                ErrorResponse.class
        );

		// Sprawdź status odpowiedzi
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();

        // Sprawdź format i zawartość błędu
        assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getBody().getMessage()).contains("User '" + username + "' not found on GitHub.");
    }
}