package com.example.questionbank.repository;

import com.example.questionbank.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role getRoleByName(String roleAdmin);
    Role findByName(String admin);
}
