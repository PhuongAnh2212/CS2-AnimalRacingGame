package AnimalRaceGame;
public enum ObstacleType {
    FIRE("FIRE"),
    WIND("BLOW");

    private final String emoji;

    ObstacleType(String emoji) {
        this.emoji = emoji;
    }

    public String getEmoji() {
        return emoji;
    }
}
