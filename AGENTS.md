## Important Note

This is my first spring boot project. Please make your explanations easy to digest. You can use
comparisons to ASP.NET Core as I've done projects in this .NET environment. 
I'm actively trying to LEARN spring boot, not just vibe code and application.
Stay loyal to KISS rule and don't overengineer simple things.

## Project description

This is a microservice API of a Auto Warehouse application. There are automated forklifts in
a warehouse that are given tasks dependent on given orders. Orders can be given by an automated system or user-submitted.
Forklifts are given tasks based on logical foundations - factors included are
- forklift location
- forklift schedule

The system uses Kafka for asynchronous communication.

**Garage** is located near 0,0 coordinates

**Delivery zone** is located near 0, sizeY-1 coordinates

**Drop zone** is located near sizeX-1, sizeY-1 coordinates

Note: road is 2 cells wide meaning that there is right-oriented traffic.

For now, user can draw a warehouse layout - the data structure recevied from the frontend is:
- sizeX (int) - warehouse size (X)
- sizeY (int) - warehouse size (Y)
- cells (Map<String, GridCell>) - map of cells - the key is coordinate in a format "X,Y", so for example "0,1".

GridCell contains such information
- coordinates (int x, int y) - for commodity coordinate information is doubled (map key and here)
- allowedDirections (Set<Direction>) - set of allowed directions from this cell - a direction is an enum (UP, DOWN, LEFT, RIGHT)
- selected (boolean) - is this cell a road
- isShelf (boolean) - is this cell a shelf

Fronted algorithm works in such way that it puts shelf in any cell neighboring a road.

## Code Fromatting

- Indentation: 4 spaces.
- Blank Lines: Use to separate logical blocks of code.
- Line Length: Maximum 120 characters.
- Use IntelliJ IDEA default code style for Java.

## Java Style

- Use UTF-8 encoding.
- Use descriptive names for classes, methods, and variables.
- Avoid `var` keyword, prefer explicit types.
- All method parameters should be `final`.
- All variables should be declared as `final` where possible.
- Preference for immutability:
- Avoid mutations of objects, specially when using for-each loops or Stream API using `forEach()`.
- Avoid magic numbers and strings; use constants instead.
- Check emptiness and nullness before operations on collections and strings.
- Avoid methods using `throws` clause; prefer unchecked exceptions.

- Comments could be applied for: cron expressions, Regex patterns, TODOs or given/when/then separation in tests.
- Use `@Override` annotation when overriding methods.
- Wrap multiple conditions in a boolean variable for better readibility
- Prefer early returns.
- Avoid else statements when not necessary and try early returns.

## Lombok Annotations

- Use `@RequiredArgsConstructor` from Lombok for dependency injection via constructor.
- Use `@Slf4j` from Lombok for logging.
- Use `@Builder(setterPrefix = "with"))` for complex object creation.
- Avoid `@Data` annotation; prefer `@Getter` and `@Setter` for granular control.

## Annotations

- **`@Service`**: For business logic classes.
- **`@Repository`**: For data access classes that extend JPA repositories or interact with the database.
- **`@RestController`**: For web controllers.
- **`@Component`**: For generic Spring components.
- **`@Configuration`**: For Spring configuration classes.
- **`@Autowired`**: Prefer constructor injection for production code and field injection only for tests.
- **`@ConfigurationProperties`**: For binding related properties avoid multiple `@Value` annotations. From more than 2 properties, consider using this annotation.
- **`@Transactional`**: Only Service classes should be annotated with @Transactional at class level to avoid transaction management in each method.
- **`@Validated`**: To enable Bean Validation in method parameters or classes.
- **`@PreAuthorize`**: at the controller layer when using Spring Security to enforce method-level security.
- Circular dependencies should be avoided. Avoid `@Order` annotation for dependency resolution.
- 
## Exception Handling

- Custom Exceptions: Create custom domain exception classes extending `RuntimeException`.
- Global Exception Handler: Use `@ControllerAdvice` and `@ExceptionHandler` to handle exceptions globally.
- HTTP Status Codes: Map exceptions to appropriate HTTP status codes in REST controllers.
- Error Response Structure: Define a consistent error response structure

## Testing

- Use JUnit 5 for unit and integration testing.
- Use Mockito for mocking dependencies in unit tests.
- Use `@WebMvcTest(ControllerClass.class)` for testing Spring MVC controllers.
- Use `@SpringBootTest` for integration tests that require the Spring context.
- Use `given/when/then` structure in test methods for clarity.
- Method naming could follow snake_case or camelCaset convention for test methods (e.g., `get_user_by_id_ok`, `get_user_by_id_not_found_ko`).
- Avoid reflection in tests.
- Avoid business logic in tests; focus on behavior verification.

## Logging

- Use `@Slf4j` annotation from Lombok for logging to avoid boilerplate code with Logger instances.
- Log at appropriate levels: `DEBUG`, `INFO`, `WARN`, `ERROR`.
- Include contextual information in logs (e.g., request IDs, user IDs).
- Avoid logging sensitive information.
- Use structured logging for better log management.
- Format log messages with placeholders (e.g., `{}`) instead of string concatenation.