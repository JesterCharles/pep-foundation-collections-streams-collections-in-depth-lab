# Collections lab: an order-routing registry

You will finish the order registry of a small warehouse. It stores orders by key, queues them at packing stations, dispatches them (and undoes a wrong dispatch), and prints two reports for the floor manager. Each of the six tasks uses one collection idea from this module.

**Time:** about 30 minutes, in two sessions: Tasks 1-4 first, then Tasks 5-6. If you pass 30 minutes, stop and finish later; your edits stay in the files.

```
Open project -> Run starter tests -> Edit TODOs (Tasks 1-6) -> Rerun tests -> Read feedback -> Submit when tests pass
```

In words: open the project, run the tests once, then for each task edit its `TODO`, rerun the tests and read the feedback. Submit when every test passes.

## Quick start

1. **Open the lab project.** The files you edit are in `src/main/java/routing/`.
2. **Run the starter tests.** Select **Run Tests**.

   **All 6 tests fail at first. That is expected:** no task's `TODO` is done yet. Each failure message starts with its task number.

## Your tasks

Edit only these two files in `src/main/java/routing/`. Each task has a `TODO Task N` comment there.

- [ ] **Task 1** `OrderKey.java`: equal keys do not find the same order. Find out why and fix the class.
- [ ] **Task 2** `RoutingRegistry.java`, `dropDiscontinued`: removing orders while looping crashes. Remove them safely.
- [ ] **Task 3** `RoutingRegistry.java`, `queue` and `dispatchNext` (one `TODO` in each): give each station a first-in, first-out queue.
- [ ] **Task 4** `RoutingRegistry.java`, `undoLastDispatch`: undo the newest dispatch (last in, first out). Needs Task 3.
- [ ] **Task 5** `RoutingRegistry.java`, `heldOrders`: replace the slow nested loop with a hash lookup.
- [ ] **Task 6** `RoutingRegistry.java`, `unitsByStation`: list the stations in sorted order.

**After each task**, select **Run Tests** and read the result for that task. A failure message names the task and what is still wrong.

To run only one task's test from a terminal instead, replace `1` with the task number:

```
mvn -q test -Dtest="RoutingRegistryTest#task1*"
```

It prints nothing when that task passes.

**You are done when all 6 tests pass.** Then select **Submit**.

Leave the other files as they are: `Order.java`, `pom.xml` and `expected/`. The tests in `src/test/java/routing/` are restored to their original state before your work is graded, so changes to them do not count.

## Hints

Read a hint only when the failure message is not enough.

**Task 1**

<details>
<summary>Hint</summary>

`find("web", 1002)` returns `null` for an order you just registered, although `equals` is correct. Before a `HashMap` ever calls `equals`, which other method does it call on the key? What does `OrderKey` inherit for that method from `Object`? Add the missing override, built from exactly the fields `equals` compares (`java.util.Objects` has a helper for several fields). A `record` would have generated it for you.

</details>

**Task 2**

<details>
<summary>Hint</summary>

A for-each loop over the map's keys removes entries, and its hidden iterator throws `ConcurrentModificationException`. Remove through the collection itself: `removeIf` on one of the map's views (its values are the orders), or an explicit `Iterator` and its own `remove` method. Keep returning how many orders were removed.

</details>

**Task 3**

<details>
<summary>Hint</summary>

In `queue`, create a station's `ArrayDeque` only the first time that station appears (`computeIfAbsent` does both), then add the order at the back. In `dispatchNext`, the station may have no queue at all (`null`), or an empty one. Take the oldest order with the `Deque` method that returns `null` instead of throwing, and push a real order onto `dispatched` before you return it.

</details>

**Task 4**

<details>
<summary>Hint</summary>

`dispatched` is a stack, with the newest dispatch on top. With nothing dispatched, return `null`. Otherwise take the newest order off and put it back at the **head** of its station's queue, the end the queue takes from. On a deque, the stack's adding method works at the head.

</details>

**Task 5**

<details>
<summary>Hint</summary>

The method compares every order with every held sku: about n × m steps. Read the held list once into a `HashSet`, then check each order's sku against the set. Fill in the two Big-O labels in the method comment yourself; the tests do not read them. The test fails if your method reads the list it is given more than once.

</details>

**Task 6**

<details>
<summary>Hint</summary>

The totals are right, but a `HashMap` keeps no order. Switch to the `Map` that keeps its keys sorted, and replace the `getOrDefault`-and-`put` line with one `merge` call. Keys sort as text, so `B12` comes before `B2`.

</details>

## Reference

**See the registry run (optional).** In a terminal, from the project folder:

```
mvn -q compile
java -cp target/classes routing.RoutingRegistry
```

It prints six lines. When all tasks are done they match `expected/test-summary.txt`. On the starter, some lines already look right (Held and Dropped); trust the tests, not the demo.

**What the tests check.** They run your code with several inputs and edge cases, and check that `heldOrders` reads its input list only once. They do not check your Big-O labels or whether you used `merge`; check those yourself.

**Start over.** Undo your edits in `OrderKey.java` and `RoutingRegistry.java`. Compiled files go to `target`, which you can delete at any time.

## Troubleshooting

| Problem | What to do |
|---|---|
| `COMPILATION ERROR` | Maven names the file and `[line,column]`. Fix that line and run the tests again. |
