package de.threesixgames.wizard.api;

import java.util.Map;
import java.util.UUID;

public record GameStartResponse(Map<UUID, Object> handsInfo, Object trump) {
}

