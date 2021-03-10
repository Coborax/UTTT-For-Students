package dk.easv.bll.bot;

import dk.easv.bll.field.IField;
import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;
import dk.easv.bll.move.Move;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Aimbot2 implements IBot{
    private static final String BOTNAME = "Aim Bot 2";

    private int[][][] allMoves =
            {
                    {
                            {0, 0}, {2, 2}, {0, 2}, {2, 0},{1,1}
                    },
                    {
                            {0, 1}, {2, 1}, {1, 0}, {1, 2}
                    },
                    {
                            {1,1}
                    }
            };

    @Override
    public IMove doMove(IGameState state) {
        List<int[]> preferredMoves = new ArrayList<>();
        for (int[][] moves: allMoves) {
            List<int[]> mv = Arrays.asList(moves);
            Collections.shuffle(mv);
            preferredMoves.addAll(mv);
        }

        //Find macroboard to play in
        for (int[] move : preferredMoves)
        {
            if(state.getField().getMacroboard()[move[0]][move[1]].equals(IField.AVAILABLE_FIELD))
            {
                //find move to play
                for (int[] selectedMove : preferredMoves)
                {
                    int x = move[0]*3 + selectedMove[0];
                    int y = move[1]*3 + selectedMove[1];
                    if(state.getField().getBoard()[x][y].equals(IField.EMPTY_FIELD))
                    {
                        return new Move(x,y);
                    }
                }
            }
        }
        //NOTE: Something failed, just take the first available move I guess!
        return state.getField().getAvailableMoves().get(0);
    }

    @Override
    public String getBotName() {
        return BOTNAME;
    }
}