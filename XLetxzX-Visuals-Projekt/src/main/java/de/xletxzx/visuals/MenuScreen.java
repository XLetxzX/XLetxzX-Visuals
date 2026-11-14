Run gradle build --no-daemon
To honour the JVM settings for this build a single-use Daemon process will be forked. For more on this, please refer to https://docs.gradle.org/9.2.0/userguide/gradle_daemon.html#sec:disabling_the_daemon in the Gradle documentation.
Daemon will be stopped at the end of the build 

> Configure project :
Fabric Loom: 1.14.10

Cannot remap modifiers because it does not exists in any of the targets [] or their parents.

> Task :compileJava
/home/runner/work/XLetxzX-Visuals/XLetxzX-Visuals/XLetxzX-Visuals-Projekt/src/main/java/de/xletxzx/visuals/MenuScreen.java:56: error: reached end of file while parsing
    private long
                ^
1 error

> Task :compileJava FAILED
gradle/actions: Writing build results to /home/runner/work/_temp/.gradle-actions/build-results/__run_3-1794694134359.json

[Incubating] Problems report is available at: file:///home/runner/work/XLetxzX-Visuals/XLetxzX-Visuals/XLetxzX-Visuals-Projekt/build/reports/problems/problems-report.html

1 actionable task: 1 executed
FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':compileJava'.
> Compilation failed; see the compiler output below.
  /home/runner/work/XLetxzX-Visuals/XLetxzX-Visuals/XLetxzX-Visuals-Projekt/src/main/java/de/xletxzx/visuals/MenuScreen.java:56: error: reached end of file while parsing
      private long
                  ^
  1 error

* Try:
> Check your code and dependencies to fix the compilation error(s)
> Run with --scan to generate a Build Scan (powered by Develocity).

BUILD FAILED in 17s
Error: Process completed with exit code 1.
