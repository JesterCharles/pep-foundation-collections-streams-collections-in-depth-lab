# Collections in Depth lab: an order-routing registry

## Goal

Finish the order-routing registry of a small warehouse in six linked tasks.
Orders arrive from the web shop and the phone line; the registry stores them by key, queues them at their packing stations, dispatches them (and undoes a wrong dispatch), and answers the floor manager's reports: which orders are on hold, and how many units each station packs.
Each task uses one collection idea from this module, and together they make the registry work end to end.
You build and test it with the real JDK and Maven on your own computer; nothing runs in the cloud.

**Time limit: 30 minutes**, split as about 3 minutes of setup and about 22 minutes of tasks (both estimates), plus about 5 minutes of buffer.
The lab runs in two sessions: part 1 is setup and Tasks 1-4 (about 22 minutes, right after the HashMap page), and part 2 is Tasks 5-6 (about 8 minutes, at the start of the next session).
If you pass 30 minutes, pause and finish in self-study. You can stop after any task and continue later: your edits stay in the files.

## What you edit

Edit only these two files. Each task has a `TODO` comment (Task 3 has one in each of its two methods), and every test failure line starts with the task number.

| File | Tasks | What you do there |
|---|---|---|
| `starter/src/main/java/routing/OrderKey.java` | 1 | Find out why equal keys do not find the same order, and fix the class |
| `starter/src/main/java/routing/RoutingRegistry.java` | 2-6 | Fix or finish one method per task (two methods in Task 3) |

Leave everything else alone: `starter/src/main/java/routing/Order.java` (the order record), `tests/` (the public tests), `starter/pom.xml` (the prewired build) and `expected/`.

The one test command, run from the `starter` folder, is `mvn -q -o test`.
Each failing check names the task and the next thing to fix.

## Prerequisites

You need JDK 25 and Apache Maven 3.9 or later, the same setup as the earlier Java labs.
Check both in a new terminal:

```
java -version
mvn -v
```

- `java -version` must report version 25.
- `mvn -v` must report Apache Maven 3.9 or later, and its `Java version:` line must also say 25.

This lab works the same on macOS, Linux and Windows (PowerShell or Command Prompt).
No Docker, database, cloud account or other service is needed.

## Setup (once, needs the internet)

Open a terminal in the lab's `starter` folder and run:

```
mvn -q dependency:go-offline
```

It prints nothing when it succeeds. After this step, everything else in the lab runs offline.

## Tasks

Open the `starter` folder (the one that holds `pom.xml`) as your project in VS Code or another Java IDE, so the IDE's own files stay inside it, where `starter/.gitignore` keeps them out of Git. The paths in the table above start at the lab folder, so inside the IDE they begin at `src/`. The public tests stay in the lab's `tests` folder next to `starter`; Maven builds them from there, and you can read them with your file browser or the IDE's **File > Open**.
Run every command in a terminal whose current folder is `starter`: a new terminal in the IDE (in VS Code, **Terminal > New Terminal**) already opens there. The Maven commands are the real build and test; any run or test buttons your IDE offers are optional, and they must use `starter` as their working directory.
After each task, run `mvn -q -o test` from the `starter` folder and check that no line for that task number is left.

### Task 1: equal keys that do not find each other (`OrderKey.java`)

1. Run the tests and read the `Task 1` lines: `find("web", 1002)` returns `null` for an order registered a moment ago, and a `HashSet` keeps two keys that `equals` calls the same.
2. `equals` is correct. Think about how a `HashMap` looks a key up: which method does it call on the key before it ever calls `equals`, and what does `OrderKey` inherit for that method from `Object`?
3. Add the missing override. It must give equal keys equal results, so build it from exactly the fields `equals` compares (`java.util.Objects` has a helper for combining several fields).
4. Done when no `Task 1` line appears. A `record` would have generated this method for you.

### Task 2: remove while iterating (`dropDiscontinued`)

1. The method removes map entries inside a for-each loop over the map's keys. The loop's hidden iterator notices and throws `ConcurrentModificationException`.
2. Remove through the collection instead of around it: either `removeIf` on one of the map's views (its values are the orders), or an explicit `Iterator` whose own `remove` method you call.
3. Keep the count of removed orders. Done when no `Task 2` line appears.

### Task 3: station queues, first in, first out (`queue` and `dispatchNext`)

1. In `queue`, get the station's queue from `stationQueues`, creating a new `ArrayDeque` only the first time that station appears (`computeIfAbsent` does both), and add the order at the back.
2. In `dispatchNext`, the station may never have had a queue, so the map lookup can be `null`; return `null` then.
3. Take the oldest order with the `Deque` method that returns `null` on an empty queue (not one of the methods that throw). If you got an order, push it onto `dispatched` before returning it.
4. Done when no `Task 3` line appears.

### Task 4: undo the last dispatch, last in, first out (`undoLastDispatch`)

1. `dispatched` is a stack: the newest dispatch is on top. With nothing dispatched, return `null` without throwing.
2. Otherwise take the newest order off the stack and put it back at the **head** of its station's queue, the end the queue takes from, so that order is dispatched next again. On a deque, the stack's adding method works at the head.
3. The checks dispatch first, so they also need Task 3. Done when no `Task 4` line appears.

### Task 5: from nested loops to a hash lookup (`heldOrders`)

1. The method is correct but slow: for each of n orders it walks all m held skus, about n × m comparisons.
2. Read the held list once into a `HashSet`, then test each order's sku against the set instead of looping over the list.
3. Fill in the two Big-O labels in the method comment (n orders, m held skus). The tests do not read them; compare with the Big-O page.
4. The test counts how often your method reads the list it is given, and fails above one read. It cannot see what you do with a copy, so it cannot tell a `HashSet` from another list copy: that part is up to you.
5. Done when no `Task 5` line appears.

### Task 6: a sorted floor report (`unitsByStation`)

1. The totals are right, but a `HashMap` makes no ordering guarantee, so stations print in a jumbled order.
2. Switch to the `Map` implementation that keeps its keys sorted.
3. Replace the `getOrDefault`-and-`put` line with one call of the map's `merge` method that adds the quantity to the station's total. The tests check the totals and the order, not which call you used.
4. Keys sort as strings, so `B12` comes before `B2`. Done when the whole test run prints nothing.

## What the tests check

Every test compiles and runs your Java code on the real JDK; there is no simulated or static check in this lab.
A passing run proves the results for the listed inputs and edge cases, and that `heldOrders` reads its input list once. It does not prove style: the Big-O labels in Task 5 and the `merge` call in Task 6 are self-checks for you.

## Test

From the `starter` folder:

```
mvn -q -o test
```

- Before you start, all six tests fail, and Maven reports `Tests run: 6, Failures: 6`.
- If your code does not compile, Maven prints `COMPILATION ERROR` with the file and `[line,column]`. Fix that line and run the test again.

## Run the registry

Run the demo in `RoutingRegistry.main` from the `starter` folder at any time:

```
mvn -q -o compile exec:java
```

It prints six lines, from `Registered 5 orders` to `Dropped discontinued`. On the starter, only the lines for Tasks 1, 3, 4 and 6 differ: they show `not found`, `nothing` and `[]`, and the station order is jumbled. The Held and Dropped lines already look right: Task 5 is correct but slow, and the demo's for-each removal happens to remove the last entry, which does not throw. The Task 2 test removes the first entry, so it is the real check; Task 5's test checks how often the list is read. When every task is done, all six lines match `expected/test-summary.txt`.

## Expected output

When every task is complete, `mvn -q -o test` prints nothing and exits with status 0.
`expected/test-summary.txt` shows the full result, including the six lines the demo prints.

## Offline note

Only the setup step uses the network. The test and run commands use `-o` (offline), so they never download anything.
If one reports that an artifact is missing, run the setup step once more while online.

## Reset and cleanup

The lab writes only the `starter/target` folder (compiled classes and test reports), which `starter/.gitignore` keeps out of Git. Delete it from the `starter` folder:

- macOS and Linux: `rm -rf target`
- Windows PowerShell: `Remove-Item -Recurse -Force target`
- Windows Command Prompt: `rmdir /s /q target`

To start the lab over, pull a fresh copy of the lab from the platform, or undo your edits in the two files you changed (in a Git clone, `git restore` followed by the file's path).
The downloads stay in your local Maven repository (`~/.m2/repository` by default) for later Java labs.
Anything Git lists outside `starter`, such as `.DS_Store` or `Thumbs.db`, is not part of the lab and can be deleted.

## Troubleshooting

| Symptom | What to do |
|---|---|
| `mvn: command not found`, or `'mvn' is not recognized` | Maven's `bin` folder is not on your `PATH`. Add it, then open a new terminal. |
| `mvn -v` shows a Java version other than 25, or `release version 25 not supported` | Set `JAVA_HOME` to your JDK 25 folder (or unset it), then open a new terminal. |
| `Cannot access central in offline mode`, or a missing artifact | Setup has not completed. Run `mvn -q dependency:go-offline` once while online. |
| `ConcurrentModificationException` in Task 2 | Something still removes from `orders` inside a for-each loop. Remove through `removeIf` or the iterator itself. |
| `NoSuchElementException` in Task 3 or 4 | A method that throws on an empty deque was called. Use the one that returns `null`, or check for empty first. |
| `NullPointerException` in Task 3 or 4 | A station's queue lookup returned `null`. Check for a missing queue before using it. |
| You have passed 30 minutes | Pause here and finish in self-study; your edits stay in the files, and each failure line still names the next task. |
