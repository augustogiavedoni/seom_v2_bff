package com.agprogramming.seom_v2_bff.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agprogramming.seom_v2_bff.models.Role;
import com.agprogramming.seom_v2_bff.models.SystemRole;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
	Optional<Role> findByName(SystemRole name);
}
