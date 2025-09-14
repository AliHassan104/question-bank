package com.example.questionbank.dto;

import com.example.questionbank.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomUserDetail implements UserDetails {

    private final User user;

    public CustomUserDetail(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        // DEBUG: Log user details
        System.out.println("=== CustomUserDetail DEBUG ===");
        System.out.println("User: " + user.getName());
        System.out.println("User ID: " + user.getId());
        System.out.println("Number of roles: " + (user.getRoles() != null ? user.getRoles().size() : 0));

        if (user.getRoles() != null) {
            // Add roles with the "ROLE_" prefix
            this.user.getRoles().forEach(role -> {
                System.out.println("Processing role: " + role.getName());

                // Add role with ROLE_ prefix for hasRole() checks
                String roleAuthority = "ROLE_" + role.getName();
                authorityList.add(new SimpleGrantedAuthority(roleAuthority));
                System.out.println("Added role authority: " + roleAuthority);

                // Also add role without prefix for hasAuthority() checks
                authorityList.add(new SimpleGrantedAuthority(role.getName()));
                System.out.println("Added plain role: " + role.getName());

                // Add permissions
                if (role.getPermissions() != null) {
                    System.out.println("Number of permissions for role " + role.getName() + ": " + role.getPermissions().size());
                    role.getPermissions().forEach(permission -> {
                        authorityList.add(new SimpleGrantedAuthority(permission.getName()));
                        System.out.println("Added permission: " + permission.getName());
                    });
                } else {
                    System.out.println("No permissions found for role: " + role.getName());
                }
            });
        } else {
            System.out.println("No roles found for user: " + user.getName());
        }

        System.out.println("Total authorities granted: " + authorityList.size());
        System.out.println("All authorities: ");
        authorityList.forEach(auth -> System.out.println(" - " + auth.getAuthority()));
        System.out.println("=== END CustomUserDetail DEBUG ===");

        return authorityList;
    }

    public Long getUserId(){
        return this.user.getId();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {return user.getName();}

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}