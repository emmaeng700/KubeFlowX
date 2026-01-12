package com.microservices.orchestration.controller;

import com.microservices.orchestration.service.KubernetesOrchestrationService;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1Pod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * REST controller for managing Kubernetes resources and orchestrating microservices.
 */
@Slf4j
@RestController
@RequestMapping("/api/orchestration")
@RequiredArgsConstructor
public class OrchestrationController {

    private final KubernetesOrchestrationService orchestrationService;

    /**
     * Scales a deployment to the specified number of replicas.
     *
     * @param namespace The Kubernetes namespace
     * @param name      The name of the deployment
     * @param replicas  The desired number of replicas
     * @return ResponseEntity indicating the success of the operation
     */
    @PostMapping("/deployments/{namespace}/{name}/scale")
    public ResponseEntity<Void> scaleDeployment(
            @PathVariable String namespace,
            @PathVariable String name,
            @RequestParam int replicas) {
        log.info("Scaling deployment {} to {} replicas in namespace: {}", name, replicas, namespace);
        try {
            orchestrationService.scaleDeployment(namespace, name, replicas);
            return ResponseEntity.ok().build();
        } catch (ApiException e) {
            log.error("Failed to scale deployment {} in namespace {}: {}", name, namespace, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Retrieves all pods in the specified namespace.
     *
     * @param namespace The Kubernetes namespace
     * @return ResponseEntity containing the list of pods
     */
    @GetMapping("/pods/{namespace}")
    public ResponseEntity<List<V1Pod>> listPods(@PathVariable String namespace) {
        log.info("Listing pods in namespace: {}", namespace);
        try {
            return ResponseEntity.ok(orchestrationService.getPodsInNamespace(namespace));
        } catch (ApiException e) {
            log.error("Failed to get pods in namespace {}: {}", namespace, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Creates a new deployment in the specified namespace.
     *
     * @param namespace  The Kubernetes namespace
     * @param deployment The deployment configuration
     * @return ResponseEntity indicating the success of the operation
     */
    @PostMapping("/deployments/{namespace}")
    public ResponseEntity<Map<String, String>> createDeployment(
            @PathVariable String namespace,
            @RequestBody V1Deployment deployment) {
        log.info("Creating deployment in namespace: {}", namespace);
        try {
            V1Deployment created = orchestrationService.createDeployment(namespace, deployment);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Deployment created successfully");
            response.put("name", created.getMetadata().getName());
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            log.error("Failed to create deployment in namespace {}: {}", namespace, e.getMessage());
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(e.getCode()).body(response);
        }
    }

    /**
     * Deletes a deployment from the specified namespace.
     *
     * @param namespace The Kubernetes namespace
     * @param name      The name of the deployment to delete
     * @return ResponseEntity indicating the success of the operation
     */
    @DeleteMapping("/deployments/{namespace}/{name}")
    public ResponseEntity<Void> deleteDeployment(
            @PathVariable String namespace,
            @PathVariable String name) {
        log.info("Deleting deployment {} in namespace: {}", name, namespace);
        try {
            orchestrationService.deleteDeployment(namespace, name);
            return ResponseEntity.ok().build();
        } catch (ApiException e) {
            log.error("Failed to delete deployment {} in namespace {}: {}", name, namespace, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Retrieves a deployment from the specified namespace.
     *
     * @param namespace The Kubernetes namespace
     * @param name      The name of the deployment to retrieve
     * @return ResponseEntity containing the deployment configuration
     */
    @GetMapping("/deployments/{namespace}/{name}")
    public ResponseEntity<V1Deployment> getDeployment(
            @PathVariable String namespace,
            @PathVariable String name) {
        log.info("Getting deployment {} in namespace: {}", name, namespace);
        try {
            return ResponseEntity.ok(orchestrationService.getDeployment(namespace, name));
        } catch (ApiException e) {
            log.error("Failed to get deployment {} in namespace {}: {}", name, namespace, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/deployments/{namespace}")
    public ResponseEntity<List<Map<String, String>>> listDeployments(@PathVariable String namespace) {
        log.info("Listing deployments in namespace: {}", namespace);
        try {
            List<V1Deployment> deployments = orchestrationService.getDeploymentsInNamespace(namespace);
            List<Map<String, String>> simplifiedDeployments = deployments.stream()
                .map(deployment -> {
                    Map<String, String> simplified = new HashMap<>();
                    simplified.put("name", deployment.getMetadata().getName());
                    simplified.put("replicas", String.valueOf(deployment.getSpec().getReplicas()));
                    return simplified;
                })
                .collect(Collectors.toList());
            return ResponseEntity.ok(simplifiedDeployments);
        } catch (ApiException e) {
            log.error("Failed to get deployments in namespace {}: {}", namespace, e.getMessage());
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(e.getCode()).body(List.of(response));
        }
    }
} 