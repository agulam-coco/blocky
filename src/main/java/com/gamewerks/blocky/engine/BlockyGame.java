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

    /**
     * Spawns a new random block on the screen from the lower center of the screen 
     */
    private void trySpawnBlock() {
        if (activePiece == null) {

            //reset peices
            if (this.current_index == PieceKind.ALL.length) {
                //shuffle peices and reset current index
                shuffle(this.block_pieces);
                this.current_index = 0;
                this.resetPieces(block_pieces);
            }

            activePiece = block_pieces[current_index];

            //increment blocks
            this.current_index++;

        }
    }

    /**
     * Shuffles the input array of block pieces using the Fisher-Yates shuffle.
     * @param arr , Array of Piece Objects
     */
    private void shuffle(Piece[] arr) {
        //System.out.println(arr.length);

        //run loop untill max_roll is exhausted and array is shuffledd
        for (int i = arr.length; i > 1; i--) {

            //random number for round
            //credit: https://stackoverflow.com/a/5271613
            Random r = new Random();
            int low = 1;
            int random_value = r.nextInt(i - low) + low;

            //if roll is not a pass
            if (random_value != i) {
                //swap random value position with corresponding end of the array
                this.swap(arr, random_value - 1, i - 1);
            }
        }    
    }

    //credit: https://stackoverflow.com/a/3624554
    /**
     * Performs a textbook swap on objects in a piece array
     * @param arr
     * @param pos1
     * @param pos2 
     */
    public void swap(Piece[] arr, int pos1, int pos2) {
        Piece temp = arr[pos1];
        arr[pos1] = arr[pos2];
        arr[pos2] = temp;
    }
    
    /**
     * This resets the position of the new blocks back to the middle top of the panel
     * @param arr 
     */
    private void resetPieces(Piece[] arr){
        for(int i = 0; i < this.block_pieces.length; i++){
            //rest position of new blocks
                    this.block_pieces[i].moveTo(new Position(2, Constants.BOARD_WIDTH / 2 - 2));
        }
    }

    //generate array of blocks 
    /**
     * Generates an array of all blocks pieces in the Blocky Universe
     * @return return_array
     */
    private Piece[] generatePieces() {
        Piece[] return_array = new Piece[PieceKind.ALL.length];
        for (int i = 0; i < PieceKind.ALL.length; i++) {
            //fill array with piece objects of each letter type.
            
            //position must start from 2 in order for object to fill in the whole screen
            //VEERY SNEAKY OSERA
            return_array[i] = new Piece(PieceKind.ALL[i], new Position(2, Constants.BOARD_WIDTH / 2 - 2));
        }
        return return_array;
    }

    /**
     * Handles movement input from user
     */
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
                //bug on the break which throws exception
                //SNEAKY OSERA!!
                break;
            default:
                throw new IllegalStateException("Unrecognized direction: " + movement.name());
        }
        if (!board.collides(activePiece.getLayout(), nextPos)) {
            activePiece.moveTo(nextPos);
        }
        
    }
    
    /**
     * Handles the falling of blocks 
     */
    private void processGravity() {
        Position nextPos = activePiece.getPosition().add(1, 0);
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

    /**
     * Process the clearing of lines ones they are filled
     */
    private void processClearedLines() {
        board.deleteRows(board.getCompletedRows());
    }

    /**
     * Steps through the game and sequentially does functions
     */
    public void step() {
        trySpawnBlock();
        //Missing movement
        processMovement();
        processGravity();
        processClearedLines();
    }

    /**
     * Returns the board current well
     * @return Boolean[][]
     */
    public boolean[][] getWell() {
        return board.getWell();
    }

    /**
     * Returns the active piece on the board
     * @return Piece
     */
    public Piece getActivePiece() {
        return activePiece;
    }

    /**
     * This sets the direction of movement of the falling block
     * @param movement, the direction the block should move to
     */ 
    public void setDirection(Direction movement) {
        this.movement = movement;
    }

    /**
     * This rotates the piece in the given direction
     * @param dir, direction the pice should be rotated
     */
    public void rotatePiece(boolean dir) {
        activePiece.rotate(dir);
    }
}
