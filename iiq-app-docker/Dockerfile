FROM tomcat:9.0.72-jdk11-temurin-focal

# Install dependencies
RUN apt-get update && \
apt-get install -y apt-utils

# Add admin/admin user
ADD tomcat-users.xml /usr/local/tomcat/conf/
#run mkdir -p /usr/local/tomcat/conf/Catalina/localhost
#ADD manager.xml /usr/local/tomcat/conf/Catalina/localhost

COPY volumes/identityiq.war /tmp
RUN mkdir /usr/local/tomcat/webapps/identityiq && \
cd /usr/local/tomcat/webapps/identityiq && \
jar -xf /tmp/identityiq.war && \
chmod +x /usr/local/tomcat/webapps/identityiq/WEB-INF/bin/iiq && \
rm /usr/local/tomcat/webapps/identityiq/WEB-INF/classes/iiq.properties && \
cd /tmp && rm identityiq.war

RUN mkdir /usr/local/tomcat/webapps/ROOT
COPY index.html /usr/local/tomcat/webapps/ROOT

RUN mkdir -p /usr/local/keystore

COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

EXPOSE 8080
EXPOSE 8009
EXPOSE 8443
VOLUME "/usr/local/tomcat/webapps"
WORKDIR /usr/local/tomcat

# Launch IIQ
CMD ["/entrypoint.sh", "run"]
#CMD ["/opt/tomcat/bin/catalina.sh", "run"]
