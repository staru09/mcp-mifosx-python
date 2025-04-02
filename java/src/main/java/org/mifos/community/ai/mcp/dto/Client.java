package org.mifos.community.ai.mcp.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Client {
    
    int officeId;
    int legalFormId;
    boolean isStaff;
    boolean  active;
    String submittedOnDate;
    String firstName;
    String lastName;
    String emailAddress;
    String mobileNo;
    String externalId;
    String  activationDate;
    ArrayList<FamilyMember> familyMembers;
    String dateFormat;
    String locale;
    
}
