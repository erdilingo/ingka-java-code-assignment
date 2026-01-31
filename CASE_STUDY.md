# Case Study Scenarios to discuss

## Scenario 1: Cost Allocation and Tracking
**Situation**: The company needs to track and allocate costs accurately across different Warehouses and Stores. The costs include labor, inventory, transportation, and overhead expenses.

**Task**: Discuss the challenges in accurately tracking and allocating costs in a fulfillment environment. Think about what are important considerations for this, what are previous experiences that you have you could related to this problem and elaborate some questions and considerations

**Questions you may have and considerations:**

- What decisions will this cost data drive and what accuracy level is needed?
- Who owns the allocation rules when costs are shared across locations?

The main challenge is that shared resources (staff, transport) across locations make direct cost attribution difficult. We need to first understand what business decisions depend on this data so we can determine the right level of detail. Too detailed adds complexity without value, too coarse makes the data unreliable for decision-making.

## Scenario 2: Cost Optimization Strategies
**Situation**: The company wants to identify and implement cost optimization strategies for its fulfillment operations. The goal is to reduce overall costs without compromising service quality.

**Task**: Discuss potential cost optimization strategies for fulfillment operations and expected outcomes from that. How would you identify, prioritize and implement these strategies?

**Questions you may have and considerations:**

- Do we have a cost baseline per location to compare against?
- What level of risk to service quality is acceptable during optimization?

I would prioritize by looking at impact vs. effort. Start with low-effort, high-impact changes (e.g. consolidating underutilized warehouses, fixing obvious inefficiencies) to show quick results, then move to larger strategic changes. A clear baseline per location is essential because without it we can't measure whether our optimizations are actually working.

## Scenario 3: Integration with Financial Systems
**Situation**: The Cost Control Tool needs to integrate with existing financial systems to ensure accurate and timely cost data. The integration should support real-time data synchronization and reporting.

**Task**: Discuss the importance of integrating the Cost Control Tool with financial systems. What benefits the company would have from that and how would you ensure seamless integration and data synchronization?

**Questions you may have and considerations:**

- Which financial systems need to be connected (ERP, general ledger, budgeting)?
- Is real-time sync required or is batch (e.g. daily) sufficient?

The main benefit is eliminating manual data transfer between systems, which reduces errors and speeds up financial reporting. An event-driven approach similar to the `StoreEventListener` pattern in this codebase works well here. Operational events trigger financial updates asynchronously, keeping both systems in sync without slowing down operations.

## Scenario 4: Budgeting and Forecasting
**Situation**: The company needs to develop budgeting and forecasting capabilities for its fulfillment operations. The goal is to predict future costs and allocate resources effectively.

**Task**: Discuss the importance of budgeting and forecasting in fulfillment operations and what would you take into account designing a system to support accurate budgeting and forecasting?

**Questions you may have and considerations:**

- What planning horizon matters most (quarterly, annual, multi-year)?
- Who consumes the forecasts and what decisions depend on them?

Good forecasting helps the team catch budget problems early. The system should be built on historical trends but also handle changing conditions like rising costs or demand shifts. I would design it so that key variables (e.g. transport costs, stock volume, capacity) can be updated easily. A model that only works under fixed assumptions will break quickly in a real environment.

## Scenario 5: Cost Control in Warehouse Replacement
**Situation**: The company is planning to replace an existing Warehouse with a new one. The new Warehouse will reuse the Business Unit Code of the old Warehouse. The old Warehouse will be archived, but its cost history must be preserved.

**Task**: Discuss the cost control aspects of replacing a Warehouse. Why is it important to preserve cost history and how this relates to keeping the new Warehouse operation within budget?

**Questions you may have and considerations:**

- Why are we replacing this warehouse? Is it for location, capacity, cost or something else?
- Will both warehouses run at the same time during the transition?

Preserving cost history matters because the `businessUnitCode` stays the same even when the warehouse is replaced. The `archivedAt` field separates old from new. Without the old warehouse's cost data, we have no reference to set a realistic budget for the new one or to check if the replacement was worth it. The old costs are basically our benchmark.

## Instructions for Candidates
Before starting the case study, read the [BRIEFING.md](BRIEFING.md) to quickly understand the domain, entities, business rules, and other relevant details.

**Analyze the Scenarios**: Carefully analyze each scenario and consider the tasks provided. To make informed decisions about the project's scope and ensure valuable outcomes, what key information would you seek to gather before defining the boundaries of the work? Your goal is to bridge technical aspects with business value, bringing a high level discussion; no need to deep dive.
