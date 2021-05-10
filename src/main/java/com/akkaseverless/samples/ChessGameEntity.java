package com.akkaseverless.samples;

import com.akkaserverless.javasdk.EntityId;
import com.akkaserverless.javasdk.eventsourcedentity.*;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.move.Move;
import com.google.protobuf.Empty;

/**
 * An event sourced entity.
 */
@EventSourcedEntity(entityType = "ChessGameService")
public class ChessGameEntity {

  private Board board = new Board();

  @SuppressWarnings("unused")
  private final String entityId;

  public ChessGameEntity(@EntityId String entityId) {
    this.entityId = entityId;
  }

  @Snapshot
  public Domain.ChessGame snapshot() {
    return toChessGame();
  }

  @SnapshotHandler
  public void handleSnapshot(Domain.ChessGame chessGame) {
    board.loadFromFen(chessGame.getFenText());
  }


  @CommandHandler
  public Empty move(ServiceApi.MovePiece movePiece, CommandContext ctx) {
    String validMovement = validateMove(movePiece.getMovement());
    ctx.emit(Domain.Moved.newBuilder().setMovement(validMovement).build());
    return Empty.getDefaultInstance();
  }

  @CommandHandler
  public Empty loadFen(ServiceApi.LoadFromFen loadFromFen, CommandContext ctx) {
    ctx.emit(Domain.BoardLoaded.newBuilder().setFenText(loadFromFen.getFenText()).build());
    return Empty.getDefaultInstance();
  }

  @CommandHandler
  public ServiceApi.Board get(ServiceApi.GetBoard getBoard) {
    return toApiBoard();
  }


  @EventHandler
  public void pieceMoved(Domain.Moved pieceMoved) {
    String movement = pieceMoved.getMovement();
    board.doMove(movement);
  }

  @EventHandler
  public void boardLoaded(Domain.BoardLoaded boardLoaded) {
    board.loadFromFen(boardLoaded.getFenText());
  }

  private String validateMove(String moveStr) {
    Move move = new Move(moveStr, board.getSideToMove());
    if (board.isMoveLegal(move, true)) {
      return moveStr;
    } else {
      // if illegal, we force it on a clone to get the error message:
      try {
        Board clone = new Board();
        clone.loadFromFen(board.getFen());
        clone.doMove(moveStr);
      } catch (Exception ex) {
        throw new RuntimeException("Invalid move: [" + ex.getMessage() + "]");
      }

      // if we throw this, it means that isMoveLegal returned false,
      // but we were able to apply it anyway
      throw new RuntimeException("Unexpected error");
    }
  }

  private Domain.ChessGame toChessGame() {
    return Domain.ChessGame.newBuilder().setFenText(board.getFen()).build();
  }

  private ServiceApi.Board toApiBoard() {

    return ServiceApi.Board.newBuilder()
        .setFenText(board.getFen())
        .setCheckmated(board.isMated())
        .build();
  }
}