class checkWinner{
    JLabel p1, p2;
    JFrame frame;
    int finish_line = 600;
    public checkWinner(JLabel p1, JLabel p2, JFrame frame){
        this.p1 = p1;
        this.p2 = p2;
        this.frame = frame;
    }

    public void check(){
        if (p1.getX() >= finish_line) {
            JOptionPane.showMessageDialog(frame, "Player 1 Wins!");
            resetGame();
        } 
        else if (p2.getX() >= finish_line) {
            JOptionPane.showMessageDialog(frame, "Player 2 Wins!");
            resetGame();
        }
    }
    
    private void resetGame() {
        frame.dispose();
        new StartMenu();
    }
}
