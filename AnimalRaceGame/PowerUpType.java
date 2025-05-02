package AnimalRaceGame;

public enum PowerUpType implements MysteryEffect {
    SPEED_BOOST("a") {
        @Override
        public void apply(AnimalRaceGame game, int playerNumber) {
            game.movePlayer(playerNumber, 30);  // example: move player forward
        }
    },
    INVINCIBILITY("b") {
        @Override
        public void apply(AnimalRaceGame game, int playerNumber) {
            game.setPlayerInvincible(playerNumber, true);  // example: make player invincible
        }
    },
    DOUBLE_POINTS("c") {
        @Override
        public void apply(AnimalRaceGame game, int playerNumber) {
            game.doublePlayerPoints(playerNumber); // example: double the player's points
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
