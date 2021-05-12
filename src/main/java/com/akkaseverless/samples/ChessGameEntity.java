package com.akkaseverless.samples;

import com.akkaserverless.javasdk.EntityId;
import com.akkaserverless.javasdk.eventsourcedentity.*;
import com.akkaseverless.samples.RefIdProto.RefId;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.move.Move;
import com.google.protobuf.Empty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

/**
 * An event sourced entity.
 */
@EventSourcedEntity(entityType = "ChessGameService")
public class ChessGameEntity {

  private Logger logger = LoggerFactory.getLogger(getClass());
  private Board board = new Board();

  @SuppressWarnings("unused")
  private final String entityId;

  public ChessGameEntity(@EntityId String entityId) {
    this.entityId = entityId;
  }

  @Snapshot
  public ChessDomain.ChessGame snapshot() {
    return toChessGame();
  }

  @SnapshotHandler
  public void handleSnapshot(ChessDomain.ChessGame chessGame) {
    board.loadFromFen(chessGame.getFenText());
  }


  @CommandHandler
  public ChessGameApi.Board move(ChessGameApi.MovePiece movePiece, CommandContext ctx) {
    String validMovement = validateMove(movePiece.getMovement(), ctx);
    logger.debug("movement '{}' is valid", validMovement);
    ctx.emit(ChessDomain.Moved.newBuilder().setMovement(validMovement).build());
    return toApiBoard();
  }

  @CommandHandler
  public RefId start(ChessGameApi.StartFromFEN loadFromFen, CommandContext ctx) {
    logger.debug("staring board from FEN '{}'", loadFromFen.getFenText());
    ctx.emit(ChessDomain.BoardLoaded.newBuilder().setFenText(loadFromFen.getFenText()).build());

    return RefId.newBuilder().setType("board").setId(entityId).build();
  }

  @CommandHandler
  public ChessGameApi.Board get(ChessGameApi.GetBoard getBoard) {
    return toApiBoard();
  }


  @EventHandler
  public void pieceMoved(ChessDomain.Moved pieceMoved) {
    String movement = pieceMoved.getMovement();
    board.doMove(movement);
  }

  @EventHandler
  public void boardLoaded(ChessDomain.BoardLoaded boardLoaded) {
    board.loadFromFen(boardLoaded.getFenText());
  }

  private String validateMove(String moveStr, CommandContext ctx) {
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
      throw ctx.fail("Unexpected error");
    }
  }

  private ChessDomain.ChessGame toChessGame() {
    return ChessDomain.ChessGame.newBuilder().setFenText(board.getFen()).build();
  }

  private ChessGameApi.Board toApiBoard() {
    return ChessGameApi.Board.newBuilder()
        .setFenText(board.getFen())
        .setCheckmated(board.isMated())
        .setNextPlayer(board.getSideToMove().name().toLowerCase(Locale.ROOT))
        .build();
  }
}