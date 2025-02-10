package com.gamewerks.blocky.engine;

import com.gamewerks.blocky.util.Constants;
import com.gamewerks.blocky.util.Position;

import java.util.Random;

public class BlockyGame {

    private static final int LOCK_DELAY_LIMIT = 30;

    private Board board;
    private Piece activePiece;
    private Direction movement;

    //variables controlling random blocks 
    private Piece[] block_pieces = generatePieces();
    private int current_index = 0;

    private int lockCounter;

    public BlockyGame() {
        board = new Board();
        movement = Direction.NONE;
        lockCounter = 0;
        shuffle(this.block_pieces);
        trySpawnBlock();
 
    }

    private void trySpawnBlock() {
        if (activePiece == null) {

            //reset peices
            if (this.current_index == PieceKind.ALL.length) {
                //shuffle peices and reset current index
                shuffle(this.block_pieces);
                this.current_index = 0;
            }

            //activePiece = new Piece(PieceKind.I, new Position(Constants.BOARD_HEIGHT - 1, Constants.BOARD_WIDTH / 2 - 2));
            
            activePiece = block_pieces[current_index];
            
            //increment blocks
            this.current_index++;
            
            if (board.collides(activePiece)) {
                System.exit(0);
            }
        }
    }

    //shuffles array 
    private void shuffle(Piece[] arr) {
        //System.out.println(arr.length);

        //run loop untill max_roll is exhausted and array is suffledd
        for (int i = arr.length; i > 1; i--) {

            //random number for round
            //credit: https://stackoverflow.com/a/5271613
            Random r = new Random();
            int low = 1;
            int random_value = r.nextInt(i - low) + low;
            
            System.out.println(i-low);

            //if roll is not a pass
            if (random_value != i) {
                //swap random value position with corresponding end of the array
                this.swap(arr, random_value - 1, i - 1);
            }
        }
    }

    //credit: https://stackoverflow.com/a/3624554
    public void swap(Piece[] arr, int pos1, int pos2) {
        Piece temp = arr[pos1];
        arr[pos1] = arr[pos2];
        arr[pos2] = temp;
    }

    //generate array of blocks 
    private Piece[] generatePieces() {
        Piece[] return_array = new Piece[PieceKind.ALL.length];
        for (int i = 0; i < PieceKind.ALL.length; i++) {
            //fill array with piece objects of each letter type.
            return_array[i] = new Piece(PieceKind.ALL[i], new Position(Constants.BOARD_HEIGHT - 1, Constants.BOARD_WIDTH / 2 - 2));
        }
        return return_array;
    }

    private void processMovement() {
        Position nextPos;
        switch (movement) {
            case NONE:
                nextPos = activePiece.getPosition();
                break;
            case LEFT:
                nextPos = activePiece.getPosition().add(0, -1);
                break;
            case RIGHT:
                nextPos = activePiece.getPosition().add(0, 1);
            default:
                throw new IllegalStateException("Unrecognized direction: " + movement.name());
        }
        if (!board.collides(activePiece.getLayout(), nextPos)) {
            activePiece.moveTo(nextPos);
        }
    }

    private void processGravity() {
        Position nextPos = activePiece.getPosition().add(-1, 0);
        if (!board.collides(activePiece.getLayout(), nextPos)) {
            lockCounter = 0;
            activePiece.moveTo(nextPos);
        } else {
            if (lockCounter < LOCK_DELAY_LIMIT) {
                lockCounter += 1;
            } else {
                board.addToWell(activePiece);
                lockCounter = 0;
                activePiece = null;
            }
        }
    }

    private void processClearedLines() {
        board.deleteRows(board.getCompletedRows());
    }

    public void step() {
        trySpawnBlock();
        processGravity();
        processClearedLines();
    }

    public boolean[][] getWell() {
        return board.getWell();
    }

    public Piece getActivePiece() {
        return activePiece;
    }

    public void setDirection(Direction movement) {
        this.movement = movement;
    }

    public void rotatePiece(boolean dir) {
        activePiece.rotate(dir);
    }
}
