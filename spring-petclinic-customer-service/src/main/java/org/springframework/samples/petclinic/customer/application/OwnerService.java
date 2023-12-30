package org.springframework.samples.petclinic.customer.application;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.customer.application.command.OwnerCommand;
import org.springframework.samples.petclinic.customer.application.data.OwnerData;
import org.springframework.samples.petclinic.customer.application.mapper.OwnerMapper;
import org.springframework.samples.petclinic.customer.domain.Owner;
import org.springframework.samples.petclinic.customer.domain.OwnerRepository;
import org.springframework.samples.petclinic.customer.domain.shared.DomainError;
import org.springframework.samples.petclinic.customer.domain.shared.DomainException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OwnerService {

    private final OwnerRepository ownerRepository;
    private final OwnerMapper ownerMapper;
    private final AtomicInteger allOwnersNumber;

    @Autowired
    public OwnerService(OwnerRepository ownerRepository, OwnerMapper ownerMapper, MeterRegistry meterRegistry) {
        this.ownerRepository = ownerRepository;
        this.ownerMapper = ownerMapper;
        allOwnersNumber = meterRegistry.gauge("ownerService.allOwnersSize", Tags.of("sizeOf", "allOwners"), new AtomicInteger(0));
    }

    public OwnerData create(OwnerCommand ownerCommand) {
        return ownerMapper.toDto(ownerRepository.save(ownerMapper.toEntity(ownerCommand)));
    }

    public List<OwnerData> findAll() {
        log.info("Starting retrieving all owners");
        List<OwnerData> owners = ownerMapper.toDto(ownerRepository.findAll());
        allOwnersNumber.set(owners.size());
        return owners;
    }

    public OwnerData updateOwner(Integer ownerId, OwnerCommand ownerCommand) {
        Owner savedOwner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new DomainException("Owner with id [" + ownerId + "] not found", DomainError.RESOURCE_NOT_FOUND));
        ownerMapper.update(ownerCommand, savedOwner);
        return ownerMapper.toDto(ownerRepository.save(savedOwner));
    }

    public OwnerData findById(int ownerId) {
        return ownerMapper.toDto(ownerRepository.findById(ownerId)
                .orElseThrow(() -> new DomainException("Owner with id [" + ownerId + "] not found", DomainError.RESOURCE_NOT_FOUND)));
    }
}
