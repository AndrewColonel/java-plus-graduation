package ru.practicum.user;

import java.util.Optional;

public enum ActiveUser {
    TRUE,
    FALSE;

    public static Optional<ActiveUser> from(String approveStateString) {
        for (ActiveUser approveState : values()) {
            if (approveState.name().equalsIgnoreCase(approveStateString))
                return Optional.of(approveState);
        }
        return Optional.empty();
    }

}
