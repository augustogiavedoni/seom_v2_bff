package com.agprogramming.seom_v2_bff.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.agprogramming.seom_v2_bff.models.RefreshToken;
import com.agprogramming.seom_v2_bff.models.User;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  Optional<RefreshToken> findByToken(String token);

  @Modifying
  int deleteByUser(User user);
}
