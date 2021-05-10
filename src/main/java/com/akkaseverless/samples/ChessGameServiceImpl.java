package com.akkaseverless.samples;

import com.akkaserverless.javasdk.EntityId;
import com.akkaserverless.javasdk.eventsourcedentity.*;
import com.google.protobuf.Empty;

/** An event sourced entity. */
@EventSourcedEntity(entityType = "ChessGameService")
public class ChessGameServiceImpl extends ChessGameService {
    @SuppressWarnings("unused")
    private final String entityId;
    
    public ChessGameServiceImpl(@EntityId String entityId) {
        this.entityId = entityId;
    }
    
    @Override
    public Domain.ChessGame snapshot() {
        // TODO: produce state snapshot here
        return Domain.ChessGame.newBuilder().build();
    }
    
    @Override
    public void handleSnapshot(Domain.ChessGame snapshot) {
        // TODO: restore state from snapshot here
        
    }
    
    @Override
    public Empty move(ServiceApi.MovePiece command, CommandContext ctx) {
        throw ctx.fail("The command handler for `Move` is not implemented, yet");
    }
    
    @Override
    public ServiceApi.Board get(ServiceApi.GetBoard command, CommandContext ctx) {
        throw ctx.fail("The command handler for `Get` is not implemented, yet");
    }
    
    @Override
    public void moved(Domain.Moved event) {
        throw new RuntimeException("The event handler for `Moved` is not implemented, yet");
    }
}