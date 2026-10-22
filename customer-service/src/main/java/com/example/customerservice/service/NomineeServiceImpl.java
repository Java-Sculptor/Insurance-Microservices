package com.example.customerservice.service;

import com.example.customerservice.dto.NomineeRequest;
import com.example.customerservice.dto.NomineeResponse;
import com.example.customerservice.entity.Customer;
import com.example.customerservice.entity.Nominee;
import com.example.customerservice.exception.CustomerNotFoundException;
import com.example.customerservice.exception.ResourceNotFoundException;
import com.example.customerservice.repository.CustomerRepository;
import com.example.customerservice.repository.NomineeRepository;
import com.example.customerservice.service.NomineeService;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NomineeServiceImpl implements NomineeService {

    private final NomineeRepository nomineeRepository;
    private final CustomerRepository customerRepository;

    public NomineeServiceImpl(
            NomineeRepository nomineeRepository,
            CustomerRepository customerRepository) {
        this.nomineeRepository = nomineeRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional
    public NomineeResponse addNominee(
            Long customerId, NomineeRequest request) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(
                        "Customer not found with ID: " + customerId));

        Nominee nominee = new Nominee();
        nominee.setCustomer(customer);

        mapRequestToEntity(request, nominee);

        Nominee savedNominee = nomineeRepository.save(nominee);

        return mapToResponse(savedNominee);
    }

    @Override
    public List<NomineeResponse> getNomineesByCustomerId(Long customerId) {

        if (!customerRepository.existsById(customerId)) {
            throw new CustomerNotFoundException(
                    "Customer not found with ID: " + customerId);
        }

        return nomineeRepository.findByCustomerCustomerId(customerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public NomineeResponse updateNominee(
            Long nomineeId, NomineeRequest request) {

        Nominee nominee = nomineeRepository.findById(nomineeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Nominee not found with ID: " + nomineeId));

        mapRequestToEntity(request, nominee);

        Nominee updatedNominee = nomineeRepository.save(nominee);

        return mapToResponse(updatedNominee);
    }

    @Override
    @Transactional
    public void deleteNominee(Long nomineeId) {

        Nominee nominee = nomineeRepository.findById(nomineeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Nominee not found with ID: " + nomineeId));

        nomineeRepository.delete(nominee);
    }

    private void mapRequestToEntity(
            NomineeRequest request, Nominee nominee) {

        nominee.setNomineeName(request.getNomineeName());
        nominee.setRelationship(request.getRelationship());
        nominee.setDateOfBirth(request.getDateOfBirth());
        nominee.setMobileNumber(request.getMobileNumber());
        nominee.setSharePercentage(request.getSharePercentage());
    }

    private NomineeResponse mapToResponse(Nominee nominee) {

        NomineeResponse response = new NomineeResponse();

        response.setNomineeId(nominee.getNomineeId());
        response.setCustomerId(nominee.getCustomer().getCustomerId());
        response.setNomineeName(nominee.getNomineeName());
        response.setRelationship(nominee.getRelationship());
        response.setDateOfBirth(nominee.getDateOfBirth());
        response.setMobileNumber(nominee.getMobileNumber());
        response.setSharePercentage(nominee.getSharePercentage());

        return response;
    }
}
