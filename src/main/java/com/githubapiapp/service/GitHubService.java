package com.githubapiapp.service;

import com.githubapiapp.dto.BranchResponse;
import com.githubapiapp.dto.RepositoryResponse;
import com.githubapiapp.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class GitHubService {

    private final RestTemplate restTemplate;

    public GitHubService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<RepositoryResponse> getRepositories(String username) {
        try {
            String url = buildRepoUrl(username);

            HttpEntity<Void> requestEntity = createGitHubRequestEntity();

            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, HttpMethod.GET, requestEntity,
                    new ParameterizedTypeReference<>() {}
            );

            List<Map<String, Object>> repos = response.getBody();
            if (repos == null) return List.of();

            List<RepositoryResponse> result = new ArrayList<>();
            for (Map<String, Object> repo : repos) {
                Boolean isFork = (Boolean) repo.get("fork");
                if (Boolean.TRUE.equals(isFork)) continue;

                String repoName = (String) repo.get("name");

                Object ownerObj = repo.get("owner");
                if (!(ownerObj instanceof Map<?, ?> ownerMap)) continue;

                Object loginObj = ownerMap.get("login");
                if (!(loginObj instanceof String login)) continue;


                List<BranchResponse> branches = fetchBranches(username, repoName);
                result.add(new RepositoryResponse(repoName, login, branches));
            }

            return result;

        } catch (HttpClientErrorException.NotFound ex) {
            log.warn("GitHub user {} not found", username);
            throw new UserNotFoundException("User not found: " + username);
        }
    }

    private List<BranchResponse> fetchBranches(String username, String repoName) {
        String url = "https://api.github.com/repos/" + username + "/" + repoName + "/branches";

        HttpEntity<Void> requestEntity = createGitHubRequestEntity();

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url, HttpMethod.GET, requestEntity,
                new ParameterizedTypeReference<>() {}
        );

        List<Map<String, Object>> branchList = response.getBody();
        if (branchList == null) return List.of();

        List<BranchResponse> branches = new ArrayList<>();

        for (Map<String, Object> branch : branchList) {
            Object nameObj = branch.get("name");
            if (!(nameObj instanceof String name)) continue;

            Object commitObj = branch.get("commit");
            if (!(commitObj instanceof Map<?, ?> commitMap)) continue;

            Object shaObj = commitMap.get("sha");
            if (!(shaObj instanceof String sha)) continue;

            branches.add(new BranchResponse(name, sha));
        }

        return branches;
    }

    private HttpEntity<Void> createGitHubRequestEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/vnd.github.v3+json");
        headers.set("User-Agent", "GithubApiApp");
        return new HttpEntity<>(headers);
    }

    private String buildRepoUrl(String username) {
        return "https://api.github.com/users/" + username + "/repos";
    }
}
