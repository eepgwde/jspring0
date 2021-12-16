# Spring0 - Testing

This is an overview of the process and the tools available to use the
Android test system and where to find code.

One key aspect of the testing process is that two Apps are developed - Android and iOS - but
there is only one Test Suite.

This is an overview of the process and the tools available to use the
Android test system and where to find code.

There is another [document](testing2.md) with more detail.

# Boot-strapping your test environment

You should be able to start an Android emulator using Android Studio.

You should then start an Appium server.

You can also use the Cucumber testing framework within the IDE.  You
must be careful here: the framework versions are incompatible, see the
other testing [document](testing2.md).

## Creating an Appium Session

As an illustration of what happens, this section starts an Appium
session using other tools than the test-suite and follows the actions.

You can then start a session with the Appium server using *curl* and
[this](session-create.json) JSON file. You will need to revise it:
change the APK location, emulator name and that is all.

    curl -s --output session-create.out -H Content-type: application/json -X POST http://localhost:4723/wd/hub/session -d @etc/appium/session-create.json

(Microsoft provides an implementation of curl, but this does not return any output, so you
will need another.)

The O2 app should load and present itself and returns a result, which, when pretty-printed, will
look like this: [session-create.out](session-create.out). Pretty printing is easy with
`python -m json.tool`.

The App has a short timeout of 180 seconds, you should extend this when testing with *curl*.

## Connecting the Inspector

Once you can start a session, you can use the Appium Inspector. You
can attach to a session by setting the remote path correctly to this:
`/wd/hub`.  Use the defaults for the other configuration data for
Appium Inspector.

You should then be able to use the "Attach to Session"
feature.

## Testing Sequence

With the Inspector, you must then make a note of the operations that you perform in the App.

You use the Inspector to get the XPaths or resource-ids of items that:

 - the App displays
 - you interact with

### Login Feature

Follow this login.feature in `weaves/src/test/resources/login/basic.feature`

    @xtra
    Scenario: Perform a step by step login to verify QA pipeline
    Given Start app Mein o2 and accept terms if there are any
    And Set backend Mock
    And Set MSISDN o2udo00000002
    And App won't save toggle
    And Set Password test and press enter

A scenario is made up of operations: Given, And, When.
You find the *step* associated with the operation, so search for "Start app" in the 
`src/test/java/de.acando.myo2.automation` files.

There is an [HTML file](extractedGherkinSteps.html) that lists all the
Steps that have been written.

#### Steps and Structures

Once you find a step, for an example look at `BasicLoginSteps.java`, the "Start app"
sequence is the function `start_app` and is the *handler function*.

You look at the sequence of operations and the pages that are
available.  Navigate to the page (F4 in the IDE is useful
here). Eventually, you get to the ConsentPage, for example.

On the ConsentPage, you will see the strings that are used to locate
the elements. These are taken from the Appium Inspector. These
identity strings can be either XPath or an ElementID.

These are then loaded into a `multiplatformByContainer` structure.

Back in the handler function `start_app()`, the if-statements follow
the actions available in that page of the App.

Eventually, you leave that handler function and move to the next in
the sequence.

In this example, basic.feature, that is the @And method.

Data entry is very difficult. It can be easier to enter data using the
Emulator, not the Inspector.

When using the IDE, it's easier to run in Debug mode with a breakpoint
in an incomplete step handler function. You then add function and
components to satisfy the operations.

##### Using the Python Client

You can download the python-client for
Appium. https://github.com/appium/python-client It was not a clean
install for me, so best of luck.

 1. Start a session - from the IDE with a break-point
 2. Capture the session id

 curl -X GET http://localhost:4723/wd/hub/sessions

# Simplest Test from the IDE

You can safely experiment with the system using 

 - RunXtraTests.java - the filter for the feature tests in xtra  
 - The features in the directory xtra/weaves/src/test/resources/features

Because that is a very long name, just refer to it as the xtra/ directory.

## RunXtraTests.java

This source file is a description of a test category.
All those feature tests that are marked "@xtra"

In the feature files in the xtra/ directory.

The RunXtraTests class is designed to be a very short check that the system is working.

