Extend SSB (Standard Service Build) to Containerize (Docker) SailPoint IdentityIQ
================================

# Summary
This solution is built on top of SSB (Standard Service Build) v7.0.1. It relies on SSB to perform the build the IdentityIQ war file. The war file is then used to build a Docker container image. The generated Docker image can then be deployed to a Kubernetes cluster or Docker container instance. In this example, we will demonstrate how to use Helm Chart to deploy the Docker image to a simulated Kubernetes cluster in local environment (Docker Desktop). 

# Folder Structure Explained
After you clone the repository to your local file system, you will see the following 2 sub-folders (***iiq-app***, ***iiq-app-docker***) under the root folder (***iiq-docker***) as explained below. 

## iiq-app
This folder represents a **SSB Install Directory** you typically will use for an IdentityIQ implementation project. You can perfrom any commands supported by SSB. You should follow the same instructions in the SSB document to configure the files under this folder except that you cannot rename this folder.

You need to download the SSB package from the link below:

[https://community.sailpoint.com/t5/Professional-Services/Services-Standard-Build-SSB-v7-0-1/ta-p/190496](url)

Then unzip the file ***ssb-v7.0.1.zip*** and copy all the files and subfolders under folder ***ssb-v7.0.1*** to this location (***iip-app***). You can choose to have a different name than *iip-app*, but you will need to update the value of variable ***SSB_INSTALL_DIR_NAME*** in *create-docker.sh* and *create-docker.bat*.

When I download SSB package to start a brand new project, I noticed I have to comment out the following section in the *build.xml* file to make the build successful.


```
	<!-- Check whether the IIQ version is earlier than a given version
        number so that we can exclude certain components from compilation
        if they are not compatible with earlier versions 
        <script language="javascript">
            <![CDATA[
             var version = parseFloat(project.getProperty('IIQVersion'));
             project.setProperty('pre6.2', version < 6.2 ? "true" : "false");
             project.setProperty('pre6.3', version < 6.3 ? "true" : "false");
             project.setProperty('pre6.4', version < 6.4 ? "true" : "false"); 
             project.setProperty('pre7.0', version < 7.0 ? "true" : "false");
             project.setProperty('pre7.1', version < 7.1 ? "true" : "false");
             project.setProperty('pre7.2', version < 7.2 ? "true" : "false");
        	 project.setProperty('pre8.0', version < 8.0 ? "true" : "false");
           ]]>
        </script>
    	-->

```

Please note that IdentityIQ is closed source so you first need to get a license for IdenityIQ and go to [https://community.sailpoint.com](url) to download the software. Then you will put the downloaded zip and patch jar file into the base/ga and base/patch directory as per SSB document.

In another scenario that you may already have all source code in an existing *SSB Install Directory*, you simply need to copy all the files and subfolders to this folder. 


## iip-app-docker
This folder contains the script files to build the docker image for IdentityIQ. The docker image is built based from:

- 9.0.72-jdk11-temurin-focal

You can modify the file ***iiq-app-docker/Dockerfile*** to change to different version of Tomcat or JDK, but you will need to test to ensure the image still works in your docker environment.

Notes: to ensure the same docker image works across multiple environment, ***iiq.properties*** is removed when building the image. The idea is to mount ***iiq.propertie**s* (or ***log4j2.properties***) to the docker container separately (in Kubernetes via ConfigMap).   


# How to build IdentityIQ Docker Image
## Prerequisites
- Install Docker Desktop from:

	[https://www.docker.com/products/docker-desktop/](url)
- All other prerequisites to run SSB (such as JDK, Apache Ant etc..). Refer to the SSB document for details


## Build docker image
### Mac (linux)
Run the following command under the root folder (***iiq-docker***):

```
./create-docker.sh <<env>> <<imageName>>
```
You need to specify the parameters "environment" and "name of image" to execute the command. The default image name "iiq-image" will be used if that parameter is not specifed. Below is an exmaple to build image against sandbox environment. 

Note: the environment parameter is required only because of SSB, the image itself doesn't contain environment related files (***iiq.properties*** is stripped off from the war file inside the image).  

```
./create-docker.sh sandbox my-iiq-image
```
### Windows
Run the following command under the root folder (***iiq-docker***):

```
create-docker.bat
```
Type the "environment" and "name of image" parameters in the prompt to execute the build.

## Publish docker image
You may need to tag and publish the image to an internal docker image registry


# How to run IdentityIQ Docker Image
## Run in local Kubernetes cluster in Docker Desktop
Refer to the following repository for steps:
- [https://github.com/renliangfeng/iiq-helm](url)
