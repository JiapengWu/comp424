package student_player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import coordinates.Coord;
import coordinates.Coordinates;
import coordinates.Coordinates.CoordinateDoesNotExistException;
import tablut.TablutBoardState;
import tablut.TablutBoardState.Piece;

public class MyTools {
    public static final int KING_STEP_WEIGHT = 100;
    public static int[][] cornerCoords = {
        {0,1}, {0,2}, 
        {1,0}, {1,1}, {1,2},
        {2,0}, {2,1}, {2,2},
        {0,6}, {0,7}, 
        {1,6}, {1,7}, {1,8},
        {2,6}, {2,7}, {2,8}, 
        {6,0}, {6,1}, {6,2},
        {7,0}, {7,1}, {7,2},
        {8,1}, {8,2}, 
        {6,6}, {6,7}, {6,8},
        {7,6}, {7,7}, {7,8},
        {8,6}, {8,7}
        };
    
    public static int[][] edgeCoords = {
        {0,1}, {0,2}, {0,3}, {0,4}, {0,5}, {0,6}, {0,7}, 
        {8,1}, {8,2}, {8,3}, {8,4}, {8,5}, {8,6}, {8,7}, 
        {1,0}, {2,0}, {3,0}, {4,0}, {5,0}, {6,0}, {7,0}, 
        {1,8}, {2,8}, {3,8}, {4,8}, {5,8}, {6,8}, {7,8} 
    };
    
    public static int[][][] blockades = {
        {
          {0,2}, {1,1}, {2,0}
        },{
          {0,5}, {1,6}, {2,8}
        },{
          {6,0}, {7,1}, {8,2}
        },{
          {6,8}, {7,7}, {8,6}
        }
    };
    
    public static int[][][] cornerRegions = {{
        {0,1}, {0,2}, {0,3}, 
        {1,0}, {1,1}, {1,2}, {1,3}, 
        {2,0}, {2,1}, {2,2}, {2,3},  
        {3,0}, {3,1}, {3,2}, {3,3}
        },{
        {0,5}, {0,6}, {0,7},
        {1,5}, {1,6}, {1,7}, {1,8},
        {2,5}, {2,6}, {2,7}, {2,8}, 
        {3,5}, {3,6}, {3,7}, {3,8}
        },{
        {5,0}, {5,1}, {5,2}, {5,3},
        {6,0}, {6,1}, {6,2}, {6,3},
        {7,0}, {7,1}, {7,2}, {7,3},
        {8,1}, {8,2}, {8,3}
        },{
        {5,5}, {5,6}, {5,7}, {5,8},
        {6,5}, {6,6}, {6,7}, {6,8},
        {7,5}, {7,6}, {7,7}, {7,8},
        {8,5}, {8,6}, {8,7}
        }};
    
    public static int[][] centerCoords = {
        {3,3}, {3,4}, {3,5}, 
        {4,3}, {4,4}, {4,5}, 
        {5,3}, {5,4}, {5,5}
    };
    
    public static int getNumberBlackPieceInTheCorder(TablutBoardState b) {
//      HashSet<Coord> muscoviteCoords = turnPlayer == 0? b.getPlayerPieceCoordinates() : b.getOpponentPieceCoordinates();
      int result = 0;
      for(int[] coord: cornerCoords) {
          if(b.getPieceAt(coord[0], coord[1]).equals(Piece.BLACK)) {
            result ++;
          }
      }
      return result;
    }
    
    public static int getNumberWhitePieceInTheCorder(TablutBoardState b) {
      int result = 0;
      for(int[] coord: cornerCoords) {
          if(b.getPieceAt(coord[0], coord[1]).equals(Piece.WHITE)) {
            result ++;
          }
      }
      return result;
    }
    
    public static int getNumberBlackPieceOnTheEdge(TablutBoardState b) {
      int result = 0;
      for(int[] coord: edgeCoords) {
          if(b.getPieceAt(coord[0], coord[1]).equals(Piece.BLACK)) {            
            result ++;
          }
      }
      return result;
    }
    
    public static int getNumberWhitePieceOnTheEdge(TablutBoardState b) {
      int result = 0;
      for(int[] coord: edgeCoords) {
          if(b.getPieceAt(coord[0], coord[1]).equals(Piece.WHITE)) {            
            result ++;
          }
      }
      return result;
    }
    
    public static double getRatioOfWhilePieceInCenterRegion(TablutBoardState b) {
      double result = 0.0;
      for(int[] coord: centerCoords) {
          if(b.getPieceAt(coord[0], coord[1]).equals(Piece.WHITE)) {            
            result ++;
          }
      }
      return result / b.getNumberPlayerPieces(0);
    }
    
    public static int getMaxNumberOfWhiteAdvantageInEachRegion(TablutBoardState b) {
      int max = Integer.MIN_VALUE;
      for(int[][] region: cornerRegions) {
        int count = 0;
        int white = 0;
        int black = 0;
        for(int[] coord: region) {
          if(b.getPieceAt(coord[0], coord[1]).equals(Piece.WHITE)) {
            white ++;
          }
          if(b.getPieceAt(coord[0], coord[1]).equals(Piece.BLACK)){
            black ++;
          }
        }
        count = white - black;
        if(count > max) max = count;
      }
      return 2 + max;
    }
    
    public static boolean isOnEdge(TablutBoardState b, Coord pos) {
      return pos.x == 0 ||pos.x == 8 || pos.y == 0 || pos.y == 8;
    }
    
    public static int numberOfBlockade(TablutBoardState b) {
      int result = 0;
      for(int[][] bk: blockades) {
        for(int[] coord: bk) {
          if(b.getPieceAt(coord[0], coord[1]).equals(Piece.BLACK)) {
            result ++;
          }
        }
      }
      return result;
    }
    
    public static boolean canMoveTo(TablutBoardState b, Coord start, Coord target) {
     
        if(b.getPieceAt(target) != Piece.EMPTY) {
          return false;
        }
        List<Coord> coords = start.getCoordsBetween(target);
        if(coords.isEmpty()) {
          return false;
        }
        
        for(Coord c : coords) {
          if(b.getPieceAt(c) != Piece.EMPTY) {
            return false;
          }
        }
        return true;
    }
    
    public static boolean canMoveTo(TablutBoardState b, Coord start, Coord target, Coord newKingPos) {
      
      if(b.getPieceAt(target) != Piece.EMPTY) {
        return false;
      }
      List<Coord> coords = start.getCoordsBetween(target);
      if(coords.isEmpty()) {
        return false;
      }
      for(Coord c : coords) {
        if(b.getPieceAt(c) != Piece.EMPTY || c.equals(newKingPos)) {
          return false;
        }
      }
      return true;
  }
    
    public static boolean canKingBeCapturedByBlackOutOfCenter(TablutBoardState b, Coord edgeCoord, HashSet<Coord> muscoviteCoords) {
      
      for (Coord neighbor : Coordinates.getNeighbors(edgeCoord)) {
        if(Coordinates.isCorner(neighbor) || b.getPieceAt(neighbor) == Piece.BLACK){
          Coord sandwichCord = null;
          try {
            sandwichCord = Coordinates.getSandwichCoord(neighbor, edgeCoord);
          } catch (CoordinateDoesNotExistException e) {
            
          }
          // if out of the board then we are good, check the next neighbor 
          if(sandwichCord == null) {
            continue;
          }
          else {
         // if a possible capture is found, return true
              for(Coord enemy: muscoviteCoords) {
                // assuming the king is at the corner, test if any black piece and sandwich the king
                if(canMoveTo(b, enemy, sandwichCord)) {
//                  b.printBoard();
                  return true;
                }
              }
          }
        }
      }
//      b.printBoard();
      return false;
  }
    
    public static boolean canKingBePotentiallyCapturedByBlackOufOfCenter(TablutBoardState b, Coord edgeCoord, HashSet<Coord> muscoviteCoords) {
        
        for (Coord neighbor : Coordinates.getNeighbors(edgeCoord)) {
          if(Coordinates.isCorner(neighbor) || b.getPieceAt(neighbor) == Piece.BLACK){
            Coord sandwichCord = null;
            try {
              sandwichCord = Coordinates.getSandwichCoord(neighbor, edgeCoord);
            } catch (CoordinateDoesNotExistException e) {
              
            }
            // if out of the board then we are good, check the next neighbor 
            if(sandwichCord == null) {
              continue;
            }
            else {
           // if a possible capture is found, return true
                for(Coord enemy: muscoviteCoords) {
                  // assuming the king is at the corner, test if any black piece and sandwich the king
                  if(canMoveTo(b, enemy, sandwichCord, edgeCoord)) {
//                    b.printBoard();
                    return true;
                  }
                }
            }
          }
        }
//        b.printBoard();
        return false;
    }
    public static boolean canKingBeCapturedByBlackEverywhere(TablutBoardState b, Coord kingPos, HashSet<Coord> muscoviteCoords) {
      if (Coordinates.isCenterOrNeighborCenter(kingPos)) {
        for (Coord neighbor : Coordinates.getNeighbors(kingPos)) {
          // if there exits a neighbor that is neither black nor the center, the king cannot be captured
            if (b.getPieceAt(neighbor) != Piece.BLACK && !Coordinates.isCenter(neighbor)) {
                return false;
            }
        }
        return true;
      }
      else {
        for (Coord neighbor : Coordinates.getNeighbors(kingPos)) {
          if(Coordinates.isCorner(neighbor) || b.getPieceAt(neighbor) == Piece.BLACK){
            Coord sandwichCord = null;
            try {
              sandwichCord = Coordinates.getSandwichCoord(neighbor, kingPos);
            } catch (CoordinateDoesNotExistException e) {
              
            }
            // if out of the board then we are good, check the next neighbor 
            if(sandwichCord == null) {
              continue;
            }
            else {
              // if a possible capture is found, return true
              for(Coord enemy: muscoviteCoords) {
                // assuming the king is at the corner, test if any black piece and sandwich the king
                if(canMoveTo(b, enemy, sandwichCord, kingPos)) {
                  return true;
                }
              }
            }
          }
        }
        return false;
      }
  }

    
    public static List<Coord> edgeCoordsCanGetToWhileNotGetSandwiched(TablutBoardState b, Coord kingPos, HashSet<Coord> muscoviteCoords) {
      List<Coord> result = new ArrayList<Coord>();
      if(isOnEdge(b, kingPos)) {
        return result;
      }
      Coord[] edgeCoords = {Coordinates.get(kingPos.x, 0), Coordinates.get(kingPos.x, 8), Coordinates.get(0, kingPos.y), Coordinates.get(8, kingPos.y)};
      for(Coord edgeCoord : edgeCoords) {    
        if(canMoveTo(b, kingPos, edgeCoord)) {
          if(!canKingBePotentiallyCapturedByBlackOufOfCenter(b, edgeCoord, muscoviteCoords)) {
            result.add(edgeCoord);
          }
        }
      }
      return result;
    }
    
    public static int numberOfOnlyWhiteOnThePath(TablutBoardState b, Coord kingPos, Coord corner) {
      List<Coord> coords = kingPos.getCoordsBetween(corner);
      int count = 0;
      for(Coord c : coords) {
        if(b.getPieceAt(c) == Piece.BLACK) {
          return 0;
        }
        if(b.getPieceAt(c) == Piece.WHITE) {
          count ++ ;
        }
      }
      return count;
    }
    
    public static boolean canBlockKing(TablutBoardState boardState, Coord kingPos, Coord corner, HashSet<Coord> muscoviteCoords) {
      List<Coord> pathToCorner = kingPos.getCoordsBetween(corner);
      pathToCorner.remove(corner);
      // king right next to the corner, black loses
      if(pathToCorner.isEmpty()) {
        return false;
      }
      boolean canBlockKing = false;
      for(Coord path: pathToCorner) {
        for(Coord muscoviteCoord : muscoviteCoords) {
          if(canMoveTo(boardState, muscoviteCoord, path)) {
            canBlockKing = true;
            break;
          }
        }
        if(canBlockKing) {
          break;
        }
      }
      return canBlockKing;
    }
    
    public static boolean canPotentiallyBlockKing(TablutBoardState boardState, Coord edgeCoord, Coord corner, HashSet<Coord> muscoviteCoords) {
      List<Coord> pathToCorner = edgeCoord.getCoordsBetween(corner);
      pathToCorner.remove(corner);
      // king right next to the corner, black loses
      if(pathToCorner.isEmpty()) {
        return false;
      }
      boolean canBlockKing = false;
      for(Coord path: pathToCorner) {
        for(Coord muscoviteCoord : muscoviteCoords) {
          if(canMoveTo(boardState, muscoviteCoord, path, edgeCoord)) {
            canBlockKing = true;
            break;
          }
        }
        if(canBlockKing) {
          break;
        }
      }
      return canBlockKing;
    }
    
    public static boolean canBlockKingToEdge(TablutBoardState boardState, Coord kingPos, Coord edgeCoord, HashSet<Coord> muscoviteCoords) {
      List<Coord> pathToEdge = kingPos.getCoordsBetween(edgeCoord);

      boolean canBlockKing = false;
      for(Coord path: pathToEdge) {
        for(Coord muscoviteCoord : muscoviteCoords) {
          if(canMoveTo(boardState, muscoviteCoord, path)) {
            canBlockKing = true;
            break;
          }
        }
        if(canBlockKing) {
          break;
        }
      }
   // if black can't block the king, then it will lose
      return true;
    }
    
    
    
    public static int getKingScore(TablutBoardState boardState, Coord kingPos, HashSet<Coord> muscoviteCoords) {    
      if(boardState.getTurnPlayer() == 0){        
        if(canKingBeCapturedByBlackEverywhere(boardState, kingPos, muscoviteCoords)) {  
//          boardState.printBoard();
          // for black, it will win
            return Integer.MAX_VALUE - 1;
          }
          // else, calculate the gain
          else {
            return getRewardStepsKingToCorner(boardState, kingPos, muscoviteCoords);
          }
      }
      else {
          return getRewardStepsKingToCorner(boardState, kingPos, muscoviteCoords);
      }
    }
    
    public static int getRewardStepsKingToCorner(TablutBoardState boardState, Coord kingPos, HashSet<Coord> muscoviteCoords) {


      // if king is already on the edge
      if(isOnEdge(boardState, kingPos)) {
        for(Coord corner : Coordinates.getCorners()) {          
          if(canMoveTo(boardState, kingPos, corner)) {
            // for white, this is equal to winning
            if(boardState.getTurnPlayer() == 1){
              //boardState.printBoard();
              return Integer.MAX_VALUE - 1;
            }
            // for black
            else {
              // if king cannot be captured, check if black can block the king from moving to the corner  
              if(!canBlockKing(boardState, kingPos, corner, muscoviteCoords)) {
                // if black can't block the king, it will loss
                //boardState.printBoard();
                return Integer.MIN_VALUE + 1;
              }
              else {
                // else we have to block the king, thus negative reward
                return -5 * KING_STEP_WEIGHT;
              }
            }
          }
        }
      }
      
      // king is not on the edge
      else{
        // get safe edge coordinate which king can move to
        List<Coord> edgeCoords = edgeCoordsCanGetToWhileNotGetSandwiched(boardState, kingPos, muscoviteCoords);
        
        if(edgeCoords.isEmpty()) {
          return 0;
        }
        else {
          int numReachableCorner = 0;
          // for each edge coordinate, check whether it can go to the corner
          for(Coord edgeCoord : edgeCoords) {       
            int cornerCount = 0;
            for(Coord corner : Coordinates.getCorners()) {          
              if(canMoveTo(boardState, edgeCoord, corner)) {
//                boardState.printBoard();
                cornerCount++;
                // if king can reach to 2 corner in a edge coord
                // note this operation is expensive, but is not very likely the case
                if(cornerCount == 2){
                  if(boardState.getTurnPlayer() == 1){            
                    //boardState.printBoard();
                    return Integer.MAX_VALUE - 1;
                  }
                  else {
                    // if it's black's turn, check if any black coord can block the path from kingPos to the edgeCoord 
                    
                    
                 // if black can't block the king, then it will lose
                    if(!canBlockKingToEdge(boardState, kingPos, edgeCoord, muscoviteCoords)) {
                      //boardState.printBoard();
                      return Integer.MIN_VALUE + 1;
                    }
                    else {
                      return -5 * KING_STEP_WEIGHT;
                    }
                  }
                }
                
                numReachableCorner++;
                
                // if found one reachable corner
                // get all the coords between the edge coord and the current corner
                if(!canPotentiallyBlockKing(boardState, edgeCoord, corner, muscoviteCoords)) {
                  // if it's is the white's turn and black can't block the king, white will win
                  if(boardState.getTurnPlayer() == 1) {          
                    //boardState.printBoard();
                    return Integer.MAX_VALUE - 1;                    
                  }
                  else {
                    // black's turn, if they can't block then black will lose
                    if(!canBlockKingToEdge(boardState, kingPos, edgeCoord, muscoviteCoords)) {
                      //boardState.printBoard();
                      return Integer.MIN_VALUE + 1;
                    }
                  }
                }
              }
              else {
                int numwhiteInTheWay = numberOfOnlyWhiteOnThePath(boardState, edgeCoord, corner);
                if(numwhiteInTheWay != 0) {
                  numReachableCorner += 1.0/(numwhiteInTheWay + 1);
                }
              }
              // for white, if king can reach to 2 corner at once, this is equal to winning  
              
              
            }
            
          }
          if(boardState.getTurnPlayer() == 1){              
            
            return numReachableCorner * KING_STEP_WEIGHT;
          }
          // for black, have to block this, thus negative reward
          else {
            return - numReachableCorner * KING_STEP_WEIGHT;
          }
        }
      }
      return 0;
    }
    
    
    public static void main(String[] args) {
      TablutBoardState b = new TablutBoardState();
      b.printBoard();
      System.out.println(getMaxNumberOfWhiteAdvantageInEachRegion(b));
    }

}
