package org.springframework.samples.petclinic.customer.application.data;

public record OwnerData(Integer id, String firstName,
                        String lastName,
                        String address,
                        String city,
                        String telephone) {
}
