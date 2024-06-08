package com.securitydemo.jwt_security_demo.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.securitydemo.jwt_security_demo.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

   @Query("SELECT u FROM User u WHERE u.username = :username")
    User loadUserByUsername(@Param("username") String username);
}
