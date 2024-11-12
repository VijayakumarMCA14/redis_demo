package com.vijay.redis_demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ResourceLoader;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.JedisPooled;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

@SpringBootApplication
public class RedisDemoApplication implements CommandLineRunner {

	@Autowired
	private ResourceLoader resourceLoader;

	public static void main(String[] args) {
		SpringApplication.run(RedisDemoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		HostAndPort address = new HostAndPort("localhost", 6379);


		JedisClientConfig config = DefaultJedisClientConfig.builder()
				.ssl(true).sslSocketFactory(sslSocketFactory())
				.user("admin") // use your Redis user. More info https://redis.io/docs/latest/operate/oss_and_stack/management/security/acl/
				.password("password") // use your Redis password
				.build();


		JedisPooled jedis = new JedisPooled(address, config);
		jedis.set("foo", "bar");
		System.out.println(jedis.get("foo"));
	}

	private SSLSocketFactory sslSocketFactory() throws NoSuchAlgorithmException, CertificateException, IOException, KeyStoreException, KeyManagementException {

		CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");
		X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(resourceLoader.getResource("classpath:redis.pem").getInputStream());

		KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
		keyStore.load(null, null); // Initialize an empty KeyStore
		keyStore.setCertificateEntry("redis_certs", certificate);

//		TrustManagerFactory with the KeyStore
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(keyStore);

		// Step 4: Create an SSLContext with the TrustManager
		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(null, tmf.getTrustManagers(), null);

		return sslContext.getSocketFactory();
	}
}
