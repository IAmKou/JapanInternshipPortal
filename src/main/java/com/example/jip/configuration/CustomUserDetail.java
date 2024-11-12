package com.example.jip.configuration;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetail implements UserDetails {
    private String username;
    private String password;
    private int accountId;
    private int roleId;

    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetail(String username, String password, int accountId, int roleId, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.accountId = accountId;
        this.authorities = authorities;
        this.roleId = roleId;
    }

    public int getAccountId() {
        return accountId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

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
    public int getRoleId() {
        return roleId;
    }


}

