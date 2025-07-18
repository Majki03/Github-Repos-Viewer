package com.example.github_repos_viewer.model;

public class BranchInfo {
    private String name;
    private String lastCommitSha;

    // Konstruktor
    public BranchInfo(String name, String lastCommitSha) {
        this.name = name;
        this.lastCommitSha = lastCommitSha;
    }

    // Gettery (potrzebne do serializacji JSON)
    public String getName() {
        return name;
    }

    public String getLastCommitSha() {
        return lastCommitSha;
    }
}
