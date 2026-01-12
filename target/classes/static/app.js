// API base URL
const API_BASE_URL = 'http://localhost:8080/api/orchestration';
const NAMESPACE = 'default';

// DOM Elements
const deploymentsList = document.getElementById('deploymentsList');
const deploymentForm = document.getElementById('deploymentForm');

// Event Listeners
document.addEventListener('DOMContentLoaded', () => {
    refreshDeployments();
    setupFormSubmit();
});

// Setup form submission
function setupFormSubmit() {
    deploymentForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        await createDeployment();
    });
}

// Refresh deployments list
async function refreshDeployments() {
    try {
        const response = await fetch(`${API_BASE_URL}/deployments/${NAMESPACE}`);
        const deployments = await response.json();
        displayDeployments(deployments);
    } catch (error) {
        showToast('Failed to fetch deployments', 'error');
    }
}

// Display deployments in the UI
function displayDeployments(deployments) {
    deploymentsList.innerHTML = deployments.map(deployment => `
        <div class="deployment-card">
            <div class="flex justify-between items-start">
                <div>
                    <h3>${deployment.name}</h3>
                    <p>Replicas: ${deployment.replicas}</p>
                </div>
                <div class="flex space-x-2">
                    <button onclick="scaleDeployment('${deployment.name}', ${parseInt(deployment.replicas) + 1})" 
                            class="action-button scale-up">
                        <svg class="h-4 w-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/>
                        </svg>
                        Scale Up
                    </button>
                    <button onclick="scaleDeployment('${deployment.name}', ${parseInt(deployment.replicas) - 1})" 
                            class="action-button scale-down">
                        <svg class="h-4 w-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 12H4"/>
                        </svg>
                        Scale Down
                    </button>
                    <button onclick="deleteDeployment('${deployment.name}')" 
                            class="action-button delete">
                        <svg class="h-4 w-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/>
                        </svg>
                        Delete
                    </button>
                </div>
            </div>
        </div>
    `).join('');
}

// Create new deployment
async function createDeployment() {
    const name = document.getElementById('deploymentName').value;
    const image = document.getElementById('containerImage').value;
    const replicas = document.getElementById('replicas').value;
    const cpuRequest = document.getElementById('cpuRequest').value;
    const memoryRequest = document.getElementById('memoryRequest').value;

    const deployment = {
        apiVersion: "apps/v1",
        kind: "Deployment",
        metadata: {
            name: name,
            labels: {
                app: name
            }
        },
        spec: {
            replicas: parseInt(replicas),
            selector: {
                matchLabels: {
                    app: name
                }
            },
            template: {
                metadata: {
                    labels: {
                        app: name
                    }
                },
                spec: {
                    containers: [{
                        name: name,
                        image: image,
                        ports: [{
                            containerPort: 80
                        }],
                        resources: {
                            requests: {
                                cpu: cpuRequest,
                                memory: memoryRequest
                            },
                            limits: {
                                cpu: cpuRequest,
                                memory: memoryRequest
                            }
                        }
                    }]
                }
            }
        }
    };

    try {
        const response = await fetch(`${API_BASE_URL}/deployments/${NAMESPACE}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(deployment)
        });

        if (response.ok) {
            showToast('Deployment created successfully', 'success');
            deploymentForm.reset();
            refreshDeployments();
        } else {
            const error = await response.json();
            showToast(error.error || 'Failed to create deployment', 'error');
        }
    } catch (error) {
        showToast('Failed to create deployment', 'error');
    }
}

// Scale deployment
async function scaleDeployment(name, replicas) {
    try {
        const response = await fetch(
            `${API_BASE_URL}/deployments/${NAMESPACE}/${name}/scale?replicas=${replicas}`,
            { method: 'POST' }
        );

        if (response.ok) {
            showToast(`Scaled deployment ${name} to ${replicas} replicas`, 'success');
            refreshDeployments();
        } else {
            showToast('Failed to scale deployment', 'error');
        }
    } catch (error) {
        showToast('Failed to scale deployment', 'error');
    }
}

// Delete deployment
async function deleteDeployment(name) {
    if (!confirm(`Are you sure you want to delete deployment ${name}?`)) {
        return;
    }

    try {
        const response = await fetch(
            `${API_BASE_URL}/deployments/${NAMESPACE}/${name}`,
            { method: 'DELETE' }
        );

        if (response.ok) {
            showToast(`Deleted deployment ${name}`, 'success');
            refreshDeployments();
        } else {
            showToast('Failed to delete deployment', 'error');
        }
    } catch (error) {
        showToast('Failed to delete deployment', 'error');
    }
}

// Show toast notification
function showToast(message, type = 'success') {
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.textContent = message;
    document.body.appendChild(toast);

    // Trigger reflow
    toast.offsetHeight;

    toast.classList.add('show');

    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => toast.remove(), 300);
    }, 3000);
} 