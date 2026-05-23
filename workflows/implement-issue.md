# Workflow: Implement Issue

## Trigger

Use this when an issue, ticket, or task description should be turned into code.

## Steps

1. Read the issue description fully.
2. Read `AGENTS.md`, `plan.md`, and `memory-bank/activeContext.md`.
3. Research related files and similar implementations.
4. Update `plan.md` with target files, phases, tests, docs impact, and risks.
5. Implement only the planned change.
6. Add or update tests for changed behavior.
7. Update docs if behavior changed.
8. Run the verification commands.
9. Review `git diff` and `git status`.
10. Update `memory-bank/progress.md`.

## Failure Handling

If tests fail:

- Do not delete failing tests.
- Record the failure in `plan.md` and `memory-bank/progress.md`.
- Make the smallest code fix that addresses the cause.
- Re-run verification.
