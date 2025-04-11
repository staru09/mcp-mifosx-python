/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.mifos.community.ai.mcp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import org.mifos.community.ai.mcp.client.MifosXClient;
import org.mifos.community.ai.mcp.dto.*;

public class MifosXServer {

    @RestClient
    MifosXClient mifosXClient;

    @Tool(description = "Search for a client account by account number or client full name")
    JsonNode getClientByAccount(@ToolArg(description = "Client account number (e.g. 00000001)") String clientAccountNumber) {
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.query=clientAccountNumber;
        return mifosXClient.getClientByAccount(searchParameters);
    }
    
    @Tool(description = "Get client by id")
    JsonNode getClientDetailsById(@ToolArg(description = "Client Id (e.g. 1)") Integer clientId) {        
        return mifosXClient.getClientDetailsById(clientId);
    }

    @Tool(description = "List out " +
            "clients")
    JsonNode listClients(@ToolArg(description = "Optional search text (e.g. John)", required = false) String searchText) throws JsonProcessingException{

        Request request = new Request();
        request.setText(searchText != null ? searchText : "");

        ClientSearch clientSearch = new ClientSearch();
        clientSearch.setRequest(request);
        clientSearch.setPage(0);
        clientSearch.setSize(50);
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String jsonClientSearch = ow.writeValueAsString(clientSearch);
        return mifosXClient.listClients(jsonClientSearch);
    }
       
    @Tool(description = "Create a client using first name, last name, email address, mobile number and external id")
    JsonNode createClient(@ToolArg(description = "First Name (e.g. Jhon)", required = true) String firstName, 
            @ToolArg(description = "Last Name (e.g. Doe)", required = true) String lastName,
            @ToolArg(description = "Optional Email Address (e.g. jhon@gmail.com)", required = false) String emailAddress,
            @ToolArg(description = "Optional Mobile Number (e.g. +5215522649494)", required = false) String mobileNo,
            @ToolArg(description = "Optional External Id (e.g. VR12)", required = false) String externalId) throws JsonProcessingException {
        Client client = new Client();
        client.setFirstname(firstName);
        client.setLastname(lastName);
        if(emailAddress != null){
            client.setEmailAddress(emailAddress);
        }
        if(mobileNo != null){
            client.setMobileNo(mobileNo);
        }
        if(mobileNo != null){
            client.setExternalId(externalId);
        }                
        client.setOfficeId(1);
        client.setLegalFormId(1);
        client.setIsStaff("false");
        client.setActive(false);        
        client.setDateFormat("yyyy-MM-dd");
        client.setLocale("en");
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(client.getDateFormat());
        String formattedDate = currentDate.format(dtf);
        client.setActivationDate(formattedDate);
        client.setSubmittedOnDate(formattedDate);
        ArrayList<FamilyMember> familyMembers = new ArrayList<FamilyMember>();
        client.setFamilyMembers(familyMembers);
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String jsonClient = ow.writeValueAsString(client);
        return mifosXClient.createClient(jsonClient);
    }

    @Tool(description = "Add an address to a client by his account number. Required fields: addressLine1, addressLine2, addressLine3, addressTypeId, " +
            "city, countryId, postalCode, stateProvinceId")
    JsonNode addAddress(@ToolArg(description = "Client Id (e.g. 1)") Integer clientId,
                        @ToolArg(description = "Address Line 1 (e.g. 742 Evergreen Terrace)") String addressLine1,
                        @ToolArg(description = "Address Line 2 (optional, e.g. Apt 2B)", required = false) String addressLine2,
                        @ToolArg(description = "Address Line 3 (optional, e.g. Floor 3)", required = false) String addressLine3,
                        @ToolArg(description = "Address Type Id (e.g. 18)", required = false) Integer addressTypeId,
                        @ToolArg(description = "City (e.g. Springfield)") String city,
                        @ToolArg(description = "Country Id (e.g. 1)", required = false) Integer countryId,
                        @ToolArg(description = "Postal Code (e.g. 12345)") String postalCode,
                        @ToolArg(description = "State/Province Id (e.g. 20)", required = false) Integer stateProvinceId) throws JsonProcessingException {
        Address address = new Address();

        address.setAddressLine1(Optional.ofNullable(addressLine1).orElse(""));
        address.setAddressLine2(Optional.ofNullable(addressLine2).orElse(""));
        address.setAddressLine3(Optional.ofNullable(addressLine3).orElse(""));
        address.setAddressTypeId(15);
        address.setCity(city);
        address.setCountryId(16); //Valor de prueba se va a cambiar
        address.setPostalCode(postalCode);
        address.setStateProvinceId(17);

        ObjectMapper ow = new ObjectMapper();
        ow.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String jsonAddress = ow.writeValueAsString(address);

        return mifosXClient.addAddress(clientId,address.getAddressTypeId(),jsonAddress);
    }

    @Tool(description = "Add a family member to a client by his account number. Required fields: firstName, lastName, age, relationship, genderId, dateOfBirth," +
            " middleName, qualification, isDependent, professionId, maritalStatusId, dateFormat, locale")
    JsonNode addFamilyMember(@ToolArg(description = "Client Id (e.g. 1)") Integer clientId,
            @ToolArg(description = "First Name (e.g. Jhon)") String firstName,
            @ToolArg(description = "Middle Name (e.g. Cena), replace with \"\" if not provided", required = false) String middleName,
            @ToolArg(description = "Last Name (e.g. Doe)") String lastName,
            @ToolArg(description = "Qualification (e.g. MBA), replace with \"\" if not provided", required = false) String qualification,
            @ToolArg(description = "Age (e.g. 25)") Integer age,
            @ToolArg(description = "Is Dependent (e.g. \"true\"), replace with \"\" if not provided", required = false) String isDependent,
            @ToolArg(description = "Relationship (e.g. friend)") String relationship,
            @ToolArg(description = "Gender (e.g. male), replace with \"\" if not provided", required = false) String gender,
            @ToolArg(description = "Profession (e.g. unemployed), replace with \"\" if not provided", required = false) String profession,
            @ToolArg(description = "Marital Status (e.g. married), replace with \"\" if not provided", required = false) String maritalStatus,
            @ToolArg(description = "Date of Birth (e.g. 03 June 2003)") String dateOfBirth,
            @ToolArg(description = "Date Format (e.g. yyyy-MM-dd)",required = false) String dateFormat,
            @ToolArg(description = "Locale (e.g. en)",required = false) String locale) throws JsonProcessingException {
        FamilyMember familyMember = new FamilyMember();

        familyMember.setMiddleName(Optional.ofNullable(middleName).orElse(""));
        familyMember.setQualification(Optional.ofNullable(qualification).orElse(""));
        familyMember.setIsDependent(Optional.ofNullable(isDependent).orElse("false"));
        familyMember.setProfessionId(getProfessionId(Optional.ofNullable(profession).orElse("")));
        familyMember.setGenderId(getGenderId(Optional.ofNullable(gender).orElse("")));
        familyMember.setMaritalStatusId(getMaritalStatusId(Optional.ofNullable(maritalStatus).orElse("")));
        familyMember.setFirstName(firstName);
        familyMember.setLastName(lastName);
        familyMember.setAge(age);
        familyMember.setDateOfBirth(dateOfBirth);
        familyMember.setDateFormat("dd MMMM yyyy");
        familyMember.setLocale("en");
        ObjectMapper ow = new ObjectMapper();
        ow.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String jsonClient = ow.writeValueAsString(familyMember);

        return mifosXClient.addFamilyMember(clientId, jsonClient);
    }

    private int getProfessionId(String profession) {
        switch (profession.toLowerCase()) {
            case "unemployed":
                return 18;
            case "student":
                return 19;
            default:
                return 18;
        }
    }

    private int getGenderId(String gender) {
        switch (gender.toLowerCase()) {
            case "male":
                return 20;
            case "female":
                return 21;
            default:
                return 22;
        }
    }

    private int getMaritalStatusId(String maritalStatus) {
        switch (maritalStatus.toLowerCase()) {
            case "single":
                return 23;
            case "married":
                return 24;
            default:
                return 23;
        }
    }
}