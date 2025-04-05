package org.mifos.community.ai.mcp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Address {
    String addressLine1;
    String addressLine2;
    String addressLine3;
    Integer addressTypeId;
    String city;
    Integer countryId;
    String postalCode;
    String stateProvinceId;
}
