package student_player;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import boardgame.Board;
import boardgame.Move;
import coordinates.Coord;
import tablut.TablutBoardState;
import tablut.TablutMove;
import tablut.TablutPlayer;

/** A player file submitted by a student. */
public class StudentPlayer extends TablutPlayer{
    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public static final int DEFAULT_SEARCH_DEPTH = 2;
    public static final int FIRST_STEP_SEARCH_DEPTH = 3;
    public static final int DEFAULT_TIMEOUT = 1930;
    public static final int FIRST_MOVE_TIMEOUT = 29930;
    public static TablutBoardState lastState;
    public static TablutBoardState secondLastState;
    
    public StudentPlayer() {
        super("260727743");
    }
    
    
    private class MoveValue {

        public double returnValue;
        public Move returnMove;
  
        public MoveValue(double returnValue) {
            this.returnValue = returnValue;
        }
  
        public MoveValue(double returnValue, Move returnMove) {
            this.returnValue = returnValue;
            this.returnMove = returnMove;
        }
    }
    
    private class StateMove{
      public Move move;
      public TablutBoardState boardState;
      public double eval;

      public StateMove(Move move, TablutBoardState boardState) {
          this.move = move;
          this.boardState = boardState;
          this.eval = evaluate(boardState);
      }
    }
    

    
    private double evaluate(TablutBoardState boardState) {

    if(boardState.getWinner() == player_id) {
      return Integer.MAX_VALUE - 1;
    }
    else if(boardState.getWinner() == 1 - player_id) {
      return Integer.MIN_VALUE + 1;
    }
    else if(boardState.getWinner() == Board.DRAW) {
      return 0.0;
    }
    Coord kingPos = boardState.getKingPosition();
    int turnPlayer = boardState.getTurnPlayer();
    HashSet<Coord> muscoviteCoords = turnPlayer == 0? boardState.getPlayerPieceCoordinates() : boardState.getOpponentPieceCoordinates(); 
    int result = MyTools.getKingScore(boardState, kingPos, muscoviteCoords);
    // winner == player_id
    if(result == Integer.MAX_VALUE - 1 || result == Integer.MIN_VALUE + 1) {
      if((turnPlayer == player_id && result == Integer.MAX_VALUE - 1) 
          || (turnPlayer != player_id && result == Integer.MIN_VALUE + 1)) {
        return Integer.MAX_VALUE - 1;
      }
      else {
        return Integer.MIN_VALUE + 1;
      }
    }
    else {
      // for black
      double extra = 0;
      if(turnPlayer == 0) {
        extra = 1000 * (boardState.getPlayerPieceCoordinates().size() - boardState.getOpponentPieceCoordinates().size()); 
      }
      else {
        extra = 10000 * (boardState.getPlayerPieceCoordinates().size() - boardState.getOpponentPieceCoordinates().size()); 
      }
      if(turnPlayer == player_id) {
          return result + extra;
      }
      else {
        return -result - extra;
      }
    }

  }


    public boolean statesEqual(TablutBoardState b1, TablutBoardState b2) {
      HashSet<Coord> swedeCoords1 = player_id == 1? b1.getPlayerPieceCoordinates() : b1.getOpponentPieceCoordinates();
      HashSet<Coord> muscoviteCoords1 = player_id == 0? b1.getPlayerPieceCoordinates() : b1.getOpponentPieceCoordinates(); 
      HashSet<Coord> swedeCoords2 = player_id == 1? b2.getPlayerPieceCoordinates() : b2.getOpponentPieceCoordinates();
      HashSet<Coord> muscoviteCoords2 = player_id == 0? b2.getPlayerPieceCoordinates() : b2.getOpponentPieceCoordinates(); 
      return swedeCoords1.equals(swedeCoords2) && muscoviteCoords1.equals(muscoviteCoords2);
    }


    protected MoveValue minimax(double alpha, double beta, int originalDepth, int maxDepth, TablutBoardState boardState, int turnplayer, final TablutMove lastMove) {      
        boolean maxplayer = (turnplayer == player_id);

        if(boardState.getWinner() == player_id) {
          return new MoveValue(Integer.MAX_VALUE - 1);
        }
        else if(boardState.getWinner() == 1 - player_id) {
          return new MoveValue(Integer.MIN_VALUE + 1);
        }
        else if(boardState.gameOver()) {
          return new MoveValue(0);
        }
        if(maxDepth == 0) {
          return new MoveValue(evaluate(boardState));
        }
        List<TablutMove> options = new LinkedList<TablutMove>(boardState.getAllLegalMoves());
        
        
        MoveValue returnMove;
        MoveValue bestMove = null;
        if(originalDepth - maxDepth >= 2) {
          if(maxplayer) {

            for (TablutMove move : options) {
              
              TablutBoardState cloneBS = (TablutBoardState) boardState.clone();
              cloneBS.processMove(move);
              if(cloneBS.getWinner() == player_id) {
                return new MoveValue(Integer.MAX_VALUE - 1, move);
              }
//              if(lastState != null && statesEqual(lastState, cloneBS)) {
//                continue;
//              }
              returnMove = minimax(alpha, beta, originalDepth, maxDepth - 1, cloneBS, 1 - turnplayer, null);
              if ((bestMove == null) || (bestMove.returnValue < returnMove.returnValue)) {
                bestMove = returnMove;
                bestMove.returnMove = move;
              }
              if (returnMove.returnValue > alpha) {
                  alpha = returnMove.returnValue;
                  bestMove = returnMove;
              }
              if (beta <= alpha) {
                  bestMove.returnValue = beta;
                  bestMove.returnMove = null;
                  return bestMove; // pruning
              }
            }
            return bestMove;
     
          }
          else {

            for (TablutMove move : options) {
              TablutBoardState cloneBS = (TablutBoardState) boardState.clone();
              cloneBS.processMove(move);
              if(cloneBS.getWinner() == 1 - player_id) {
                return new MoveValue(Integer.MIN_VALUE + 1, move);
              }
              returnMove = minimax(alpha, beta, originalDepth, maxDepth - 1, cloneBS, 1 - turnplayer, null);
              if ((bestMove == null) || (bestMove.returnValue > returnMove.returnValue)) {
                bestMove = returnMove;
                bestMove.returnMove = move;
              }
              if (returnMove.returnValue < beta) {
                  beta = returnMove.returnValue;
                  bestMove = returnMove;
              }
              if (beta <= alpha) {
                  bestMove.returnValue = alpha;
                  bestMove.returnMove = null;
                  return bestMove; // pruning
              }
            }
            return bestMove;
          }
        }
        else {
          PriorityQueue<StateMove> queue;
          Comparator<StateMove> comp;
          if(maxDepth == originalDepth && lastMove != null) {
            comp = new Comparator<StateMove>() {
              @Override
              public int compare(StateMove b1, StateMove b2) {
                  if (lastMove == b2.move) return Integer.MAX_VALUE - 2;
                  else if (lastMove == b1.move) return Integer.MIN_VALUE + 2;
                  // descending order
                  return (int) (b2.eval - b1.eval);
              }
            };
          }
          else if(maxplayer) {
            comp = new Comparator<StateMove>() {
              @Override
              public int compare(StateMove b1, StateMove b2) {
                  // descending order
                  return (int) (b2.eval - b1.eval);
              }
            };
          }
          else {
            comp = new Comparator<StateMove>() {
              @Override
              public int compare(StateMove b1, StateMove b2) {
                  // descending order
                return (int) (b1.eval - b2.eval);
              }
            };
          }
          
          if(maxplayer) {
            queue = new PriorityQueue<StateMove>(100, comp);
            for (TablutMove move : options) {
              TablutBoardState cloneBS = (TablutBoardState) boardState.clone();
              cloneBS.processMove(move);
              if((secondLastState != null && statesEqual(secondLastState, cloneBS))
                  || (lastState != null && statesEqual(lastState, cloneBS))) {
                continue;
              }
              queue.add(new StateMove(move, cloneBS));
            }
            
            while(!queue.isEmpty()) {
              StateMove stateMove = queue.remove();
              TablutBoardState state = stateMove.boardState;
              Move move = stateMove.move;
              if(state.getWinner() == player_id) {
                return new MoveValue(Integer.MAX_VALUE - 1, move);
              }
  
              returnMove = minimax(alpha, beta, originalDepth, maxDepth - 1, state, 1 - turnplayer, null);
              if ((bestMove == null) || (bestMove.returnValue < returnMove.returnValue)) {
                bestMove = returnMove;
                bestMove.returnMove = move;
              }
              if (returnMove.returnValue > alpha) {
                  alpha = returnMove.returnValue;
                  bestMove = returnMove;
              }
              if (beta <= alpha) {
                  bestMove.returnValue = beta;
                  bestMove.returnMove = null;
                  return bestMove; // pruning
              }
            }
            return bestMove;
     
          }
          else {
            
            queue = new PriorityQueue<StateMove>(100, comp);
            for (TablutMove move : options) {
              TablutBoardState cloneBS = (TablutBoardState) boardState.clone();
              cloneBS.processMove(move);
              queue.add(new StateMove(move, cloneBS));
            }
            while(!queue.isEmpty()) {
              StateMove stateMove = queue.remove();
              TablutBoardState state = stateMove.boardState;
              Move move = stateMove.move;
              
              if(state.getWinner() == 1 - player_id) {
                return new MoveValue(Integer.MIN_VALUE + 1, move);
              }
              returnMove = minimax(alpha, beta, originalDepth, maxDepth - 1, state, 1 - turnplayer, null);
              if ((bestMove == null) || (bestMove.returnValue > returnMove.returnValue)) {
                bestMove = returnMove;
                bestMove.returnMove = move;
              }
              if (returnMove.returnValue < beta) {
                  beta = returnMove.returnValue;
                  bestMove = returnMove;
              }
              if (beta <= alpha) {
                  bestMove.returnValue = alpha;
                  bestMove.returnMove = null;
                  return bestMove; // pruning
              }
            }
            return bestMove;
         }
      }
    }


    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    
    //*
    public Move chooseMove(final TablutBoardState boardState) {

      final List<TablutMove> result = new LinkedList<TablutMove>();
      TablutMove randomMove = (TablutMove) boardState.getRandomMove();
      result.add(randomMove);
      result.add(randomMove);
      final int deadline = boardState.getTurnNumber() == 0 ? FIRST_MOVE_TIMEOUT: DEFAULT_TIMEOUT;
      final int initial = boardState.getTurnNumber() == 0 ? FIRST_STEP_SEARCH_DEPTH:DEFAULT_SEARCH_DEPTH;
      
      Callable<Object> iterativeDeepening = new Callable<Object>()
      {
          @Override
          public Object call() throws Exception
          {
              // Iterative Deepening with start depth of 8
              int depth = initial;
              while(!Thread.currentThread().isInterrupted()) {
                MoveValue bestmove = null;
                if(depth == initial) {                    
                  bestmove = minimax(Integer.MIN_VALUE, Integer.MAX_VALUE, depth, depth, boardState, player_id, null);
                }
                else {                  
                  bestmove = minimax(Integer.MIN_VALUE, Integer.MAX_VALUE, depth, depth, boardState, player_id, result.get(1));
                }
                if(depth == initial) {
                  result.set(1, (TablutMove) bestmove.returnMove);
                }
                else {
                  result.set(1, result.get(0));
                }
                result.set(0, (TablutMove) bestmove.returnMove);
                depth++;
              }
              return 0;
          }
      };

      final ExecutorService executor = Executors.newSingleThreadExecutor();

      try
      {
          final Future<Object> f = executor.submit(iterativeDeepening);
          f.get(deadline, TimeUnit.MILLISECONDS);
      }
      catch (TimeoutException e)
      {
          if(lastState != null) {        
            secondLastState = (TablutBoardState) lastState.clone();
          }
          lastState = (TablutBoardState) boardState.clone();
          lastState.processMove(result.get(0));
          return result.get(0);
      }
      catch (Exception e)
      {
          throw new RuntimeException(e);
      }
      finally
      {
        executor.shutdownNow();
      }

      return result.get(0);
    }
    
}
