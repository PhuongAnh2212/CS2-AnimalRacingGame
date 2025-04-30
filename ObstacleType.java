public enum ObstacleType {
    FIRE("🔥"),
    WIND("💨");

    private final String emoji;

    ObstacleType(String emoji) {
        this.emoji = emoji;
    }

    public String getEmoji() {
        return emoji;
    }
}
