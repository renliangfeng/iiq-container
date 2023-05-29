FROM tomcat:9.0.72-jdk11-temurin-focal AS build-iiq-app
ARG SPTARGET
# Install dependencies
RUN apt-get update && \
apt-get install -y apt-utils && \
apt-get install -y ant

RUN echo "Oh dang look at that $SPTARGET"

WORKDIR /iiq-app/
COPY iiq-app ./
RUN export SPTARGET=$SPTARGET && \
rm -Rf build && \
# The following line is needed to build docker image in windows. Without this, it will complain ./build.sh file can not be found. This is due to encoding issue.
sed -i 's/\r$//' build.sh  && \
chmod +x build.sh && \
./build.sh clean war


FROM tomcat:9.0.72-jdk11-temurin-focal
ARG FULL_TEXT_INDEX_PATH
ARG UPLOAD_FILE_PATH

# Install dependencies
RUN apt-get update && \
apt-get install -y apt-utils

COPY --from=build-iiq-app /iiq-app/build/deploy/identityiq.war /tmp

# Add admin/admin user
ADD tomcat-users.xml /usr/local/tomcat/conf/
#run mkdir -p /usr/local/tomcat/conf/Catalina/localhost
#ADD manager.xml /usr/local/tomcat/conf/Catalina/localhost

# COPY volumes/identityiq.war /tmp
RUN mkdir /usr/local/tomcat/webapps/identityiq && \
cd /usr/local/tomcat/webapps/identityiq && \
jar -xf /tmp/identityiq.war && \
chmod +x /usr/local/tomcat/webapps/identityiq/WEB-INF/bin/iiq && \
rm /usr/local/tomcat/webapps/identityiq/WEB-INF/classes/iiq.properties && \
rm -Rf /usr/local/tomcat/webapps/identityiq/WEB-INF/config/custom && \
cd /tmp && rm identityiq.war

RUN mkdir /usr/local/tomcat/webapps/ROOT
COPY index.html /usr/local/tomcat/webapps/ROOT

RUN mkdir -p /usr/local/keystore

RUN if [ -z "$FULL_TEXT_INDEX_PATH" ] ; then echo No directory is created for full text index; else mkdir -p $FULL_TEXT_INDEX_PATH; fi
RUN if [ -z "$UPLOAD_FILE_PATH" ] ; then echo No directory is created for file uploading; else mkdir -p $UPLOAD_FILE_PATH; fi

COPY entrypoint.sh /entrypoint.sh
RUN sed -i 's/\r$//' /entrypoint.sh  && \
chmod +x /entrypoint.sh

EXPOSE 8080
EXPOSE 8009
EXPOSE 8443
VOLUME "/usr/local/tomcat/webapps"
WORKDIR /usr/local/tomcat

# Launch IIQ
CMD ["/entrypoint.sh", "run"]
#CMD ["/opt/tomcat/bin/catalina.sh", "run"]