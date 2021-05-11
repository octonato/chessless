package com.akkaseverless.samples;

import com.akkaserverless.javasdk.EntityId;
import com.akkaserverless.javasdk.Reply;
import com.akkaserverless.javasdk.ServiceCallRef;
import com.akkaserverless.javasdk.valueentity.CommandHandler;
import com.akkaserverless.javasdk.valueentity.CommandContext;
import com.akkaserverless.javasdk.valueentity.ValueEntity;
import com.akkaseverless.samples.ChessPuzzleApi.ChessPuzzle;
import com.akkaseverless.samples.ChessPuzzleApi.CreatePuzzle;
import com.akkaseverless.samples.ChessPuzzleApi.GetPuzzle;
import com.akkaseverless.samples.PuzzleDomain.Puzzle;
import com.google.protobuf.Empty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A value entity.
 */
@ValueEntity(entityType = "ChessPuzzleService")
public class ChessPuzzleEntity {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @SuppressWarnings("unused")

  private final String entityId;

  public ChessPuzzleEntity(@EntityId String entityId) {
    this.entityId = entityId;
  }

  @CommandHandler
  public Empty create(CreatePuzzle createPuzzle, CommandContext<Puzzle> ctx) {
    ctx.updateState(PuzzleConversions.create(createPuzzle));
    return Empty.getDefaultInstance();
  }

  @CommandHandler
  public ChessPuzzle get(GetPuzzle getPuzzle, CommandContext<Puzzle> ctx) {
    return PuzzleConversions.toApiPuzzle(getPuzzle(ctx));
  }

  @CommandHandler
  public Reply<Empty> start(ChessPuzzleApi.StartGame startGame, CommandContext<Puzzle> ctx) {

    Puzzle puzzle = getPuzzle(ctx);
    String fen = puzzle.getFenText();

    ServiceCallRef<ChessGameApi.StartFromFEN> callRef =
        ctx.serviceCallFactory().lookup(
            "com.akkaseverless.samples.ChessGameService",
            "Start",
            ChessGameApi.StartFromFEN.class);

    logger.debug("creating new game from FEN '{}', game id: '{}'", fen, startGame.getBoardId());
    ChessGameApi.StartFromFEN start =
        ChessGameApi.StartFromFEN
            .newBuilder()
            .setBoardId(startGame.getBoardId())
            .setFenText(fen)
            .build();

    return Reply.forward(callRef.createCall(start));
  }

  private Puzzle getPuzzle(CommandContext<Puzzle> ctx) {
    return ctx.getState().orElseThrow(() -> new RuntimeException("No puzzle found for id: " + ctx.entityId()));
  }
}
