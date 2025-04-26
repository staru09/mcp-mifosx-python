package org.mifos.community.ai.mcp.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Currency {
    String code;
    String name;
    Integer decimalPlaces;
    String displaySymbol;
    String nameCode;
    String displayLabel;
}
