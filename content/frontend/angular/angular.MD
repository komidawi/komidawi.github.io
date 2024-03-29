---
layout: page
title: Angular
permalink: /angular/
---

# Angular

1. Property binding
   1. _Property binding_ `[ ]` lets you use the property value in a template expression.
   2. `<a [title]="product.name">(...)</a>`
2. Event binding
   1. _Event binding_ uses a set of parentheses, `( )` around the event which tell Angular to listen for the `<button>` element's `click` event. When the user clicks the `<button>`, Angular executes given expression.
   1. `<button type="button" class="clear" (click)="messageService.clear()">`
3. `@Component()`
   1. `@Component` is a _decorator function_ that specifies the Angular metadata for the component.
   2. Key features in the `@Component()` are as follows:
      1. The `selector` identifies the component. By convention, Angular component selectors begin with the prefix `app-`, followed by the component name.
      2. The `templateUrl` and `styleUrl` reference the component's HTML and CSS
      3. The `@Component()` definition also exports the class
4. The `@Input()` _decorator_ indicates that the property value passes in from the component's parent
5. The generator automatically adds newly created Component to the `AppModule` to make it available to other components in the application.
6. You develop applications in the context of an Angular _workspace_.
   - A _workspace_ contains the files for one or more projects.
   - A _project_ is the set of files that make up an application or a library.
7. Interpolation Binding
   - The double curly braces `{{ }}` are Angular's _interpolation binding_ syntax. This _interpolation binding_ presents the component's title property value inside the HTML header tag.
   - `<div *ngFor="let message of messageService.messages">{{ message }}</div>`
8. Always export the Component class so you can import it elsewhere.
9. Pipes
   - _Pipes_ are a good way to format strings, currency amounts, dates, and other display data. Angular ships with several built-in _pipes_, and you can create your own.
   - The word `uppercase` in the _interpolation binding_ after the _pipe_ `|` character, activates the built-in `UppercasePipe`.
   - `<h2>{{ hero.name | uppercase }} Details</h2>`
10. `[(ngModel)]` is Angular's _two-way data binding_ syntax.
11. Metadata
    - Angular needs to know how the pieces of your application fit together and what other files and libraries the application requires. This information is called _metadata_.
    - Some of the _metadata_ is in the `@Component` _decorators_ that you added to your component classes. Other critical _metadata_ is in `@NgModule` _decorators_.
    - The most important `@NgModule` _decorator_ annotates the top-level `AppModule` class. `ng new` created an `AppModule` class in `src/app/app.module.ts` when it created the project. This is where you opt in to the `FormsModule`.
12. `[ngStyle]`
    - _Attribute directive_ - it means it changes look and behavior of an HTML element
    - Used to set a CSS style property based on TS expression
    - Example: `[ngStyle]="{color : myProperty.available === 'Available' ? 'Green' : 'Red'}"`
13. Every Component must be declared in exactly one `NgModule`.
14. Generating building blocks
    - `ng generate component [path]`
      - `ng generate component components\header`
    - `ng generate service [path]`
15. `*ngFor`
    - The `*ngFor` is Angular's _repeater directive_. It repeats the host element for each element in a list.
16. Private styles
    - You can define private styles either inline in the `@Component.styles` array or as style sheet files identified in the `@Component.styleUrls` array.
17. `*ngIf`
    1. A _structural directive_ that conditionally includes a template based on the value of an expression coerced to Boolean.
    2. `<div *ngIf="messageService.messages.length">`
18. Class Binding
    - _Class Binding_ can add and remove a CSS class conditionally.
    - Add `[class.some-css-class]="some-condition"` to the element you want to style
    - `[class.selected]="hero === selectedHero"`
19. Services
    - Components shouldn't fetch or save data directly, and they certainly shouldn't knowingly present fake data. They should focus on presenting data and delegate data access to a _service_.
    - _Services_ are a great way to share information among classes that don't know each other.
    - Create a WeatherService next and inject it in the WeatherComponent, to provide weather data.
20. `@Injectable()` services
    - This marks the class as one that participates in the dependency injection system.
    - The `WeatherService` class is going to provide an injectable service, and it can also have its own injected dependencies.
    - The `@Injectable()` decorator accepts a metadata object for the service, the same way the `@Component()` decorator did for your component classes.
21. Providers
    - You must make the `WeatherService` available to the dependency injection system before Angular can inject it into the `WeatherComponent` by registering a _provider_.
    - A _provider_ is something that can create or deliver a _service_.
22. To make sure that the `WeatherService` can provide this service, register it with the _injector_.
    - The _injector_ is the object that chooses and injects the _provider_ where the application requires it.
    - By default, `ng generate service` registers a _provider_ with the `root` _injector_ for your _service_ by including _provider metadata_, that's `providedIn: 'root'` in the `@Injectable()` _decorator_.
    - When you _provide_ the _service_ at the `root` level, Angular creates a single, shared instance of _service_ and _injects_ into any class that asks for it
    - _Registering_ the _provider_ in the `@Injectable` _metadata_ also allows Angular to optimize an application by removing the service if it isn't used.
23. Constructor
    - `constructor(private weatherService: WeatherService) {}`
    - The parameter simultaneously defines a private `weatherService` _property_ and identifies it as a `WeatherService` injection site.
24. `ngOnInit()` (vs `constructor()`)
    - While you could call `getWeatherInfo()` in the constructor, that's not the best practice.
    - Reserve the constructor for minimal initialization such as wiring constructor parameters to properties. The constructor shouldn't do anything. It certainly shouldn't call a function that makes HTTP requests to a remote server as a real data service would.
25. In Angular, the best practice is to load and configure the _router_ in a separate, top-level module. The _router_ is dedicated to routing and imported by the root `AppModule`.
    - By convention, the module class name is `AppRoutingModule` and it belongs in the `app-routing.module.ts` in the `src/app` directory.
    - `ng generate module app-routing --flat --module=app`
    1. `--flat` Puts the file in `src/app` instead of its own directory.
    2. `--module=app` Tells `ng generate` to register it in the imports array of the `AppModule`.
26. Routing
    - The `@NgModule` metadata initializes the router and starts it listening for browser location changes.
    - `RouterModule.forRoot(routes)` - The method is called `forRoot()` because you configure the router at the application's root level.
    - The `forRoot()` method supplies the service _providers_ and directives needed for routing, and performs the initial navigation based on the current browser URL.
