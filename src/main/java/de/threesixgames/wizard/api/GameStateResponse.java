package de.threesixgames.wizard.api;

import de.threesixgames.wizard.domain.cards.Card;
import de.threesixgames.wizard.domain.game.Game;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record GameStateResponse(
        Game game,
        Map<UUID, List<Card>> handsInfo
) {}
