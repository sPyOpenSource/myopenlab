# Focused Unit-Test Suite for MyOpenLab — Design

Date: 2026-08-06
Status: Approved by user

## Goal

Introduce the first unit tests to MyOpenLab. The project currently has zero tests
(`test/` is empty). This effort wires up the existing JUnit test infrastructure and
writes a focused core of 7 test classes covering the just-committed security fixes and
a handful of pure-logic utilities.

## Approach

Direct unit tests against public APIs. No production code changes, no refactoring.
Where an API is slightly awkward (e.g. `MyParser.Expression` writes into a static
`liste`), the test handles it rather than changing production code.

## Scope

In scope:

- Test classpath wiring in `nbproject/project.properties` (only non-test file touched).
- Seven JUnit 4 test classes under `test/` (see table below).

Out of scope (explicitly):

- Refactoring or modifying any production source under `src/`.
- Testing Swing/GUI code (`FrameMain`, panels, dialogs).
- Coverage tooling (JaCoCo) and CI — the project has no CI today.
- Broad coverage of all testable classes (parsers beyond Expression, SimpleFileSystem,
  filters, CodeEditor scanner, etc.). These are follow-up candidates.

## Build integration

- Add `file.reference.junit.jar=distribution/lib/junit.jar` to
  `nbproject/project.properties` and append `${file.reference.junit.jar}` to
  `javac.test.classpath`.
- The NetBeans Ant scaffolding in `nbproject/build-impl.xml` already defines the
  `test`, `test-single`, and `test-report` targets; they activate once
  `org.junit.Test` is present on the test classpath.
- No changes to `build.xml`.

## Test classes

| Test class | Production class under test | Cases |
|---|---|---|
| `test/VisualLogic/CredentialCryptoTest` | `src/VisualLogic/CredentialCrypto.java` | encrypt→decrypt roundtrip; empty/null → `""`; non-`enc:v1:` value passes through `decrypt` unchanged; tampered payload passes through unchanged; random IV — two encryptions of the same input differ |
| `test/VisualLogic/SafeXmlTest` | `src/VisualLogic/SafeXml.java` | XML with DOCTYPE/external entity rejected at parse time; well-formed XML parses successfully |
| `test/VisualLogic/XMLSerializerTest` | `src/VisualLogic/XMLSerializer.java` | `Settings` write→read roundtrip; `java.awt.Point`, `String`, `java.util.ArrayList` payloads allowed; XML declaring `class="java.lang.Runtime"` → `SecurityException`; XML with DOCTYPE blocked |
| `test/de/myopenlab/update/UnzipFilesTest` | `src/de/myopenlab/update/UnzipFiles.java` | Normal zip (multiple files + subdirectory) extracts to temp dir with correct content; zip containing `../evil.txt` → `IOException`, nothing written outside destination |
| `test/de/myopenlab/update/IniFileTest` | `src/de/myopenlab/update/IniFile.java` | Multi-section parse; `getString/getInt/getFloat/getDouble` with present values; missing section/key returns default |
| `test/de/myopenlab/update/Tools2Test` | `src/de/myopenlab/update/Tools2.java` | `deleteFolder` removes nested directory tree; `copy` transfers bytes, including content spanning multiple buffers |
| `test/MyParser/ExpressionTest` | `src/MyParser/Expression.java` | `1+2` → `[PUSHI 1, PUSHI 2, ADD]`; assignment `a=5`; trig `SIN(1)`; relational `1<2`; malformed `1+` → `yyException` |

## Conventions

- JUnit 4 only (`org.junit.Test`, `org.junit.Assert.*`). The jar already ships at
  `distribution/lib/junit.jar`.
- GPL header comment on each test file, matching repo style.
- Java 11, UTF-8 source encoding.
- JUnit 4 `TemporaryFolder` rule for temp files/directories.
- `Expression.liste` (public static) cleared in `@Before` so tests are independent.
- Tests must not require a display/headless mode — all target classes are UI-free.

## Verification

- `ant test` compiles the full project and runs all tests; all pass.
- `ant test-report` generates an HTML report (spot-checked, not required to pass CI).
- Sanity: no changes under `src/`; `git status` shows only `test/`,
  `nbproject/project.properties`, and this spec.

## Risks / notes

- `Expression` writes diagnostics to stdout/stderr (e.g. `EOL `, parse errors); this is
  expected noise in test output, not a failure.
- `Expression.yyparse` throws `Expression.yyException` for malformed input; tests assert
  on that.
- `XMLSerializer.read` validates via `SafeXml` (DOCTYPE blocking) before the class
  allowlist; the two security layers are therefore exercised together.
