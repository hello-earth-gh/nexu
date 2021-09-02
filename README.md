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
* support of OpenJDK
* fix the [javafx mess](https://stackoverflow.com/questions/18547362/javafx-and-openjdk)
* caches entered password for the duration of a single session
* caches selected product for the duration of a single session
* created InnoSetup script and incorporated the setup package build process in the Maven POM file for nexu-standalone project
* added flag to be able to bypass product/token selection dialog, if exactly one card was found (as if the user selected it)

NB: ...\nexu-bundle\src\main\resources\inno-setup-script.iss is a handy script that creates a setup package with InnoSetup.

InnoSetup has to be installed on the development machine. When building nexu-bundle, the following extras have been added in the fork:

1) additional Maven targets, win32 and win64 that bundle nexu with 32-bit and 64-bit JRE respectively (have to change target and build two times)
2) exec-maven-plugin that runs inno-setup-script.iss during package phase of Maven build - the result is in \target\nexu\nexu\inno directory

The install package will install NexU and place it into Windows Startup folder.

How to debug: (https://github.com/nowina-solutions/nexu/issues/31)

- [edit] I figured out that nexu-standalone is not supposed to run as a jar - it is supposed to be referenced in nexu-app, and nexu-app should be launched with java -jar ... command - this way the resources referenced in nexu-standalone are relative to root classpath, which is nexu-app - anyway, the references make sense then. - nexu-app SHOULD NOT be referenced by nexu-standalone of course, because this creates cyclic reference, and doesn't make sense anyway.

- [edit] The above way of running nexu seems to create a problem for Netbeans - because nexu-app uses Maven shade plugin to create the final JAR, Netbeans doesn't seem to detect the Main Class for execution, which is actually situated in a dependency artifact, nexu-standalone, and is appended to the MANIFEST.MF during the creation of the shaded JAR... the only way I found to be able to debug NexU this way was to start up NexU from command line, supplying JDWP options to Java, and then to attach the Netbeans debugger to this process:

```
java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000 -jar nexu-app-shaded.jar
```

### Installation

The installation of this project is done with maven.


# Setup for java version >= 9

**NOTE: The following instructions are based on the use of the eclipse ide**

Now the project is been upgrade to DSS 5.9.RC1 and OpenJDK 11.

The only thing you must do for a effective build is to:

1) Setup the toolchain
2) Setup smartcardio for JDk 11


### Setup the toolchain for maven

Follow this tutorial for set your jdk machine with the maven plugin the id key is "11":

https://maven.apache.org/guides/mini/guide-using-toolchains.html

### [Setup Smartcardio for JDk 11](https://nicedoc.io/jnasmartcardio/jnasmartcardio)

(Previously known as jna2pcsc.) A re-implementation of the javax.smartcardio API. It allows you to communicate to a smart card (at the APDU level) from within Java.

This library allows you to transmit and receive application protocol data units (APDUs) specified by ISO/IEC 7816-3 to a smart card.

This java library is built on top of the WinSCard native library that comes with the operating system (or libpcsclite1 installed on Linux), which in turn communicates to the myriad USB smart card readers, contactless card readers, and dongles.

Protocols built on top of APDUs include PKCS#15 public key authentication, EMV credit/debit transaction, GSM SIM cellular subscriber information, CAC U.S. military identification, Mifare Classic or DESfire transit payment, and any number of custom protocols.
Alternatives

First, if you are using smart cards for authentication and it comes with a PKCS#11 native library (or is supported by opensc-pkcs11), you should probably use the SunPKCS11 KeyStore provider instead of implementing the PKCS#15 client protocol yourself.

Once you have decided on APDU communication, you may wonder why this library exists, given that the JRE already comes with an implementation of javax.smartcardio. What’s wrong with it? There are a couple reasons you might consider switching to a JNA solution:

The default smartcardio library in JRE 7 and JRE 8 on 64-bit OS X was compiled incorrectly
; bug 7195480. In particular, Terminal.isCardPresent() always returns false, Terminals.list() occasionally causes SIGSEGV, and Terminal.waitForCard(boolean, long) and Terminals.waitForChange(long) don’t wait. Ivan Gerasimov (igerasim) fixed waitForCard, fixed list and isCardPresent, and fixed Card.openLogicalChannel for JRE 7u80, 8u20, and 9.

The default smartcardio library only calls SCardEstablishContext once. If the daemon isn’t up yet, then your process will never be able to connect to it again. This is a big problem because in Windows 8, OS X, and new versions of pcscd, the daemon is not started until a reader is plugged in, and it quits when there are no more readers.

It’s easier to fix bugs in this project than it is to fix bugs in the libraries that are bundled with the JRE. Anybody can create and comment on issues.

Another implementation of the smartcardio API is intarsys/smartcard-io, which is much more mature than this project. Please consider it. You might choose jnasmartcardio instead because:

- jnasmartcardio is much smaller and has fewer dependencies than smartcard-io.

### Installation jnasmartcardio

Requires JDK 1.6 or above.

Download the most recent published release from the Maven Central Repository. If you are using maven, you simply add this dependency to your own project’s pom.xml:

```
<dependency>
    <groupId>io.github.jnasmartcardio</groupId>
    <artifactId>jnasmartcardio</artifactId>
    <version>0.2.7</version>
</dependency>
```

To build from source, run the following command to compile, jar, and install to your local Maven repository. Don’t forget to also modify your own project’s pom.xml to depend on the same SNAPSHOT version. You may need to learn the Maven version arcana.

`mvn install`

Once you have jnasmartcardio in your classpath, there are 3 ways to use this smartcard provider instead of the one that is bundled with JRE:

Modify 

`<java_home>/jre/lib/security/java.security;` replace `security.provider.9=sun.security.smartcardio.SunPCSC` with `security.provider.9=jnasmartcardio.Smartcardio`

Then use `TerminalFactory.getDefault()`.

Create a file override.java.security, then add system property -Djava.security.properties=override.java.security. This should be a file that contains a line like the above. But make sure that you override the same numbered line as the existing SunPCSC in your JRE; otherwise, you may disable some other factory too! Then use TerminalFactory.getDefault()
   
Explicitly reference the Smartcardio class at compile time. There are a few variations of this:

```
Security.addProvider(new Smartcardio()); TerminalFactory.getInstance("PC/SC", null, Smartcardio.PROVIDER_NAME);
Security.insertProviderAt(new Smartcardio(), 1); TerminalFactory.getInstance("PC/SC", null);
TerminalFactory.getInstance("PC/SC", null, new Smartcardio());
```

Once you have a TerminalFactory, you call cardTerminals = factory.terminals();; see javax.smartcardio API javadoc.


This library requires JNA to talk to the native libraries (winscard.dll, libpcsc.so, or PCSC). You can’t use this library if you are writing an applet or are otherwise using a security manager.
Differences from JRE

Some things to keep in mind which are different from JRE:

Generally, all methods will throw a JnaPCSCException if the daemon/service is off (when there are no readers). On Windows 8, the service is stopped immediately when there are no more readers.

```
TerminalFactory
TerminalFactory.terminals()
```

will (re-)establish connection with the PCSC daemon/service. If the service is not running, terminals() will throw an unchecked exception EstablishContextException.

```
JnaCardTerminals
```

JnaCardTerminals owns the SCardContext native handle, and you should call cardTerminals.close() to clean up. Unfortunately, close() does not exist on the base class, so this library also closes it in its finalizer.

To make the implementation simpler, the caller must be able to handle spurious wakeups when calling waitForChange(long). In other words, list(State.CARD_REMOVAL) and list(State.CARD_INSERTION) might both be empty lists after waitForChange returns.

```
list(State)
```

with CARD_INSERTION/CARD_REMOVAL always reflects the result of the previous waitForChange call. If there was no previous waitForChange call, it returns an empty list; I do not return the current CARD_PRESENT/CARD_ABSENT value as Sun does because this would be inconsistent with the internal waitForChange state.

As well as waking up when a card is inserted/removed, waitForChange will also wake up when a card reader is plugged in/unplugged. However, in Windows 8, when all readers are unplugged the service will immediately exit, so waitForChange will throw an exception instead of returning.

```
JnaCardTerminal
connect(String protocol)
```

supports exactly the same connection modes as Sun does: T=0, T=1, T=*, and T=DIRECT (T=CL is mentioned in the smartcardio documentation but is not accepted). Unlike Sun, it does not return the same connection when you connect twice.

If the protocol is prepended with EXCLUSIVE; the usual SCARD_SHARE_SHARED mode shall be replaced with SCARD_SHARE_EXCLUSIVE. This allows to use a safely locked reader on Windows 8+ where otherwise a transaction initiated with SCardBeginTransaction (beginExclusive()) would be closed after 5 seconds and SCARD_W_RESET_CARD returned. See this post on MSDN.

```
JnaCard
beginExclusive()
```

simply calls SCardBeginTransaction. It does not use thread-local storage, as Sun does.

disconnect(boolean reset)
did the opposite in Sun’s implementation, which suffered bug 7047033. Ivan Gerasim of Oracle fixed their implementation to match mine in JRE 7u80, 8u20, and 9, although the old behavior can be obtained by -Djdk.smartcard.invertReset=true in JRE 8.

`JnaCardChannel`

transmit(CommandAPDU command)

currently has a response limit of 8192 bytes.

Transmit does the following automatically:

```
Sets the channel number in the class byte (CLA)
If T=0 and Lc ≠ 0 and Le ≠ 0, then the Le byte is removed as required.
If sw=61xx, then Get Response is automatically sent until the entire response is received.
If sw=6cxx, then the request is re-sent with the right Le byte.
```

However, keep in mind:

- If T=0, then you must not send a Command APDU with extended Lc/Le. User is responsible for using Envelope commands if needed to turn T=1 commands into T=0 commands.
- You may perform your own command chaining (e.g. if command is too long to fit in one Command APDU). You must put the command chaining bits in the correct position within the CLA byte, depending on the channel number.
- If you are using secure messaging, you must put the secure messaging bits in the right position within the CLA byte, depending on the channel number.

# Credits

Ty to all the other developer with their contribution and all their fork...

- [dlemaignent](https://github.com/dlemaignent/nexu) for [use jsonp to avoid cors errors](https://github.com/dlemaignent/nexu/commit/60aa14245f5e2ffce70aa21d214367e36f4b458b)
- [sharedchains](https://github.com/sharedchains/nexu/) for [Fixed failure removing and inserting SmartCard Token ](https://github.com/sharedchains/nexu/commit/7b2d18f361d59ba5351efc4035a8f1c6aa19fbed)
- [IntesysOpenway](https://github.com/IntesysOpenway) for [some modification](https://github.com/IntesysOpenway)
- [hello-earth-gh](https://github.com/hello-earth-gh) for [various changes and fixes](https://github.com/hello-earth-gh)
