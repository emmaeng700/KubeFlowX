# Kubernetes Microservices Orchestration Tool

A web application for managing Kubernetes deployments and microservices. Built with Spring Boot and Java's K8 cluster client.


## Features

- **Web Interface**
  - Clean, responsive design built with Tailwind CSS
  - Real-time deployment status updates
  - Interactive deployment management
  - Toast notifications for operation feedback

- **Deployment Management**
  - Create new deployments with custom configurations
  - Scale deployments up and down
  - Delete deployments with confirmation
  - View deployment details and status

- **Resource Management**
  - Configure CPU and memory requests
  - Set container images and versions
  - Manage replica counts
  - Monitor resource usage

## Getting Started

### Prerequisites

- Java 17 or later
- Maven 3.6+
- Kubernetes cluster
- kubectl configured with cluster access

### Building the Application

```bash
# Clone the repository
git clone https://github.com/Fnuworsu/kubernetes-orchestration-tool.git

# Navigate to the project directory
cd kubernetes-orchestration-tool

# Build the application
mvn clean package
```

### Running the Application

```bash
# Run the application
java -jar target/microservices-orchestration-0.0.1-SNAPSHOT.jar
```

The application will be available at `http://localhost:8080`

## Configuration

### Kubernetes Configuration

The application uses the default Kubernetes configuration from:
- `~/.kube/config` (Linux/Mac)
- `C:\Users\<username>\.kube\config` (Windows)

### Application Properties

Configure the application in `src/main/resources/application.properties`:

```properties
# Server port
server.port=8080

# Kubernetes namespace
kubernetes.namespace=default

# Logging level
logging.level.com.microservices.orchestration=INFO
```

## API Endpoints

### Deployments

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/orchestration/deployments/{namespace}` | List all deployments |
| POST | `/api/orchestration/deployments/{namespace}` | Create a new deployment |
| DELETE | `/api/orchestration/deployments/{namespace}/{name}` | Delete a deployment |
| POST | `/api/orchestration/deployments/{namespace}/{name}/scale` | Scale a deployment |

### Example Request

```json
{
  "apiVersion": "apps/v1",
  "kind": "Deployment",
  "metadata": {
    "name": "nginx-deployment",
    "labels": {
      "app": "nginx"
    }
  },
  "spec": {
    "replicas": 3,
    "selector": {
      "matchLabels": {
        "app": "nginx"
      }
    },
    "template": {
      "metadata": {
        "labels": {
          "app": "nginx"
        }
      },
      "spec": {
        "containers": [{
          "name": "nginx",
          "image": "nginx:1.14.2",
          "ports": [{
            "containerPort": 80
          }],
          "resources": {
            "requests": {
              "cpu": "100m",
              "memory": "128Mi"
            },
            "limits": {
              "cpu": "100m",
              "memory": "128Mi"
            }
          }
        }]
      }
    }
  }
}
```

## Development

### Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/microservices/orchestration/
│   │       ├── controller/
│   │       ├── service/
│   │       ├── config/
│   │       └── model/
│   └── resources/
│       ├── static/
│       │   ├── index.html
│       │   ├── styles.css
│       │   └── app.js
│       └── application.properties
└── test/
```

### Building for Development

```bash
# Run tests
mvn test

# Run with development profile
mvn spring-boot:run -Dspring.profiles.active=dev
``` 
