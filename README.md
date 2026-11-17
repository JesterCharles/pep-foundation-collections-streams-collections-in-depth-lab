# Collections lab: an order-routing registry

You will finish the order registry of a small warehouse. It stores orders by key, queues them at packing stations, dispatches them (and undoes a wrong dispatch), and prints two reports for the floor manager. Each of the six tasks uses one collection idea from this module.

The full learner instructions are in [`.evolv/instructions.md`](.evolv/instructions.md), which the lab platform shows as the lab's instructions.

## Layout

This repository follows the Exa coding-lab scaffold for Java.

| Path | What it is |
|---|---|
| `src/main/java/exercise/Order.java` | Lab source |
| `src/main/java/exercise/OrderKey.java` | Lab source |
| `src/main/java/exercise/RoutingRegistry.java` | Lab source |
| `src/test/java/exercise/VisibleTests.java` | Visible tests |
| `.evolv/instructions.md` | Learner instructions |
| `.evolv/grade.yml` | Grading manifest, used by Run Tests and by the grader |
| `pom.xml` | Maven build, identical to the scaffold's: Java 17 and JUnit 5.11.3 |
| `Dockerfile` | Image for local Run Tests; warms the Maven cache |
| `expected/test-summary.txt` | The result of a finished lab |

There are no held-out tests in this lab: every test is visible.

## Run the tests

On the platform, select **Run Tests**. From a terminal with JDK 17 or later and Apache Maven 3.9:

```
mvn -q test
```

All 6 tests fail until the `TODO`s are done. That is expected.
