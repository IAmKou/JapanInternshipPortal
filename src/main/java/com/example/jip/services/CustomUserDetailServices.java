package com.example.jip.services;

import com.example.jip.configuration.CustomUserDetail;
import com.example.jip.entity.Account;
import com.example.jip.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailServices implements UserDetailsService {
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));


        List<GrantedAuthority> grantedAuthorities = List.of(new SimpleGrantedAuthority
                (account.getRole().getName()));

        return new CustomUserDetail(
                account.getUsername(),
                account.getPassword(),
                account.getId(),
                grantedAuthorities);
    }
}
