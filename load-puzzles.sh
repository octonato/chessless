#!/bin/sh

 grpcurl  -plaintext -d '{"puzzleId": "foo", "fenText": "k7/3Q4/p7/5K2/8/8/8/1R6 w - - 0 1", "description": "easy one", "difficultLevel": "easy" }' localhost:9000 com.akkaseverless.samples.ChessPuzzleService/Create