#!/bin/sh


function puzzle.create() {
  grpcurl  -plaintext -d @ localhost:9000 com.akkaseverless.samples.ChessPuzzleService/Create <<EOM
{
  "puzzleId": "$1",
  "fenText": "$2",
  "description": "$3",
  "difficultLevel": "$4"
}
EOM
}

function puzzle.startGame() {
  grpcurl  -plaintext -d @ localhost:9000 com.akkaseverless.samples.ChessPuzzleService/Start <<EOM
{
  "puzzleId": "$1",
  "boardId": "$2"
}
EOM


}
function puzzle.get() {
  grpcurl  -plaintext -d @ localhost:9000 com.akkaseverless.samples.ChessPuzzleService/Get <<EOM
{
  "puzzleId": "$1"
}
EOM
}


function board.move() {
  grpcurl  -plaintext -d @ localhost:9000 com.akkaseverless.samples.ChessGameService/Move <<EOM
{
  "boardId": "$1",
  "movement": "$2"
}
EOM
}

function board.get() {
  grpcurl  -plaintext -d @ localhost:9000 com.akkaseverless.samples.ChessGameService/Get <<EOM
{
  "boardId": "$1"
}
EOM
}