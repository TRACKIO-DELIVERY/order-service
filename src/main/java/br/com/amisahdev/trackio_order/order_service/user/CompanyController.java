package br.com.amisahdev.trackio_order.order_service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import br.com.amisahdev.trackio_order.order_service.user.models.Company;
import br.com.amisahdev.trackio_order.order_service.user.repository.CompanyRepository;
import br.com.amisahdev.trackio_order.order_service.user.repository.UserRepository;
import br.com.amisahdev.trackio_order.order_service.user.models.User;

@RestController
@RequestMapping("/api/company")
public class CompanyController {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createCompany(@RequestBody Company company) {

        companyRepository.save(company);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "The company was created");
        response.put("status", 201);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
