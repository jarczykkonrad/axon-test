# Lab 1: Getting Started with Docker: Deploying a Basic Web Application

| Lab 1:           | Getting Started with Docker: Deploying a Basic Web Application |
| ---------------- | -------------------------------------------------------------- |
| Subject:         | DAT515 Cloud Computing                                         |
| Deadline:        | **August 27, 2024 23:59**                                      |
| Expected effort: | 2-5 hours                                                      |
| Grading:         | Pass/fail                                                      |
| Submission:      | Individually                                                   |

## Table of Contents

- [Lab 1: Getting Started with Docker: Deploying a Basic Web Application](#lab-1-getting-started-with-docker-deploying-a-basic-web-application)
  - [Table of Contents](#table-of-contents)
  - [Part 1 - Prepare Virtual Machine on UiS Cloud](#part-1---prepare-virtual-machine-on-uis-cloud)
  - [Part 2 - Install Docker on the Ubuntu Virtual Machine](#part-2---install-docker-on-the-ubuntu-virtual-machine)
  - [Part 3 - Deploying a Sample Web Application using Docker](#part-3---deploying-a-sample-web-application-using-docker)
  - [Basic Docker Commands and Their Usage](#basic-docker-commands-and-their-usage)
  - [Troubleshooting Common Docker Container Issues](#troubleshooting-common-docker-container-issues)
  - [Part 4 - Building an Image using a Dockerfile](#part-4---building-an-image-using-a-dockerfile)
    - [What is a Dockerfile?](#what-is-a-dockerfile)
    - [Basic Structure of a Dockerfile](#basic-structure-of-a-dockerfile)
    - [Key Dockerfile Instructions](#key-dockerfile-instructions)
    - [Running a basic web server using Nginx via Docker](#running-a-basic-web-server-using-nginx-via-docker)
    - [Building an Image from a Dockerfile](#building-an-image-from-a-dockerfile)
    - [Dockerfile - Best Practices](#dockerfile---best-practices)
  - [Part 5 - Deploying a Multi-Container Web Application using Docker Compose](#part-5---deploying-a-multi-container-web-application-using-docker-compose)
    - [What is Docker Compose?](#what-is-docker-compose)
    - [Key Concepts](#key-concepts)
    - [Basic docker-compose Commands](#basic-docker-compose-commands)
    - [Create a docker-compose.yml file](#create-a-docker-composeyml-file)
    - [Docker Compose - Best Practices](#docker-compose---best-practices)
  - [Part 6 - Assignment](#part-6---assignment)
    - [Requirements](#requirements)
    - [Evaluation Criteria](#evaluation-criteria)
    - [Submission](#submission)

For this assignment, please form groups of 2-3 people on QuickFeed.
This will give you a shared repository.

## Part 1 - Prepare Virtual Machine on UiS Cloud

**Note that certain parts of this lab require UiS campus network access.**

1. Login in to [cloud2.ux.uis.no/horizon](https://cloud2.ux.uis.no/horizon) using the details provided to your group.

2. Go to `Project->Network->Networks` [You will perform these steps only once]

   1. Click `+ Create Network`:

      - Pick a Network Name, click Next.
      - Pick a Subnet Name.
      - Enter Network Address: `192.168.X.0/24` [X = Group Number], click Next.
      - Enter DNS Name Servers: `8.8.8.8`.
      - Click Create.

      For example, Group 1 may have entered the following details:

      ```text
      Name: Group1_Network
      Subnet Name: Group1_subnet
      Network Address: 192.168.1.0/24
      DNS Name Servers: 8.8.8.8
      Gateway IP: 192.168.1.1
      ```

   2. Add a router by navigating to `Project->Network->Routers`, click `+ Create Router`:

      - Pick Router Name.
      - Choose External Network from the dropdown menu.
      - Click Create Router.

      For example:

      ```text
      Router Name: Group1_router
      External Network: public-network1
      ```

   3. Add a network interface:

      - Choose router by click on the router name.
      - Select the Interfaces Tab, and click `+ Add Interface`.
      - Select your Subnet Name from the dropdown menu.
      - Click Submit to create the interface.

   4. You can check the Network Topology for each step.

   5. Manage Security Group Rules by navigating to `Project->Network->Security Groups`.

      - Select Manage Rules, and click the `+ Add Rule`.
      - Select SSH from the Rule dropdown menu.
      - In the CIDR field, enter the IP address from where you want to connect.
        For example, to grant access to the whole university network, you can enter `152.94.0.0/16`.
      - Click Add.

   6. (Alt 1) Import your existing SSH public key by navigating to `Project->Compute->Key Pairs`.

      This assumes you have already generated an SSH key pair on your local machine.
      Here is short blog explaining how to [Generate SSH keys](https://romanzolotarev.com/ssh.html).

      - Click `+ Import Public Key`
      - Enter Key Pair Name.
      - Select SSH Key from the Key Type dropdown menu.
      - Choose File or Paste your SSH public key in the Public Key field.
      - Click Import Public Key.

   7. (Alt 2) Create key pair by navigating to `Project->Compute->Key Pairs`.

      - Click `+ Create Key Pair`
      - Enter Key Pair Name.
      - Select SSH Key from the Key Type dropdown menu.
      - Click Create Key Pair.
      - This will download the private key file; you will want to move this to your `.ssh` directory and configure your SSH client to find it.

3. Create a test VM by navigating to `Project->Compute->Instances`.

   - Click Launch Instance.
   - Enter Instance Name.
   - Select Source in the left menu.
   - Pick a VM Image, e.g., Ubuntu 24.04, from the Available section; use the Up arrow to select the desired image.
   - Select Flavor in the left menu.
   - Pick a Flavor, e.g., m1.large, from the Available section; use the Up arrow to select the desired flavor.
   - Select Key Pair in the left menu.
   - Pick the Key Pair you created or imported earlier; use the Up arrow to select the desired key pair.
   - You may wish to inspect the Networks and Security Groups sections in the left menu, but you shouldn't need to change anything.
     They should have been set up correctly in the previous steps.
   - Click Launch Instance.

4. Associate a floating IP to the VM (first time only). Still on the `Project->Compute->Instances` page:

   Associating a floating IP (which is public IPv4 IP) to a VM is required, especially when you want the VM to be accessible from outside the OpenStack environment, such as from the internet.

   - Click the Down arrow on the right side of the Create Snapshot button.
   - Select Associate Floating IP from the dropdown menu.
   - Click the + button to create a new floating IP Address (first time only, unless the floating ip is deleted).
   - Click Allocate IP.
   - Click Associate.

5. SSH to the VM. [UiS campus network access required]

   You can now SSH to the VM using the floating IP and the private key you created or imported earlier.

   ```console
   ssh ubuntu@floating_ip -i ssh_key
   ```

   This step is only possible from UiS campus because the floating IP is only accessible from within the UiS network.

## Part 2 - Install Docker on the Ubuntu Virtual Machine

1. Update Your System: Ensure your system package database is up-to-date.

   ```console
   sudo apt update
   sudo apt upgrade
   sudo reboot # only needed if the kernel was updated
   ```

   After a reboot, log in again using SSH.

2. Install Docker

   ```console
   sudo apt install docker.io
   ```

3. Add user to the `docker` group.
   This allows you to run Docker commands without sudo.

   ```console
   sudo usermod -aG docker ${USER}
   ```

   Log out and log back in for the group changes to take effect.
   Confirm that your user is part of the `docker` group. If docker is not shown, reboot the VM.

   ```console
   $ groups
   ubuntu adm sudo dip lxd docker
   ```

4. Check Docker Status: Verify that Docker is running.

   ```console
   sudo systemctl status docker
   ```

5. Start and Enable Docker: Ensure Docker starts on boot.
   These steps are not necessary if the above status command shows that Docker is already running.

   ```console
   sudo systemctl enable docker
   sudo systemctl start docker
   ```

6. Verify Docker Installation: Check the Docker version to ensure it's installed correctly.

   ```console
   $ docker --version
   Docker version 24.0.7, build 24.0.7-0ubuntu4
   ```

7. **Install Docker Buildx:**
   Docker Buildx is a CLI plugin that extends the capabilities of the Docker CLI to work with BuildKit and provides additional features for building multi-platform images.
   Note that `sudo` is required for all `buildx` commands. Here are the steps to install:

   ```console
   # Download the latest release of buildx
   mkdir -p ~/.docker/cli-plugins/
   curl -sSL https://github.com/docker/buildx/releases/latest/download/buildx-$(uname -s)-$(uname -m) -o ~/.docker/cli-plugins/docker-buildx

   # Make the buildx binary executable
   chmod +x ~/.docker/cli-plugins/docker-buildx

   # Install buildx plugin
   sudo apt install docker-buildx

   # Verify installation
   sudo docker buildx version
   ```

## Part 3 - Deploying a Sample Web Application using Docker

1. Pull a Sample Web Application Image: For this guide, we'll use a simple HTTP server image from Docker Hub.

   ```console
   docker pull httpd
   ```

2. Run the Web Application: Start a container using the httpd image. This will run the web server on port 80.

   ```console
   docker run -d -p 80:80 --name sample-webapp httpd
   ```

   Note that the `-d` flag runs the container in detached mode (in the background).
   The `-p` flag maps port 80 on the host to port 80 on the container.

3. Access the Web Application.
   We can now check that the server is running by running `curl` on the VM itself:

   ```console
   $ curl localhost
   <html><body><h1>It works!</h1></body></html>
   $ curl http://localhost:80
   <html><body><h1>It works!</h1></body></html>
   ```

4. Accessing the Web Application over the Internet.
   To accomplish this, you need to add a firewall rule to allow incoming traffic on port 80.

   - Navigate to `Project->Network->Security Groups`.
   - Select Manage Rules, and click the `+ Add Rule` button.
   - Select HTTP from the Rule dropdown menu.
   - In the CIDR field, enter the IP address range from where you want to connect.
     For example, to grant access to the whole university network, you can enter `152.94.0.0/16`.
   - Click Add.

   You can now access the web application using the floating IP assigned to your VM by running `curl` on your local machine (not the VM):

   ```console
   $ curl floating_ip
   <html><body><h1>It works!</h1></body></html>
   ```

   You can of course use any browser to access the web application using the floating IP as well.

5. Stop and Remove the Web Application:
   When you're done testing the web application, you can stop and remove the container.

   ```console
   docker stop sample-webapp
   docker rm sample-webapp
   ```

## Basic Docker Commands and Their Usage

```text
docker --version
    Usage: Displays the Docker version installed.
    Example: docker --version
docker info
    Usage: Provides detailed information about the Docker installation.
    Example: docker info
docker pull <image_name>
    Usage: Downloads a Docker image from Docker Hub.
    Example: docker pull nginx
docker build -t <image_name>:<tag> <path>
    Usage: Builds a Docker image from a Dockerfile located at <path>.
    Example: docker build -t myapp:latest .
docker images
    Usage: Lists all available Docker images on the system.
    Example: docker images
docker run <options> <image_name>
    Usage: Creates and starts a container from a Docker image.
    Example: docker run -d -p 80:80 nginx
docker ps
    Usage: Lists running containers.
    Example: docker ps
docker ps -a
    Usage: Lists all containers, including stopped ones.
    Example: docker ps -a
docker stop <container_id/container_name>
    Usage: Stops a running container.
    Example: docker stop my_container
docker rm <container_id/container_name>
    Usage: Removes a stopped container.
    Example: docker rm my_container
docker rmi <image_name>
    Usage: Removes a Docker image.
    Example: docker rmi nginx
docker logs <container_id/container_name>
    Usage: Displays logs from a running or stopped container.
    Example: docker logs my_container
```

## Troubleshooting Common Docker Container Issues

- Container Fails to Start
  - Check Logs: Use docker logs <container_name> to check for any error messages.
  - Inspect Configuration: Ensure that the Docker run command has the correct parameters, such as port mappings and volume mounts.
- Networking Issues
  - Check IP Address: Use docker inspect <container_name> | grep IPAddress to find the container's IP address.
  - Check Port Bindings: Ensure that the ports inside the container are correctly mapped to the host using the -p option.
  - You may use docker port <container_name> to further check the port mapping.
- File or Directory Not Found in Container
  - Check Volumes: Ensure that directories or files from the host are correctly mounted into the container using the -v option.
  - You may use docker volume ls to list all volumes mapped and docker volume inspect  <volume_name> to inspect a selected volume.
  - Inspect Image: Use docker image inspect <image_name> to see the image's layers and ensure the required files are present.
- Container Performance Issues
  - Check Resources: Containers might face performance issues if they're not allocated enough resources. Use docker stats to check the resource usage of running containers.
  - Limit Resources: When running a container, you can use flags like --cpus and --memory to limit its resources.
  - You can use docker top <container_name> to see some stats.
- Image-Related Issues
  - Pull Latest Image: Ensure you have the latest version of the image using docker pull <image_name>.
  - Check Dockerfile: If you're building your own image, ensure that the Dockerfile has the correct instructions.
- Permission Issues
  - User Mappings: If a containerized application can't access certain files, it might be a user permission issue. Ensure that the user inside the container has the necessary permissions.
  - Use --user Flag: When running a container, you can specify which user the container should run as using the --user flag.

## Part 4 - Building an Image using a Dockerfile

### What is a Dockerfile?

A Dockerfile is a script containing a set of instructions used by Docker to automate the process of building a new container image.
It defines the environment inside the container, installs necessary software, sets up commands, and more.

### Basic Structure of a Dockerfile

A `Dockerfile` consists of a series of instructions and arguments.
Each instruction is an operation used to build the image, like installing a software package or copying files.
The instruction is written in uppercase, followed by its arguments.

### Key Dockerfile Instructions

```dockerfile
FROM: Specifies the base image to start from. It's usually an OS or another application.
    Example: FROM ubuntu:24.04

LABEL: Adds metadata to the image, like maintainer information.
    Example: LABEL maintainer="name@example.com"

RUN: Executes commands in a new layer on top of the current image and commits the result.
    Example: RUN apt-get update && apt-get install -y nginx

CMD: Provides defaults for the executing container. There can only be one CMD instruction in a Dockerfile.
    Example: CMD ["nginx", "-g", "daemon off;"]

ENTRYPOINT: Configures the container to run as an executable. It's often used in combination with CMD.
    Example: ENTRYPOINT ["nginx"]

COPY: Copies files or directories from the host machine to the container.
    Example: COPY ./webapp /var/www/webapp

ADD: Similar to COPY, but can also handle URLs and tarball extraction.
    Example: ADD https://example.com/app.tar.gz /app/

WORKDIR: Sets the working directory for any subsequent RUN, CMD, ENTRYPOINT, COPY, and ADD instructions.
    Example: WORKDIR /app

EXPOSE: Informs Docker that the container listens on the specified network port at runtime.
    Example: EXPOSE 80

ENV: Sets environment variables.
    Example: ENV MY_VARIABLE=value

VOLUME: Creates a mount point for external storage or other containers.
    Example: VOLUME /data
```

### Running a basic web server using Nginx via Docker

Let's create a `Dockerfile` for a basic Nginx web server.
First, let's set up the directory structure and files as follows:

```console
mkdir my-webserver
cd my-webserver
mkdir website
echo '<h1>Hello, World!</h1>' > website/index.html
```

Next, create a file `Dockerfile` with the following content within the `my-webserver` folder:

```dockerfile
# Use the official Nginx image as a base
FROM nginx:latest

# Set the maintainer label
LABEL maintainer="name@example.com"

# Copy static website files to the Nginx web directory
COPY ./website /usr/share/nginx/html

# Expose port 80 for the web server
EXPOSE 80

# Default command to run Nginx in the foreground
CMD ["nginx", "-g", "daemon off;"]
```

### Building an Image from a Dockerfile

To build an image from your Dockerfile using Docker Buildx, follow these steps:

1. **Install Docker Buildx**:
   Already completed in Part 2.7 ([Docker Buildx documentation](https://docs.docker.com/go/buildx/).)

2. **Create and Use a Buildx Builder**:
   After installing Buildx, you need to create a new builder instance and switch to it:

   ```console
   # Create a new builder instance
   sudo docker buildx create --name mybuilder --use

   # Inspect the builder to ensure it's active
   sudo docker buildx inspect --bootstrap

   # Verify that the builder is being used
   sudo docker buildx ls
   ```

3. **Enable BuildKit**: Set the `DOCKER_BUILDKIT` environment variable to `1`.

   ```console
   echo 'export DOCKER_BUILDKIT=1' >> ~/.bashrc

   # Source the bashrc file to apply the changes
   source ~/.bashrc
   ```

4. **Build the Image**: To build an image from your Dockerfile, navigate to the directory containing the `Dockerfile` and run:

   ```console
   # Build the image using Buildx
   sudo docker buildx build --output type=docker --tag my-webserver:latest .
   ```

This command tells Docker to build an image using the `Dockerfile` in the current directory (`.`) and tag it as `my-webserver:latest`. `--output type=docker` specifies that the output should be a Docker image. Otherwise, Build result will only remain in the build cache. To push result image into registry use --push or to load image into docker use --load. The `--tag` flag assigns a name and tag to the image.

To run the newly built image as a container, use the following command:

```console
docker run -d -p 80:80 my-webserver
```

And test the web server by accessing `http://<Floating_IP>` in your browser, or using `curl` from the VM:

```console
curl floating_ip
```

### Dockerfile - Best Practices

- Minimize Layers: Try to reduce the number of layers in your image to make it lightweight. For instance, chain commands using `&&` in a single `RUN` instruction.
- Use `.dockerignore`: To exclude files that aren't needed in the container.
- Avoid Installing Unnecessary Packages: Only install the packages that are necessary to run your application.
- Clean Up: Remove temporary files and caches to reduce image size.

## Part 5 - Deploying a Multi-Container Web Application using Docker Compose

### What is Docker Compose?

Docker Compose is a tool for defining and running multi-container Docker applications.
With Compose, you can define a multi-container application in a single file, then spin up your applications with a single command:

```console
#install docker-compose on Ubuntu22.04
sudo apt  install docker-compose

#install docker-compose on Ubuntu24.04
sudo apt install docker-compose-v2

docker-compose --version

# Run the application - throws error if no docker-compose.yml file is present
docker-compose up
```

### Key Concepts

- **Services:** Each container started by Docker Compose is a service. Services are defined in the `docker-compose.yml` file.
- **Networks:** By default, Docker Compose sets up a single network for your application. Each container for a service joins the default network and is discoverable via a hostname identical to the container name.
- **Volumes:** Volumes can be used to share files between the host and container or between containers.

### Basic docker-compose Commands

- `docker-compose up`: Starts up the services defined in the docker-compose.yml file.
- `docker-compose down`: Stops and removes all the containers defined in the docker-compose.yml file.
- `docker-compose ps`: Lists the services and their current state (running/stopped).
- `docker-compose logs`: Shows the logs from the services.

Deploying WordPress with Docker Compose

Let's deploy a WordPress application using two containers: one for WordPress and another for the MySQL database.

### Create a docker-compose.yml file

```yml
version: "3"

services:
  # Database Service
  db:
    image: mysql:5.7
    volumes:
      - db_data:/var/lib/mysql
    environment:
      MYSQL_ROOT_PASSWORD: somewordpress
      MYSQL_DATABASE: wordpress
      MYSQL_USER: wordpress
      MYSQL_PASSWORD: wordpress

  # WordPress Service
  wordpress:
    depends_on:
      - db
    image: wordpress:latest
    ports:
      - "8080:80"
    environment:
      WORDPRESS_DB_HOST: db:3306
      WORDPRESS_DB_USER: wordpress
      WORDPRESS_DB_PASSWORD: wordpress
      WORDPRESS_DB_NAME: wordpress
    volumes:
      - wordpress_data:/var/www/html

volumes:
  db_data: {}
  wordpress_data: {}
```

Start the WordPress and Database Containers: Navigate to the directory containing the `docker-compose.yml` file and run:

```console
docker-compose up -d
```

This command will start the services in detached mode.
Once the services are up, you can access the WordPress site by navigating to `http://<Floating_IP>:8080` from your browser.

Stopping the Services: To stop the services, navigate to the same directory and run:

```console
docker-compose down
```

### Docker Compose - Best Practices

- Explicit Service Names: Give your services explicit names to make it clear what each service does.
- Environment Variables: Use environment variables for sensitive information and configurations.
- Service Dependencies: Use the depends_on option to ensure services start in the correct order.

## Part 6 - Assignment

Deploy a web application of your choice using Docker.
Your deployment must include multiple Docker containers.
For example, you can use three containers: one for the web application, one for a database, and one for data storage.

### Requirements

- Use at least two Docker containers.
- You may use pre-built containers from Docker Hub.
- Demonstrate the functionality of the deployed web application.

### Evaluation Criteria

- Successful deployment of multiple Docker containers.
- Proper configuration and orchestration of the containers.
- Functionality and accessibility of the web application.
- Documentation of the deployment process.
  (Not required for this lab, but can be used for future reference and project report.)

### Submission

- Submit your code to the GitHub repository before the deadline.
- Attend the lab session to demonstrate your deployment and obtain approval.
- If you submit before the lab session but cannot get approval on the last date (because of the queue), we will try to approve it in the next lab session without any issues.
