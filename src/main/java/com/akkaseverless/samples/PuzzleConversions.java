package com.akkaseverless.samples;

import com.akkaseverless.samples.ChessPuzzleApi;
import com.akkaseverless.samples.PuzzleDomain;

public class PuzzleConversions {
  public static PuzzleDomain.Puzzle create(ChessPuzzleApi.CreatePuzzle create) {
    // TODO: check that FEN is valid, for instance: not a checkmate game
    return PuzzleDomain.Puzzle
        .newBuilder()
        .setPuzzleId(create.getPuzzleId())
        .setFenText(create.getFenText())
        .setDescription(create.getDescription())
        .setDifficultLevel(create.getDifficultLevel())
        .build();
  }

  public static ChessPuzzleApi.ChessPuzzle toApiPuzzle(PuzzleDomain.Puzzle puzzle) {
    return ChessPuzzleApi.ChessPuzzle.newBuilder()
        .setPuzzleId(puzzle.getPuzzleId())
        .setFenText(puzzle.getFenText())
        .setDifficultLevel(puzzle.getDifficultLevel())
        .setDescription(puzzle.getDescription())
        .build();
  }
}
