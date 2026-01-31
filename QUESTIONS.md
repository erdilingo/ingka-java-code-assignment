# Questions

Here we have 3 questions related to the code base for you to answer. It is not about right or wrong, but more about what's the reasoning behind your decisions.

1. In this code base, we have some different implementation strategies when it comes to database access layer and manipulation. If you would maintain this code base, would you refactor any of those? Why?

**Answer:**
```txt

The codebase has two different database access strategies: Store uses the Active Record pattern (extends PanacheEntity, static methods like Store.findById()), while Product and Warehouse use the Repository pattern (separate PanacheRepository classes). I would refactor Store to use the Repository pattern for consistency. The Repository pattern is better suited here because it separates persistence logic from the entity (single responsibility), makes unit testing straightforward since repositories can be mocked without needing a running database, and aligns all three domains under the same approach.
```
----
2. When it comes to API spec and endpoints handlers, we have an Open API yaml file for the `Warehouse` API from which we generate code, but for the other endpoints - `Product` and `Store` - we just coded directly everything. What would be your thoughts about what are the pros and cons of each approach and what would be your choice?

**Answer:**
```txt
In API first approcah, the contract is finalized upfront using openapi.yaml, which helps align backend, frontend, and external stakeholders early and enables parallel development. It has client generation not only Java and also for different languages like TS or Kotlin for frontend application development. This approach works well for larger teams or public APIs where consistency and governance matter. The downside is higher upfront effort and the risk of dealing with verbose or rigid generated code, especially for simple services. It is also a little bit time consuming for small teams.

In code first approach endpoints are developed directly in code (of course according to feature requirements) but generation of OpenAPI documentation from annotations is done later, which is faster and feels more natural for Java developers. It’s well suited for internal services and smaller teams where requirements evolve quickly. The main drawback is that the API contract emerges later, which can lead to inconsistencies or rework when multiple consumers are involved, making it less suitable for large teams.

My choice depends on project needs and team size. If the requirements are changing frequently and team size is small, then code first approach would be a good fit for fast development and easy iterations. However, if the project and team are large, involve many stakeholders, and have well-defined, stable requirements, I would choose an API-first approach.
```
3. Given the need to balance thorough testing with time and resource constraints, how would you prioritize and implement tests for this project? Which types of tests would you focus on, and how would you ensure test coverage remains effective over time?

**Answer:**
```txt
If time and resources are limited, I would prioritize tests based on risk and cost of maintenance. Unit tests come first because they are fast, cheap to write, and catch most logic bugs early, especially in the use case layer. Next, I would add a small number of integration tests to cover critical paths such as database access or external system boundaries. End-to-end tests have the lowest priority because they are slow, fragile, and expensive to maintain, so I keep only a few to validate main user flows.

To keep test coverage meaningful over time, I focus on testing behavior, not implementation details, so refactoring does not break tests unnecessarily. I also make tests part of the CI pipeline, ensuring that new code cannot be merged without tests. I would also regularly review tests to ensure that critical business rules and edge cases are covered, and I remove or update tests that no longer provide value.
```