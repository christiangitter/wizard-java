package de.threesixgames.wizard.data;

import de.threesixgames.wizard.domain.game.Game;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InMemoryGameRepository implements GameRepository {
    private final Map<UUID, Game> storage = new HashMap<>();

    @Override
    public Game getGame(UUID id) {
        return storage.get(id);
    }

    @Override
    public void save(Game game) {
        storage.put(game.getId(), game);
    }
}
