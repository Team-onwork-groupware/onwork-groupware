# Workflow: Self-Healing Loop

## Trigger

Use this whenever a verification agent (`qa-frontend-agent`, `test-agent`, or
`nfr-performance-agent`) returns an Output Contract with `"status": "fail"`.
The orchestrator runs this loop **automatically** — it does not ask the user for
permission to retry routine failures.

## Goal

Close the failure without human intervention when possible: parse the failure,
re-dispatch the owning implementation agent with a focused fix request, then
re-verify. Surface to the user only when the loop budget is exhausted or a fix
crosses an Auto-Continue Policy boundary.

## Loop Parameters

- `MAX_HEAL = 3` — maximum automatic iterations per failing handoff.
- Each iteration appends a checkpoint row to `memory-bank/progress.md`.
- The benchmark harness reads those checkpoints to count `self_heal_iterations`.

## Steps

1. Parse the failing agent's JSON Output Contract. Collect `failures[]` (and
   `screens[]` / `suites[]` as applicable).
2. Map failure → owning agent:
   - `qa-frontend-agent` failures → `frontend-agent`
   - `test-agent` failures → `implementation-agent`
   - `nfr-performance-agent` budget misses → `api-integration-agent` (caching/
     aggregation) or `implementation-agent` (logic), whichever the diagnosis points to.
3. Re-dispatch the owning agent with ONLY the parsed `failures[]` as scope. Do not
   widen scope beyond the reported failures.
4. Append a checkpoint to `memory-bank/progress.md`:
   `| <ts> | self-heal | iter N/3 | <agent> fixing: <short failure> |`
5. Re-run the verification agent. If `status` is now `pass`, exit the loop.
6. If still `fail` and `N < MAX_HEAL`, increment N and go to step 1.
7. If `N == MAX_HEAL` and still failing, STOP and surface to the user with the
   accumulated failures and what was attempted.

## Boundaries (when to stop and ask the user)

- A fix would add/remove a dependency, change DB schema, change auth, or touch
  external API keys (see `plan.md` Auto-Continue Policy).
- The same failure repeats unchanged across 2 iterations (no progress) — stop early.
- `MAX_HEAL` reached.

## Output

The orchestrator records `self_heal_iterations` in its Completion Output JSON and
leaves the checkpoint trail in `memory-bank/progress.md`.
