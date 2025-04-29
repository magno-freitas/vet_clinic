# Setting up HikariCP without Maven - COMPLETED

I have set up the project to use HikariCP without Maven. Here's what has been done:

1. Created a `lib` directory in your project root
2. Added a README.txt file in the lib directory with:
   - Direct download links for required JARs
   - Instructions for adding JARs to your project's classpath
   - IDE-specific instructions for both Eclipse and IntelliJ IDEA

Next steps for you:
1. Follow the instructions in lib/README.txt to download the required JAR files
2. Place the downloaded JARs in the lib directory
3. Add the JARs to your project's classpath following the IDE-specific instructions

Your existing code in src/vet/util/ConnectionPool.java is already configured to use HikariCP
correctly. Once you add the JARs to your classpath, the connection pooling will work as expected.

The configuration is already reading from your application.properties file and using the following
settings:
- Maximum pool size: 10 connections
- Minimum idle connections: 5
- Idle timeout: 5 minutes
- Connection timeout: 20 seconds

No further code changes are needed - just add the JARs as described above!