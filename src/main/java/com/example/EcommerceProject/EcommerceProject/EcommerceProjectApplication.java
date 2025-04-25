package com.example.EcommerceProject.EcommerceProject;

import com.example.EcommerceProject.EcommerceProject.Entity.User.Admin;
import com.example.EcommerceProject.EcommerceProject.Entity.User.Role;
import com.example.EcommerceProject.EcommerceProject.Entity.User.User;
import com.example.EcommerceProject.EcommerceProject.Repository.AdminRepository;
import com.example.EcommerceProject.EcommerceProject.Repository.RoleRepository;
import com.example.EcommerceProject.EcommerceProject.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

@SpringBootApplication(scanBasePackages = "com.example.EcommerceProject.EcommerceProject")
@EnableJpaAuditing(auditorAwareRef = "auditProvider")
@EnableAsync
@EnableScheduling
@EnableMethodSecurity
public class EcommerceProjectApplication {
   @Autowired
	RoleRepository roleRepository;

   @Autowired
	AdminRepository adminRepository;

	public static void main(String[] args) {
		SpringApplication.run(EcommerceProjectApplication.class, args);
	}
	@Bean
	CommandLineRunner createAdmin(AdminRepository adminRepository,
								  RoleRepository roleRepository,
								  PasswordEncoder passwordEncoder) {
		return args -> {

			if(roleRepository.findAll().isEmpty()){
				Role roleAdmin = new Role();
				Role roleSeller = new Role();
				Role roleCustomer = new Role();

				roleAdmin.setAuthority("ROLE_ADMIN");
				roleSeller.setAuthority("ROLE_SELLER");
				roleCustomer.setAuthority("ROLE_CUSTOMER");
				roleRepository.save(roleAdmin);
				roleRepository.save(roleSeller);
				roleRepository.save(roleCustomer);
				System.out.println("✅ Role created successfully!");
			}

			if (adminRepository.findByEmail("divyanshi.sarraf@tothenew.com").isEmpty()) {
				Admin admin = new Admin();
				admin.setFirstName("Divyanshi");
				admin.setLastName("Sarraf");
				admin.setEmail("divyanshi.sarraf@tothenew.com");
				admin.setPassword(passwordEncoder.encode("Strong@123"));

				Optional<Role> optionalRole = roleRepository.findByAuthority("ROLE_ADMIN");
				if (optionalRole.isEmpty()) {
					throw new RuntimeException("ROLE_ADMIN not found in the database.");
				}

				Role role = optionalRole.get();
				admin.setRole(role);

				admin.setActive(true);
				admin.setDeleted(false);
				admin.setExpired(false);
				admin.setLocked(false);
				admin.setPasswordUpdateDate(LocalDate.now());
				admin.setInvalidAttemptCount(0);

				adminRepository.save(admin); // ✅ Will go to the `admin` table
				System.out.println("✅ Admin user created successfully!");
			}
		};
	}

}
