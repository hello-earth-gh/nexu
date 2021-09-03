# NexU

To fill in the gap left by the demise of Java Applets to communicate with smartcards, 
Nowina has developed an innovative, open-source multi-browser multi-platform remote 
signature tool called NexU.

## Overview 

http://nowina.lu/solutions/java-less-browser-signing-nexu/

# INFORMATION ABOUT THIS FORK

This project is a friendly fork of the [nowina-solutions/nexu project](https://github.com/nowina-solutions/nexu). We will provide pull requests as needed.

The purpose of this fork is to provide caching ability for user entered data, for the length of the signing session
Features:

The main features we are targeting are :

* provide an easier installation procedure for Ubuntu LTS
* support of OpenJDK 11
* fix the [javafx mess](https://stackoverflow.com/questions/18547362/javafx-and-openjdk)
* caches entered password for the duration of a single session
* caches selected product for the duration of a single session
* created InnoSetup script and incorporated the setup package build process in the Maven POM file for nexu-standalone project
* added flag to be able to bypass product/token selection dialog, if exactly one card was found (as if the user selected it)
* upgrade the esign DSS to 5.9RC1

NB: ...\nexu-bundle\src\main\resources\inno-setup-script.iss is a handy script that creates a setup package with InnoSetup.

InnoSetup has to be installed on the development machine. When building nexu-bundle, the following extras have been added in the fork:

1) additional Maven targets, win32 and win64 that bundle nexu with 32-bit and 64-bit JRE respectively (have to change target and build two times)
2) exec-maven-plugin that runs inno-setup-script.iss during package phase of Maven build - the result is in \target\nexu\nexu\inno directory

The install package will install NexU and place it into Windows Startup folder.

How to debug: (https://github.com/nowina-solutions/nexu/issues/31)

- [edit] I figured out that nexu-standalone is not supposed to run as a jar - it is supposed to be referenced in nexu-app, and nexu-app should be launched with java -jar ... command - this way the resources referenced in nexu-standalone are relative to root classpath, which is nexu-app - anyway, the references make sense then. - nexu-app SHOULD NOT be referenced by nexu-standalone of course, because this creates cyclic reference, and doesn't make sense anyway.

- [edit] The above way of running nexu seems to create a problem for Netbeans - because nexu-app uses Maven shade plugin to create the final JAR, Netbeans doesn't seem to detect the Main Class for execution, which is actually situated in a dependency artifact, nexu-standalone, and is appended to the MANIFEST.MF during the creation of the shaded JAR (... the only way I found to be able to debug NexU this way was to start up NexU from command line, supplying JDWP options to Java, and then to attach the Netbeans debugger to this process:

```
java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000 -jar nexu-app-1.23-SNAPSHOT-jar-with-dependencies.jar
```

### System

For now this is tested on windows machine 64bit

# Additional info

- Due to the lack of maven projects to download runtime for OpenJDK 11 and JavaFX 11 (or OpenJFX) and for ease of integration some unpacked versions of the two have been made for the integration of the bundle. This mechanism needs to be revised.

- The project "nexu-proxy" is still in developing...

- [Setup the toolchain for maven](https://maven.apache.org/guides/mini/guide-using-toolchains.html)

- [Setup Smartcardio for JDk 11](https://nicedoc.io/jnasmartcardio/jnasmartcardio)


# LICENSE

- BSD License [intarsys smartcard-io](https://github.com/mkentaro1/smartcard-io/blob/master/License.txt) 
- CC0 License [jnasmartcardio](https://github.com/jnasmartcardio/jnasmartcardio/blob/master/LICENSE)
- MIT License [apdu4j](https://github.com/martinpaljak/apdu4j/blob/master/LICENSE)
- Apache License 2.0 [Spring boot](https://github.com/spring-projects/spring-boot/blob/main/LICENSE.txt)
- GNU General Public License 2.1 (LPGL) [eSign DSS](https://github.com/esig/dss/blob/master/LICENSE)
- GNU General Public License 2.0 (GPL) [OpenJdk](https://openjdk.java.net/legal/gplv2+ce.html)
- GNU General Public License 2.0 (GPL) [JavaFX 11 or OpenJFX](https://github.com/openjdk/jfx/blob/master/LICENSE)

# Credits

Ty to all the other developer with their contribution and all their fork...

- [dlemaignent](https://github.com/dlemaignent/nexu) for [use jsonp to avoid cors errors](https://github.com/dlemaignent/nexu/commit/60aa14245f5e2ffce70aa21d214367e36f4b458b)
- [sharedchains](https://github.com/sharedchains/nexu/) for [Fixed failure removing and inserting SmartCard Token ](https://github.com/sharedchains/nexu/commit/7b2d18f361d59ba5351efc4035a8f1c6aa19fbed)
- [IntesysOpenway](https://github.com/IntesysOpenway) for [some modification and the starting nexu-proxy](https://github.com/IntesysOpenway)
- [hello-earth-gh](https://github.com/hello-earth-gh) for [various changes and fixes](https://github.com/hello-earth-gh)
