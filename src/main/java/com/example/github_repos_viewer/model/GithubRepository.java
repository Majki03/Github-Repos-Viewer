package com.example.github_repos_viewer.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GithubRepository {
    private String name;
    private Owner owner;
    private boolean fork;
    private String branchesUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public boolean isFork() {
        return fork;
    }

    public void setFork(boolean fork) {
        this.fork = fork;
    }

    @JsonProperty("branches_url")
    public String getBranchesUrl() {
        return branchesUrl != null ? branchesUrl.replace("{/branch}", "") : null;
    }

    @JsonProperty("branches_url")
    public void setBranchesUrl(String branchesUrl) {
        this.branchesUrl = branchesUrl;
    }

    public static class Owner {
        private String login;

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }
    }
}
