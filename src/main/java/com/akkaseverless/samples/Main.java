package com.akkaseverless.samples;

import com.akkaserverless.javasdk.AkkaServerless;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.move.Move;

public final class Main {
    
    public static void main(String[] args) throws Exception {
        new AkkaServerless()
            .registerEventSourcedEntity(
                ChessGameEntity.class,
                ServiceApi.getDescriptor().findServiceByName("ChessGameService"),
                Domain.getDescriptor()
            )
            .start().toCompletableFuture().get();

    }

    
}