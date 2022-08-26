package com.agprogramming.seom_v2_bff.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agprogramming.seom_v2_bff.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);
	Boolean existsByEmail(String email);
}
