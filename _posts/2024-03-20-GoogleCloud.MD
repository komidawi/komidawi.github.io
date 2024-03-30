---
title: Google Cloud Storage
date: 2024-03-20 00:00:00 +0100
categories: [default]
tags: [gcp, gcloud] # TAG names should always be lowercase
---

## Google Cloud

- [Google Cloud](#google-cloud)
- [VPC](#vpc)
- [Commands 🔨](#commands-)
  - [Container images 🔨](#container-images-)
  - [Buckets 🔨](#buckets-)
- [Storage Options](#storage-options)
- [Database concepts](#database-concepts)
  - [Entity](#entity)
- [Cloud Foundation Toolkit (CFT)](#cloud-foundation-toolkit-cft)
- [Compute Offerings](#compute-offerings)
  - [Compute Engine](#compute-engine)
  - [GKE (Google Kubernetes Engine)](#gke-google-kubernetes-engine)
  - [App Engine](#app-engine)
  - [Cloud Functions](#cloud-functions)
  - [Cloud Run](#cloud-run)
- [Billing](#billing)
- [Virtualization options](#virtualization-options)
  - [Local computer](#local-computer)
  - [Virtual Machine (VM)](#virtual-machine-vm)
  - [Containers](#containers)
- [Kubernetes](#kubernetes)
  - [Pod](#pod)
  - [Deployment](#deployment)
  - [Service](#service)
  - [K8s overview](#k8s-overview)
  - [Kubernetes Object Model](#kubernetes-object-model)
  - [Control Plane 📜](#control-plane-)
  - [Nodes](#nodes)
- [Commands 🔨](#commands--1)
- [Summary](#summary)
- [Other](#other)
  - [AppEngine vs Cloud Run](#appengine-vs-cloud-run)
  - [Naming Best Practices](#naming-best-practices)
- [Legend](#legend)
- [Sources](#sources)

## VPC

- Provides networking to Compute Engine VMs, Kubernetes Engine containers and App Engine flexible environment
  - It implies that without VPC one can't create VMs, containers or App Engine apps
  - Each GCP project has a default network
- VPC can be perceived like a physical network virtualized then within Google Cloud
- It's a global resource consisting of regional virtual subnets in data centers, all connected by global WAN
  - Each subnet is associated with a given region and a private CIDR block for internal IP address range and a gateway
- VPC networks are logically isolated from each other

## Commands 🔨

### Container images 🔨

- Build container image using Cloud Build (invoke it in directory with Dockerfile)
  - `gcloud builds submit --tag gcr.io/$GOOGLE_CLOUD_PROJECT/helloworld`
- List all the container images associated with current project
  - `gcloud container images list`
- Register gcloud as the credential helper for all Google-supported Docker registries
  - `gcloud auth configure-docker`
- Start app locally from Cloud Shell
  - `docker run -d -p 8080:8080 gcr.io/$GOOGLE_CLOUD_PROJECT/helloworld`
- Deploy containerized app to Cloud Run
  - `gcloud run deploy --image gcr.io/$GOOGLE_CLOUD_PROJECT/helloworld --allow-unauthenticated --region=$LOCATION`
  - The `allow-unauthenticated` flag in the command above makes your service publicly accessible.
- You can delete GCP project to avoid incurring charges (it will stop billing for all the resources used within that project), or just delete the image:
  - `gcloud container images delete gcr.io/$GOOGLE_CLOUD_PROJECT/helloworld`
- To delete the Cloud Run service run:
  - `gcloud run services delete helloworld --region=us-west1`

### Buckets 🔨

1. Create a new bucket
   - `gcloud storage buckets create -l $LOCATION gs://$DEVSHELL_PROJECT_ID`
2. Get a png from a public bucket
   - `gcloud storage cp gs://cloud-training/gcpfci/my-excellent-blog.png my-excellent-blog.png`
3. Copy png to the own bucket
   - `gcloud storage cp my-excellent-blog.png gs://$DEVSHELL_PROJECT_ID/my-excellent-blog.png`
4. Make it readable by everyone
   - `gsutil acl ch -u allUsers:R gs://$DEVSHELL_PROJECT_ID/my-excellent-blog.png`

## Storage Options

See separate article [here]({% post_url 2024-03-21-GoogleCloud-Storage %}).

## Database concepts

### Entity

1. Data objects are called _Entities_
2. Entities consists one or more _properties_
3. Each entity has a key defining it uniquely having:
   - Namespace
   - Entity kind
   - Identifier (string or number)
   - [Optional] Ancestor path
4. Operations on one ore more Entities are _Transactions_, which are atomic

## Cloud Foundation Toolkit (CFT)

TODO: See what's that as it seems to be nice

## Compute Offerings

### Compute Engine

- IaaS product that consists of: Compute, Network, Storage
  - Similar to physical data centers
- Predefined and customized VMs
- Storage options:
  - Persistent disks
  - Local SSDs
- Workloads can be placed behind global Load Balancers which support autoscaling
  - You can create Instance Groups, where resources can be defined to automatically meet demand
- Per-second billing
  - Low costs for rare, batch operations
- Preemptible VMs
  - For workloads, that can be safely interrupted
- Popular choice for devs:
  - Complete control over the infrastructure
  - Operating systems can be customized
  - Can run apps requiring many different operating systems
  - On-premise workloads can be easily moved to cloud without need to rewrite
  - Fine option when other options don't support app or other requirements

### GKE (Google Kubernetes Engine)

- Runs containerized apps
- Cluster contains control plane and worker nodes
- Nodes are machines (physical or virtual ones)
- Nodes contain Pods
- Pod is a group of containers sharing network and storage resources within the same node

### App Engine

- Fully managed PaaS product
- Bind code to the libraries that provide access to the infrastructure application needs
- You can upload code and App Engine will deploy required infrastructure
- Supports popular programming languages
- Can run containers
- Integrated with Cloud Tools (Monitoring, Logging, Profiler, Error Reporting)
- Supports version control and traffic splitting
- Good choice when:
  - You want to focus on writing code
  - Don't need to build a highly reliable and scalable infrastructure
  - Don't want to focus on deploying and managing environment
  - Examples: Websites, mobile app and gaming backends, method of presenting a RESTful API

### Cloud Functions

- FaaS product
- Lightweight, event-based, asynchronous compute solution
- Create small, single-purpose functions without need to manage environment
- Billed to 100ms (only when running)
- Use cases:
  - Part of microservice architecture
  - Simple, serverless mobile or IoT backends or integrations with 3rd party services and APIs
  - Real-time Cloud Storage files processing
  - Part of intelligence systems (like virtual assistants, video or image analysis, sentiment analysis)

### Cloud Run

- Managed compute platform that runs stateless containers via web requests or Pub/Sub events
- Serverless, no infrastructure management
- Built on Knative (an API and runtime environment built on Kubernetes)
- It scales very quickly, near instantaneously, and you pay only for what you used
  - (but it still may charge you for storing container images)
- Billed for resources, 100ms rounding
- You can use both container-based and sourcecode-based workflow
  - Source code approach deploys the source code (instead of container image)
    Cloud run then builds and packages the image (using Buildpacks)

## Billing

How not to become bankrupt

1. Budget
   - Setup Budget with a fixed limit or tied to some metric
     - (e.g. percentage of previous month spend)
2. Alerts
   - Above x% or above $n
3. Reports
   - Monitor expenditure by project or services
4. Quotas
   - Prevent from over-consumption due to attack or failure
   - Rate Quotas
     - Reset after a specific time
     - Example: GKE 3000 calls to API from each GCP project every 100s
   - Allocation Quotas
     - Number of resources you can have
     - Example: No more than 5 VPC networks

## Virtualization options

### Local computer

Requirements:

- Physical space
- Power
- Cooling
- Network Connectivity

Things managed manually:

- Operating system
- Software dependencies
- Application

Every bulk change require to go from desk to desk and repeat things over again

Scaling options:

- Add more computers manually
  - and configure everything manually

Resources:

- Whole computers were dedicated for a single purpose

### Virtual Machine (VM)

- Makes possible to run multiple virtual servers and operating systems on a single physical machine
- You can easily move VM from one computer to another
- Still required manual intervention for configuration and updates
- Booting OS still takes a while
- Applications that share dependencies are not isolated from each other
  - Failure of one of them may drain all resources for everyone
  - Upgrade of dependency may cause some apps to stop working
  - The solution would be to run two separate VMs for two apps
    - However this approach is limiting benefits VMs give us compared to sheer physical machines

### Containers

- Virtualizing only user space, not the entire machine or entire OS
- The user space is code residing above the kernel - especially apps and their dependencies
- That code also is packaged along with its dependencies
- Containers don't boot an entire VM - there are only processes started, so it's quick to run up or down
- Code runs the same way on local dev and production machines, no need for sophisticated configuration

## Kubernetes

### Pod

- The smallest unit that can be created or deployed
- Represents a running process on the cluster as an app or a component of an app
- Usually there is one container in one Pod
  - However you can package multiple dependencies into a single Pod and share networking and storage resources between them
- Pod provides a unique network IP and set of ports
- One way to run a container in a Pod is `kubectl run` (which starts a Deployment)

### Deployment

- Deployment represents a group of replicas of the same Pod
  - it also keeps Pods running even when nodes they run on fail
- Deployment can represent a component of an application or even an entire app

### Service

- Service is an abstraction which defines a logical set of Pods and a policy of access to them
- Service has fixed IP (unlike ephemeral IP of Pods)

### K8s overview

![K8s overview](/assets/img/posts/2024-03-20-GoogleCloud/1-k8s-overview.png)

### Kubernetes Object Model

- Each item managed by K8s is represented by an object, which can be viewed and its attributes and state can be changed
- An object is defined as a persistent entity representing state of something running on a cluster: its desired state (object spec) and its current state (object status)
- There are different Kinds of objects: containerized applications, resources, policies
- Pods are foundational building blocks
- Every running container is in a Pod
- Pod creates environment where one or more containers live (sharing resources like network and storage)
- Each Pod has unique IP, and containers within a Pod share network namespace (IP address and network ports)
  - They can communicate through localhost

### Control Plane 📜

- **kube-API server**
  - The only component to interact with directly
  - Accepts commands that view or change state of a cluster, like launching pods
- **kubectl**
  - Connects to kube-API server and interacts with it
  - Authenticates and authorizes incoming requests
- **etcd**
  - Cluster's database
  - Store configuration data and dynamic information like node info, what and where Pods should be running
- **kube-scheduler**
  - Schedules Pods onto the Nodes
  - Analyzes requirements of each Pod and finds the most suitable Node
    - Requirements are like software, hardware, policy, affinity (Pods on the same Node), anti-affinity (no Pods on the same Node)
  - It only chooses Pods/Nodes, it does not launch it
- **kube-controller-manager**
  - Component of a broader job
  - Monitors the state of a cluster through kube-APIserver
  - Makes changes to match desired state
  - btw "controller" here means that there's a code loop
    - Other controllers can be e.g. Node controller
- **kube-cloud-manager**
  - Manages controllers interacting with underlying cloud providers
  - Example: running K8s cluster on Compute Engine - then it would bring Google Cloud features like Load Balancers and Storage Volumes

### Nodes

- **kubelet**
  - Each Node runs a kubelet, a small set of control-plane components
  - It can be perceived as a K8s agent
  - When kube-APIserver wats to start a Pod on a Node, it connects to Node's kubelet
- **kube-proxy**
  - Maintains network connectivity with Pods in a cluster
  - Things like firewalls, iptables

## Commands 🔨

- Connecting kubectl to GKE cluster
  - `gcloud container clusters get-credentials [CLUSTER_NAME] --region [REGION_NAME]`
- Some basic commands
  - `kubectl get pods`
  - `kubectl describe pod [POD_NAME]`
  - `kubectl exec [POD_NAME] -- [command]`
    - `-it` runs interactive shell, so you can work inside the container
  - `kubectl logs [POD_NAME]`
- Create kubeconfig file (containing credentials for authentication, providing endpoint details for a given cluster and enabling kubectl communication):
  - `gcloud container clusters get-credentials $my_cluster --region $my_region`
  - It creates kubeconfig file located in `~/.kube/config`
  - BTW: This file can contain info of many clusters. You can check current context with `current-context` property
- Print kubeconfig
  - `kubectl config view`
- Print cluster info
  - `kubectl cluster-info`
- Print info of all contexts for kubeconfig
  - `kubectl config get-contexts`
- Change active context
  - `kubectl config use-context gke_${GOOGLE_CLOUD_PROJECT}_us-central1_autopilot-cluster-1`
- View resource usage of nodes in the cluster
  - `kubectl top nodes`
- View resource usage of pods in the cluster
  - `kubectl top pods`
- View details of services in the cluster
  - `kubectl get services`
- Enable bash autocompletion for kubectl
  - `source <(kubectl completion bash)`
- deploy nginx as a Pod named nginx-one:
  - `kubectl create deployment --image nginx nginx-one`
- Copy file
  - `kubectl cp ~/test.html $my_nginx_pod:/usr/share/nginx/html/test.html`
- Create a service and expose Pod outside the cluster
  - `kubectl expose pod $my_nginx_pod --port 80 --type LoadBalancer`
- Set port forwarding from Cloud Shell to nginx Pod
  - `kubectl port-forward $another_nginx_pod 10081:80`
  - This is an easier way to connect to and test container,the other one would be to create a service
- Display and stream logs
  - `kubectl logs $another_nginx_pod -f --timestamps`

## Summary

![](/assets/img/posts/2024-03-20-GoogleCloud/3-where-to-run-what-workloads.png)

## Other

### AppEngine vs Cloud Run

![](/assets/img/posts/2024-03-20-GoogleCloud/4-app-engine-vs-cloud-run.png)

### Naming Best Practices

![](/assets/img/posts/2024-03-20-GoogleCloud/2-naming-best-practices.png)

## Legend

🔨 - paragraph with higher amount of practical information

📜 - paragraph with information worth to pay attention to

## Sources

Getting Started With Application Development  
https://www.cloudskillsboost.google/paths/19/course_templates/22

Getting Started with Google Kubernetes Engine  
https://www.cloudskillsboost.google/paths/19/course_templates/2

Getting Started with Terraform for Google Cloud  
https://www.cloudskillsboost.google/course_templates/443

Google Cloud Fundamentals: Core Infrastructure  
https://www.cloudskillsboost.google/paths/14/course_templates/60

App Deployment, Debugging, and Performance  
https://www.cloudskillsboost.google/course_templates/43
