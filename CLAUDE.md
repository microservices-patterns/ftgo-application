# Project Guidelines

## Executing bash commands

IMPORTANT: Use simple commands that you have permission to execute. Avoid complex commands that may fail due to permission issues.

When copying or moving files:
- Avoid compound commands with `&&` - run commands separately
- Avoid wildcard patterns (`*.java`) - copy files individually
- Single-file operations are more reliable with Bash permission system

## Skills

Always invoke the relevant skill before performing these actions:

- **Before creating git commits**: Use the `idea-to-code:commit-guidelines` skill
- **When practicing TDD**: Use the `idea-to-code:tdd` skill
- **When working from a plan file**: Use the `idea-to-code:plan-tracking` skill
- **When creating Dockerfiles**: Use the `idea-to-code:dockerfile-guidelines` skill
- **When moving/renaming files**: Use the `idea-to-code:file-organization` skill
- **When writing multiple similar files**: Use the `idea-to-code:incremental-development` skill

## Code Style

- Prefer intention-revealing method names over comments. If you find yourself writing a comment to explain what code does, extract it into a method whose name conveys the intent.

## Tool Selection

Before running any Bash command, ask: "Is there a specialized tool for this?"

- File search → Glob (NOT find or ls)
- Content search → Grep (NOT grep or rg)
- Read files → Read (NOT cat/head/tail)

The specialized tools are faster, have correct permissions, and provide better output formatting.

## Git Commands

Always run git commands from the project root directory. If you need to operate on the repository, cd to the root directory first rather than using `git -C`. This prevents accidentally committing files outside the project root.

## Pattern-Based Fixes

When fixing issues caused by naming conventions or patterns:
1. Search the entire codebase for similar occurrences before making any changes
2. Fix ALL instances in a single commit
3. Never commit partial fixes for pattern-based problems

## Migration Complete Criteria

When migrating a service to a multi-module structure, verify the following before marking migration complete:

1. **TEST*.xml files exist**: Check that up-to-date TEST*.xml files exist for all test types (unit, integration, contract). This confirms tests actually executed rather than being silently skipped.
   ```bash
   find <service>/*/build/test-results -name "TEST-*.xml" -newer <service>/build.gradle
   ```

2. **No tests deleted**: Compare test files before and after migration to ensure no tests were accidentally deleted:
   ```bash
   # List tests before migration
   git ls-tree -r <commit-before-migration>^ -- <service>/src/test <service>/src/integrationTest <service>/src/contractTest

   # Compare with current test locations in submodules
   ```
   All original tests should exist in their new locations (possibly renamed/modernized to JUnit 5).

3. **Service added to build-and-test-all.sh**: The migrated service must be added to the `MIGRATED_SERVICES` array in `build-and-test-all.sh` and the script must complete successfully:
   ```bash
   ./build-and-test-all.sh
   ```

4. **Contracts published**: If the service has contracts (in a corresponding `*-contracts` project), verify they are published to the local repository:
   - The contract project should be in the `CONTRACT_PROJECTS` array in `build-and-test-all.sh`
   - Run `./gradlew publishStubsPublicationToLocalRepository` in the contract project
   - Verify stubs JAR exists in `../build/repo/`

## Test Location in Multi-Module Projects

Tests should live in the module containing the code they test:

| Test Type | Location | Example |
|-----------|----------|---------|
| Domain unit tests | `*-domain/src/test/` | `ConsumerTest.java` |
| JPA/Repository tests | `*-persistence/src/integrationTest/` or `*-domain/src/integrationTest/` | `ConsumerJpaTest.java` |
| Command handler tests | `*-command-handlers/src/test/` | `ConsumerCommandHandlersTest.java` |
| Event handler tests | `*-event-handling/src/test/` | `KitchenServiceEventHandlerTest.java` |
| REST controller tests | `*-restapi/src/test/` | `ConsumerControllerTest.java` |
| Full service integration tests | `*-main/src/integrationTest/` | `ConsumerServiceIntegrationTest.java` |
| Component tests (out-of-process) | `*-main/src/componentTest/` | `ConsumerServiceComponentTest.java` |
| Provider contract tests | Module that publishes events/responses | `MessagingBase.java` |
| Consumer contract tests | Module that consumes events/requests | In `*-event-handling/` |

**Principle**: A test should be in the same module as the code it exercises.

<!-- claude-config-files-sha: f8e6469fd91735ffcae2dc46f979cfb0677ec5b6 -->
