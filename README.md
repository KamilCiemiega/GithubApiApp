# GitHub API App

A simple application built as part of a recruitment task. It integrates with the public GitHub API and exposes selected repository data via a REST API.

## 🔧 Technologies

- Java 21
- Spring Boot 3
- RestTemplate
- JUnit 5
- Maven

## ✅ Task Requirements

- Fetch all **non-forked** repositories for a given GitHub user.
- For each repository, return:
    - Repository name
    - Owner login
    - List of branches (name + last commit SHA)
- If the user does not exist, return a 404 response in the following format:

```json
{
  "status": 404,
  "message": "User not found: {username}"
}
```

## 🚀 How to Run the Application

Make sure you have Java 21 and Maven installed.

To start the application:

```bash
mvn spring-boot:run
```

The app will be available at:  
`http://localhost:8080`

---

## 📡 API Endpoint

### `GET /api/github/{username}/repositories`

Returns a list of **non-fork** repositories for the given GitHub username, along with their branches and the last commit SHA.

#### Example request

```
GET http://localhost:8080/api/github/octocat/repositories
```

#### Example response

```json
[
  {
    "repositoryName": "Hello-World",
    "ownerLogin": "octocat",
    "branches": [
      {
        "name": "main",
        "lastCommitSha": "e5bd3914e2e596debea16f433f57875b5b90bcd6"
      }
    ]
  }
]
```

---

## ❌ Error Handling

If the user does not exist, a `404` response is returned in the following format:

```json
{
  "status": 404,
  "message": "User not found: {username}"
}
```

---

## 🧪 Running Tests

To run integration tests:

```bash
mvn test
```

Tests verify:

- Successful response for existing GitHub users
- Proper error handling for non-existing users

