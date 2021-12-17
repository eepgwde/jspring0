# Spring0 - Development environments

Developers need to work with an IDE and with Maven.

For building, the Intellij IDE uses Maven.

For testing, Maven can be used from the command-line for batch tests, bu JUnit
can be used within the IDE for debug.

Dev environments are all very different. This system uses Cucumber and it is
difficult to change the testconfig.properties for your environment. And neither
the cucumber.xml or testconfig.properties files should be changed.

Here are two ways that the Maven build can be configured to build and test for a
developer's own environment.

These are:

 - change the test resources
 - pass more command-line arguments

## Adding Local JAR files
 
Maven has deprecated the local system

    <dependency>
        <groupId>com.sample</groupId>
        <artifactId>sample</artifactId>
        <version>1.0</version>
        <scope>system</scope>
        <systemPath>${project.basedir}/src/main/resources/Name_Your_JAR.jar</systemPath>
    </dependency>

You can also use an install

    mvn install:install-file \
    -Dfile=<path-to-file> \
    -DgroupId=<group-id> \
    -DartifactId=<artifact-id> \
    -Dversion=<version> \
    -Dpackaging=<packaging> \
    -DgeneratePom=true

And

Where each refers to:

<path-to-file>: the path to the file to load e.g → c:\kaptcha-2.3.jar

<group-id>: the group that the file should be registered under e.g → com.google.code

<artifact-id>: the artifact name for the file e.g → kaptcha

<version>: the version of the file e.g → 2.3

<packaging>: the packaging of the file e.g. → jar

https://maven.apache.org/general.html#importing-jars

Spring Boot JAR files cannot be used directly. You will find another JAR in the target/ 
directory with the suffix .jar.original. This is the one you want!

## Testing with JUnit 5

Another system re-written, this has a tutorial here.

https://github.com/eugenp/tutorials/tree/master/testing-modules/junit-5-basics

And specifically for Spring, https://www.baeldung.com/junit-5
https://www.baeldung.com/spring-boot-testing

## Change the Test Resources

### Adding personal resources

By changing the pom.xml, extra test resource directories can be added. The extra test resources can be added using
a profile activated in the developer's settings.xml file.

This is the "build" profile that been added to the pom.xml.

       <profile>
           <id>xtra</id>
                 <activation>
                       <activeByDefault>false</activeByDefault>
                 </activation>
           <build>
               <testResources>
                   <testResource>
                       <directory>xtra/${user.name}/src/test/resources</directory>
                   </testResource>
                   <testResource>
                       <directory>src/test/resources</directory>
                       <excludes>
                           <exclude>testconfig.properties</exclude>
                           <exclude>cucumber.xml</exclude>
                       </excludes>
                   </testResource>
               </testResources>
           </build>
       </profile>

The effect of this is that the resources copied into target/test-classes will not include the default
cucumber.xml or testconfig.properties from src/test/resources.

To activate this profile, the settings.xml file for the developer has this added to it:

       <activeProfiles>
           <activeProfile>xtra</activeProfile>
       </activeProfiles>

##### The cucumber.xml file #####

If you do create your own directory for extra resources, you can add a cucumber.xml file that uses a file URI.
Here's a snippet for Windows. 

        <bean id="testConfiguration"
                class="org.springframework.context.support.PropertySourcesPlaceholderConfigurer">
                <property name="location" value="file:///D:/weaves/Documents/cache/weaves1-testconfig.properties" />
        </bean>


##### Notes #####

For the user "weaves", the user.name property, there will be the developer's own
copies in xtra/weaves/src/test/resources.

On the filesystem, you *cannot* use a soft link to point to directory.
For me, "weaves", if I with multiple scenarios, I need to copy files into the xtra/weaves/src/test/resources
directory.

You need to rebuild between changes. mvn clean and mvn test-compile.
You can explicitly switch off the xtra copying by specifying not that profile.

    $ mvn -P\!xtra clean test-compile

You need to quote the exclamation mark in most shells.
 
## Pass More Command-Line Arguments

The first method is easy to set-up and only extends the build set-up. 

This method is more generally useful. It can be used to add arbitrary command-line arguments to all JVM executions.

### Minor Changes to the pom.xml

In pom.xml I have added space for some extra argLine parameters to be added. There is already a section that adds
some argLine parameters.

          <!-- dummy argument to allow users with settings.xml to add arguments from the profile -->
          <argLine>${test.cmdline.args}</argLine>

This change will not affect the Jenkins build.b

The test.cmdline.args feature can be used to add any JVM command-line arguments. The example below adds the parameters
for Appium testing.

### Example: Passing Appium Test Parameters

The change in the pom.xml add test.cmdline.args to the build. It can be used like this:

In your settings.xml file, you create test.cmdline.args and these will be your Appium
test parameters. That can be done using Maven profiles. 

See [this](settings.xml) example for Linux.

In that file, I have two profiles: one instances the test parameters as
properties for appium - the profile emulator2-weaves.

The other creates the test.cmdline.args variable, appium-test.

Then the two profiles are made active. To check that parameter has been set-up use this invocation of Maven.

    $ mvn help:evaluate -q -DforceStdout -Dexpression=test.cmdline.args
    
    -Dappium.serverUrl=http://localhost:4723/wd/hub
    -Dcapability.deviceName=emulator2
    -Dandroid.capability.avd=emulator2
    -Dcapability.isHeadless=false
    -Dcapability.platformName=Android
    -Dcapability.platformVersion=9
    -Dandroid.capability.app.o2=/home/build/0/o2/cache/Mein_o2.apk
    -Dandroid.capability.app.blau=/home/build/0/o2/cache/Mein_Blau.apk
    -Dandroid.capability.appPackage=canvasm.myo2

You can provide alternative configurations by adding another profile like emulator2-weaves

### Testing with JUnit

The Intellij IDE doesn't use Maven for testing. It uses JUnit runs code in a JVM and can attach a debugger.
So if you need to copy the extra command-line arguments generated by Maven to a JUnit run.

When you use the Intellij IDE, you will probably use a test class like 

    de.acando.myo2.automation.RunAlpTests
    src/test/java/de/acando/myo2/automation/RunAlpTests.java 
 
#### Run Configurations

You then add a Run Configuration that adds the test.cmdline.args

##### For a test file - RunAlpTests
 
You can use the test.cmdline.args above for JUnit tests. In the IDE, load the
project, navigate to RunAlpTests.java and use the Context Menu to Modify Run
Configuration - the command-line arguments appear in one of the fields. It
usually just contains "-ea", you can copy and paste the lines of
test.cmdline.args into that field.

##### Provide alternative JUnit Configurations

From the Tools menu of the IDE, you can Run -> Edit Configuration and add a named configuration that you can choose.

##### JUnit Template

From Run -> Edit Configuration, the pane on the left has "Edit Configuration Templates"

Use that feature to change the configuration for all JUnit runs.

## Note: Adding Maven Targets to the IDE

The Intellij IDE can run Maven from within the IDE. Add a new Run Configuration of type Maven.
The Target should be: test -Dtest=RunAlpTests or similar. The Working Directory should be the project directory.
