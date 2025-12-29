package de.threesixgames.wizard.data;

import de.threesixgames.wizard.domain.game.Game;

import java.util.UUID;

public interface GameRepository {
    Game getGame(UUID id);
    void save(Game game);
}
