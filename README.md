# Portfolio (Servlet learning project)

## Run it on Windows

1. Start MySQL and run `database/schema.sql` once in MySQL Workbench.
2. Set `CATALINA_HOME` to your Apache Tomcat 11 installation folder if it is not at the default path in the launcher.
3. Double-click `run-portfolio.bat`.

The launcher builds the WAR, verifies that MySQL Connector/J is packaged, deploys it to Tomcat, starts Tomcat if needed, and opens `http://localhost:8080/swaraj/`. The frontend is packaged into the WAR, so it uses the same origin as the servlets and does not need a separate Live Server.

The first build needs internet access so Maven can download its distribution and dependencies. MySQL must be installed and running on port 3306; the launcher attempts to start a stopped MySQL Windows service when permitted.
