# Create User in redis:
ACL SETUSER admin ON >password ~* +@all

### Generate a private key
openssl genrsa -out redis.key 2048

### Generate a certificate signing request (CSR)
openssl req -new -key redis.key -out redis.csr -subj "/CN=localhost/OU=IT/O=OWN/L=CHENNAI/ST=TN/C=IN"

### Generate a self-signed certificate
openssl x509 -req -days 365 -in redis.csr -signkey redis.key -out redis.pem

openssl x509 -outform der -in redis.pem -out redis.der

### Copy certs or generate cert in to some location
copy certificate in to /etc/redis/certs 

#### Configure the redis.conf file

### Start the redis server 
sudo redis-server /etc/redis/redis.conf

### Start the redis client 
sudo redis-cli -h 127.0.0.1 -p 6379 --tls --cert /etc/redis/certs/redis.pem --key /etc/redis/certs/redis.key --cacert /etc/redis/certs/redis.pem

### Authenticate using username and password
auth admin password

### Test the redis connection type ping & you wil get response as PONG
ping 

### Example

> /etc/redis$ sudo redis-cli -h 127.0.0.1 -p 6379 --tls --cert /usr/local/redis_certs/redis.pem --key /usr/local/redis_certs/redis.key --cacert /usr/local/redis_certs/redis.pem
127.0.0.1:6379> AUTH admin password
OK
127.0.0.1:6379> ping
PONG
127.0.0.1:6379> set testkey testvalue
OK
127.0.0.1:6379> get testkey
"testvalue"
127.0.0.1:6379>
