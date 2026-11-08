package com.safeer.accurateManhunt.manhunt;

import java.util.UUID;

public record Participant(UUID uniqueId, Team team) {


    public static String getTeamName(Team team) {
        switch (team) {
            case HUNTER -> {
                return "HUNTER";
            }
            case SPEEDRUNNER -> {
                return "SPEEDRUNNER";
            }
            default -> {
                return "NONAME(UNEXPECTED)";
            }
        }
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Participant)) return false;

        Participant other = (Participant) obj;
        return uniqueId.equals(other.uniqueId);
    }

    @Override
    public int hashCode() {
        return uniqueId.hashCode();
    }
}
