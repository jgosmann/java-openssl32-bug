# Java <-> OpenSSL 3.2 incompatibility

Launch the Docker image with:

```shell
docker run --rm -it jgosmann/java-openssl32-bug
```

## Reproducing the problem

```shell
java TLSServer &  # Run the Java TLS server in the background
LD_LIBRARY_PATH=/opt/openssl-3.2.0/lib64/ /opt/openssl-3.2.0/bin/openssl s_client -msg localhost:9999
```

The server accepts a single line of input and then terminates the connection
before shutting down itself.

Provide some arbitrary input and notice that the client terminates with:

```
Received from client: some input
<<< TLS 1.2, RecordHeader [length 0005]
    17 03 03 00 23
<<< TLS 1.3, InnerContent [length 0001]
    15
<<< TLS 1.3, Alert [length 0002], warning user_canceled
    01 5a
<<< TLS 1.2, RecordHeader [length 0005]
    17 03 03 00 23
<<< TLS 1.3, InnerContent [length 0001]
    15
<<< TLS 1.3, Alert [length 0002], warning close_notify
    01 00
read:errno=0
>>> TLS 1.2, RecordHeader [length 0005]
    17 03 03 00 13
>>> TLS 1.2, InnerContent [length 0001]
    15
>>> TLS 1.3, Alert [length 0002], warning close_notify
    01 00
```

Note, that the Java server sends a `user_canceled` alert before the
`close_notify`. This causes OpenSSL to error out. If this wasn't treated as an
error, it would say `closed` instead.

## Workarounds

The problem does not occur with an earlier OpenSSL version (3.1), e.g.:

```shell
/usr/bin/openssl s_client -msg localhost:9999
```

It also does not occur when prohibiting TLS 1.3 and using TLS 1.2 instead:

```shell
LD_LIBRARY_PATH=/opt/openssl-3.2.0/lib64/ /opt/openssl-3.2.0/bin/openssl s_client -msg -no_tls1_3 localhost:9999
```
