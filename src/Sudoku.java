/**
 * The Sudoku solver.
 *
 * @author Maxwell S. Freudenburg
 * @author Rowan Mukaida
 */
public class Sudoku {

    /** Prints the solution to the puzzle in the specified directory. */
    public static void main(String[] args) {
        String puzzle = new In("sudoku1.txt").readAll();
        Square[][] grid = createSquares(puzzle);
        long startTime = System.nanoTime();
        solve(grid);
        long elapsedTime = System.nanoTime() - startTime;
        System.out.println("Elapsed Time: " + elapsedTime);
        System.out.println(toString(grid));
        System.out.println(puzzle.length());
        puzzle = new In("sudoku - hardcore mode.txt").readAll();
        grid = createSquares(puzzle);
        startTime = System.nanoTime();
        solve(grid);
        elapsedTime = System.nanoTime() - startTime;
        System.out.println("Elapsed Time: " + elapsedTime);
        System.out.println(toString(grid));
        System.out.println(puzzle.length());
    }

    /** Returns a new Location object with the specified row and column. */
    static Location createLocation(int r, int c) {
        Location out = new Location();
        out.column = c;
        out.row = r;
        return out;
    }

    /** Returns an array of the eight Locations in the same row as here. */
    static Location[] findRow(Location here) {
        int i = 0;
        Location[] out = new Location[8];
        int row = here.row;
        for (int col = 0; col < 9; ++col){
            if (col == here.column) continue;
            out[i] = createLocation(row, col);
            i++;
        }
        return out;
    }

    /** Returns an array of the eight Locations in the same column as here. */
    static Location[] findColumn(Location here) {
        int i = 0;
        Location[] out = new Location[8];
        int col = here.column;
        for (int row = 0; row < 9; ++row){
            if (row == here.row) continue;
            out[i] = createLocation(row, col);
            i++;
        }
        return out;
    }

    /** Returns an array of the eight Locations in the same 3x3 block as here. */
    static Location[] findBlock(Location here) {
        Location[] out = new Location[8];
        Location blockLocation = new Location();
        switch (here.column) {
            case 0: // no break means cases 0-2 default to the argument below case 2.
            case 1:
            case 2:
                blockLocation.column = 0;
                break;
            case 3:
            case 4:
            case 5:
                blockLocation.column = 1;
                break;
            case 6:
            case 7:
            case 8:
                blockLocation.column = 2;
                break;
        }
        switch (here.row) {
            case 0:
            case 1:
            case 2:
                blockLocation.row = 0;
                break;
            case 3:
            case 4:
            case 5:
                blockLocation.row = 1;
                break;
            case 6:
            case 7:
            case 8:
                blockLocation.row = 2;
                break;
        }
        //  finds coords to populate via:
        //  (block coord * 3) thru ((block coord * 3) + 3)
        //  thus, block 2,0 searches coords (6-9, 0-3)
        int i = 0;
        for (int row = blockLocation.row * 3; row < (blockLocation.row * 3) + 3; ++row){
            for (int col = blockLocation.column * 3; col < (blockLocation.column * 3) + 3; ++col){
                Location temp = createLocation(row, col);
                if (temp.row == here.row && temp.column == here.column) continue;
                out[i] = temp;
                ++i;
            }
        }
//        System.out.println("here: " + here.row + "," + here.column);
//        for (i = 0; i < 8; ++i){
//            System.out.print(out[i].row + "," + out[i].column + " | ");
//        }
        return out;
    }

    /** Returns a String representing grid, showing the numbers (or . for squares with value 0). */
    static String toString(Square[][] grid) {
        String out = new String();
        out = "";
        for (int row = 0; row < 9; ++row){
            for (int col = 0; col < 9; ++col){
                if (grid[row][col].value == 0) {
                    out += ".";
                } else out += grid[row][col].value;
            }
            out += "\n";
        }
        return out;
    }

    /**
     * Returns a 9x9 array of Squares, each of which has value 0 and knows about the other squares in its row,
     * column, and block.
     */
    static Square[][] createSquares() {
        Square[][] out = new Square[9][9];

        for (int col = 0; col < 9; ++col){
            for (int row = 0; row < 9; ++row){
                out[row][col] = new Square();
            }
        }
        for (int col = 0; col < 9; ++col){
            for (int row = 0; row < 9; ++row){
                Location here = createLocation(row,col);
                out[row][col].value = 0;
                out[row][col].row = populate(findRow(here), out);
                out[row][col].column = populate(findColumn(here), out);
                out[row][col].block = populate(findBlock(here), out);
            }
        }
        return out;
    }

    /**
     * Returns a 9x9 array of Squares, each of which has value 0 and knows about the other squares in its row,
     * column, and block. Any numbers in diagram are filled in to the grid; empty squares should be given as
     * periods.
     */
    static Square[][] createSquares(String diagram) {
        Square[][] out = new Square[9][9];
        int i = 0;
        for (int r = 0; r < 9; ++r) {
            for (int c = 0; c < 9; ++c) {
                out[r][c] = new Square();
                if (i >= diagram.length()) continue;
                if (diagram.charAt(i) == '.') {
                    out[r][c].value = 0;
                } else {
                    out[r][c].value = Character.getNumericValue(diagram.charAt(i));
                }
                i++;
            }
            i++;
        }
        for (int col = 0; col < 9; ++col){
            for (int row = 0; row < 9; ++row){
                Location here = createLocation(row,col);
                out[row][col].row = populate(findRow(here), out);
                out[row][col].column = populate(findColumn(here), out);
                out[row][col].block = populate(findBlock(here), out);
            }
        }
        return out;
    }

    /**
     * Takes an array of locations as arguments and returns the squares at location-referenced indices
     * @param locations Locations in array to fetch squares for
     * @param map Reference to map being created
     * @return an array of the squares at [locations] in map
     */
    private static Square[] populate(Location[] locations, Square[][] map) {
        Square[] out = new Square[locations.length];
        for (int i = 0; i < locations.length; i++) {
            out[i] = map[locations[i].row][locations[i].column];
        }
        return out;
    }

    /**
     * Returns a boolean array of length 10. For each digit, the corresponding entry in the array is true
     * if that number does not appear elsewhere in s's row, column, or block.
     */
    static boolean[] findValidNumbers(Square s) {
        boolean[] validNumbers = new boolean[10];
        // Set all indices to "true" to prep for process of elimination
        for (int i = 0; i < validNumbers.length; ++i) validNumbers[i] = true;
        // Go through every array at once.
        // Every number found sets its corresponding index in validNumbers to false.
        // Ex.: s has a "7" in s.row[i].value. validNumbers[7] is set to false.
        for (int i = 0; i < 8; ++i){
            validNumbers[s.row[i].value] = false;
            validNumbers[s.column[i].value] = false;
            validNumbers[s.block[i].value] = false;
        }
        return validNumbers;
    }

    /**
     * Returns true if grid can be solved. If so, grid is modified to fill in that solution.
     */
    static boolean solve(Square[][] grid) {
        Location[] queue = createQueue(grid);
        for (int i = 0; i < queue.length; i++) {
            if (queue[i] == null) return false;
        }
        solve(grid, queue, 0, 1);
        return true;
    }

    /**
     * Recursive arm of solving mechanism
     * @param queue order in which to guess
     * @param i index in guess queue
     * @param g guess
     * @return true => puzzle solved. false => guess again/go back
     */
    static boolean solve(Square[][] grid, Location[] queue, int i, int g) {
        if (i == queue.length) return true; // Exception: queue finished (we win)
        Location coords = queue[i]; // easier reference
        boolean[] options = findValidNumbers(grid[coords.row][coords.column]);
        if (g >= options.length){
            return false; // Exception: index out of bounds. No options work.
        }
        if (!options[g]){
            return solve(grid, queue, i, g+1);
        }
        grid[coords.row][coords.column].value = g; // Guesses g, which IS a valid option
        // Default/continue statement
        if (solve(grid,queue,i+1, 1)) { // GO DEEPER! Try next index. This is START
            return true;
        } else {
            grid[coords.row][coords.column].value = 0; // undo guess
            if (solve(grid, queue, i, g+1)){
                return true;
            }
        }
        return false;
    }

    public static int countEmpty(Square[][] grid) {
        int out = 0;
        for (int c = 0; c < 9; ++c){
            for (int r = 0; r < 9; ++r){
                if (grid[r][c].value == 0){
                    out++;
                }
            }
        }
        return out;
    }

    public static Location[] createQueue(Square[][] grid) {
        int[][] options = new int[9][9];
        int emptySquares = countEmpty(grid);
        Location[] queue = new Location[emptySquares];
        for (int row = 0; row < 9; row++){
            for (int col = 0; col < 9; col++){
                if (grid[row][col].value != 0){
                    options[row][col] = 0;
                    continue;
                }
                options[row][col] = countOptions(grid, createLocation(row,col));
            }
        }
        int i = 0;
        int s = 1;
        do {
            for (int row = 0; row < 9; row++){
                for (int col = 0; col < 9; col++){
                    if (options[row][col] == 0) {
                        continue;
                    } else if (options[row][col] <= s && !inQueue(queue, createLocation(row,col))){
                        queue[i] = createLocation(row,col);
                        //System.out.println("Adding location (" + row + "," + col + ") to index " + i + ". options = " + options[row][col]);
                        i++;
                    }
                }
            }
            s++;
        } while (s < 9 && i < queue.length);
        for (int j = 0; j < queue.length; j++) {
            if (queue[j] == null) System.out.println("Uh oh! Queue index " + j + " is null!");
        }
        return queue;
    }

    private static int countOptions(Square[][] grid, Location coords){
        boolean[] validNumbers;
        validNumbers = findValidNumbers(grid[coords.row][coords.column]);
        return countOptions(grid, coords, validNumbers, 0);
    }

    private static int countOptions(Square[][] grid, Location coords, boolean[] validNumbers, int i){
        if (i >= validNumbers.length) return 0;
        if (validNumbers[i]) return 1 + countOptions(grid, coords, validNumbers, i+1);
        return countOptions(grid, coords, validNumbers, i+1);
    }

    private static boolean inQueue(Location[] queue, Location check){
        for (int i = 0; i < queue.length; ++i) {
            if (queue[i] == null) return false;
            if (queue[i].row == check.row && queue[i].column == check.column) return true;
        }
        return false;
    }

}
