package AnimalRaceGame;

public enum PowerUpType implements MysteryEffect {
    SPEED_BOOST("a") {
        @Override
        public void apply(AnimalRaceGame game, int playerNumber) {
            game.movePlayer(playerNumber, 30);  // Move player forward
        }
    },
    INVINCIBILITY("b") {
        @Override
        public void apply(AnimalRaceGame game, int playerNumber) {
            game.setPlayerInvincible(playerNumber, true);  // Make player invincible
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
}
