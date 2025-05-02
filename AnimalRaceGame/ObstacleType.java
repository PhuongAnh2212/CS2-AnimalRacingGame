package AnimalRaceGame;

public enum ObstacleType implements MysteryEffect {
    FIRE("fire") {
        @Override
        public void apply(AnimalRaceGame game, int playerNumber) {
            game.freezePlayer(playerNumber); 
        }
    },
    WIND("wind") {
        @Override
        public void apply(AnimalRaceGame game, int playerNumber) {
            game.freezePlayer(playerNumber); 
        }
    }; // Make sure to close with a semicolon here

    private final String emoji;

    ObstacleType(String emoji) {
        this.emoji = emoji;
    }

    public String getEmoji() {
        return emoji;
    }
}
