package com.akkaseverless.samples;

import com.akkaserverless.javasdk.eventsourcedentity.CommandContext;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class ChessGameServiceTest {
  private String entityId = "entityId1";

  private ServiceApi.GetBoard getBoard = ServiceApi.GetBoard.newBuilder().build();
  private CommandContext context = Mockito.mock(CommandContext.class);

  @Test
  public void startAndGetBoard() {
    ChessGameEntity game = new ChessGameEntity(entityId);

    // move white pawn to e4
    String moveStr = "e2e4";
    game.move(ServiceApi.MovePiece.newBuilder().setMovement(moveStr).build(), context);

    Domain.Moved moved = Domain.Moved.newBuilder().setMovement(moveStr).build();
    Mockito.verify(context).emit(moved);
    game.pieceMoved(moved);

    ServiceApi.Board board = game.get(getBoard);

    assertEquals("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1", board.getFenText());
    assertEquals("black", board.getNextPlayer());
  }

  @Test
  public void whiteQueenCheckMates() {

    ChessGameEntity game = new ChessGameEntity(entityId);

    { // load board from a FEN
      String fen = "k7/3Q4/p7/5K2/8/8/8/1R6 w - - 0 1";
      game.loadFen(ServiceApi.LoadFromFen.newBuilder().setFenText(fen).build(), context);

      Domain.BoardLoaded boardLoaded = Domain.BoardLoaded.newBuilder().setFenText(fen).build();
      Mockito.verify(context).emit(boardLoaded);
      game.boardLoaded(boardLoaded);

      assertFalse(game.get(getBoard).getCheckmated());
    }

    { // move queen and checkmate
      String movement = "d7b7";
      ServiceApi.MovePiece moveQueen = ServiceApi.MovePiece.newBuilder().setMovement(movement).build();
      game.move(moveQueen, context);

      Domain.Moved queenMoved = Domain.Moved.newBuilder().setMovement(movement).build();
      Mockito.verify(context).emit(queenMoved);
      game.pieceMoved(queenMoved);

      assertTrue(game.get(getBoard).getCheckmated());
    }
  }

  @Test
  public void cantPlayAFinishedGame() {

    ChessGameEntity game = new ChessGameEntity(entityId);

    { // load board from a FEN
      String fen = "k7/1Q6/p7/5K2/8/8/8/1R6 b - - 1 1";
      game.loadFen(ServiceApi.LoadFromFen.newBuilder().setFenText(fen).build(), context);

      Domain.BoardLoaded boardLoaded = Domain.BoardLoaded.newBuilder().setFenText(fen).build();
      Mockito.verify(context).emit(boardLoaded);
      game.boardLoaded(boardLoaded);

      assertTrue(game.get(getBoard).getCheckmated());
    }

    { // kind can't move pawn after end game
      String movement = "a6a5";
      RuntimeException exception =
          assertThrows(RuntimeException.class, () ->
              game.move(ServiceApi.MovePiece.newBuilder().setMovement(movement).build(), context)
          );

      assertTrue(exception.getMessage().startsWith("Invalid move: "));
    }
  }

  @Test
  public void illegalMovesAreRejected() {
    ChessGameEntity game = new ChessGameEntity(entityId);
    // illegal move
    String movement = "e2e1";

    RuntimeException exception =
        assertThrows(RuntimeException.class, () ->
            game.move(ServiceApi.MovePiece.newBuilder().setMovement(movement).build(), context)
        );

    assertTrue(exception.getMessage().startsWith("Invalid move: "));

  }

}