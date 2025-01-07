package com.example.politix.models;

public class VoteCount {
    private String candidateName;
    private int voteCount;

    public VoteCount(String candidateName, int voteCount) {
        this.candidateName = candidateName;
        this.voteCount = voteCount;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public int getVoteCount() {
        return voteCount;
    }
}