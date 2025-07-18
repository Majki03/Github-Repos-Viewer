# GitHub Repositories Viewer

This is a Spring Boot application designed to fetch and display non-forked GitHub repositories for a given user, including details about their branches and last commit SHAs. It also handles cases where the specified GitHub user does not exist.

## Acceptance Criteria

The application addresses the following requirements:

* **List Repositories:** As an API consumer, I can list all non-forked GitHub repositories for a given user.
    * **Information Required:** Repository Name, Owner Login, and for each branch: its name and last commit SHA.
* **Handle Non-Existing User:** As an API consumer, given a non-existing GitHub user, I receive a 404 response in the format:
    ```json
    {
        "status": ${responseCode},
        "message": ${whyHasItHappened}
    }
    ```

## Technologies Used

* **Java 21**
* **Spring Boot 3.x**
* **Maven**
* **REST Template** for consuming GitHub API

## How to Run the Application

### Prerequisites

* Java Development Kit (JDK) 21
* Maven 3.x

### Steps

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/YOUR_GITHUB_USERNAME/github-repos-viewer.git](https://github.com/YOUR_GITHUB_USERNAME/github-repos-viewer.git)
    cd github-repos-viewer
    ```
    (Remember to replace `YOUR_GITHUB_USERNAME` with your actual GitHub username and the repository name.)

2.  **Build the project:**
    ```bash
    mvn clean install
    ```

3.  **Run the application:**
    ```bash
    mvn spring-boot:run
    ```
    The application will start on port `8080` by default.

## API Endpoint

The application exposes the following endpoint:

### Get User Repositories

`GET /api/github/users/{username}/repos`

**Description:** Retrieves a list of non-forked GitHub repositories for the specified `username`, including branch details.

**Path Parameters:**
* `username` (string): The GitHub username whose repositories are to be fetched.

**Headers:**
* `Accept`: Must be `application/json`. Requests with other `Accept` headers will result in a `406 Not Acceptable` response.

**Example Request (using curl):**

For an existing user (e.g., `octocat`):
```bash
curl -H "Accept: application/json" http://localhost:8080/api/github/users/octocat/repos
```

For a non-existing user:
```bash
curl -H "Accept: application/json" http://localhost:8080/api/github/users/nonexistentuser1234567890abcdef/repos
```

**Example Successful Response (200 OK):**
```json
[
    {
        "repositoryName": "Spoon-Knife",
        "ownerLogin": "octocat",
        "branches": [
            {
                "name": "main",
                "lastCommitSha": "your-sha-here"
            }
            // ... more branches
        ]
    }
    // ... more repositories
]
```

**Example Error Response (404 Not Found):**
```json
{
    "status": 404,
    "message": "User 'nonexistentuser1234567890abcdef' not found on GitHub."
}
```

**Example Error Response(406 Not Acceptable):**
```json
{
    "status": 406,
    "message": "Client requested unsupported media type. Please set 'Accept' header to 'application/json'."
}
```

## Running Tests

### To tun the integration test:
```bash
mvn test
```

## Considerations

* **GitHub API Rate Limits:** Be aware of GitHub API rate limits. Excessive requests may lead to temporary blocking.
* **Authentication:** For unauthenticated requests, GitHub API has a lower rate limit. For production-grade applications, using a GitHub Personal Access Token for authenticated requests would be recommended to increase rate limits. This project does not implement authentication as per task requirements.
* **Pagination:** Pagination is not implemented for API consumption or exposure, as explicitly requested in the task.