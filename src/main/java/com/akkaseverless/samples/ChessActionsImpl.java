package com.akkaseverless.samples;

import com.akkaserverless.javasdk.Reply;
import com.akkaserverless.javasdk.ServiceCallRef;
import com.akkaserverless.javasdk.action.Action;
import com.akkaserverless.javasdk.action.ActionContext;
import com.akkaserverless.javasdk.action.Handler;
import com.akkaseverless.samples.ChessPuzzleApi.CreatePuzzle;
import com.akkaseverless.samples.RefIdProto.RefId;
import com.google.protobuf.Empty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * This Action is trying to act as Controller / Entpoint. 
 * It's sole job is to take incoming request, convert to domain commands and forward to the entity.
 * 
 * TODO: extend this to be the only external API.
 */
@Action
public class ChessActionsImpl {

  private Logger logger = LoggerFactory.getLogger(getClass());
  @Handler
  public Reply<RefId> createPuzzle(ChessActions.PuzzleDesc puzzleDesc, ActionContext ctx) {

    ServiceCallRef<CreatePuzzle> call =
        ctx.serviceCallFactory().lookup(
            "com.akkaseverless.samples.ChessPuzzleService",
            "Create",
            CreatePuzzle.class);

    String puzzleId = UUID.randomUUID().toString();
    String fen = puzzleDesc.getFenText();
    logger.info("creating new chess puzzle from FEN '{}' with puzzle id: '{}'", fen, puzzleId);

    CreatePuzzle createPuzzle = CreatePuzzle.newBuilder()
        .setPuzzleId(puzzleId)
        .setFenText(fen)
        .setDescription(puzzleDesc.getDescription())
        .setDifficultLevel(puzzleDesc.getDifficultLevel())
        .build();


    return Reply.forward(call.createCall(createPuzzle));
  }
}
