# Collections lab: an order-routing registry

You will finish the order registry of a small warehouse. It stores orders by key, queues them at packing stations, dispatches them (and undoes a wrong dispatch), and prints two reports for the floor manager. Each of the six tasks uses one collection idea from this module.

**Time:** about 30 minutes, in two sessions: Tasks 1-4 first, then Tasks 5-6. If you pass 30 minutes, stop and finish later; your edits stay in the files.

```
Open project -> Run starter tests -> Edit TODOs (Tasks 1-6) -> Rerun tests -> Read feedback -> Stop when tests pass
```

In words: open the project, run the tests once, then for each task edit its `TODO`, rerun the tests and read the feedback. Stop when every test passes.

## Quick start

1. **Open the terminal.** In the provided VS Code project, choose **Terminal > New Terminal**.
2. **Go to the `starter` folder.** Every command in this lab runs there. (If the terminal prompt already ends in `starter`, skip this step.)

   ```
   cd starter
   ```

3. **Download the build's dependencies (once).** It prints nothing when it works:

   ```
   mvn -q dependency:go-offline
   ```

4. **Run the starter tests:**

   ```
   mvn -q -o test
   ```

   **All 6 tests fail at first. That is expected:** no task's `TODO` is done yet. Maven reports `Tests run: 6, Failures: 6`, and each failure line starts with its task number.

## Your tasks

Edit only these two files in `starter/src/main/java/routing/`. Each task has a `TODO Task N` comment there.

- [ ] **Task 1** `OrderKey.java`: equal keys do not find the same order. Find out why and fix the class.
- [ ] **Task 2** `RoutingRegistry.java`, `dropDiscontinued`: removing orders while looping crashes. Remove them safely.
- [ ] **Task 3** `RoutingRegistry.java`, `queue` and `dispatchNext` (one `TODO` in each): give each station a first-in, first-out queue.
- [ ] **Task 4** `RoutingRegistry.java`, `undoLastDispatch`: undo the newest dispatch (last in, first out). Needs Task 3.
- [ ] **Task 5** `RoutingRegistry.java`, `heldOrders`: replace the slow nested loop with a hash lookup.
- [ ] **Task 6** `RoutingRegistry.java`, `unitsByStation`: list the stations in sorted order.

**After each task**, run only that task's test. Replace `1` with the task number:

```
mvn -q -o test -Dtest="RoutingRegistryTest#task1*"
```

It prints nothing when that task passes. If it fails, read the failure line: it names the task and what is still wrong.

**You are done when the full test command prints nothing:**

```
mvn -q -o test
```

Silence means all 6 tests pass. Leave the other files as they are: `Order.java`, `tests/`, `starter/pom.xml` and `expected/`.

## Hints

Read a hint only when the failure message is not enough.

**Task 1.** `find("web", 1002)` returns `null` for an order you just registered, although `equals` is correct. Before a `HashMap` ever calls `equals`, which other method does it call on the key? What does `OrderKey` inherit for that method from `Object`? Add the missing override, built from exactly the fields `equals` compares (`java.util.Objects` has a helper for several fields). A `record` would have generated it for you.

**Task 2.** A for-each loop over the map's keys removes entries, and its hidden iterator throws `ConcurrentModificationException`. Remove through the collection itself: `removeIf` on one of the map's views (its values are the orders), or an explicit `Iterator` and its own `remove` method. Keep returning how many orders were removed.

**Task 3.** In `queue`, create a station's `ArrayDeque` only the first time that station appears (`computeIfAbsent` does both), then add the order at the back. In `dispatchNext`, the station may have no queue at all (`null`), or an empty one. Take the oldest order with the `Deque` method that returns `null` instead of throwing, and push a real order onto `dispatched` before you return it.

**Task 4.** `dispatched` is a stack, with the newest dispatch on top. With nothing dispatched, return `null`. Otherwise take the newest order off and put it back at the **head** of its station's queue, the end the queue takes from. On a deque, the stack's adding method works at the head.

**Task 5.** The method compares every order with every held sku: about n × m steps. Read the held list once into a `HashSet`, then check each order's sku against the set. Fill in the two Big-O labels in the method comment yourself; the tests do not read them. The test fails if your method reads the list it is given more than once.

**Task 6.** The totals are right, but a `HashMap` keeps no order. Switch to the `Map` that keeps its keys sorted, and replace the `getOrDefault`-and-`put` line with one `merge` call. Keys sort as text, so `B12` comes before `B2`.

## Reference

**Requirements.** JDK 25 and Apache Maven 3.9 or later. To check, run `java -version` (must say 25) and `mvn -v` (Maven 3.9 or later, and its `Java version:` line must say 25). No database, container or cloud service is needed.

**Why `-o`?** The dependency step downloads the build's plugins and JUnit into your local Maven repository. After that, `-o` (offline) runs the tests and the demo without the network.

**See the registry run (optional).** From `starter`:

```
mvn -q -o compile exec:java
```

It prints six lines. When all tasks are done they match `expected/test-summary.txt`. On the starter, some lines already look right (Held and Dropped); trust the tests, not the demo.

**What the tests check.** They run your code with several inputs and edge cases, and check that `heldOrders` reads its input list only once. They do not check your Big-O labels or whether you used `merge`; check those yourself.

**Start over.** Undo your edits in `OrderKey.java` and `RoutingRegistry.java`. Compiled files go to `starter/target`, which you can delete at any time.

## Troubleshooting

| Problem | What to do |
|---|---|
| `there is no POM in this directory` | The terminal is not in `starter`. Run `cd starter`, then the command again. |
| `mvn: command not found` or `'mvn' is not recognized` | Maven is not available in this terminal. Ask your instructor, or see **Requirements**. |
| `mvn -v` shows a Java version other than 25, or `release version 25 not supported` | The build needs JDK 25. Ask your instructor, or point `JAVA_HOME` at a JDK 25 and open a new terminal. |
| `Cannot access central in offline mode`, or a missing artifact | The dependency step has not finished. Run `mvn -q dependency:go-offline` once with internet access. |
| `COMPILATION ERROR` | Maven names the file and `[line,column]`. Fix that line and run the tests again. |
