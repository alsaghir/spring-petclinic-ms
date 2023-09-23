package org.springframework.samples.petclinic.customer.application.data;

import java.util.List;

public record OwnerData(Integer id, String firstName,
                        String lastName,
                        String address,
                        String city,
                        String telephone, List<PetData> pets) {
}
