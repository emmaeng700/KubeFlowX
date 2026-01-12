package com.microservices.orchestration.service;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1DeploymentStrategy;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.openapi.models.V1Scale;
import io.kubernetes.client.openapi.models.V1ScaleSpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for managing Kubernetes resources and orchestrating microservices.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KubernetesOrchestrationService {

    private final ApiClient apiClient;

    /**
     * Scales a deployment to the specified number of replicas.
     *
     * @param namespace      The Kubernetes namespace
     * @param deploymentName The name of the deployment to scale
     * @param replicas      The desired number of replicas
     * @throws ApiException if the Kubernetes API call fails
     */
    public void scaleDeployment(String namespace, String deploymentName, int replicas) throws ApiException {
        log.info("Scaling deployment {} in namespace {} to {} replicas", deploymentName, namespace, replicas);
        AppsV1Api appsApi = new AppsV1Api(apiClient);
        V1Scale scale = new V1Scale();
        V1ScaleSpec spec = new V1ScaleSpec();
        spec.setReplicas(replicas);
        scale.setSpec(spec);
        appsApi.replaceNamespacedDeploymentScale(deploymentName, namespace, scale, null, null, null, null);
        log.info("Successfully scaled deployment {} to {} replicas", deploymentName, replicas);
    }

    /**
     * Retrieves all pods in the specified namespace.
     *
     * @param namespace The Kubernetes namespace
     * @return List of pods in the namespace
     * @throws ApiException if the Kubernetes API call fails
     */
    public List<V1Pod> getPodsInNamespace(String namespace) throws ApiException {
        log.debug("Getting pods in namespace: {}", namespace);
        CoreV1Api api = new CoreV1Api(apiClient);
        V1PodList podList = api.listNamespacedPod(namespace, null, null, null, null, null, null, null, null, null, null);
        return podList.getItems();
    }

    /**
     * Retrieves all deployments in the specified namespace.
     *
     * @param namespace The Kubernetes namespace
     * @return List of deployments in the namespace
     * @throws ApiException if the Kubernetes API call fails
     */
    public List<V1Deployment> getDeploymentsInNamespace(String namespace) throws ApiException {
        log.debug("Getting deployments in namespace: {}", namespace);
        AppsV1Api api = new AppsV1Api(apiClient);
        return api.listNamespacedDeployment(namespace, null, null, null, null, null, null, null, null, null, null).getItems();
    }

    /**
     * Creates a new deployment in the specified namespace.
     *
     * @param namespace  The Kubernetes namespace
     * @param deployment The deployment configuration
     * @return The created deployment
     * @throws ApiException if the Kubernetes API call fails
     */
    public V1Deployment createDeployment(String namespace, V1Deployment deployment) throws ApiException {
        log.debug("Creating deployment in namespace: {}", namespace);
        if (deployment == null) {
            throw new IllegalArgumentException("Deployment cannot be null");
        }
        if (deployment.getMetadata() == null || deployment.getMetadata().getName() == null) {
            throw new IllegalArgumentException("Deployment must have a name in metadata");
        }
        if (deployment.getSpec() == null || deployment.getSpec().getTemplate() == null) {
            throw new IllegalArgumentException("Deployment must have a spec and template");
        }
        
        // Set default strategy if not provided
        if (deployment.getSpec().getStrategy() == null) {
            deployment.getSpec().setStrategy(new V1DeploymentStrategy());
        }
        if (deployment.getSpec().getStrategy().getType() == null) {
            deployment.getSpec().getStrategy().setType("RollingUpdate");
        }
        
        log.debug("Deployment details - Name: {}, Namespace: {}", deployment.getMetadata().getName(), namespace);
        AppsV1Api api = new AppsV1Api(apiClient);
        try {
            V1Deployment created = api.createNamespacedDeployment(namespace, deployment, null, null, null, null);
            log.info("Successfully created deployment {} in namespace {}", deployment.getMetadata().getName(), namespace);
            return created;
        } catch (ApiException e) {
            String errorMessage = String.format("Failed to create deployment %s in namespace %s: %s (Code: %d, Response: %s)", 
                deployment.getMetadata().getName(), namespace, e.getMessage(), e.getCode(), e.getResponseBody());
            log.error(errorMessage);
            throw e;
        }
    }

    /**
     * Deletes a deployment from the specified namespace.
     *
     * @param namespace      The Kubernetes namespace
     * @param deploymentName The name of the deployment to delete
     * @throws ApiException if the Kubernetes API call fails
     */
    public void deleteDeployment(String namespace, String deploymentName) throws ApiException {
        log.info("Deleting deployment {} from namespace {}", deploymentName, namespace);
        AppsV1Api appsApi = new AppsV1Api(apiClient);
        appsApi.deleteNamespacedDeployment(deploymentName, namespace, null, null, null, null, null, null);
        log.info("Successfully deleted deployment {}", deploymentName);
    }

    /**
     * Retrieves a deployment from the specified namespace.
     *
     * @param namespace      The Kubernetes namespace
     * @param deploymentName The name of the deployment to retrieve
     * @return The deployment configuration
     * @throws ApiException if the Kubernetes API call fails
     */
    public V1Deployment getDeployment(String namespace, String deploymentName) throws ApiException {
        log.debug("Getting deployment {} in namespace: {}", deploymentName, namespace);
        AppsV1Api api = new AppsV1Api(apiClient);
        return api.readNamespacedDeployment(deploymentName, namespace, null);
    }
} 