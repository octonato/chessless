package com.akkaseverless.samples;

import com.akkaserverless.javasdk.valueentity.CommandContext;
import com.akkaseverless.samples.ChessPuzzleApi.*;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.*;

import java.util.Optional;

public class ChessPuzzleServiceTest {
  private String puzzleId = "puzzle1";
  private CommandContext<PuzzleDomain.Puzzle> context = Mockito.mock(CommandContext.class);

  @Test
  public void createTest() {
    ChessPuzzleEntity puzzleEntity = new ChessPuzzleEntity(puzzleId);

    String fen = "k7/3Q4/p7/5K2/8/8/8/1R6 w - - 0 1";

    CreatePuzzle createPuzzle = CreatePuzzle
        .newBuilder()
        .setPuzzleId(puzzleId)
        .setFenText(fen)
        .setDifficultLevel("easy")
        .setDescription("Move one piece and checkmate")
        .build();

    puzzleEntity.create(createPuzzle, context);

    PuzzleDomain.Puzzle state = PuzzleConversions.create(createPuzzle);
    Mockito.verify(context).updateState(state);

    Mockito.when(context.getState()).thenReturn(Optional.of(state));
    ChessPuzzle puzzle = puzzleEntity.get(GetPuzzle.newBuilder().setPuzzleId(puzzleId).build(), context);
    Assert.assertEquals(puzzle.getFenText(), fen);
  }
}