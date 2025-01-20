# Use the official Keycloak image
FROM quay.io/keycloak/keycloak:26.0.7

# Switch to root user to copy the JAR file and configuration files
USER root

# Create the "providers" directory if it doesn't exist
RUN mkdir -p /opt/keycloak/providers/

# Copy the custom provider JAR file into the container
COPY target/user-provider-0.0.1-SNAPSHOT.jar /opt/keycloak/providers/

COPY spring-security-crypto-6.4.1.jar /opt/keycloak/providers/

# Copy keycloak.conf
COPY conf/quarkus.properties /opt/keycloak/conf/
#COPY conf/keycloak.conf /opt/keycloak/conf/keycloak.conf

# Ensure the "keycloak" user has access to the "providers" directory and configuration files
RUN chown -R keycloak:keycloak /opt/keycloak/providers/ /opt/keycloak/conf/ | xargs command

# Switch back to the "keycloak" user to run Keycloak
USER keycloak
ENV KEYCLOAK_ADMIN=admin
ENV KEYCLOAK_ADMIN_PASSWORD=admin123

# Start Keycloak
ENTRYPOINT ["/opt/keycloak/bin/kc.sh", "start-dev"]