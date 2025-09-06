# POJO to JSON/TypeScript IntelliJ IDEA Plugin

An IntelliJ IDEA plugin that generates JSON examples and TypeScript interface definitions from Java POJO classes.

## Features

- **Generate JSON Examples**: Right-click on Java classes to generate JSON with random sample values
- **Generate TypeScript Interfaces**: Convert Java POJOs to TypeScript interface definitions
- **Automatic Clipboard Copy**: Generated content is automatically copied to clipboard
- **Nested Object Support**: Handles complex nested objects and collections
- **Circular Reference Detection**: Prevents infinite recursion with circular references
- **Configurable Settings**: Customize generation behavior through IDE settings

## Installation

1. Clone this repository
2. Build the plugin using Gradle:

   ```bash
   ./gradlew buildPlugin
   ```

3. Install the generated plugin file in IntelliJ IDEA:
   - Go to `File → Settings → Plugins`
   - Click gear icon → `Install Plugin from Disk...`
   - Select the generated `.zip` file from `build/distributions/`

## Usage

### Generate JSON Example

1. Right-click on any Java class file (in Project View or Editor tab)
2. Select `Generate → Generate JSON Example`
3. The JSON with random values will be copied to your clipboard

### Generate TypeScript Interface

1. Right-click on any Java class file
2. Select `Generate → Generate TypeScript Interface`
3. The TypeScript interface definition will be copied to your clipboard

### Keyboard Shortcuts

- **Ctrl+Alt+J**: Generate JSON Example
- **Ctrl+Alt+T**: Generate TypeScript Interface

### Settings

Configure the plugin behavior in:
`File → Settings → Tools → POJO to JSON/TS`

Available settings:

- Maximum recursion depth for nested objects
- Array maximum size for collections
- Date format for date/time fields
- Enable/disable random value generation

## Supported Types

### JSON Generation

- **Primitives**: `int`, `long`, `double`, `float`, `boolean` → Random values
- **Strings**: `String` → Random sample text
- **Dates**: `Date`, `LocalDateTime` → Random dates in configurable format
- **Collections**: `List`, `Set`, `Collection` → Arrays with 1-3 random elements
- **Objects**: Custom POJOs → Nested JSON objects
- **Arrays**: Java arrays → JSON arrays

### TypeScript Generation

- **Primitives**: `int`, `long`, `double`, `float` → `number`
- **Booleans**: `boolean` → `boolean`
- **Strings**: `String` → `string`
- **Dates**: `Date`, `LocalDateTime` → `string`
- **Collections**: `List<T>`, `Set<T>` → `T[]`
- **Maps**: `Map<K,V>` → `Record<K,V>` or `{ [key: K]: V }`
- **Objects**: Custom POJOs → TypeScript interfaces

## Example

Given this Java class:

```java
public class User {
    private String name;
    private int age;
    private boolean active;
    private List<String> tags;
    private Address address;
    // ... getters and setters
}

public class Address {
    private String street;
    private String city;
    private String country;
    // ... getters and setters
}
```

### Generated JSON

```json
{
  "name": "Lorem ipsum123",
  "age": 25,
  "active": true,
  "tags": ["sample456", "test789", "demo012"],
  "address": {
    "street": "dolor345",
    "city": "sit678",
    "country": "amet901"
  }
}
```

### Generated TypeScript

```typescript
export interface Address {
  street: string;
  city: string;
  country: string;
}

export interface User {
  name: string;
  age: number;
  active: boolean;
  tags: string[];
  address: Address;
}
```

## Development

### Requirements

- IntelliJ IDEA 2023.1 or higher
- JDK 17
- Gradle 8.0+

### Project Structure

```text
src/main/
├── java/com/yourname/pojo2jsts/
│   ├── actions/           # Right-click menu actions
│   ├── generators/        # JSON/TS generation logic
│   ├── ui/               # Settings UI
│   └── utils/            # Utility classes
└── resources/META-INF/
    └── plugin.xml        # Plugin configuration
```

### Building

```bash
# Build the plugin
./gradlew buildPlugin

# Run tests
./gradlew test

# Run IntelliJ IDEA with the plugin for development
./gradlew runIde
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## License

This project is licensed under the MIT License.

## Support

For issues and feature requests, please use the GitHub issue tracker.
