package AnimalRaceGame;

public enum PowerUpType implements MysteryEffect {
    SPEED_BOOST("🚀") {
        @Override
        public void apply(AnimalRaceGame game, int playerNumber) {
            // Move the player forward (adjust this logic based on your game's design)
            game.movePlayer(playerNumber, 30);  // This method would handle the player's movement
        }
    },
    INVINCIBILITY("🛡️") {
        @Override
        public void apply(AnimalRaceGame game, int playerNumber) {
            // Set the player as invincible (game logic to handle invincibility)
            game.setPlayerInvincible(playerNumber, true);
        }
    },
    DOUBLE_POINTS("💎") {
        @Override
        public void apply(AnimalRaceGame game, int playerNumber) {
            // Apply double points or any other power-up logic here
            game.doublePlayerPoints(playerNumber);
        }
    };

    private final String emoji;

    PowerUpType(String emoji) {
        this.emoji = emoji;
    }

    @Override
    public String getEmoji() {
        return emoji;
    }

    // If you need a type getter for your PowerUpType
    public String getType() {
        return this.name();
    }

    // Other methods related to PowerUpType can be added here if needed.
}
