package com.akkaseverless.samples;

import com.akkaserverless.javasdk.AkkaServerless;
import com.akkaseverless.samples.persistence.Domain;

public final class Main {
    
    public static void main(String[] args) throws Exception {
        new AkkaServerless()
            .registerEventSourcedEntity(
                MyServiceEntityImpl.class,
                MyEntity.getDescriptor().findServiceByName("MyServiceEntity"),
                Domain.getDescriptor()
            )
            .start().toCompletableFuture().get();
    }
    
}