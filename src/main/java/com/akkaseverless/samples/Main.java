package com.akkaseverless.samples;

import com.akkaserverless.javasdk.AkkaServerless;

public final class Main {

  public static void main(String[] args) throws Exception {
    new AkkaServerless()
        .registerAction(
            ChessActionsImpl.class,
            ChessActions.getDescriptor().findServiceByName("ChessService"),
            ChessActions.getDescriptor()
        )
        .registerEventSourcedEntity(
            ChessGameEntity.class,
            ChessGameApi.getDescriptor().findServiceByName("ChessGameService"),
            ChessDomain.getDescriptor()
        )
        .registerValueEntity(
            ChessPuzzleEntity.class,
            ChessPuzzleApi.getDescriptor().findServiceByName("ChessPuzzleService"),
            PuzzleDomain.getDescriptor()
        )

        .start().toCompletableFuture().get();
  }

}