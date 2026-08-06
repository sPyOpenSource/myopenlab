# MyOpenLab Security Audit — 2026-08-06

Scope: `src/`, `distribution/Elements/` (shipped circuit elements), `nbproject/`, bundled JARs in `dist/lib`. Full check: dependency/CVE scan + code-level audit.

Summary: **5 HIGH, 9 MEDIUM, 11 LOW** findings. No hardcoded secrets found. The highest-risk class of bugs is network-facing: unauthenticated listeners that deserialize or download-and-execute untrusted data. Two of the five HIGH findings are in shipped *elements* (SocketServer/SocketClient), which users can place in a circuit.

---

## 1. Dependency & CVE scan

### Shipped runtime dependencies (effective)
| Dependency | Version | CVE status |
|---|---|---|
| org.json (`json-20200518.jar`) | 20200518 | **CVE-2022-45688** — stack-overflow DoS on deeply nested JSON (fixed in 20230227). LOW |
| jssc (`jssc-2.9.6.jar`) | 2.9.6 | No notable CVEs |
| SteelSeries gauges | 3.9.13 | No CVEs |
| jakarta.xml.bind-api | 2.3.3 | Clean |
| slf4j-simple | 1.7.36 | Clean (not log4j — no Log4Shell exposure) |
| assets.jar (bundles zip4j 2.x, trident, redhorizon) | 2.x | zip4j 2.x clean |
| Embedded element JARs (RXTXcomm, jssc-2.8.0, CElement, jay, etc.) | — | No notable CVEs |

### Legacy shared library folder — NOT shipped, but risky
`/Users/xuyi/Source/Java/lib` contains many old versions: commons-collections **3.2.2** (CVE-2015-7501 gadget), log4j **1.2.17** (CVE-2019-17571, CVE-2021-4104, CVE-2022-23305), xalan **2.7.2** (CVE-2022-34169), batik **1.7** (multiple), httpclient **4.5.2** (CVE-2020-13956), commons-io **2.7** (CVE-2021-29425), commons-beanutils **1.9.3** (CVE-2019-10086), guava **14.0.1**, jython **2.7.1**, bsh **2.0b5**. These are NOT on the current build classpath (`nbproject/project.properties` javac.classpath references only 6 JARs + redhorizon) and are not in `dist/lib`. **Risk:** they are one `project.properties` edit away from being reintroduced, and several are deserialization/exec gadgets that amplify the HIGH findings below (e.g., commons-collections + the network `readObject` calls = weaponized RCE). Recommend purging or upgrading this folder.

---

## 2. Code-level audit findings

### HIGH

**H1 — Network Java deserialization → RCE (shipped element, unauthenticated, all interfaces)**
`distribution/Elements/CircuitElements/Sockets/SocketServer/SocketServer.java:64,100-117`
`new ServerSocket(4444)` binds all interfaces, accepts any connection, and calls `serverIn.readObject()` on the raw socket stream. With a gadget library on the classpath (commons-collections etc.), a remote attacker gets code execution on the machine running the circuit. The matching client element (`SocketClient.java:563`) is the reverse boundary — it calls `readObject()` against a user-configured peer, so a malicious server can compromise the client.

**H2 — Zip-slip during package install (network-fetched archives → arbitrary file write)**
`src/de/myopenlab/update/UnzipFiles.java:35,43,62`
Zip entry names are concatenated onto the destination dir with no `..` check. Packages are fetched from a user-configurable repository (`InstallPackages.java`), and for any non-default domain the download is plain HTTP. A malicious/MITM repository can write anywhere the user can, including `~/VisualLogic/config.xml` (feeding H3) and element class files (feeding H5).

**H3 — Unsafe XMLDecoder on config file**
`src/VisualLogic/gui/FrameMain.java:1882-1883`
`XMLDecoder.readObject()` on `~/VisualLogic/config.xml` with no object-input filtering. XMLDecoder can instantiate arbitrary classes → RCE if an attacker can plant/tamper that file (e.g. via H2, shared profile, or another local foothold).

**H4 — Self-update over cleartext HTTP, no signature/checksum, and trust-all TLS installed globally**
`src/VisualLogic/gui/DialogUpdate.java:375,391` — the app's own `update.jar` is fetched from `http://www.myopenlab.de/downloads/...` with no integrity verification.
`src/de/myopenlab/update/frmUpdate.java:647-673` — a no-op `X509TrustManager` is installed as the **JVM-wide default** (`HttpsURLConnection.setDefaultSSLSocketFactory`), disabling server-certificate validation for every HTTPS connection in the process. Combined, a MITM can substitute a malicious update package that executes on the next start.

**H5 — Untrusted class loading as the core design**
`src/VisualLogic/Loader.java:41-63`, `src/VisualLogic/Element.java:746-763`, `src/VisualLogic/VMObject.java:3029`
Element class names and classpath dirs come from the `.vlogic` project file and are loaded via `URLClassLoader` and instantiated. Projects auto-open from the OS/command line (`FrameMain.java:2008-2011`). This is the tool's intended plugin mechanism, so `.vlogic` files must be treated as **untrusted executable code** (no sandbox). Combined with H2/H4, a compromised repository yields arbitrary code execution through this path.

### MEDIUM

**M1 — Trust-all SSL as JVM default (same code as H4)** — remediation: pin/verify the repository cert or remove the trust-all manager; never set a process-wide trust-all.

**M2 — Repository credentials in cleartext `config.xml`**
`src/VisualLogic/gui/FrameMain.java:763-765`, `src/VisualLogic/XMLSerializer.java:27-33`
Settings bean including `repository_login_password` is serialized via `XMLEncoder` to `~/VisualLogic/config.xml` in readable plaintext.

**M3 — Basic-auth credentials over HTTP for non-default repository domains**
`src/de/myopenlab/update/frmUpdate.java:92-98`, `Tools2.java:48-54`
Only the exact literal `http://myopenlab.de` is force-upgraded to HTTPS (`frmUpdate.java:130-132`); any other `http://` domain (or proxy) transmits the base64 username:password in cleartext.

**M4 — Unauthenticated TCP listener on port 1024**
`src/SimulatorSocket/Server.java:65-70`
`new ServerSocket(1024)` on all interfaces, no auth, no allowlist; each connection spawns a thread reading bytes into `ownerMessage`. Currently only started by the demo `SimulatorSocket/FrameMain`, not the main app — but it is an unauthenticated listener primitive if ever launched.

**M5 — JavaScript `eval` of program expressions (no sandbox)**
`src/VisualLogic/Basis.java:457,2043`
Nashorn `ScriptEngine.eval` on expressions built from user programs and variable values — including strings read from the serial port (`VSserialPort.java:126-136`). A hostile serial/network device can influence script evaluation with full JVM access. Design-intent scripting, but unsandboxed.

**M6 — XXE in `info.xml` parsing**
`src/VisualLogic/Tools.java:1552-1555`, `src/de/myopenlab/update/frmUpdate.java:379-382,498-501`
`DocumentBuilderFactory` created without disabling DOCTYPE/external entities; parses `info.xml` that arrives inside element packages from the repository. A malicious package can read local files / SSRF.

**M7 — Basic-auth sent as base64 (not encryption even over TLS)** — same sites as M3; use HTTPS-only and stop relying on base64.

**M8 — XMLDecoder in `XMLSerializer`**
`src/VisualLogic/XMLSerializer.java:33-40` — currently used only for the config write/read path (H3); flag any future wiring of project-file loading to it.

**M9 — Unbounded connections/threads on listeners (DoS)** — `SimulatorSocket/Server.java` and `SocketServer.java` accept without limits.

### LOW

- **L1** Generated circuit password printed to stdout — `src/VisualLogic/Basis.java:1273-1275`
- **L2** Circuit protection password stored as **unsalted SHA-1**; `generatePassword()` uses `Math.random()`-derived millis — `src/VisualLogic/Basis.java:1255-1264,1390-1403,1616-1630`
- **L3** Password kept as static `String` from `JPasswordField` via `Arrays.toString` (never cleared, mangled value) — `src/VisualLogic/gui/DialogPassword.java:150-159`
- **L4** Server responses/URLs/proxy settings logged to stdout — `frmUpdate.java:135,1346,630-636`
- **L5** Java deserialization of local settings file — `src/VisualLogic/gui/DialogNewJavaComponentAssistent.java:1390-1391`
- **L6** `Runtime.exec` with string concatenation, unquoted paths — `src/BasisStatus/StatusIdle.java:213`, `src/VisualLogic/Tools.java:237-241,416-421`, `src/VisualLogic/gui/FrameMain.java:445,456,4330`
- **L7** org.json 20200518 → CVE-2022-45688 (nested-JSON DoS); upgrade to ≥ 20230227
- **L8** Serial reads unbounded, data flows into program variables (amplifies M5) — `VSserialPort.java:126-136`
- **L9** SimpleFileSystem container: attacker-controlled index offsets used as stream positions — `src/SimpleFileSystem/FileSystemInput.java:72-91,135`
- **L10** Proxy host/port set from user config as JVM properties — `frmUpdate.java:628-638`
- **L11** Legacy dev lib folder with CVE-laden old jars (not shipped — see dependency scan)

### Clean
- No hardcoded API keys, tokens, private keys, or external-service secrets in source.
- No SQL injection (no JDBC usage).
- No Jython/BeanShell/Groovy; only JDK `javax.script` (M5).
- No UDP/raw sockets; no remote URL classloading (classpaths are local file paths).
- JNLP signing keystore settings are empty; no keystores/pem/key files in repo.

---

## Recommended remediation priority
1. **H4/M1** — Remove the process-wide trust-all SSL manager; verify certificates. (Highest ROI.)
2. **H1** — Stop deserializing on the network boundary: add `ObjectInputFilter` (JEP 290) on all `readObject` sites, or move to a validated framing/JSON protocol. Do the same for the SocketClient element.
3. **H2** — Sanitize zip entry names (reject `..`, absolute paths) in `UnzipFiles`.
4. **H3** — Add an `ObjectInputFilter`/allowlist to the XMLDecoder config load (JEP 290 applies to XMLDecoder too).
5. **M3/M2** — Enforce HTTPS for the repository, and stop storing the password in plaintext XML (keychain/credential store, keep `char[]`).
6. **M6** — Harden all `DocumentBuilderFactory` parses (disable DOCTYPE/external entities).
7. **H5** — Document/sandbox the element-loading trust boundary; treat `.vlogic` and downloaded packages as code (code signing would help).
8. Clean up the legacy lib folder (`/Users/xuyi/Source/Java/lib`) so gadget libraries aren't accidentally reintroduced.
