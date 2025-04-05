/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.mifos.community.ai.mcp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FamilyMember {
    String firstName;
    String middleName;
    String lastName;
    String qualification;
    Integer age;
    String isDependent;
    Integer relationship;
    Integer genderId;
    Integer professionId;
    Integer maritalStatusId;
    String dateOfBirth;
    String dateFormat;
    String locale;
}
