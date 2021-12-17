# Spring0 - Testing - Part II - Development.

The Cucumber testing system is a Behavioural one. [BDD](https://cucumber.io/docs/bdd/better-gherkin/).

## Cucumber and Gherkin

Gherkin is the testing language. It tests a Feature in different
Scenarios. Feature and Scenario are descriptive, but the Steps are
prescriptive - they can describe things that could happen and so have
code associated with them. It may be that the step is optional, it is
something the user could do, but is not required to do.

Generally, the "Steps" are

  The "Given" statement establishes a session for testing.
  The When and And statements are actions and indicate some kind of user input.
  The Then statement is a response from the system.

You can also use verb forms that are send or receive, so I have used the verb "am-shown" instead of "see"

### Examples

#### Using Scenario

Here is an idealized sequence for login.

    Feature: Login-Logout
    Scenario: Basic Successful Login
      Given I am a "valid" user for "app" using "backend"
      Then I am-shown "start-page"

      # start-page 
      When I must-type "username" in the "account" field
      And I must-type "mypassword" in the "password" field
      And I may-choose "save-credentials" in the "credentials" field
      And I may-choose "backend" in the "backend" field
      And I tap "enter"
      Then I am-shown "myaccount" 

      # myaccount page  
      When I tap "settings"
      Then I am-shown "mysettings"
      When I tap "logout"
      Then I am-shown "are-you-sure"
      When I tap "yes"
      Then I exit


The When statement starts a sequence, the And statement can be thought
of as continuing it, but the When and And don't imply sequencing: all
of them are required, but each individually may not do anything.

#### Using Scenario Outline

As you can see the scenario is very detailed. There is a better system for handling varieties of input.
You can use a Scenario-Outline. This is the same sequence, but many of the parameters can be changed.

    Scenario-Outline: Basic Successful Login
      Given I am a <user-type> user for <app-type> using <backend-type> with defaults <save-credentials> and <backend-type>
      Then I am-shown "start-page"

      # start-page 
      When I must-type <username> in the "account" field
      And I must-type <password> in the "password" field
      And I may-choose <save-credentials> in the "credentials" field
      And I may-choose <backend-type> in the "backend" field
      And I tap "enter"
      Then I am-shown "myaccount"
      # myaccount page is as above
      When I tap "settings"
      Then I am-shown "mysettings"
      When I tap "logout"
      Then I am-shown "are-you-sure"
      When I tap "yes"
      Then I exit

    Examples:
    | user-type | app-type | backend-type | username      | password | save-credentials |
    | valid     | Mein o2  | Mock         | 555-1234567   | test     | yes              |
    | valid     | Mein o2  | Mock         | 555-1234567   | test     | no               |
    | valid     | Mein o2  | default      | 555-1234569   | test     | default          |


You would then repeat the whole block for users that are invalid.

    Scenario-Outline: Basic Successful Login
      Given I am a <user-type> user for <app-type> using <backend-type> with defaults <save-credentials> and <backend-type>
      Then I am-shown "start-page"
      # start-page 
      When I must-type <username> in the "account" field
      And I must-type <password> in the "password" field
      And I may-choose <save-credentials> in the "credentials" field
      And I may-choose <backend-type> in the "backend" field
      And I tap "enter"
      Then I am-shown "warning-message"
      When I tap "OK"
      Then I exit

    Examples:
    | user-type | app-type | backend-type | username      | password | save-credentials |
    | locked    | Mein o2  | default      | 555-1234567   | test     | default          |
    | locked    | Mein o2  | Mock         | 555-1234569   | test     | yes              |
    | wrong1    | Mein o2  | Mock         | 555-1234568   | test     | yes              |

#### Using Background and Scenario Outline

Another feature is Background. It allows for a set of operations to be
performed for all Scenarios (and Scenario-Outlines) of a Feature.

It is only available in later versions, so it is not discussed here.

### Implementing Steps

(The Testing Framework in the Intellij IDE can generate functions for
Set operations from feature files.)

First, here is some design discussion. In the features implemented in
spring0, the Given operation is the first interaction with the
user and performs must of the login steps in one: it involves the
username, password and other fields.

The Gherkin language is not very sophisticated. It does not capture required or optional.
"And" statements are compulsory, but the code within them may not require input.
"Then" statements are actually expected outputs.

Like am-shown, I use must-type; it is a phrasing I recommend, it is
for fields that must be assigned a value.  may-choose is another I
recommend, it is for fields that may optionally be assigned a value.

### Using the Cucumber Testing Framework in your IDE

The Intellij IDE does have support for the Cucumber testing framework,
but requires a later version. It is added as a plug-in. 

Do not directly upgrade the Cucumber dependencies, the version of
Cucumber is fixed. You can appear to upgrade for the plug-in by adding
the new Cucumber dependencies with scope "provided" as part of a Maven
Profile, see the `xtra` profile in the pom.xml.

#### Coding

##### API Test 7 Design

Here are some buzzwords you may encounter in using this BDD Behaviour-Driven Development.

This testing system does not use Dependency Injection, DI.

The Browser Automation tool is Appium which is an Android and IOS
system that converts the output of those Apps into web compatible output that can be
used with Selenium - the original browser automation tool.

The software stack to the browser automation tool is

 package de.acando.myo2.automation.steps.ui.*
  use
 package de.acando.myo2.automation.util.App
  uses
 package io.appium.java_client.AppiumDriver
 is
 package org.openqa.selenium.remote.DefaultGenericMobileDriver; 
 is
 package org.openqa.selenium.remote.RemoteWebDriver;

And RemoteWebDriver interacts with the RESTful interface of the app to provide:

 getPageSource()

and perform all the find operations to track down elements.

Unfortunately, pages have not been given a title. Or anything unique
to identify them as one of the pages given in

 package de.acando.myo2.automation.screens

So this test system just gets the page source, and searches it for any
of the elements named on the page. If it doesn't locate the element, a
timeout error is recorded.

##### Hooks

Cucumber has a feature where hooks can be added to the beginning or
the end of each test sequence.  These could be used for starting a
session for the user under a user-scenario session-id. This could also
use DI and create objects as they are needed for the session.

##### Actions

Many of the Action methods implemented are tied to a specific page.

So the Given function that is actually used for login by login_logout.feature is 
ein_Nutzer_mit_der_App_mit_Backend() in BasicLoginSteps is a session-create method, but
has methods that interact with the login page: screens.Login.

It should not have been implemented in this way. For this example,
ein_Nutzer_... , the function has assumed that the Login page is the
current one and only when the function attempts to put data into the
app's current page does one discover that the Login page is not the
current one.

The test handler then times out and the test fails.

It may also be the case, that the app is on the wrong page, but that page
has an element with the same name.

##### Re-using tests

If you use a different design, you can combine the valid and invalid users and re-use more code.

    Scenario-Outline: Basic Successful Login
      Given I am a <user-type> user for <app-type> using <backend-type> with defaults <save-credentials> and <backend-type>
      Then I am-shown "start-page"

      # start-page 
      When I must-type <username> in the "account" field
      And I must-type <password> in the "password" field
      And I may-choose <save-credentials> in the "credentials" field
      And I may-choose <backend-type> in the "backend" field
      And I tap "enter"
      Then I am-shown <next-page> 
      And I exit

    Examples:
    | user-type | app-type | backend-type | username      | password | save-credentials | next-page |
    | valid     | Mein o2  | Mock         | 555-1234567   | test     | yes              | my-page   |
    | valid     | Mein o2  | Mock         | 555-1234567   | test     | no               | my-page   |
    | valid     | Mein o2  | default      | 555-1234570   | test     | default          | my-page   |
    | locked    | Mein o2  | Mock         | 555-1234568   | test     | yes              | lockd     |
    | locked    | Mein o2  | default      | 555-1234569   | test     | default          | lockd     |
    | nrgstrd   | Mein o2  | Mock         | 555-1234571   | test     | yes              | nrgstrd   |

So here, the scenario performs a shorter set of actions, but when "enter" is tapped, the step handler
checks what the new loaded page is and compares it to the recorded pages: "my-page", "locked" and "nrgstrd".

The And "I exit" step function, gracefully terminates the test, so that it can be quickly re-started.

## Spring and Beans

If you look at BasicLoginSteps.java
You can see it uses some DI - Dependency Injection - to create pages.
The annotations

    @Autowired 
    LoginPage loginPage 

This does not mean the pages have been loaded. They have beeen constructed for use within the system.  

