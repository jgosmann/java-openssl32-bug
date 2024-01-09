FROM debian:experimental-20231218

RUN apt-get update \
    && apt-get install --assume-yes openjdk-21-jdk-headless gcc make perl wget \
    && apt-get clean

COPY sha256sums sha256sums
RUN wget https://www.openssl.org/source/openssl-3.2.0.tar.gz \
    && sha256sum -c sha256sums \
    && tar xzf openssl-3.2.0.tar.gz \
    && cd openssl-3.2.0 \
    && ./Configure --prefix=/opt/openssl-3.2.0 \
    && make -j 4 \
    && make -j 4 install

COPY TLSServer.java TLSServer.java
RUN javac TLSServer.java \
    && keytool -genkey -keyalg RSA -alias selfsigned \
    -keystore server_keystore.jks -storepass your_keystore_password \
    -validity 365 -keysize 2048 \
    -dname "CN=YourName,OU=YourUnit,O=YourOrg,L=YourCity,S=YourState,C=YourCountry" \
    -noprompt


COPY README.md README.md

ENTRYPOINT ["/bin/bash"]
