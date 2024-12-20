## 

IntelliJ REST API Feature

1. You can use live templates like `gtr` for GET request
    1. Press `Ctrl + J` to see all live templates.
2. In the right top, there's "Example" button, where you can see examples.
3. You can quickly generate an HTTP request if you have a URL starting with http or https in your code string literals or in JSON, YAML, TOML, and Properties files (Alt+Enter).
4. You can create requests from OpenAPI Specification (Endpoints window).
    1. Also it supports auto-complete for requests basing on it.
5. With response handler scripts, you can programmatically react to a received HTTP response. By using these scripts, you can automatically process the received data as well as validate it against the conditions that you specify. Response handler scripts are provided as a part of the request within the HTTP request file and are executed as soon as a response is received. To view the response handling examples, open the Requests with Authorization or Requests with Tests and Scripts requests collections.
    1. To insert the script in-place, prepend it with `>` and enclose it in `{% %}`:\
    `> {% /* body */ %}`
    2. To insert the script from an external file, prepend it with `>`:\
    `> scripts/my-script.js`
6. You can insert a pre-request script into your request in-place or by referring to an external file, similarily like in the case above.
7. Enable JavaScript coding assistance for response handler and pre-request scripts
    1. See: https://www.jetbrains.com/help/idea/http-client-in-product-code-editor.html#22db7591
8. You can compare responses in requests folder by choosing requests and pressing `Ctrl + D`
9. To prevent saving a request to the request history, add a comment line with the `@no-log` tag before the request. This can be helpful in case a request contains some sensitive data, and you don't want to log it.
10. The HTTP Client can redirect output to a custom file or directory. It supports two operators for force and soft redirects:
    1. The `>>` operator always creates a new file, adding an -n suffix to a filename if the requested filename already exists.
    2. The `>>!` operator rewrites the file if it already exists.
    3. Example: `>> myFolder/myFile.json`
11. Cookies are saved in requests directory. You can prevent saving the received cookie to the cookie jar by adding a comment line with the `@no-cookie-jar` tag before the request.
12. If an HTTP server requires SSL/TLS authentication for secure communication, you may need to specify the client certificate before sending an HTTPS request. In the HTTP Client, you can set up the client certificate using the private environment file.
    1. https://www.jetbrains.com/help/idea/http-client-in-product-code-editor.html#ssl_certificate
13. Within a request, start any line with `//` or `#` to make it a comment line.
14. Set names for HTTP requests above the request next to `###`, `# @name`, or `# @name =`.
15. For GET requests, you can omit the request method and only specify the URI.
    1. In the Java context, you can use code completion `Ctrl+Space` to specify URIs based on the defined `@Path` annotations. Any changes to the `@Path` annotations in Java code will be reflected in the contents of the suggestion list.
16. Break long requests into several lines
    1. Indent all query string lines but the first one.
    2. If it's due to query string - `Alt+Enter` -> _Put query parameters on separate lines_
        1. You can enforce the consistent wrapping of query parameters using the HTTP Client code style in _Settings | Editor | Code Style | HTTP Request | Wrapping and Braces | Query parameters wrap_.
17. To provide the request message body, inside the request, prepend the request body with a blank line and do one of the following
    1. Type the request body in place
    2. In the Java context, you can use code completion `Ctrl+Space` to specify the Accept header field value based on the defined `@Produces` annotations. Any changes to the `@Produces` annotations in Java code will be reflected in the contents of the suggestion list.
    3. To read the request body from a file, type the `<` symbol followed by the path to the file, like `< ./input.json`

## Variables

1. `<Select Environment Before Run>`: with this option selected, you'll have to choose an environment each time you click The Run button. This can be convenient if you often switch environments and want to explicitly select them for each run to make sure you execute requests with the needed environments. click The Run button.
2. Manage multiple environment files
    1. https://www.jetbrains.com/help/idea/exploring-http-syntax.html#manage-multiple-environment-files
2. Per-request variables
    1. You can use the `request.variables.set(variableName, variableValue)` method to set values of variables used in HTTP requests.
    2. Variables defined in a pre-request script are available only within a single request that follows the script.
    3. To quickly insert the variable, use the Initialize variable context action
    4. In pre-request scripts, you can also use HTTP Client Crypto API to generate HTTP signatures based on cryptographic hash functions, such as SHA-1, SHA-256, SHA-512, MD5, and pass them as variable to your requests.
        1. https://www.jetbrains.com/help/idea/exploring-http-syntax.html#per_request_variables
