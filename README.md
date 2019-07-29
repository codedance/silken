SILKEN - A nicer tasting Soy Tofu (Google Closure Templates)
======

Silken is the easiest way to drop in [Google Closure
Templates](http://code.google.com/closure/templates/) into your Java web
application.

Silken wraps Google Closure Templates (Soy Templates) in a managed servlet
environment simplifying template use in *push-MVC* environments.  Silken
encourages convention over configuration, and promotes a set standard structure
for template management.

<a href="http://www.flickr.com/photos/fotoosvanrobin/5776783857/" 
    title="Silken Tofu by FotoosVanRobin on Flickr">
    <img src="http://farm6.staticflickr.com/5227/5776783857_02dbeb4d1b_m.jpg">
</a>

## Motivation
Google Closure Templates (aka Soy Templates) is a fantastic language neutral
templating system.  It has an advanced syntax, great localization support,
enforces good practice such as parameter documentation, and allows the same
templates to be used on both client and server.

**However...**

Google Closure Templates does not provide a clear set of standards for
organizing and structuring your project.  For example it provides little
guidance on managing namespaces, shared resource, file naming conventions,
caching and methods of integrating with existing tools.  Silken wraps Google
Closure Templates in a nice consumable form.

Silken was initially developed to compliment the
[HtmlEasy](http://code.google.com/p/htmleasy/) project however is not tied to
HtmlEasy in any way and can be used from any push-MVC environment/framework or
your own Servlet controller code.

Silken is a zero-dependency JAR (other than Closure Templates of course) and
can be quickly integrated into a new or existing Java web project via a simple
servlet mapping.  See the installation section to find out more about manual
setup or using via Maven/Ivy.

## Silken's Benefits

* **Loose Coupling:** Clear separation between controller code, models and
  template rendering (views).
* **Simple API:** Render soy templates from your controller code with a simple
  Servlet forward/dispatch.
* **Convention over Configuration:** Clear conventions for file placement and
  naming conventions.
* **Managed namespaces:** Both isolated and shared namespaces.
* **Smart Caching:** Compiled templates are cached while JVM memory is available.
* **Auto Publish as JavaScript:** Expose selected templates as JavaScript (i.e.
  use the same templates on the client as on the server).
* **Edit->Refresh Development:** Turn off caching to speed on-the-fly template
  editing.
* **Runtime Management:** Precompile templates on startup, and flush caches or
  recompile remotely via management URLs.
* **Globals:** Conventions for defining both compile-time and run-time globals.
* **Translation:** Conventions for message bundle/file management.
* **Simple Setup:** Simple Servlet configuration. Optional Maven/Ivy support.


## How To Use

Before reading this section you should already be familiar with [Google Closure
Template's
documentation](http://code.google.com/closure/templates/docs/overview.html) and
the concept of [template
namespaces](http://code.google.com/closure/templates/docs/helloworld_java.html).

### Step 1: Setup the Silken Servlet

Silken is a simple servlet and should be deployed on a ``/soy`` context (or
equivalent).  After adding the required dependencies to your project (see
installing), add the following to your ``web.xml`` file:

    <servlet>
        <servlet-name>soy</servlet-name>
        <servlet-class>com.papercut.silken.SilkenServlet</servlet-class>
        <load-on-startup>1</load-on-startup>
    </servlet>
    
    <servlet-mapping>
        <servlet-name>soy</servlet-name>
        <url-pattern>/soy/*</url-pattern>
    </servlet-mapping>

*Note:* Although any URL prefix is supported, ``/soy`` is recommended and the
remainder of the documentation assumes the servlet is hosted here.

### Step 2: Create your directory or classpath structure

Silken expects your soy templates to exist either under your web app root in
``/templates`` or ``/WEB-INF/templates``, or as resources on the classpath.
All soy templates have a namespace and just like a Java Class file, should be
stored under a directory structure *matching* the namespace.  There is a special
namespace called ``shared``.  By default a template can call all other
templates in its own namespace as well as templates contained in the
``shared`` namespace.

For example your project may look like:

    templates/shared/footer.soy
    templates/shared/header.soy
    templates/products/summaryViews.soy
    templates/products/boats/boats.soy
    templates/products/boats/powerBoatView.soy
    templates/products/boats/sailingBoatView.soy
    templates/users/signup.soy
    templates/users/cart.soy
    templates/com/myorg/fully/qualified/misc.soy


Alternatively you may choose to put your ``*.soy`` files as a resource on the
classpath in the corresponding namespace.  This approach allows you to locate
``*.soy`` files relative to its supporting server-side code.  The merits of
classpath vs. web root is a personal choice - Silken will search both
locations.  When in doubt, choose the web root - soy files contain
presentation/views only, and the choice of web root makes this distinction
clear, and the location is analogous to ``*.jsp`` files.

### Step 3: Write your templates

Unlike Classes in ``*.java`` files, each ``*.soy`` file may contain more
than one template. The suggest practice is to place large templates (e.g.
complete pages) in their own file, while smaller related templates (e.g. "a
partial view" or section templates) in a single file together.

Example: ``templates/products/summaryViews.soy``

```
{namespace products}
/**
* Product template with stock remaining.
* @param productName The product name.
* @param qty The quantity remaining as an int.
*/
{template .remaining}
<tr>
  <td>{$productName}</td>
   {if $qty == 0}
     <td class="nostock">{$qty}</td>
   {else}
     <td>{$qty}</td>
   {/if}
</tr>
```

### Step 4: Rendering template in your controller logic

Templates are rendered by forwarding (dispatching) the request from your
controller code across to the Silken Servlet.  The target/forwarded URL will be
in the format:

    /soy/[namespace.templateName]

For example:

    /soy/products.boats.sailingBoatView  
    /soy/com.myorg.fully.qualified.logoutPage

The general approach is as follows:

1. The requst/browser hits your controller code (e.g. servlet code, or an MVC
   framework like [HtmlEasy](http://code.google.com/p/htmleasy/), or
  [SpringMVC](http://static.springsource.org/spring/docs/2.0.x/reference/mvc.html) ).
2. Your controller code generates the data parameters that will be consumed by 
   the template by constructing a "model".  e.g. it may query a database 
   and make a ``Boat()`` POJO/Bean class.
3. The model is set as a [Servlet Request Attribute](http://docs.oracle.com/javaee/1.3/api/javax/servlet/ServletRequest.html) under a name ``model``.
4. Finally, the controller code forwards/dispatches the request across to
   Silken and the template is rendered (returned to the browser).

Data parameters (the model) are passed to the templates via a [request
attribute](http://docs.oracle.com/javaee/1.3/api/javax/servlet/ServletRequest.html).
The model may either be:

* A map of key-value pairs (``Map<String, ?>``)
* A [POJO](http://en.wikipedia.org/wiki/Plain_Old_Java_Object] or Bean (nested 
  POJOs are supported - see **Referencing Model Data** below)
* An instance of
  [SoyMapData](http://closure-templates.googlecode.com/svn/trunk/javadoc-lite/com/google/template/soy/data/SoyMapData.html)
(if you wish to couple your controller logic with Soy)

**Examples:**

*Using a simple servlet:*

```java
public class SimpleBoatServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
	
        String sailNumber = req.getParameter("sailNumber");
		// Validate and fetch the SailBoat POJO from data store
        // ...
		SailBoat boatPojo = datastore.fetchSailingBoat(sailNumber);
		
		// Push the model into the request attribute under key "model".
		req.setAttribute("model", boatPojo);
		
		// Forward across to Silken - the path denotes the template to render.
		RequestDispatcher rd = getServletContext().getRequestDispatcher("/soy/products.boat.sailingBoatView");
		rd.forward(req, resp);
	}
}
```

*Using [HtmlEasy](http://code.google.com/p/htmleasy/) (recommended) in a nice-URL JAX-RS RESTful style:*

```java
public class BoatDisplayController {

    @Path("/boats/sailing/{sailNumber}")
    @View("/soy/products.boat.sailingBoatView")
	public SailBoat showSailingBoat(@PathParam("sailNumber") String sailNumber) {
		// Validate and fetch the SailBoat POJO from data store
        // ...
		return datastore.fetchSailingBoat(sailNumber);
	}

}
```

In the example above the model (data passed to the template) is a POJO/Bean. The
template parameters are populated with data from the matching POJO getter
names.

For example the template:

```
{namespace products.boat}
/**
* Page template for sail boats.
* @param name The boat's name.
* @param lengthOverAll The boat's LOA.
*/
{template .sailingBoatView}
    {call shared.header /}
    <h1>Boat Details</h1>
    <table>
        <tr>
          <td>Name:</td>
          <td>{$name}</td>
        </tr>
        <tr>
          <td>Length Over All (LOA):</td>
          <td>{$lengthOverAll}m</td>
        </tr>
    </table>
    {call shared.footer /}
{/template}
```

Could be rendered my passing an instance of the POJO:

```java
class SailBoat {
    private String name;
    private int lengthOverAll;
    
    public String getName() {
        return name;
    }
    public int getLengthOverAll() {
        return lengthOverAll;
    }
    // Setters ...
}
```

Or by passing a ``Map<String,?>`` like:

```java
ImmutableMap.of("name", "Australia II", "lengthOverAll", 19);
```

*Note:* There is no relationship between the dispatch URL used to render a
template, and the name or path of the ``*.soy`` file.  Although, like with
public Classes and ``*.java`` files, there is often a one-to-one relationship
between a template and a ``*.soy`` file, this is not always the case.
Multiple templates can exist in the one ``*.soy`` file.

### Step 5: Use advanced features as required (e.g. globals, translations, etc.)

On large projects you may need to consider translation/localization of
messages, global variables (both run time and compile time) and pre-compiling
your key templates on startup.  These advanced features and others are
discussed in detail below.

## Referencing Model Data
Silken enhances Soy by supporting POJOs (aka simple Java Beans) as model data. 
POJOs are automatically converted to ``Maps`` before being passed to the 
template.  POJOs may also be nested (referenced or as ``List`` elements).  
For example, model data constructed like:

```java

Employee manager = new Employee()
manager.setFirstName("Mary");

Employee projectLead = new Employee();
projectLead.setFirstName("John");
projectLead.setNickNames(Lists.newArrayList("Johnny", "Jack", "Johno"));
projectLead.setManager(manager);

Map<String, ?> model = ImmutableMap.of(
                "project", "Project X", 
                "lead", projectLead);
```

can be accessed with template syntax:

```
Project: {$project}
Lead: {$lead.firstName}
Lead's Manager: {$lead.manager.firstName}
Lead is also known as:
{foreach $nick in $lead.nickNames}
   {$nick},
{/foreach})
```

See [Soy Expressions](http://code.google.com/closure/templates/docs/concepts.html#expressions)
for more information supported types and how to reference deep/nested data and 
list elements.

## Message Bundles and Translation

### Message Files
One of Google Closure Templates most powerful features is it's first-class
support for [message
translations](http://code.google.com/closure/templates/docs/translation.html).
Silken offers a set of conventions to help with message file management.
Message files should confirm to the following conventions:

* Confirm to the file nameing convention ``*.[JavaLocaleString].xlf``  (e.g.
  ``messages.pt_BR.xlf``, ``messages.fr_FR.xlf``)

* Reside in the same namespace (directory) as the corresponding templates. 
  Templates only have access to message files within its own namespace and the
  ``shared`` namespace.

For example:

    templates/shared/footer.soy
    templates/shared/header.soy
    templates/shared/messages.pt_BR.xlf
    templates/shared/messages.de.xlf

    templates/products/summaryViews.soy
    templates/products/messages.fr_FR.xlf
    templates/products/messages.de_DE.xlf

Again a single messages file per namespace/locale is a recommendation only.
Silken will endeavor to source all ``*.[locale].xlf`` files located within
the namespace. If it can't match a file using the full ``language_COUNTRY`` 
format it will revert to searching at the wider language-only level 
(e.g. pt_BR will match ``messages-pt.xlf`` if ``message-pt_BR.xlf`` does 
not exist).

### Locale Selection
By default, Silken selects the locale based on the *Accept-Language* header.
[ServletRequest.getLocale()](http://docs.oracle.com/javaee/1.4/api/javax/servlet/ServletRequest.html).
You can change this behaviour by pointing the ``localeResolver`` servlet init
parameter to a new class that implements
``com.papercut.silken.LocaleResolver``.  (See Advanced Servlet configuration
options below.)

## Management URLs
Silken exposes the following management URLs that can assist with development 
and debugging:

``/soy/_precompile/[namespace]`` - 
Pre-compiles all templates in the given [namespace] and returns 200 OK on success.

``/soy/_flush/[namespace]`` - 
Flushes any cached compiled templates in the given [namespace] forcing a recompile on next access.

``/soy/_flushAll`` -
Flushes all cached compiled templates from all namespaces.

## Globals

### Compile-time Globals:
Compile-time global variables are compiled into the template on first use and
always remain fixed. Reasons for using compile-time globals include:

* A deployment version number
* A domain name (e.g. for absolute paths)
* A variable for cache busting assets

By default globals are sourced from a ``*.globals`` file located in the
"shared" namespace.  This file should be in ``key = value`` format as
outlined in the [Closure Templates
documentation](http://code.google.com/closure/templates/docs/java_usage.html#globals)
.  You may define globals in code by setting the
``compileTimeGlobalsProvider`` servlet init parameter to the fully qualified
name of your class that implements ``com.papercut.silken.CompileTimeGlobalsProvider``.

### Run-time Globals (Advanced):
Run-time globals are available as Soy 
[Injected Data](http://code.google.com/closure/templates/docs/concepts.html#injecteddata)
(``$ij.foo``).

Reasons for using run-time globals include:

* Passing in a user name so it's available in the header on every page.
* Session data that may be useful across many pages.

Runtime globals can be set in one of two ways:

* By setting *servlet request attribute* under the key "globals".
* In code by implementing ``com.papercut.silken.RuntimeGlobalsResolver``.

A logical place to set your runtime globals would in a *Servlet Filter* or a 
[JAX-RS PreProcessInterceptors](http://docs.jboss.org/resteasy/docs/2.3.1.GA/userguide/html/Interceptors.html#PreProcessInterceptors).

```java
public class SetGlobalsFilter extends Filter {

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        // All templates need access to the logged in user ($ij.username)
        String username = request.getUserPrincipal().getName()

        // Let's make our user agent a global as well.
        String userAgent = request.getHeader("User-Agent");

        Map<String, ?> globals = ImmutableMap.of("username", username,
                                                 "userAgent", userAgent);

        request.setAttribute("globals", globals);
    }
}
```

Runtime globals can also be defined in code by implementing 
``com.papercut.silken.RuntimeGlobalsResolver``.  This interface gives you
access to the ``HTTPServletRequest``.  To define an implementation, set the
``runtimeGlobalsResolver`` servlet init parameter to a fully qualified name
of your class that implements ``com.papercut.silken.RuntimeGlobalsResolver``.

It is also possible to construct and inject an alternate ``GlobalsResolver``
at runtime.  See *Injecting Configuration and Resolvers at Runtime* below.

## Publishing Templates as JavaScript

Closure Templates offer a unique advantage where the same template language can
be used on both the client and the server.  Advanced web development
technologies such as ajax, push state, and pjax make the re-use of the same
template on both the client and the server very attractive.  Templates defined
in a file with a ``js.soy`` extension (as apposed to just ``soy`` are
published as compiled JavaScript so they can included/consumed as a JavaScript 
resources on the client.  The URL to request the compiled templates (templates 
in ``js.soy`` files)  for a given namespace is:

    /soy/js/[serial]/[locale]/[namespace].js


Where:

* ``[serial]`` - a _mandatory_ component that can be used for cache busting.
 For example this may be a date or version number that increments every time a
 new version is deployed.
* ``[locale]`` - an _optional_ component that denotes the locale (in Java
  string format like pt_BR, en, de, etc.) used to compile the template.  If
[locale] is not defined, the locale is selected using the accept-header or as
implemented by the ``localeResolver`` (see below).
* ``[namespace]`` - the namespace.  All templates defined in ``js.soy``
  files will be rendered into the request.

An example URL: ``http://myserver.com/soy/js/20120108/de/myproject.mytemplates.js``
   

*Note:* JavaScript files are served up with a cache-control header setting the
cache time to 30-days. Using the cache busting ``[serial]`` is the best way
to ensure browsers always pick up the latest version of your templates.

## Fast Edit->Refresh Development

To speed up template development and editing, Silken may be run with caching
disabled ensuring templates are recompiled on every request.  Caching may be
disabled with one of two ways:

1. By setting a system variable ``silken.disableCaching``  For example, by 
   adding ``-Dsilken.disableCaching`` as a VM argument in your IDE launcher.
2. By setting the servlet init-parameter ``disableCaching``.

*Note:* For obvious reasons, it's not a good idea to run in this mode in
production!

## Installation

Silken is a single zero-dependency JAR (other than Closure Templates of course)
and can be quickly integrated into a new or existing Java web project via a
servlet mapping. To install:
 
### Manual Install
Add the
[soy-[version].jar](http://code.google.com/p/closure-templates/downloads/list)
and the ``silken-[version].jar`` file onto your project's class path. The
latest version of Silken is:

***[silken-2013-03-05.jar](https://raw.github.com/codedance/maven-repository/master/com/papercut/silken/silken/2013-03-05/silken-2013-03-05.jar)***

### Maven/Ivy Install

Silken (and its dependency Google Closure Templates) are hosted in a Maven
repository.

Repository: 

```
<repository>
    <id>codedance on Github</id>
    <url>https://github.com/codedance/maven-repository/raw/master</url>
</repository>
```

Artifact:

```
<groupId>com.papercut.silken</groupId>
<artifactId>silken</artifactId>
<version>2013-03-05</version>
```


*Note:* Please check the repository for the latest version ID.

### Servlet Configuration Init Parameters

Silken has the following servlet init parameters to support advanced
configuration. They are usually defined in your ``web.xml`` file as follows,
however may also be set in code.

```
<servlet>
    <servlet-name>soy</servlet-name>
    <servlet-class>com.papercut.silken.SilkenServlet</servlet-class>
    <load-on-startup>1</load-on-startup>
    <init-param>
        <param-name>sharedNamespaces </param-name>
        <param-value>share,com.myapp.shared</param-value>
    </init-param>
    <init-param>
        <param-name>precompileNamespaces</param-name>
        <param-value>com.myapp.home,com.myapp.settings</param-value>
    </init-param>
</servlet>
```


```showStackTracesInErrors``` - Set to "true" to show stack traces in the
browser/response. **Default**: *false*

```disableCaching``` - Set to "true" to turn off caching. Helps when authoring
templates (i.e. live refresh). **Default**: *false*
 
```sharedNamespaces``` - A comma separated list of namespaces shared
(available) to all templates. **Default**: *shared*
 
```localeResolver``` - Customize the locale resolver. Set to a fully qualified
class name pointing to an implementation of *LocaleResolver*.  **Default**:
*AcceptHeaderLocaleResolver*
 
```modelResolver``` - Customize the model resolver. Set to a fully qualified
class name pointing to an implementation of ``ModelResolver``.  **Default**:
*RequestAttributeModelResolver*
 
```fileSetResolver``` - Customize the model resolver. Set to a fully qualified
class name pointing to an implementation of ``FileSetResolver``. **Default**:
*WebAppFileSetResolver*
 
```compileTimeGlobalsProvider``` - Provide a custom map of Soy Template compile
time globals. **Default**: *none*
 
```runtimeGlobalsResolver``` - Provide a custom map of runtime globals passed
into every template render. **Default**: *none*
 
```precompileNamespaces``` - a comma separated list of namespaces to
pre-compile.
 
```searchPath``` - Advanced: Modify the default search path used to locate
``*.soy`` and associated files. Value is a colon separated path that may
contain/reference ``$CLASSPATH`` and ``$WEBROOT``. **Default**:
*$CLASSPATH:$WEBROOT/templates:$WEBROOT/WEB-INF/templates*

## Injecting Configuration and Resolvers at Runtime (Advanced)

In addition to using Servlet Init Parameters, configuration can be modified in
code at runtime. This includes modifying config options and also injecting
alternate provider implementation.  To access the configuration at runtime,
grab a reference to the Silken Config class via the ``silken.config`` servlet
context attribute.  Example code:

```java
import com.papercut.silken.Config;
// ...
Config config = (Config) getServletContext().getAttribute("silken.config");
config.setLocaleResolver(new MyLocaleResolver());
```
A logical place to perform this initialization would be in a startup servlet's
init method. It is also possible to access the ``Config`` class using the
convenience method: ``SilkenServlet.getConfig()``. 

## Accessing the Template Renderer (Advanced)

It's possible for non-servlet request code to access Silken's template
rendering service. For example you may have a service that generates template
emails. By using Silken's ``TemplateRenderer`` service, your template email
generation code can benefit from the same template structure, caching layer,
etc. Example usage:

```java
Map<String, String> model = ImmutableMap.of("username", username
                                            "password", password); 

TemplateRenderer renderer = (TemplateRenderer) context.getAttribute("silken.templateRenderer");

String emailText = renderer.render("email.lostPassword", model);
myEmailService.sendEmail(emailAddress, emailText);
```

It is also possible to access the ``TemplateRenderer`` using the convenience
method: ``SilkenServlet.getTemplateRenderer()``. 

## Supported Environments

Silken will run in any standard Servlet hosting environment, including [Google
App Engine](http://code.google.com/appengine/). Silken is written to work with
the latest version of Soy Templates.

## Demo

To get a quick fee for Silken it is recommended that you download and play with
the [Htmleasy Playground Project](http://code.google.com/p/htmleasy/downloads/list).
This is a pre-assembled Google App Engine Eclipse project demonstrating 
Htmleasy and includes some Silken examples.

## Miscellaneous Questions

**Q:** What is the difference between Plovr and Silken?

**A:** [Polvr](http://plovr.com/) dynamtically compiles JavaScript. Silken is
designed to assist with the development of *both* the server-side Java and 
client-side JavaScript Closure templates.

## Future

Silken's development is supported by [PaperCut
Software](http://www.papercut.com/) (makers of print management software) and
is use in production.  It's been actively developed.  If you have any ideas for
features please submit them as issues. A few ideas:

* An implicit or explicit import system so it's easy to define dependencies
  between namespaces.
* Options to define deligate templates (via a resolver).
* Maybe a way of publishing multiple namespaces into one JavaScript file.
* Lock down management URLs to set client IPs.

## Why is the project called "Silken"?

Google Closure Templates is also referred to as Soy Templates.  You'll find
references to Soy and Tofu throughout the project's class names.  Silken is a
type of smooth fine Tofu.

## Release History

**2011-12-20** 

* Initial public release.

**2012-01-05**

* BUGFIX: Explicitly set the output character encoding to UTF-8.

**2012-02-23**

* Nested POJO support. Globals are now set as ``$ij`` Injected Date.

**2012-02-27** 

* Runtime globals are now no longer also put into the Model. They are only 
  available via ``$ij`` Injected Data.  
* Improved documentation of injecting provider implementations at runtime.
* Fixed at potential NPE that may occur if the model is null and a custom
  runtime globals provider is implemented.

**2012-03-04** 

* Global ``$ij`` data may now be defined by setting a request attribute 
  "globals".
* Experimental: The TemplateRenderer is now set as a servlet context
  attribute under "silken.templateRenderer". This allow other code to
  potentially use the template rendering layer in a raw form from outside web
  request code.
* Renamed RuntimeGlobalsProvider to RuntimeGlobalsResolver to make it consistent
  with the other request based resolvers. Sites using a custom implementation 
  will need to rename their class.
* A few changes to areas of code that may not have been thread-safe. 

**2012-05-14** 

* Fixed a potential thread-safety issue that may occur when
  running with ``disableCaching`` set to true (i.e. development mode).

**2013-03-05**

* Updated POM dependencies to bring in latest Closure/Soy version.
* Code changes to leverage later version of Guava (e.g. Loading Cache)
* Minor performance work: POJOs are now parsed using java.beans.Introspector
  to benefit from its caching.
* Documentation improvements


License
=======

    (c) Copyright 2011-2013 PaperCut Software Int. Pty. Ltd. http://www.papercut.com/

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
