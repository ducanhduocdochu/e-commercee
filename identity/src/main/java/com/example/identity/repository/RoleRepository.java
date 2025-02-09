package com.example.identity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.identity.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {}
