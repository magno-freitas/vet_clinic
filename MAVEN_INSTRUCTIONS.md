# Maven Wrapper Instructions

This project includes Maven Wrapper, which allows you to run Maven commands without having Maven installed globally on your system.

## Windows Instructions

1. Open a Command Prompt or PowerShell window
2. Navigate to the project directory (where the `pom.xml` file is located)
3. Run Maven commands using the wrapper:

```
.\mvnw clean compile
```

Or for other Maven commands:

```
.\mvnw package
.\mvnw test
```

## Linux/Mac Instructions

1. Open a Terminal
2. Navigate to the project directory (where the `pom.xml` file is located)
3. Make the wrapper script executable (if needed):

```
chmod +x ./mvnw
```

**Note:** The mvnw script may not be executable by default when you clone or download this repository. Make sure to run the chmod command above before using it.

4. Run Maven commands using the wrapper:

```
./mvnw clean compile
```

Or for other Maven commands:

```
./mvnw package
./mvnw test
```

## Troubleshooting

If you encounter any issues with the Maven wrapper:

1. Make sure Java is installed and JAVA_HOME is set correctly
2. Try running with verbose output:

```
.\mvnw -X clean compile  # Windows
./mvnw -X clean compile  # Linux/Mac
```

3. Check that the `.mvn/wrapper/maven-wrapper.properties` file exists and contains valid URLs

## Installing Maven Globally (Alternative)

If you prefer to install Maven globally:

### Windows
1. Download Maven from https://maven.apache.org/download.cgi
2. Extract the archive to a directory of your choice (e.g., `C:\Program Files\Apache\maven`)
3. Add the `bin` directory to your PATH environment variable:
   - Right-click on "This PC" or "My Computer" and select "Properties"
   - Click on "Advanced system settings"
   - Click on "Environment Variables"
   - Under "System variables", find the "Path" variable and click "Edit"
   - Add the path to the Maven bin directory (e.g., `C:\Program Files\Apache\maven\bin`)
   - Click "OK" to save changes
4. Open a new Command Prompt and verify the installation with `mvn -version`

### Linux/Mac
1. Download Maven from https://maven.apache.org/download.cgi
2. Extract the archive to a directory of your choice (e.g., `/opt/maven`)
3. Add Maven to your PATH by editing your profile file (`~/.bashrc`, `~/.zshrc`, etc.):
   ```
   export M2_HOME=/opt/maven
   export PATH=$M2_HOME/bin:$PATH
   ```
4. Apply the changes: `source ~/.bashrc` (or the appropriate profile file)
5. Verify the installation with `mvn -version`