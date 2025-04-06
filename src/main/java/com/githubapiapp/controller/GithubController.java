package com.githubapiapp.controller;

import com.githubapiapp.dto.RepositoryResponse;
import com.githubapiapp.service.GitHubService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/github")
public class GithubController {
    private final GitHubService githubService;

    public GithubController(GitHubService githubService) {
        this.githubService = githubService;
    }

    @GetMapping("/{username}/repositories")
    public List<RepositoryResponse> getRepositories(@PathVariable String username) {
        return githubService.getRepositories(username);
    }
}
