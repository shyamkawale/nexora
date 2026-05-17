package com.svk.nexora_be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import io.github.cdimascio.dotenv.Dotenv;

@EnableCaching
@SpringBootApplication
public class NexoraBeApplication {

	static {
		try {
			Dotenv dotenv = Dotenv.load();
			
			// Database Configuration (Environment-specific)
			setPropertyIfExists(dotenv, "DB_URL");
			setPropertyIfExists(dotenv, "DB_USERNAME");
			setPropertyIfExists(dotenv, "DB_PASSWORD");
			setPropertyIfExists(dotenv, "DB_DEFAULT_SCHEMA");
			
			// Database Connection Pool Configuration (Tuning per environment)
			setPropertyIfExists(dotenv, "DB_HIKARI_MAXIMUM_POOL_SIZE");
			setPropertyIfExists(dotenv, "DB_HIKARI_MINIMUM_IDLE");
			setPropertyIfExists(dotenv, "DB_HIKARI_CONNECTION_TIMEOUT");
			setPropertyIfExists(dotenv, "DB_HIKARI_IDLE_TIMEOUT");
			setPropertyIfExists(dotenv, "DB_HIKARI_MAX_LIFETIME");
			
			// Redis Configuration (Environment-specific)
			setPropertyIfExists(dotenv, "REDIS_HOST");
			setPropertyIfExists(dotenv, "REDIS_PORT");
			
			// JWT Configuration (Environment-specific)
			setPropertyIfExists(dotenv, "JWT_SECRET");
			
			// AWS S3 Configuration (Environment-specific)
			setPropertyIfExists(dotenv, "AWS_ACCESS_KEY");
			setPropertyIfExists(dotenv, "AWS_SECRET_KEY");
			setPropertyIfExists(dotenv, "AWS_REGION");
			setPropertyIfExists(dotenv, "AWS_BUCKET_NAME");

			// OpenAI Configuration (Environment-specific)
			setPropertyIfExists(dotenv, "OPENAI_API_KEY");
			
			System.out.println("✓ Environment variables loaded from .env file successfully");
		} catch (Exception e) {
			System.out.println("⚠ Warning: Could not load .env file: " + e.getMessage());
			System.out.println("  Using environment variables or default values from application.yaml");
		}
	}

	/**
	 * Helper method to safely set system property from .env file
	 * Only sets if the value exists in .env and is not null
	 */
	private static void setPropertyIfExists(Dotenv dotenv, String key) {
		String value = dotenv.get(key);
		if (value != null && !value.isEmpty()) {
			System.setProperty(key, value);
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(NexoraBeApplication.class, args);
	}

}
