import itertools
import random
random.seed(0x0FF1CE)


class Minesweeper():
    """
    Minesweeper game representation
    """

    def __init__(self, height=8, width=8, mines=8):

        # Set initial width, height, and number of mines
        self.height = height
        self.width = width
        self.mines = set()

        # Initialize an empty field with no mines
        self.board = []
        for i in range(self.height):
            row = []
            for j in range(self.width):
                row.append(False)
            self.board.append(row)

        # Add mines randomly
        while len(self.mines) != mines:
            i = random.randrange(height)
            j = random.randrange(width)
            if not self.board[i][j]:
                self.mines.add((i, j))
                self.board[i][j] = True

        # At first, player has found no mines
        self.mines_found = set()

    def print(self):
        """
        Prints a text-based representation
        of where mines are located.
        """
        for i in range(self.height):
            print("--" * self.width + "-")
            for j in range(self.width):
                if self.board[i][j]:
                    print("|X", end="")
                else:
                    print("| ", end="")
            print("|")
        print("--" * self.width + "-")

    def is_mine(self, cell):
        i, j = cell
        return self.board[i][j]

    def nearby_mines(self, cell):
        """
        Returns the number of mines that are
        within one row and column of a given cell,
        not including the cell itself.
        """

        # Keep count of nearby mines
        count = 0

        # Loop over all cells within one row and column
        for i in range(cell[0] - 1, cell[0] + 2):
            for j in range(cell[1] - 1, cell[1] + 2):

                # Ignore the cell itself
                if (i, j) == cell:
                    continue
                # Ignore the diagonal cell
                if (abs(i-cell[0]) == abs(j-cell[1])):
                    continue
                # Update count if cell in bounds and is mine
                if 0 <= i < self.height and 0 <= j < self.width:
                    if self.board[i][j]:
                        count += 1

        return count

    def won(self):
        """
        Checks if all mines have been flagged.
        """
        return self.mines_found == self.mines


class Sentence():
    """
    Logical statement about a Minesweeper game
    A sentence consists of a set of board cells,
    and a count of the number of those cells which are mines.
    """

    def __init__(self, cells, count):
        self.cells = set(cells)
        self.count = count

    def __eq__(self, other):
        return self.cells == other.cells and self.count == other.count

    def __str__(self):
        return f"{self.cells} = {self.count}"

    def known_mines(self):
        """
        Returns the set of all cells in self.cells known to be mines.
        """
        if (len(self.cells) == self.count):
            return self.cells
        return set()

    def known_safes(self):
        """
        Returns the set of all cells in self.cells known to be safe.
        """
        if self.count == 0:
            return self.cells
        return set()

    def mark_mine(self, cell):
        """
        Updates internal knowledge representation given the fact that
        a cell is known to be a mine.
        """
        if cell in self.cells:
            self.cells.remove(cell)
            self.count -= 1

    def mark_safe(self, cell):
        """
        Updates internal knowledge representation given the fact that
        a cell is known to be safe.
        """
        if cell in self.cells:
            self.cells.remove(cell)


class MinesweeperAI():
    """
    Minesweeper game player
    """

    def __init__(self, height=8, width=8):

        # Set initial height and width
        self.height = height
        self.width = width

        # Keep track of which cells have been clicked on
        self.moves_made = set()

        # Keep track of cells known to be safe or mines
        self.mines = set()
        self.safes = set()

        # List of sentences about the game known to be true
        self.knowledge = []

    def mark_mine(self, cell):
        """
        Marks a cell as a mine, and updates all knowledge
        to mark that cell as a mine as well.
        """
        self.mines.add(cell)
        for sentence in self.knowledge:
            sentence.mark_mine(cell)

    def mark_safe(self, cell):
        """
        Marks a cell as safe, and updates all knowledge
        to mark that cell as safe as well.
        """
        self.safes.add(cell)
        for sentence in self.knowledge:
            sentence.mark_safe(cell)

    def add_knowledge(self, cell, count):
        """
        Called when the Minesweeper board tells us, for a given
        safe cell, how many neighboring cells have mines in them.

        This function should:
            1) mark the cell as a move that has been made
            2) mark the cell as safe
            3) add a new sentence to the AI's knowledge base
               based on the value of `cell` and `count`
            4) mark any additional cells as safe or as mines
               if it can be concluded based on the AI's knowledge base
            5) add any new sentences to the AI's knowledge base
               if they can be inferred from existing knowledge
        """
        self.moves_made.add(cell)
        self.mark_safe(cell)

        neighbors, count = self.get_non_diagonal_neighbors(cell, count)
        if len(neighbors) > 0:
            print(f"Action {cell} has added knowledge: {neighbors}={count}")
            self.knowledge.append(Sentence(neighbors, count))
        self.run_inference()

        # print statistics
        print("Knowledge base length: " + str(len(self.knowledge)))
        print("Known mines: " + str(self.mines))
        print("Safe moves remaining: " + str(self.safes - self.moves_made))
        print("-----------------------------------------")

    def make_safe_move(self):
        """
        Returns a safe cell to choose on the Minesweeper board.
        The move must be known to be safe, and not already a move
        that has been made.

        This function may use the knowledge in self.mines, self.safes
        and self.moves_made, but should not modify any of those values.
        """
        safe_moves = self.safes - self.moves_made
        if not safe_moves:
            return None
        print("Available safe moves: " + str(safe_moves))
        return random.choice(list(safe_moves))

    def make_random_move(self):
        """
        Returns a move to make on the Minesweeper board.
        Should choose randomly among cells that:
            1) have not already been chosen, and
            2) are not known to be mines
        """
        moves = set()
        for i in range(self.height):
            for j in range(self.width):
                moves.add((i, j))
        moves = moves - self.moves_made - self.mines
        if not moves:
            return None
        m = random.choice(list(moves))
        print(f"AI randomly picking {m}")
        return m

    def get_non_diagonal_neighbors(self, cell, count):
        neighbors = set()
        for i in range(cell[0]-1, cell[0]+2):
            for j in range(cell[1]-1, cell[1]+2):
                
                if (i, j) == cell or i < 0 or j < 0 or i >= self.height or j >= self.width:
                    continue
                
                # check if the cell lies in the diagonal
                if (abs(i-cell[0]) == abs(j-cell[1])):
                    continue
                
                # ignore the cell if it is already marked as safe
                if (i, j) in self.safes:
                    continue

                # ignore the cell if it is already marked as mine
                if (i, j) in self.mines:
                    count -= 1  # the remaining neighbors will have one less mine
                    continue

                neighbors.add((i, j))
        return (neighbors, count)

    def run_inference(self):
        updated = True
        while updated:
            updated = False
            known_mines = set()
            known_safes = set()
            for sentence in self.knowledge:
                known_mines = known_mines.union(sentence.known_mines())
                known_safes = known_safes.union(sentence.known_safes())

            for mine in known_mines:
                self.mark_mine(mine)
                updated = True
            for safe in known_safes:
                self.mark_safe(safe)
                updated = True

            # keep only the sentences that have non-zero length
            self.knowledge = [s for s in self.knowledge if len(s.cells) > 0]
            updated = self.infer_new_sentences()

    def infer_new_sentences(self):
        updated=False
        for s1 in self.knowledge:
            for s2 in self.knowledge:
                if s1 == s2:
                    continue
                if s1.cells.issubset(s2.cells):
                    new_cells = s2.cells - s1.cells
                    new_count = s2.count - s1.count
                    new_sentence = Sentence(new_cells, new_count)
                    if new_sentence in self.knowledge or len(new_cells) == 0:
                        continue
                    self.knowledge.append(new_sentence)
                    updated = True
                    print(f"Inferred {new_sentence} using {s2} and {s1}")
        return updated
