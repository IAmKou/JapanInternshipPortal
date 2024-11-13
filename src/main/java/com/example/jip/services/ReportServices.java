package com.example.jip.services;

import com.example.jip.entity.Account;
import com.example.jip.entity.Report;
import com.example.jip.repository.AccountRepository;
import com.example.jip.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReportServices {
    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private AccountRepository accountRepository;

    public Report createReport(String title, String content, int reporter_id) {
        Optional<Account> accountOpt = accountRepository.findById(reporter_id);
        if (!accountOpt.isPresent()) {
            throw new IllegalArgumentException("No account found with id: " + reporter_id);
        }

        Report report = new Report();
        report.setTitle(title);
        report.setContent(content + "; User id : " + reporter_id);
        report.setAccount(accountOpt.get());

        return reportRepository.save(report);
    }
}
