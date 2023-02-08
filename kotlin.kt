class Node(val state: Any, val parent: Node?, val children: MutableList<Node> = mutableListOf()) {
    var wins = 0
    var visits = 0
    var untriedMoves: List<Any> = listOf()
}

fun mcts(initialState: Any, timeLimit: Long, iterations: Int, evaluate: (Any) -> Double) {
    val root = Node(initialState, null)
    root.untriedMoves = getUntriedMoves(root.state)
    val start = System.currentTimeMillis()
    var i = 0
    while (i < iterations && System.currentTimeMillis() - start < timeLimit) {
        val node = select(root)
        val winner = playout(node)
        backpropagate(node, winner)
        i++
    }
    println("Selected move: " + bestChild(root, 0.0).state)
}

fun select(node: Node): Node {
    var current = node
    while (!current.untriedMoves.isEmpty() && current.children.isNotEmpty()) {
        current = bestChild(current, 1.4)
    }
    if (current.untriedMoves.isNotEmpty()) {
        val move = randomMove(current.untriedMoves)
        val newState = doMove(current.state, move)
        current = Node(newState, current)
        current.untriedMoves = getUntriedMoves(current.state)
    }
    return current
}

fun randomMove(moves: List<Any>): Any {
    val index = (Math.random() * moves.size).toInt()
    return moves[index]
}

fun playout(node: Node): Double {
    var current = node
    while (current.untriedMoves.isNotEmpty()) {
        val move = randomMove(current.untriedMoves)
        val newState = doMove(current.state, move)
        current = Node(newState, current)
        current.untriedMoves = getUntriedMoves(current.state)
    }
    return evaluate(current.state)
}

fun backpropagate(node: Node, winner: Double) {
    var current = node
    while (current != null) {
        current.visits++
        current.wins += winner
        current = current.parent
    }
}

fun bestChild(node: Node, c: Double): Node {
    var best = node.children[0]
    for (child in node.children) {
        val score = (child.wins / child.visits) + c * Math.sqrt(2 * Math.log(node.visits) / child.visits)
        if (score > (best.wins / best.visits) + c * Math.sqrt(2 * Math.log(node.visits) / best.visits)) {
            best = child
        }
    }
    return best
}

fun getUntriedMoves(state: Any): List<Any> {
    // implementation left to user
    return listOf()
