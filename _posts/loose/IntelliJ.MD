## Debugging

TODO: perform all of this before pushing to blog

1. Show more info
    1. `Ctrl + Q`
2. Disable breakpoint
    1. Alt + Click or MMB on breakpoint in gutter to disable it
3. By default Step Into doesn't step into JDK methods
    1. Use Force Step Into to step into nonetheless
4. Easy reach point in code
    1. To reach some point in code use Run to Cursor
    2. You can also click on the line number to show menu
5. Quick Evaluate Expression
    1. Alt + Click on code inside IDE
    2. Also, you don't have to select code - you can just place a cursor and hit Quick Evaluate shortcut
    3. It works also on objects ofc
    4. It also works on variables in Evaluate window!
6. Watches
    1. You can quickly add to Watches just by PPM on code -> Add to Watches
    2. You can also drag&drop innards of objects in Variables to create a new Watch (or drag&drop from IDE window)
7. Change variables to change program behavior
    1. If you want ad-hoc check what happens when certain conditions occur, you can just change variable values in the
       running application - you don't have to change unit test and rerun it
8. Breakpoints
    1. You can create groups of breakpoints (and e.g. disable or enable all of them at once)
    2. You can add custom labels (descriptions) for given breakpoints
    3. You can easily open advanced breakpoint window by unchecking Suspend
    4. You can Evaluate and Log on breakpoint hit
        1. You can easily create such breakpoint by selecting an expression and Shift + Click on gutter
    5. You can print Stack Trace on breakpoint hit
    6. You can make breakpoint work only if certain other breakpoint has already been hit
    7. For multithreading cases you can set to stop on Thread (not on All) -> then all other threads will work and e.g.
       also reach this breakpoint. In such scenario you will have multiple breakpoint hits between which you can switch
    8. You can make breakpoint hitable only for particular instances of a class (Instance Filter)
        1. For example you have multiple objects of a given class, but you are interested only in one of them
        2. You can quick enable if from Intention Menu (Alt + Enter)
    9. You can also widen it to all instances, but only of a given class
        1. You can include and/or exclude such classes
    10. You can skip every n hits of a breakpoint (Pass Count)
    11. You can set Method Breakpoint on interface to catch it from all implementations
    12. You can use Hide Lines From Libraries to see Stack Trace only of your code
    13. You can set breakpoint only for given pattern of class (e.g. com.github.komidawi.\*) and only for given
        methods (e.g. handle\*)
    14. You can have Exception Breakpoints and catch Exceptions when they are thrown (and the state is present), not
        when they are caught (and we lost much information)
        1. There's also predefined Any Exception Breakpoint as well
        2. You can also define a filter based on given catch - you can make Breakpoint detect only certain Exceptions (
           that are to be caught in a particular catch)
            1. This way you can filter to only e.g. Exceptions that are caught in your code (instead of library code)
        3. You can filter basing if Exception is caught or uncaught
            1. Uncaught ones are these who can stop your program
9. Remember that you can Force Return
10. Remember that you can Throw Exception
11. E.g. when the exception doesn't want to occur naturally, you can artificially make it thrown
12. Remember you can use Renderers to have own object evaluator (like custom `toString()`)
    1. Also you can have such Renderers set for children ("When expanding a node")
13. Tests
    1. You can set Repeat Mode to e.g. Until Failure
14. `Ctrl + Q` to view Database rows in Transposed Popup

---

## Shortcuts

1. Szybkie przypisanie skrótu
   W oknie Find wciśnij Alt + Enter na znalezionej akcji

2. Schowaj/Pokaż wszystkie okna
   Ctrl + Shift + F12

3. Poszerzanie/zwężanie zakładki (czy też różnych okienek)
   Ctrl + Alt + Shift + Left/Right (or Up/Down)

4. Scratch files

5. Scratch files: HTTP Requests

    - możliwość konwersji curl <> http request

6. Następne użycie np. metody w pliku
   Next Highlighted Usage // trzeba dodać własny skrót

7. Opcja Collapse Docs Documents

8. Poprzednie/Następne miejsce w kodzie
   Ctrl + Alt + Left/Right

9. Poprzednio edytowana lokalizacja
   Ctrl + Shift + Backspace

10. More options dialog podczas np. ekstraktowania fielda

11. Bookmarki

https://github.com/kacperkoza/tips_IntelliJ_IDEA

---

## HTTP Client

###

1. You can use live templates like `gtr` for GET request
    1. Press `Ctrl + J` to see all live templates.
2. In the right top, there's "Example" button, where you can see examples.
3. You can quickly generate an HTTP request if you have a URL starting with http or https in your code string literals
   or in JSON, YAML, TOML, and Properties files (Alt+Enter).
4. You can create requests from OpenAPI Specification (Endpoints window).
    1. Also it supports auto-complete for requests basing on it.
5. With response handler scripts, you can programmatically react to a received HTTP response. By using these scripts,
   you can automatically process the received data as well as validate it against the conditions that you specify.
   Response handler scripts are provided as a part of the request within the HTTP request file and are executed as soon
   as a response is received. To view the response handling examples, open the Requests with Authorization or Requests
   with Tests and Scripts requests collections.
    1. To insert the script in-place, prepend it with `>` and enclose it in `{% %}`:\
       `> {% /* body */ %}`
    2. To insert the script from an external file, prepend it with `>`:\
       `> scripts/my-script.js`
6. You can insert a pre-request script into your request in-place or by referring to an external file, similarily like
   in the case above.
7. Enable JavaScript coding assistance for response handler and pre-request scripts
    1. See: https://www.jetbrains.com/help/idea/http-client-in-product-code-editor.html#22db7591
8. You can compare responses in requests folder by choosing requests and pressing `Ctrl + D`
9. To prevent saving a request to the request history, add a comment line with the `@no-log` tag before the request.
   This can be helpful in case a request contains some sensitive data, and you don't want to log it.
10. The HTTP Client can redirect output to a custom file or directory. It supports two operators for force and soft
    redirects:
    1. The `>>` operator always creates a new file, adding an -n suffix to a filename if the requested filename already
       exists.
    2. The `>>!` operator rewrites the file if it already exists.
    3. Example: `>> myFolder/myFile.json`
11. Cookies are saved in requests directory. You can prevent saving the received cookie to the cookie jar by adding a
    comment line with the `@no-cookie-jar` tag before the request.
12. If an HTTP server requires SSL/TLS authentication for secure communication, you may need to specify the client
    certificate before sending an HTTPS request. In the HTTP Client, you can set up the client certificate using the
    private environment file.
    1. https://www.jetbrains.com/help/idea/http-client-in-product-code-editor.html#ssl_certificate
13. Within a request, start any line with `//` or `#` to make it a comment line.
14. Set names for HTTP requests above the request next to `###`, `# @name`, or `# @name =`.
15. For GET requests, you can omit the request method and only specify the URI.
    1. In the Java context, you can use code completion `Ctrl+Space` to specify URIs based on the defined `@Path`
       annotations. Any changes to the `@Path` annotations in Java code will be reflected in the contents of the
       suggestion list.
16. Break long requests into several lines
    1. Indent all query string lines but the first one.
    2. If it's due to query string - `Alt+Enter` -> _Put query parameters on separate lines_
        1. You can enforce the consistent wrapping of query parameters using the HTTP Client code style in _Settings |
           Editor | Code Style | HTTP Request | Wrapping and Braces | Query parameters wrap_.
17. To provide the request message body, inside the request, prepend the request body with a blank line and do one of
    the following
    1. Type the request body in place
    2. In the Java context, you can use code completion `Ctrl+Space` to specify the Accept header field value based on
       the defined `@Produces` annotations. Any changes to the `@Produces` annotations in Java code will be reflected in
       the contents of the suggestion list.
    3. To read the request body from a file, type the `<` symbol followed by the path to the file, like `< ./input.json`

### Variables

https://www.jetbrains.com/help/idea/exploring-http-syntax.html#using_request_vars

1. `<Select Environment Before Run>`: with this option selected, you'll have to choose an environment each time you
   click The Run button. This can be convenient if you often switch environments and want to explicitly select them for
   each run to make sure you execute requests with the needed environments. click The Run button.
2. Manage multiple environment files
    1. https://www.jetbrains.com/help/idea/exploring-http-syntax.html#manage-multiple-environment-files
3. Per-request variables

    1. You can use the `request.variables.set(variableName, variableValue)` method to set values of variables used in
       HTTP requests.
    2. Variables defined in a pre-request script are available only within a single request that follows the script.
    3. To quickly insert the variable, use the Initialize variable context action
    4. In pre-request scripts, you can also use HTTP Client Crypto API to generate HTTP signatures based on
       cryptographic hash functions, such as SHA-1, SHA-256, SHA-512, MD5, and pass them as variable to your requests.
        1. https://www.jetbrains.com/help/idea/exploring-http-syntax.html#per_request_variables

4. `move caret to ...` actions!
5. Last edit Location
    - `Ctrl + Shift + Backspace`
6. Move to target
    - `Ctrl + Home`
7. Move Caret to matching Brace
    - `Ctrl + Shift + M`
8. Move Caret to Code Block end
    - `Ctrl + ]`
9. Go to text (not Find, go to Text)
    - `Ctrl + Alt + Shift + E`
