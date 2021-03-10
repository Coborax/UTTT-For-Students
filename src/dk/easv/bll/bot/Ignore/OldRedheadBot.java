package dk.easv.bll.bot;



import dk.easv.bll.bot.IBot;
import dk.easv.bll.field.Field;
import dk.easv.bll.field.IField;
import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;
import dk.easv.bll.move.Move;

import javax.swing.*;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class OldRedheadBot implements IBot {

    private static final String BOTNAME = "Redhead";
    private Random rand = new Random();

    private HashMap<String, Double> scoreLookup = new HashMap<>();

    /**
     * Makes a turn. Edit this method to make your bot smarter.
     * Currently does only random moves.
     *
     * @return The selected move we want to make.
     */
    @Override
    public IMove doMove(IGameState state) {
        String[][] board = new String[9][9];
        String[][] macroBoard = new String[3][3];
        List<IMove> possibleMoves = state.getField().getAvailableMoves();

        System.arraycopy(state.getField().getBoard(), 0, board, 0, 9);
        System.arraycopy(state.getField().getMacroboard(), 0, macroBoard, 0, 3);

        scoreLookup.put("1", 1.0);
        scoreLookup.put("0", -1.0);

        double bestScore = Double.NEGATIVE_INFINITY;
        IMove bestMove = state.getField().getAvailableMoves().get(0);

        for (IMove move : possibleMoves) {
            board[move.getX()][move.getY()] = "1";
            double score = minimax(board, macroBoard,5, false);
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        return bestMove;
    }

    private double minimax(String[][] board, String[][] macroBoard, int depth, boolean maximizingPlayer) {
        System.out.println("Minmax is being called!!");

        String res = checkWinner(board, macroBoard);
        if (res != null) {
            return scoreLookup.get(res);
        } else if (depth == 0) {
            return 0;
        }

        if (maximizingPlayer) {
            double bestScore = Double.NEGATIVE_INFINITY;
            for (int x = 0; x < board.length; x++) {
                for (int y = 0; y < board[x].length; y++) {
                    if (board[x][y].equals(Field.EMPTY_FIELD) && isInActiveMicroBoard(x, y, macroBoard)) {
                        board[x][y] = "1";
                        double score = minimax(board, macroBoard, depth - 1, false);
                        board[x][y] = Field.EMPTY_FIELD;
                        bestScore = Math.max(bestScore, score);
                    }
                }
            }
            return bestScore;
        } else {
            double bestScore = Double.POSITIVE_INFINITY;
            for (int x = 0; x < board.length; x++) {
                for (int y = 0; y < board[x].length; y++) {
                    if (board[x][y].equals(Field.EMPTY_FIELD) && isInActiveMicroBoard(x, y, macroBoard)) {
                        board[x][y] = "1";
                        double score = minimax(board, macroBoard, depth - 1, true);
                        board[x][y] = Field.EMPTY_FIELD;
                        bestScore = Math.min(bestScore, score);
                    }
                }
            }
            return bestScore;
        }
    }

    private String checkWinner(String[][] board, String[][] macroBoard) {
        String winner = null;

        //Update macro board
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++) {
                for (int i = 0; i < macroBoard.length; i++)
                    for (int k = 0; k < macroBoard[i].length; k++) {
                        if(macroBoard[i][k].equals(IField.AVAILABLE_FIELD))
                            macroBoard[i][k] = IField.EMPTY_FIELD;
                    }

                int xTrans = x%3;
                int yTrans = y%3;

                if(macroBoard[xTrans][yTrans].equals(IField.EMPTY_FIELD))
                    macroBoard[xTrans][yTrans] = IField.AVAILABLE_FIELD;
                else {
                    // Field is already won, set all fields not won to avail.
                    for (int i = 0; i < macroBoard.length; i++)
                        for (int k = 0; k < macroBoard[i].length; k++) {
                            if(macroBoard[i][k].equals(IField.EMPTY_FIELD))
                                macroBoard[i][k] = IField.AVAILABLE_FIELD;
                        }
                }
            }
        }

        // Horizontal check
        for (int i = 0; i < 3; i++) {
            if (isSame3(macroBoard[i][0], macroBoard[i][1], macroBoard[i][2])) {
                winner = macroBoard[i][0];
            }
        }

        // Vertical check
        for (int i = 0; i < 3; i++) {
            if (isSame3(macroBoard[0][i], macroBoard[1][i], macroBoard[2][i])) {
                winner = macroBoard[0][i];
            }
        }

        // Diagonal check
        if (isSame3(macroBoard[0][0], macroBoard[1][1], macroBoard[2][2])) {
            winner = macroBoard[0][0];
        }
        if (isSame3(macroBoard[2][0], macroBoard[1][1], macroBoard[0][2])) {
            winner = macroBoard[2][0];
        }

        return winner;
    }

    private boolean isSame3(String a, String b, String c) {
        return a.equals(b) && b.equals(c) && !c.isEmpty() && !a.equals(Field.EMPTY_FIELD) && !a.equals(Field.AVAILABLE_FIELD);
    }

    private Boolean isInActiveMicroBoard(int x, int y, String[][] macroBoard) {
        int xTrans = x>0 ? x/3 : 0;
        int yTrans = y>0 ? y/3 : 0;
        String value = macroBoard[xTrans][yTrans];
        return value.equals(IField.AVAILABLE_FIELD);
    }

    @Override
    public String getBotName() {
        return BOTNAME;
    }
}
