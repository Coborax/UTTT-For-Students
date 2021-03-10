package dk.easv.bll.bot;



import dk.easv.bll.bot.IBot;
import dk.easv.bll.field.Field;
import dk.easv.bll.field.IField;
import dk.easv.bll.game.GameManager;
import dk.easv.bll.game.GameState;
import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;
import dk.easv.bll.move.Move;

import javax.swing.*;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class RedheadBot implements IBot {

    private static final String BOTNAME = "Redhead";
    private Random rand = new Random();

    private HashMap<String, Double> scoreLookup = new HashMap<>();
    private int player = -1;

    /**
     * Makes a turn. Edit this method to make your bot smarter.
     * Currently does only random moves.
     *
     * @return The selected move we want to make.
     */
    @Override
    public IMove doMove(IGameState state) {

        if (state.getMoveNumber() % 2 == 0) {
            player = 0;
        } else {
            player = 1;
        }

        double bestScore = Double.NEGATIVE_INFINITY;
        IMove bestMove = state.getField().getAvailableMoves().get(0);

        for (IMove move : state.getField().getAvailableMoves()) {
            IGameState simState = new GameState(state);
            GameManager simGame = new GameManager(simState);
            double score = minimax(simGame, 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, false);
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        return bestMove;
    }

    private double minimax(GameManager game, double depth, double a, double b, boolean maximizing) {
        if (game.getGameOver() == GameManager.GameOverState.Win) {
            if (game.getCurrentPlayer() == player) {
                return 10;
            } else {
                return -10;
            }
        } else if (game.getGameOver() == GameManager.GameOverState.Tie){
            return 0;
        }

        double bestScore;
        if (maximizing) {
            bestScore = Double.NEGATIVE_INFINITY;
            for (IMove move : game.getCurrentState().getField().getAvailableMoves()) {
                // Save old copy of boards
                String[][] oldBoard = new String[9][9];
                String[][] oldMacroBoard = new String[3][3];
                System.arraycopy(game.getCurrentState().getField().getBoard(), 0, oldBoard, 0, 9);
                System.arraycopy(game.getCurrentState().getField().getMacroboard(), 0, oldMacroBoard, 0, 3);

                // Do move and do recursive call
                game.updateGame(move);
                double score = minimax(game, depth + 1, a, b,false);

                // Undo move by setting boards to the old board
                game.getCurrentState().getField().setBoard(oldBoard);
                game.getCurrentState().getField().setMacroboard(oldMacroBoard);

                // Alpha Beta pruning
                bestScore = Math.max(bestScore, score);
                a = Math.max(a, score);
                if (b <= a) {
                    break;
                }
            }
        } else {
            bestScore = Double.POSITIVE_INFINITY;
            for (IMove move : game.getCurrentState().getField().getAvailableMoves()) {
                // Save old copy of boards
                String[][] oldBoard = new String[9][9];
                String[][] oldMacroBoard = new String[3][3];
                System.arraycopy(game.getCurrentState().getField().getBoard(), 0, oldBoard, 0, 9);
                System.arraycopy(game.getCurrentState().getField().getMacroboard(), 0, oldMacroBoard, 0, 3);

                // Do move and do recursive call
                game.updateGame(move);
                double score = minimax(game, depth + 1, a, b, true);

                // Undo move by setting boards to the old board
                game.getCurrentState().getField().setBoard(oldBoard);
                game.getCurrentState().getField().setMacroboard(oldMacroBoard);

                // Alpha Beta pruning
                bestScore = Math.min(bestScore, score);
                b = Math.min(b, score);
                if (b <= a) {
                    break;
                }
            }
        }
        return bestScore;
    }

    @Override
    public String getBotName() {
        return BOTNAME;
    }
}
